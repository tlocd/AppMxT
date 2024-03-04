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
    private String nametwo;
    private String maixiangfeaturetwo;
    private String maindiseasetwo;


    public MaiXiang(String name, String maixiangFeature, String mainDisease, String nameTwo,
                    String maixiangFeatureTwo, String mainDiseaseTwo) {
        this.name = name;
        this.nametwo = nameTwo;
        this.maixiangfeature = maixiangFeature;
        this.maindisease = mainDisease;
        this.maindiseasetwo = mainDiseaseTwo;
        this.maixiangfeaturetwo = maixiangFeatureTwo;
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

    public String getMaindiseasetwo() {
        return maindiseasetwo;
    }

    public String getMaixiangfeaturetwo() {
        return maixiangfeaturetwo;
    }

    public String getNametwo() {
        return nametwo;
    }

    public static List<MaiXiang> parseJson(InputStream inputStream) {
        List<MaiXiang> maiXiangList = new ArrayList<>();
        String jsonString = new Scanner(inputStream).useDelimiter("\\A").next();
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("name");
                String nameTwo = jsonObject.getString("name_two");
                String maixiangFeature = jsonObject.getString("maixiang_feature");
                String mainDisease = jsonObject.getString("main_disease");
                String mainDiseaseTwo = jsonObject.getString("main_disease_two");
                String maixiangFeatureTwo = jsonObject.getString("main_disease_two");
                MaiXiang maiXiang = new MaiXiang(name, maixiangFeature, mainDisease, nameTwo,
                        maixiangFeatureTwo, mainDiseaseTwo);
                maiXiangList.add(maiXiang);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return maiXiangList;
    }
}
