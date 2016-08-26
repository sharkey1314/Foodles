package com.example.sharkey.foodles.FireBaseDemonstration;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.facebook.AccessToken;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by sharkey on 14/7/16.
 */
public class UploadTask extends AsyncTask<Uri, Void, Void> {


    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;

    private Uri mDownloadUrl = null;

    @Override
    protected Void doInBackground(Uri... params) {
        uploadFromUri(params[0]);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    private void uploadFromUri(Uri fileUri) {
        Log.d("upload", "uploadFromUri:src:" + fileUri.toString());
        mStorageRef = FirebaseStorage.getInstance().getReference();
        // [START get_child_ref]
        // Get a reference to store file at photos/<FILENAME>.jpg
        // UPDATE TO FIT ACCORDING HOW YOU WANT TO ORGANISE
        final StorageReference photoRef = mStorageRef.child("photos")
                .child(fileUri.getLastPathSegment());
        // [END get_child_ref]
        photoRef.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<com.google.firebase.storage.UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(com.google.firebase.storage.UploadTask.TaskSnapshot taskSnapshot) {
                // get the url for the photo
                // update the database with the url and relevant data
                Log.d("upload success","uploadFromUri:onSuccess");
                mDownloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                updateDatabase();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("failure", "login fail");


            }
        });
    }

    private void updateDatabase() {
        // figure out how to push in JSON object maybe?
        // put into respective user database based on their facebook user id

        mDatabase = FirebaseDatabase.getInstance().getReference();
        String userID = AccessToken.USER_ID_KEY;

        // pushing into database UPDATE please
        String key = mDatabase.child("pictures").push().getKey();
        mDatabase.child("pictures").child(key).setValue(mDownloadUrl.toString());

    }
}
