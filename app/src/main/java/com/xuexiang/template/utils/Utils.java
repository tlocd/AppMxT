
package com.xuexiang.template.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.xuexiang.template.R;
import com.xuexiang.template.core.webview.AgentWebActivity;
import com.xuexiang.template.fragment.other.ServiceProtocolFragment;
import com.xuexiang.xpage.base.XPageFragment;
import com.xuexiang.xpage.core.PageOption;
import com.xuexiang.xui.utils.ColorUtils;
import com.xuexiang.xui.utils.ResUtils;
import com.xuexiang.xui.widget.dialog.DialogLoader;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xutil.XUtil;

import static com.xuexiang.template.core.webview.AgentWebFragment.KEY_URL;
import static com.xuexiang.template.fragment.other.ServiceProtocolFragment.KEY_IS_IMMERSIVE;
import static com.xuexiang.template.fragment.other.ServiceProtocolFragment.KEY_PROTOCOL_TITLE;

/**
 * 工具类
 *
 */
public final class Utils {

    private Utils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 这里填写你的应用隐私政策网页地址
     */
    private static final String PRIVACY_URL = "https://gitee.com/xuexiangjys/TemplateAppProject/raw/master/LICENSE";

    /**
     * 显示隐私政策的提示
     *
     * @param context
     * @param submitListener 同意的监听
     * @return
     */
    public static Dialog showPrivacyDialog(Context context, MaterialDialog.SingleButtonCallback submitListener) {
        MaterialDialog dialog = new MaterialDialog.Builder(context).title(R.string.title_reminder).autoDismiss(false).cancelable(false)
                .positiveText(R.string.lab_agree).onPositive((dialog1, which) -> {
                    if (submitListener != null) {
                        submitListener.onClick(dialog1, which);
                    } else {
                        dialog1.dismiss();
                    }
                })
                .negativeText(R.string.lab_disagree).onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        DialogLoader.getInstance().showConfirmDialog(context, ResUtils.getString(R.string.title_reminder), String.format(ResUtils.getString(R.string.content_privacy_explain_again), ResUtils.getString(R.string.app_name)), ResUtils.getString(R.string.lab_look_again), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                showPrivacyDialog(context, submitListener);
                            }
                        }, ResUtils.getString(R.string.lab_still_disagree), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                DialogLoader.getInstance().showConfirmDialog(context, ResUtils.getString(R.string.content_think_about_it_again), ResUtils.getString(R.string.lab_look_again), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        showPrivacyDialog(context, submitListener);
                                    }
                                }, ResUtils.getString(R.string.lab_exit_app), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        XUtil.exitApp();
                                    }
                                });
                            }
                        });
                    }
                }).build();
        dialog.setContent(getPrivacyContent(context));
        //开始响应点击事件
        dialog.getContentView().setMovementMethod(LinkMovementMethod.getInstance());
        dialog.show();
        return dialog;
    }

    /**
     * @return 隐私政策说明
     */
    private static SpannableStringBuilder getPrivacyContent(Context context) {
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder()
                .append("    欢迎来到").append(ResUtils.getString(R.string.app_name)).append("!\n")
                .append("    我们深知个人信息对你的重要性，也感谢你对我们的信任。\n")
                .append("    为了更好地保护你的权益，同时遵守相关监管的要求，我们将通过");
        stringBuilder.append(getPrivacyLink(context, PRIVACY_URL))
                .append("向你说明我们会如何收集、存储、保护、使用及对外提供你的信息，并说明你享有的权利。\n")
                .append("    更多详情，敬请查阅")
                .append(getPrivacyLink(context, PRIVACY_URL))
                .append("全文。");
        return stringBuilder;
    }

    /**
     * @param context 隐私政策的链接
     * @return
     */
    private static SpannableString getPrivacyLink(Context context, String privacyUrl) {
        String privacyName = String.format(ResUtils.getString(R.string.lab_privacy_name), ResUtils.getString(R.string.app_name));
        SpannableString spannableString = new SpannableString(privacyName);
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                goWeb(context, privacyUrl);
            }
        }, 0, privacyName.length(), Spanned.SPAN_MARK_MARK);
        return spannableString;
    }


    /**
     * 请求浏览器
     *
     * @param url
     */
    public static void goWeb(Context context, final String url) {
        Intent intent = new Intent(context, AgentWebActivity.class);
        intent.putExtra(KEY_URL, url);
        context.startActivity(intent);
    }


    /**
     * 打开用户协议和隐私协议
     *
     * @param fragment
     * @param isPrivacy   是否是隐私协议
     * @param isImmersive 是否沉浸式
     */
    public static void gotoProtocol(XPageFragment fragment, boolean isPrivacy, boolean isImmersive) {
        PageOption.to(ServiceProtocolFragment.class)
                .putString(KEY_PROTOCOL_TITLE, isPrivacy ? ResUtils.getString(R.string.title_privacy_protocol) : ResUtils.getString(R.string.title_user_protocol))
                .putBoolean(KEY_IS_IMMERSIVE, isImmersive)
                .open(fragment);

    }

    /**
     * 是否是深色的颜色
     *
     * @param color
     * @return
     */
    public static boolean isColorDark(@ColorInt int color) {
        return ColorUtils.isColorDark(color, 0.382);
    }
}
