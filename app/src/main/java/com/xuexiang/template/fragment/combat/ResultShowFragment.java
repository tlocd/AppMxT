package com.xuexiang.template.fragment.combat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.xuexiang.template.core.BaseFragment;
import com.xuexiang.template.databinding.FragmentResultShowBinding;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.launcher.XRouter;




@Page
public class ResultShowFragment extends BaseFragment<FragmentResultShowBinding> {

    public static final String KEY_TITLE_NAME = "title_name";
    /**
     * 自动注入参数，不能是private
     */
    @AutoWired(name = KEY_TITLE_NAME)
    String title;

    @NonNull
    @Override
    protected FragmentResultShowBinding viewBindingInflate(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, boolean attachToRoot) {
        return FragmentResultShowBinding.inflate(inflater, container, attachToRoot);
    }

    @Override
    protected void initArgs() {
        // 自动注入参数必须在initArgs里进行注入
        XRouter.getInstance().inject(this);
    }

    @Override
    protected String getPageTitle() {
        return title;
    }


    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {
        binding.last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        binding.end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                openNewPage(ResultShowFragment.class, ResultShowFragment.KEY_TITLE_NAME, KEY_TITLE_NAME);
            }
        });

        binding.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }


}
