package com.xuexiang.template.fragment.other;
import static android.content.ContentValues.TAG;
import static com.xuexiang.template.config.Config.baseUrl;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.xuexiang.template.R;
import com.xuexiang.template.activity.MainActivity;
import com.xuexiang.template.core.BaseFragment;
import com.xuexiang.template.databinding.FragmentLoginBinding;
import com.xuexiang.template.utils.MQTT;
import com.xuexiang.template.utils.NetworkUtils;
import com.xuexiang.template.utils.RandomUtils;
import com.xuexiang.template.utils.Response;
import com.xuexiang.template.utils.SettingUtils;
import com.xuexiang.template.utils.TokenUtils;
import com.xuexiang.template.utils.Utils;
import com.xuexiang.template.utils.sdkinit.UMengInit;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.utils.CountDownButtonHelper;
import com.xuexiang.xui.utils.ResUtils;
import com.xuexiang.xui.utils.ThemeUtils;
import com.xuexiang.xui.utils.ViewUtils;
import com.xuexiang.xui.utils.XToastUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xutil.app.ActivityUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Objects;


/**
 * 登录页面
 */
@Page(anim = CoreAnim.none)
public class LoginFragment extends BaseFragment<FragmentLoginBinding> implements View.OnClickListener {
    private String emailCode = null;
    private View mJumpView;

    private CountDownButtonHelper mCountDownHelper;

    @NonNull
    @Override
    protected FragmentLoginBinding viewBindingInflate(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, boolean attachToRoot) {
        return FragmentLoginBinding.inflate(inflater, container, attachToRoot);
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle()
                .setImmersive(true);
        titleBar.setBackgroundColor(Color.TRANSPARENT);
        titleBar.setTitle("");
        titleBar.setLeftImageDrawable(ResUtils.getVectorDrawable(getContext(), R.drawable.ic_login_close));
        titleBar.setActionTextColor(ThemeUtils.resolveColor(getContext(), R.attr.colorAccent));
        mJumpView = titleBar.addAction(new TitleBar.TextAction(R.string.title_jump_login) {
            @Override
            public void performAction(View view) {
                openPage(RegisterFragment.class);
            }
        });
        return titleBar;
    }

    @Override
    protected void initViews() {
        mCountDownHelper = new CountDownButtonHelper(binding.btnGetVerifyCode, 60);
        //隐私政策弹窗
        if (!SettingUtils.isAgreePrivacy()) {
            Utils.showPrivacyDialog(getContext(), (dialog, which) -> {
                dialog.dismiss();
                handleSubmitPrivacy();
            });
        }
        boolean isAgreePrivacy = SettingUtils.isAgreePrivacy();
        binding.cbProtocol.setChecked(isAgreePrivacy);
        refreshButton(isAgreePrivacy);
        binding.cbProtocol.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SettingUtils.setIsAgreePrivacy(isChecked);
            refreshButton(isChecked);
        });

        binding.tvLoginPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPage(LoginPwdFragment.class);
                onDestroyView();
            }
        });

        Intent serviceIntent = new Intent(this.getContext(), MQTT.class);
        this.requireContext().startService(serviceIntent);
    }

    @Override
    protected void initListeners() {
        binding.btnGetVerifyCode.setOnClickListener(this);
        binding.btnLogin.setOnClickListener(this);
        binding.tvForgetPassword.setOnClickListener(this);
        binding.tvUserProtocol.setOnClickListener(this);
        binding.tvPrivacyProtocol.setOnClickListener(this);
    }

    private void refreshButton(boolean isChecked) {
        ViewUtils.setEnabled(binding.btnLogin, isChecked);
        ViewUtils.setEnabled(mJumpView, isChecked);
    }

    private void handleSubmitPrivacy() {
        SettingUtils.setIsAgreePrivacy(true);
        UMengInit.init();
        // 应用市场不让默认勾选
//        ViewUtils.setChecked(cbProtocol, true);
    }

    @SingleClick
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_login) {
            if (binding.etPhoneNumber.validate()) {
                if (binding.etVerifyCode.validate()) {
                    try {
                        login(binding.etPhoneNumber.getEditValue(), binding.etVerifyCode.getEditValue());
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        } else if (id == R.id.tv_forget_password) {
            XToastUtils.info("忘记密码");
        }else if (id == R.id.tv_user_protocol) {
            Utils.gotoProtocol(this, false, true);
        } else if (id == R.id.tv_privacy_protocol) {
            Utils.gotoProtocol(this, true, true);
        } else if (id == R.id.btn_get_verify_code) {
            mCountDownHelper.start();
            // 执行登录请求
            String email = null;
            if (binding.etPhoneNumber.validate()) {
                email = binding.etPhoneNumber.getEditValue();
            }
            String urlString = null;
            try {
                urlString = baseUrl + "user/email?email=" + URLEncoder.encode(email, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            new EmailCodeTask().execute(urlString);
        }

    }

    /**
     * 根据验证码登录
     *
     * @param email 邮箱
     * @param password  密码
     */
    private void login(String email, String password) throws UnsupportedEncodingException {
        // 执行登录请求
        if (emailCode == null) {
            XToastUtils.warning("请等待验证码到达!!!");
        } else if (emailCode.equals(binding.etVerifyCode.getEditValue())) {
            onLoginSuccess();
        } else {
            XToastUtils.warning("验证码错误!!!");
        }
    }

    /**
     * 登录成功的处理
     */
    private void onLoginSuccess() {
        String token = RandomUtils.getRandomNumbersAndLetters(16);
        if (TokenUtils.handleLoginSuccess(token)) {
            popToBack();
            ActivityUtils.startActivity(MainActivity.class);
        }
    }

    @Override
    public void onDestroyView() {
        if (mCountDownHelper != null) {
            mCountDownHelper.recycle();
        }
        super.onDestroyView();
    }

    private class EmailCodeTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return NetworkUtils.performGetRequest(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            Gson gson = new Gson();
            Response response = gson.fromJson(result, Response.class);
            if (response.getCode() == 200) {
                Log.d(TAG, "验证码" + response.getMessage());
                emailCode = response.getMessage();
            } else {
                // 登录失败
                XToastUtils.warning("请求失败，请稍后重试!!!" + response.getMessage());
            }
        }
    }

}

