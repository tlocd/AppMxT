package com.xuexiang.template.utils;

import android.content.Context;
import com.google.gson.Gson;
import com.xuexiang.template.adapter.entity.MaiXiang;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

public class JsonUtils {

    // 泛型方法：从 raw 目录中读取 JSON 文件，并将其内容转换为对象数组
    public static <T> List<T> loadListFromRaw(Context context, int resourceId, Type typeOfT) {
        try {
            InputStream is = context.getResources().openRawResource(resourceId);
            InputStreamReader reader = new InputStreamReader(is);
            return new Gson().fromJson(reader, typeOfT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
