package com.magdy.travelli.Data;

public class User {
    private String fullName, email, token,profilePhoto;

    public User() {
    }

    public User(String fullName, String email, String token) {
        this.fullName = fullName;
        this.email = email;
        this.token = token;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
