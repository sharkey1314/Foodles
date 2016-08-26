package com.example.sharkey.foodles.Finder;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.facebook.AccessToken.getCurrentAccessToken;

/**
 * Created by sharkey on 13/7/16.
 */
public class DownloadNearbyFoodTask extends AsyncTask<Void, Void ,Void> {

    // params sent to the task upon execution
    // progress type of progress units published during the background computation
    // results type of the result done by the computation

    // I want to fetch the relevant data that are nearby my current location, recommended by my friends
    // WTF

    // gets a list of facebook friends id
    // loop through the friend's id for their photos upload
    // check each photo if is within the location I want

    /** Checks if the place is within certain radius from the origin coordinates
     *  latitude is y axis, longitude is x axis
     *  x and y are the place to compare with
     * */

    private boolean withinCircle(int latitude, int longitude, int radius, int x, int y) {
        return ((x-longitude)*(x-longitude) - (y-latitude)*(y-latitude)) <= (radius * radius);
    }

    @Override
    protected Void doInBackground(Void... params) {

        String id = Profile.getCurrentProfile().getId();
        String path = String.format("/%s/friends", id);
        Log.d("path", path);
        for (Void v :  params) {
            /* make the API call */
            graphRequest();

        }
        return null;
    }
    private void graphRequest() {
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            Log.d("successful",
                                    getFriendId(response.getJSONObject().getJSONArray("data")).toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
        ).executeAsync();
    }
    /** Parsing the JSONArray to get the friends' ids
     */
    private String getFriendId(JSONArray jsonArray) {
        JSONObject jsonObject = new JSONObject();
        /*String[] ids = new String[jsonArray.length()];*/
        String ids = "";
        Log.d("length", jsonArray.length()+"");
        for (int i = 0; i<jsonArray.length(); i++) {
            try {
                jsonObject = jsonArray.getJSONObject(i);
                ids = ids + jsonObject.getString("id");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return ids;
    }

    /** access database to fetch relevant data to display the right stuff
     *
     */
    private void fillRelevantData() {

    }
}
