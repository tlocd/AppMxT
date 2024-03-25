package com.xuexiang.template.fragment.learning;

import static com.xuexiang.template.config.Config.baseUrl;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xuexiang.template.R;
import com.xuexiang.template.adapter.entity.MaiXiang;
import com.xuexiang.template.adapter.entity.MaiXiangData;
import com.xuexiang.template.core.BaseFragment;
import com.xuexiang.template.databinding.FragmentGridItemDetailBinding;
import com.xuexiang.template.utils.JsonUtils;
import com.xuexiang.template.utils.NetworkUtils;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.utils.XToastUtils;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;





@Page
public class GridItemDetialFragment extends BaseFragment<FragmentGridItemDetailBinding> {
    private static Handler mainHandler = new Handler(Looper.getMainLooper());
    public static final String KEY_TITLE_NAME = "title_name";
    private static LineChart chart = null;
    private static Thread thread;
    private static final int prolong = 9;
    /**
     * 自动注入参数，不能是private
     */

    @AutoWired(name = KEY_TITLE_NAME)
    static String title;
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
        binding.button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    receive(title);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        chart = (LineChart) findViewById(R.id.chart);
        List<Entry> coords = new ArrayList<>();
        for (int i = 0; i < 1; i++){
            coords.add(new Entry(i, i));
        }

        LineDataSet dataSet = new LineDataSet(coords, "");
        LineData lineData = new LineData(dataSet);
        lineData.setDrawValues(false);

        chart.setData(lineData);
        Description description = new Description();//描述信息
        description.setEnabled(false);//是否可用
        chart.setDescription(description);//不然会显示默认的 Description。
        chart.setDrawGridBackground(false);// 是否显示表格颜色
        chart.animateY(1000, Easing.Linear);//设置动画
        chart.setTouchEnabled(false); // 设置是否可以触摸
        chart.setDragEnabled(false);// 是否可以拖拽
        chart.setScaleEnabled(false);// 是否可以缩放
        chart.setDoubleTapToZoomEnabled(false);//是否允许双击进行缩放
        //x轴配置
        XAxis xAxis = chart.getXAxis();
        xAxis.setEnabled(false);//是否可用
        xAxis.setDrawLabels(false);//是否显示数值
        xAxis.setDrawAxisLine(true);//是否显示坐标线
        xAxis.setAxisLineColor(Color.BLACK);//设置坐标轴线的颜色
        xAxis.setDrawGridLines(false);//是否显示竖直风格线
        xAxis.setTextColor(Color.BLACK);//X轴文字颜色
        xAxis.setSpaceMin(1f);//左空白区大小
        xAxis.setSpaceMax(1f);//右空白区大小
        // 将X坐标轴放置在底部，默认是在顶部。
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //左y轴配置
        YAxis lyAxis = chart.getAxisLeft();
        lyAxis.setEnabled(true);//是否可用
        lyAxis.setDrawLabels(false);//是否显示数值
        lyAxis.setDrawAxisLine(false);//是否显示坐标线
        lyAxis.setDrawGridLines(false);//是否显示水平网格线
        lyAxis.setDrawZeroLine(false);////是否绘制零线
        lyAxis.setZeroLineColor(Color.BLACK);
        lyAxis.setZeroLineWidth(0.8f);
        lyAxis.enableGridDashedLine(10f, 10f, 0f);//网格虚线
        lyAxis.setGridColor(Color.BLACK);//网格线颜色
        lyAxis.setGridLineWidth(0.8f);//网格线宽度
        lyAxis.setAxisLineColor(Color.BLACK);//坐标线颜色

        //右y轴配置
        YAxis ryAxis = chart.getAxisRight();
        ryAxis.setEnabled(false);//是否可用
        //标签配置
        Legend legend = chart.getLegend();
        legend.setEnabled(false);//是否可用
        chart.setMinimumHeight(500);
        chart.invalidate();
    }

    private void receive(String name) throws UnsupportedEncodingException {
        // 执行登录请求
        String urlString = baseUrl + "mqtt/publish";
        String data = "{\nname:" + URLEncoder.encode(name, "UTF-8") + "\n}";
        new ReceiveData().execute(urlString, data);
    }

    private static class ReceiveData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return NetworkUtils.performPostRequest(params[0], params[1]);
        }
        @Override
        protected void onPostExecute(String result) {
            Log.d("TAG", result);
            Gson gson = new Gson();
            MaiXiangData response = gson.fromJson(result, MaiXiangData.class);
            if (response.getName().equals(title)) {
                // 将以逗号分隔的数字字符串拆分为字符串数组
                String[] numbersArrayX = response.getX().split(",");
                String[] numbersArrayY = response.getY().split(",");

                if (numbersArrayX.length != numbersArrayY.length){
                    XToastUtils.warning("Error！！！");
                    return;
                }
                List<Entry> coords = new ArrayList<>();
                List<Entry> coord = new ArrayList<>();
                for (int i = 0; i < numbersArrayX.length; i++){
                    coords.add(new Entry(Float.parseFloat(numbersArrayX[i]), Float.parseFloat(numbersArrayY[i])));
                }
                LineDataSet lineDataSet = new LineDataSet(coords, "");//一个LineDataSet就是一条线
                lineDataSet.setDrawCircleHole(false);//设置曲线值的圆点是实心还是空心
                lineDataSet.setValueTextSize(0);//设置显示值的字体大小
                lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);//线模式为圆滑曲线（默认折线）
                lineDataSet.setLineWidth(14f); // 设置折线宽度

                LineDataSet lineDataSet1 = new LineDataSet(coord, "");
                lineDataSet1.setColor(Color.RED);
                lineDataSet1.setDrawCircleHole(true);
                lineDataSet1.setCircleRadius(5);
                lineDataSet1.setCircleColor(Color.RED);

                mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            LineData lineData = new LineData(lineDataSet, lineDataSet1);
                            lineData.setDrawValues(false);
                            chart.setData(lineData);
                            chart.invalidate(); // 刷新

                            GridItemDetialFragment.thread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    for (Entry entry: coords){
                                        coord.clear();
                                        coord.add(entry);
                                        lineDataSet1.notifyDataSetChanged();
                                        chart.notifyDataSetChanged();
                                        chart.invalidate();
                                        try {
                                            Thread.sleep(prolong);
                                        } catch (InterruptedException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                }
                            });
                            thread.start();
                        }
                    });
            } else {
                // 没有取得数据
                XToastUtils.warning("Error！！！");
            }
        }
    }
}
