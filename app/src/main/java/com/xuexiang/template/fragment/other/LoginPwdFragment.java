package com.xuexiang.template.fragment.other;

import com.google.gson.Gson;
import static com.xuexiang.template.config.Config.baseUrl;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.xuexiang.template.R;
import com.xuexiang.template.activity.MainActivity;
import com.xuexiang.template.core.BaseFragment;
import com.xuexiang.template.databinding.FragmentLoginPwdBinding;
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
import com.xuexiang.xui.utils.ResUtils;
import com.xuexiang.xui.utils.ThemeUtils;
import com.xuexiang.xui.utils.ViewUtils;
import com.xuexiang.xui.utils.XToastUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xutil.app.ActivityUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


/**
 * 登录页面
 */
@Page(anim = CoreAnim.none)
public class LoginPwdFragment extends BaseFragment<FragmentLoginPwdBinding> implements View.OnClickListener {
    private View mJumpView;

    @NonNull
    @Override
    protected FragmentLoginPwdBinding viewBindingInflate(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, boolean attachToRoot) {
        return FragmentLoginPwdBinding.inflate(inflater, container, attachToRoot);
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle()
                .setImmersive(true);
        titleBar.setBackgroundColor(Color.TRANSPARENT);
        titleBar.setTitle("");
        titleBar.setLeftImageDrawable(ResUtils.getVectorDrawable(getContext(), R.drawable.ic_web_back));
        titleBar.setActionTextColor(ThemeUtils.resolveColor(getContext(), R.attr.colorAccent));
        mJumpView = titleBar.addAction(new TitleBar.TextAction(R.string.title_jump_login) {
            @Override
            public void performAction(View view) {
                openPage(RegisterFragment.class);
//                onLoginSuccess();
            }
        });
        return titleBar;
    }

    @Override
    protected void initViews() {
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
    }

    @Override
    protected void initListeners() {
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
                        loginByVerifyCode(binding.etPhoneNumber.getEditValue(), binding.etVerifyCode.getEditValue());
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        } else if (id == R.id.tv_forget_password) {
            XToastUtils.info("忘记密码");
        } else if (id == R.id.tv_user_protocol) {
            Utils.gotoProtocol(this, false, true);
        } else if (id == R.id.tv_privacy_protocol) {
            Utils.gotoProtocol(this, true, true);
        }

    }
    /**
     * 根据验证码登录
     *
     * @param email 邮箱
     * @param password  密码
     */
    private void loginByVerifyCode(String email, String password) throws UnsupportedEncodingException {
        // 执行登录请求
        String urlString = baseUrl + "user/login?email=" + URLEncoder.encode(email, "UTF-8") + "&pwd=" + URLEncoder.encode(password, "UTF-8");
        new LoginTask().execute(urlString);
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
        super.onDestroyView();
    }

    private class LoginTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return NetworkUtils.performGetRequest(params[0]);
        }
        @Override
        protected void onPostExecute(String result) {
            Gson gson = new Gson();
            Response response = gson.fromJson(result, Response.class);
            if (response.getCode() == 200) {
                // 登录成功
                onLoginSuccess();
            } else {
                // 登录失败
                XToastUtils.warning("登录失败!!!" + response.getMessage());
            }
        }
    }
}

