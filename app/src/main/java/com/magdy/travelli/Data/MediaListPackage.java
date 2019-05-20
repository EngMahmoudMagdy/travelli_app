package com.magdy.travelli.Data;

import java.io.Serializable;
import java.util.List;

public class MediaListPackage implements Serializable {
    List<Media>mediaList;

    public MediaListPackage() {
    }

    public MediaListPackage(List<Media> mediaList) {
        this.mediaList = mediaList;
    }

    public List<Media> getMediaList() {
        return mediaList;
    }

    public void setMediaList(List<Media> mediaList) {
        this.mediaList = mediaList;
    }
}
