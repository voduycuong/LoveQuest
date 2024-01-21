package com.example.lovequest.model;

import com.google.firebase.Timestamp;

public class UserModel {
    private String phone;
    private String username;
    private String email;
    private Timestamp createdTimestamp;
    private String userId;
    private String fcmToken;
    private String hobbies;
    private String name;
    private String dateOfBirth;
    private String age;
    private String gender;
    private String country;
    private String nationality;
    private String city;
    private String description;
<<<<<<< HEAD
    private String photoUrl; // URL to the user's photo
    private String eventDate;
=======
    private String photoUrl;
>>>>>>> add_profile_picture

    public UserModel() {
    }

    // Constructor with new fields
    public UserModel(String phone, String username, String email, Timestamp createdTimestamp, String userId, String name, String dateOfBirth, String gender, String country, String nationality, String city, String description, String photoUrl, String eventDate,String hobbies) {
        this.phone = phone;
        this.username = username;
        this.email = email;
        this.createdTimestamp = createdTimestamp;
        this.userId = userId;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.age = age;
        this.gender = gender;
        this.country = country;
        this.nationality = nationality;
        this.city = city;
        this.description = description;
        this.photoUrl = photoUrl;
        this.eventDate = eventDate;
        this.hobbies = hobbies;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public String getHobbies() {return hobbies;}

    public void setHobbies(String hobbies) {this.hobbies = hobbies;}

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

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
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

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

}