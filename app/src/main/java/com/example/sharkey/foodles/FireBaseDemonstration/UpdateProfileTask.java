package com.example.sharkey.foodles.FireBaseDemonstration;

import android.os.AsyncTask;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;

/**
 * Created by sharkey on 13/7/16.
 * Updates the relevant details from Facebook
 * Getting the friendlist etc
 */
public class UpdateProfileTask extends AsyncTask<Void,Void, Void> {
    @Override
    protected Void doInBackground(Void... params) {
        String id = Profile.getCurrentProfile().getId();
        String path = String.format("/%s/friends", id);
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                path,
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        // upload to firebase database
                    }
                }
        ).executeAsync();
        return null;
    }
}
