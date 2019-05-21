package com.magdy.travelli.Data;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Hotspot implements Serializable {
    private String id, text, key;
    private double yaw, pitch;
    private int type;

    public Hotspot() {
    }

    public Hotspot(String id, String text, double yaw, double pitch, int type) {
        this.id = id;
        this.text = text;
        this.yaw = yaw;
        this.pitch = pitch;
        this.type = type;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPitch(double pitch) {
        this.pitch = pitch;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setYaw(double yaw) {
        this.yaw = yaw;
    }

    public String getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public double getPitch() {
        return pitch;
    }

    public double getYaw() {
        return yaw;
    }

    public String getText() {
        return text;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
