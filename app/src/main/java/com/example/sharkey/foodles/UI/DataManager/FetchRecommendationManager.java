package com.example.sharkey.foodles.UI.DataManager;

import android.content.Context;

/**
 * Created by sharkey on 22/7/16.
 */
public class FetchRecommendationManager extends BaseDataManager {
    // fetching data depending on the location you suggest
    // get locations that fit into the radius
    public Double latitude;
    public Double longitude;
    public Double radius;

    public FetchRecommendationManager(Context context, Double latitude, Double longitude, Double radius) {
        super(context);
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        return null;
    }

    private boolean withinCircle(int x, int y) {
        return ((x-longitude)*(x-longitude) - (y-latitude)*(y-latitude)) <= (radius * radius);
    }


}
