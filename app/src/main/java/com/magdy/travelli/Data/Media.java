package com.magdy.travelli.Data;

import java.io.Serializable;
import java.util.List;

public class Media implements Serializable {
    private int type;
    private String id, name, link, key;
    private List<Hotspot> hotspots;


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
        return hotspots;
    }

    public void setHotspots(List<Hotspot> hotspots) {
        this.hotspots = hotspots;
    }
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
