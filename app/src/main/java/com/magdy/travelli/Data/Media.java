package com.magdy.travelli.Data;

import android.os.Parcel;
import android.os.Parcelable;

public class Media implements Parcelable {
    private int type;
    private String id, name, link, key;

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public String getLink() {
        return link;
    }

    public String getName() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private Media(Parcel in) {
        this.id = in.readString();
        this.link = in.readString();
        this.name = in.readString();
        this.type = in.readInt();
        this.key = in.readString();
    }

    public Media() {

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.link);
        dest.writeString(this.name);
        dest.writeInt(this.type);
        dest.writeString(this.key);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Media createFromParcel(Parcel in) {
            return new Media(in);
        }

        public Media[] newArray(int size) {
            return new Media[size];
        }
    };

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
