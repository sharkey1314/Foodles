package com.example.sharkey.foodles.UI.DataManager;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.sharkey.foodles.UI.API.model.Location;
import com.example.sharkey.foodles.UI.API.model.Recommendation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sharkey on 19/7/16.
 * Responsible for uploading recommendation into database
 */
public class UploadRecommendationManager extends BaseDataManager {

    private Recommendation recommendation;
    private Uri fileUri;
    private View rootView;
    private StorageReference photoRef;
    private DatabaseReference mDatabase;
    private String locationId;
    private Uri mDownloadUrl;
    private String restaurantId;
    private String recKey;
    private Map<String, Object> childUpdates;

    public UploadRecommendationManager(Context context, Recommendation recommendation, Uri fileUri, String id, String resID, String recKey) {
        super(context);
        this.recommendation = recommendation;
        this.fileUri = fileUri;
        this.locationId = id;
        this.restaurantId = resID;
        this.recKey = recKey;
        this.childUpdates = new HashMap<>();
    }

    public UploadRecommendationManager(Context context, View rootView, Uri fileUri, String locationId) {
        super(context);
        this.rootView = rootView;
        this.fileUri = fileUri;
        this.locationId = locationId;
        this.childUpdates = new HashMap<>();

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // show the uploading signal
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        // upload the shit
        mDatabase = getDatabaseRef();
        photoRef = getStorageRef().child(this.getUid()).child(this.fileUri.getLastPathSegment());
        photoRef.putFile(this.fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // success in uploading the image so update the remote database of the recommendation
                Log.d("upload", "uploadFromUri:onSuccess");
                mDownloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                recommendation.setPhotoUrl(mDownloadUrl.toString());
                String recKey = mDatabase.child("users").child(getUid()).child("recommendations").push().getKey();
                mDatabase.child("recommendations").child(recKey).setValue(recommendation);
                Log.d("locationID is", locationId);
                Log.d("restaurantID is", restaurantId);
                mDatabase.child("location").child("Singapore").child(locationId)
                        .child("restaurantMap").child(restaurantId)
                        .child("recommendationsMap").child(recKey).setValue(recommendation);
                mDatabase.child("restaurants").child("Singapore").child(restaurantId)
                        .child("recommendationsMap").child(recKey).setValue(recommendation);
                //recommendation.key = key;/*
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // upload fail
                Log.d("uploadFailure", "fail uploading");
            }
        });

        return true;

    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        // update UI
    }
}
