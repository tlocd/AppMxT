/*
 * Copyright (C) 2021 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.xuexiang.template.fragment.other;

import static android.content.ContentValues.TAG;
import static com.xuexiang.template.config.Config.baseUrl;

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
import com.xuexiang.template.databinding.FragmentRegisterBinding;
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


/**
 * 登录页面
 */
@Page(anim = CoreAnim.none)
public class RegisterFragment extends BaseFragment<FragmentRegisterBinding> implements View.OnClickListener {
    private CountDownButtonHelper mCountDownHelper;
    private String emailCode = null;
    @NonNull
    @Override
    protected FragmentRegisterBinding viewBindingInflate(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, boolean attachToRoot) {
        return FragmentRegisterBinding.inflate(inflater, container, attachToRoot);
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle()
                .setImmersive(true);
        titleBar.setBackgroundColor(Color.TRANSPARENT);
        titleBar.setTitle("");
        titleBar.setLeftImageDrawable(ResUtils.getVectorDrawable(getContext(), R.drawable.ic_web_back));
        titleBar.setActionTextColor(ThemeUtils.resolveColor(getContext(), R.attr.colorAccent));
        return titleBar;
    }

    @Override
    protected void initViews() {
        mCountDownHelper = new CountDownButtonHelper(binding.btnGetVerifyCode, 360);
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
        binding.btnGetVerifyCode.setOnClickListener(this);
        binding.btnLogin.setOnClickListener(this);
        binding.tvUserProtocol.setOnClickListener(this);
        binding.tvPrivacyProtocol.setOnClickListener(this);
    }

    private void refreshButton(boolean isChecked) {
        ViewUtils.setEnabled(binding.btnLogin, isChecked);
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
        if (id == R.id.btn_get_verify_code) {
            if (binding.etPhoneNumber.validate()) {
                try {
                    getVerifyCode(binding.etPhoneNumber.getEditValue());
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        } else if (id == R.id.btn_login) {
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
     * 获取验证码
     */
    private void getVerifyCode(String email) throws UnsupportedEncodingException {
        mCountDownHelper.start();
        // 执行登录请求
        String urlString = baseUrl + "user/email?email=" + URLEncoder.encode(email, "UTF-8");
        new RegisterFragment.EmailCodeTask().execute(urlString);
    }

    /**
     * 注册
     */
    private void register(String username, String email, String pwd) throws UnsupportedEncodingException {
        mCountDownHelper.start();
        // 执行登录请求
        String urlString = baseUrl + "user/register";
        String data = "{\nusername:" + URLEncoder.encode(username, "UTF-8") + ",\nemail:" + URLEncoder.encode(email, "UTF-8") +
                ",\npassword:" + URLEncoder.encode(pwd, "UTF-8") + "\n}";
        new RegisterTask().execute(urlString, data);
    }

    /**
     * 根据验证码登录
     * @param phoneNumber 手机号
     * @param verifyCode  验证码
     */
    private void loginByVerifyCode(String phoneNumber, String verifyCode) throws UnsupportedEncodingException {

        if (emailCode == null){
            XToastUtils.warning("请等待验证码到达!!!");
        } else if (emailCode.equals(verifyCode)){
            if (binding.etPassword.getEditValue().equals(binding.etPasswordTwo.getEditValue())){
                // TODO 登录成功， 向数据库发送注册数据完成注册
                register(binding.tvUserName.getEditValue(), binding.etPhoneNumber.getEditValue(), binding.etPassword.getEditValue());
                onLoginSuccess();
                return;
            }
            XToastUtils.warning("两次密码不一致，请重新输入!!!");
            binding.etPasswordTwo.clear();
            binding.etPassword.clear();
        }else {
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


    private class RegisterTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            Log.d(TAG, "Login successful. Response: " + params.length);
            return NetworkUtils.performPostRequest(params[0], params[1]);
        }
        @Override
        protected void onPostExecute(String result) {

        }
    }

}

