package com.magdy.travelli.Data;

import java.io.Serializable;

/**
 * Created by engma on 10/4/2017.
 */

public class Tour implements Serializable {
    private String id , name , detials, imageLink , from , to,agency ;
    private int price , reviewers;
    private float rate ;
    private boolean fav ;
    public Tour(String id , String name , String detials , int price ,String imageLink,String from,String to,String agency , float rate , int reviewers,boolean fav)
    {
        this.id=id;
        this.fav=fav;
        this.reviewers = reviewers ;
        this.rate = rate;
        this.name =name;
        this.detials=detials;
        this.price=price;
        this.imageLink=imageLink;
        this.agency=agency;
        this.from =from;
        this.to=to;
    }

    public boolean isFav() {
        return fav;
    }

    public void setFav(boolean fav) {
        this.fav = fav;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public int getReviewers() {
        return reviewers;
    }

    public void setReviewers(int reviewers) {
        this.reviewers = reviewers;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }

    public String getAgency() {
        return agency;
    }

    public int getPrice() {
        return price;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDetials(String detials) {
        this.detials = detials;
    }



    public String getName() {
        return name;
    }

    public String getDetials() {
        return detials;
    }

    public String getId() {
        return id;
    }


}
