package com.example.sharkey.foodles.UI.API.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sharkey on 19/7/16.
 */
public class Recommendation {
    public String address;
    public String caption;
    public String displayPicUrl;
    public String locationId;
    public String photoUrl;
    public String price;
    public Double ratings;
    public String userName;
    public Recommendation(String address, String caption, String displayPicUrl, String locationId, String price, Double ratings, String userName) {
        this.address = address;
        this.caption = caption;
        this.displayPicUrl = displayPicUrl;
        this.locationId = locationId;
        this.price = price;
        this.ratings = ratings;
        this.userName = userName;
    }


    public Recommendation() {
        // Default constructor required for calls to DataSnapshot.getValue(Recommendation.class)
    }


    public Recommendation(String address, String displayPicUrl, String locationId, String userName, String caption) {
        this.address = address;
        this.caption = caption;
        this.displayPicUrl = displayPicUrl;
        this.locationId = locationId;
        this.userName = userName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getAddress() {
        return address;
    }

    public String getDisplayPicUrl() {
        return displayPicUrl;
    }


    public String getLocationId() {
        return locationId;
    }

}
