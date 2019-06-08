package com.magdy.travelli.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Media implements Serializable {
    private int type;
    private String id, name, link, key, thumbnail;
    private Map<String, Hotspot> hotspots;
    private List<String> parts;

    public Media() {
    }

    public void setHotspots(Map<String, Hotspot> hotspots) {
        this.hotspots = hotspots;
    }

    public List<String> getParts() {
        return parts;
    }

    public void setParts(List<String> parts) {
        this.parts = parts;
    }

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

    public List<Hotspot> getHotspots() {
        List<Hotspot> hotspotList = new ArrayList<>();
        if (hotspots != null)
            for (Map.Entry<String, Hotspot> entry : hotspots.entrySet()) {
                hotspotList.add(entry.getValue());
            }
        return hotspotList;

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
