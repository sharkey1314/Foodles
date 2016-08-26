package com.example.sharkey.foodles.UI.API.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by sharkey on 19/7/16.
 */
public class Location {

    public String address;
    public Coordinates coordinates;
    public String locId;
    public String name;
    // id to recommendation details
    public HashMap<String, Restaurant> restaurantMap;

    public void setLocId(String locId) {
        this.locId = locId;
    }

    public void setRestaurantMap(HashMap<String, Restaurant> restaurantMap) {
        this.restaurantMap = restaurantMap;

    }

    public Location() {
    }

    public Location(String name, String address, String locId, Coordinates coordinates, HashMap<String, Restaurant> restaurantMap) {
        this.name = name;
        this.address = address;
        this.coordinates = coordinates;
        this.restaurantMap = restaurantMap;
        this.locId = locId;
    }

    public String getLocId() {
        return locId;
    }

    public String getName() {
        return name;
    }

    public HashMap<String, Restaurant> getRestaurantMap() {
        return restaurantMap;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }


    public String getAddress() {
        return address;
    }


}
