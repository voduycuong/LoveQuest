package com.example.lovequest.model;

import com.google.firebase.Timestamp;

public class UserModel {
    private String phone;
    private String username;
    private String email;
    private Timestamp createdTimestamp;
    private String userId;
    private String fcmToken;

    // New fields
    private String name;
    private String dateOfBirth; // Assuming date of birth is a string in format "YYYY-MM-DD"
    private String gender;
    private String country;
    private String nationality;
    private String city;
    private String description;
    private String photoUrl; // URL to the user's photo

    public UserModel() {
    }

    // Constructor with new fields
    public UserModel(String phone, String username, String email, Timestamp createdTimestamp, String userId, String name, String dateOfBirth, String gender, String country, String nationality, String city, String description, String photoUrl) {
        this.phone = phone;
        this.username = username;
        this.email = email;
        this.createdTimestamp = createdTimestamp;
        this.userId = userId;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.country = country;
        this.nationality = nationality;
        this.city = city;
        this.description = description;
        this.photoUrl = photoUrl;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}