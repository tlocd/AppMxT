package com.xuexiang.template.adapter.entity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MaiXiang {
    private String name;
    private String maixiangfeature;
    private String maindisease;


    public MaiXiang(String name, String maixiangFeature, String mainDisease) {
        this.name = name;
        this.maixiangfeature = maixiangFeature;
        this.maindisease = mainDisease;
    }

    public String getName() {
        return name;
    }

    public String getMaixiangFeature() {
        return maixiangfeature;
    }

    public String getMainDisease() {
        return maindisease;
    }


    public static List<MaiXiang> parseJson(InputStream inputStream) {
        List<MaiXiang> maiXiangList = new ArrayList<>();
        String jsonString = new Scanner(inputStream).useDelimiter("\\A").next();
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("name");
                String maixiangFeature = jsonObject.getString("maixiang_feature");
                String mainDisease = jsonObject.getString("main_disease");
                MaiXiang maiXiang = new MaiXiang(name, maixiangFeature, mainDisease);
                maiXiangList.add(maiXiang);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return maiXiangList;
    }
}
