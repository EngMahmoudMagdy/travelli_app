package com.magdy.travelli.Data;

import android.os.Parcel;
import android.os.Parcelable;

public class Hotspot implements Parcelable {
    private String id, text ;
    private double yaw , pitch;
    private int type;

    public Hotspot(String id,String text ,double yaw,double pitch,int type  )
    {
        this.id = id;
        this.text = text;
        this.yaw = yaw;
        this.pitch = pitch;
        this.type = type ;
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
    private Hotspot(Parcel in)
    {
        id  = in.readString();
        text = in.readString();
        yaw = in.readDouble();
        pitch = in.readDouble();
        type = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(text);
        dest.writeDouble(yaw);
        dest.writeDouble(pitch);
        dest.writeInt(type);
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Hotspot createFromParcel(Parcel in) {
            return new Hotspot(in);
        }
        public Hotspot[] newArray(int size) {
            return new Hotspot[size];
        }
    };
}
