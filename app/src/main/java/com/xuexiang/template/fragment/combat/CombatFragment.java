/*
 * Copyright (C) 2019 xuexiangjys(xuexiangjys@163.com)
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

package com.xuexiang.template.fragment.combat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xuexiang.template.core.BaseFragment;
import com.xuexiang.template.databinding.FragmentCombatBinding;
import com.xuexiang.template.databinding.FragmentComprehensiveTestBinding;
import com.xuexiang.template.fragment.learning.GridItemDetialFragment;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;

/**
 * 实战中心
 */
@Page(anim = CoreAnim.none)
public class CombatFragment extends BaseFragment<FragmentCombatBinding> {
    @NonNull
    @Override
    protected FragmentCombatBinding viewBindingInflate(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, boolean attachToRoot) {
        return FragmentCombatBinding.inflate(inflater, container, attachToRoot);
    }

    /**
     * @return 返回为 null意为不需要导航栏
     */
    @Override
    protected TitleBar initTitle() {
        return null;
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {
        binding.button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openNewPage(ComprehensiveTest.class, ComprehensiveTest.KEY_TITLE_NAME, binding.button1.getText());
            }
        });

        binding.button2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openNewPage(GridCombatPartItemFragment.class, GridCombatPartItemFragment.KEY_TITLE_NAME, binding.button2.getText());
            }
        });
    }

//    @Override
//    protected void initListeners() {
//        //下拉刷新
//        binding.refreshLayout.setOnRefreshListener(refreshLayout -> {
//            // TODO: 2020-02-25 这里只是模拟了网络请求
//            refreshLayout.getLayout().postDelayed(() -> {
//                mNewsAdapter.refresh(DemoDataProvider.getDemoNewInfos());
//                refreshLayout.finishRefresh();
//            }, 1000);
//        });
//        //上拉加载
//        binding.refreshLayout.setOnLoadMoreListener(refreshLayout -> {
//            // TODO: 2020-02-25 这里只是模拟了网络请求
//            refreshLayout.getLayout().postDelayed(() -> {
//                mNewsAdapter.loadMore(DemoDataProvider.getDemoNewInfos());
//                refreshLayout.finishLoadMore();
//            }, 1000);
//        });
//        binding.refreshLayout.autoRefresh();//第一次进入触发自动刷新，演示效果
//    }
}
