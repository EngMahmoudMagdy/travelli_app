package com.magdy.travelli.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class Place implements Serializable {
    private String key;
    private HashMap<String,Integer> media;
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public HashMap<String, Integer> getMedia() {
        return media;
    }

    public void setMedia(HashMap<String, Integer> media) {
        this.media = media;
    }

    @com.google.gson.annotations.SerializedName("id")
    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    private String id ;

    @com.google.gson.annotations.SerializedName("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name ;

    @com.google.gson.annotations.SerializedName("desc")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String description ;

    @com.google.gson.annotations.SerializedName("image")
    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    private String imageLink ;

    @com.google.gson.annotations.SerializedName("lat")
    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    private double lat;

    @com.google.gson.annotations.SerializedName("longt")
    public double getLongt() {
        return longt;
    }

    public void setLongt(double longt) {
        this.longt = longt;
    }

    private double longt ;


    @Override
    public boolean equals(Object o) {
        return o instanceof Place && ((Place) o).id .equals( id);
    }

    @Override
    public String toString() {
        return getName();
    }

}
