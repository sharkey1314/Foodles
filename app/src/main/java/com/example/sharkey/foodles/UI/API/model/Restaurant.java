package com.example.sharkey.foodles.UI.API.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sharkey on 17/8/16.
 */
public class Restaurant {
    public String avgPrice;
    public Double avgRatings;
    public String caption;
    public HashMap<String, Recommendation> recommendationsMap;
    public String restaurantName;
    public String restaurantPicUri;
    public ArrayList<String> tags;

    public void setRestaurantPicUri(String restaurantPicUri) {
        this.restaurantPicUri = restaurantPicUri;
    }

    public String getRestaurantPicUri() {
        return restaurantPicUri;
    }

    public Restaurant(String avgPrice, Double avgRatings, String caption, String restaurantName, String restaurantPicUri) {
        this.avgPrice = avgPrice;
        this.avgRatings = avgRatings;
        this.caption = caption;
        this.restaurantName = restaurantName;
        this.restaurantPicUri = restaurantPicUri;
        this.recommendationsMap = new HashMap<>();
        this.tags = new ArrayList<>();
    }

    public Restaurant() {

    }

    public String getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(String avgPrice) {
        this.avgPrice = avgPrice;
    }

    public void setAvgRatings(Double avgRatings) {
        this.avgRatings = avgRatings;
    }

    public void setRecommendationsMap(HashMap<String, Recommendation> recommendationsMap) {
        this.recommendationsMap = recommendationsMap;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public Double getAvgRatings() {
        return avgRatings;
    }

    public HashMap<String, Recommendation> getRecommendationsMap() {
        return recommendationsMap;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public ArrayList<String> getTags() {
        return tags;
    }
}
