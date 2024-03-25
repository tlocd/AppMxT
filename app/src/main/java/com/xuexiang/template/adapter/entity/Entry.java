package com.xuexiang.template.adapter.entity;

public class Entry {
    private float valueX = 0;
    private float valueY = 0;

    public void setValueX(float x) {
        this.valueX = x;
    }

    public void setValueY(float y) {
        this.valueY = y;
    }

    public float getValueX() {
        return this.valueX;
    }

    public float getValueY() {
        return this.valueY;
    }

    public Entry(float x, float y){
        this.valueX = x;
        this.valueY = y;

    }
}
