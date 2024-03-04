package com.xuexiang.template.fragment.learning;

import static com.xuexiang.xutil.app.AppUtils.getPackageName;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.reflect.TypeToken;
import com.xuexiang.template.R;
import com.xuexiang.template.adapter.entity.MaiXiang;
import com.xuexiang.template.core.BaseFragment;
import com.xuexiang.template.databinding.FragmentGridItemDetailBinding;
import com.xuexiang.template.utils.JsonUtils;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.utils.XToastUtils;

import java.lang.reflect.Type;
import java.util.List;


@Page
public class GridItemDetialFragment extends BaseFragment<FragmentGridItemDetailBinding> {

    public static final String KEY_TITLE_NAME = "title_name";
    /**
     * 自动注入参数，不能是private
     */
    @AutoWired(name = KEY_TITLE_NAME)
    String title;

    @NonNull
    @Override
    protected FragmentGridItemDetailBinding viewBindingInflate(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, boolean attachToRoot) {
        return FragmentGridItemDetailBinding.inflate(inflater, container, attachToRoot);
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
        Type listType = new TypeToken<List<MaiXiang>>() {}.getType();
        List<MaiXiang> maiXiangList = JsonUtils.loadListFromRaw(getContext(), R.raw.maixiang_description, listType);
        if (maiXiangList == null || maiXiangList.isEmpty()) {
            XToastUtils.toast("系统错误：文件缺失");
        } else {
            for (MaiXiang maiXiang: maiXiangList){
                if (maiXiang.getName().equals(title)){
                    if (title.equals("洪脉")){
                        binding.maixiangFeature.setText(maiXiang.getMaixiangFeature());
                        binding.mainDisease.setText(maiXiang.getMainDisease());
                        binding.mainDiseaseTitleTwo.setVisibility(View.VISIBLE);
                        binding.maixiangFeatureTitleTwo.setVisibility(View.VISIBLE);
                        binding.titleTwo.setVisibility(View.VISIBLE);
                        binding.titleTwo.setText(maiXiang.getNametwo());
                        binding.maixiangFeatureTwo.setVisibility(View.VISIBLE);
                        binding.maixiangFeatureTwo.setText(maiXiang.getMaixiangfeaturetwo());
                        binding.mainDiseaseTwo.setVisibility(View.VISIBLE);
                        binding.mainDiseaseTwo.setText(maiXiang.getMaindiseasetwo());
                    } else if (title.equals("平脉")) {
                        binding.maixiangFeature.setText(maiXiang.getMaixiangFeature());
                        binding.mainDiseaseTitle.setVisibility(View.GONE);
                    } else {
                        binding.maixiangFeature.setText(maiXiang.getMaixiangFeature());
                        binding.mainDisease.setText(maiXiang.getMainDisease());
                    }
                    break;
                }
            }
        }

        // 获取 VideoView 组件的引用
        VideoView videoView = findViewById(R.id.videoView);

        // 设置视频路径
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.duanmai; // 替换 your_video 为你的视频文件名
        Uri uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);

        // 设置循环播放
        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            videoView.start();
        });

    }

}
