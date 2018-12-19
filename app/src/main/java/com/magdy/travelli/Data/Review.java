package com.magdy.travelli.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Suleiman on 02/03/17.
 */

public class Review {

    private String name;
    private String description;
    private String firstLetter;
    private String rate;

    public Review(String name, String description, String rate) {
        this.name = name;
        this.firstLetter = String.valueOf(name.charAt(0));
        this.description = description;
        this.rate = rate;
    }
    public String getRate() {
        return rate;
    }
    public void setrate(String name) {
        this.rate = rate;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFirstLetter() {
        return firstLetter;
    }

    public void setFirstLetter(String firstLetter) {
        this.firstLetter = firstLetter;
    }

    public static List<Review> prepareReviews(String[] names, String[] descriptions,String rates[]) {
        List<Review> reviews = new ArrayList<>(names.length);

        for (int i = 0; i < names.length; i++) {
            Review dessert = new Review(names[i], descriptions[i],rates[i]);
            reviews.add(dessert);
        }

        return reviews;
    }
}