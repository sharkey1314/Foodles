package com.example.sharkey.foodles.UI.DataManager;

import android.content.Context;
import android.os.AsyncTask;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by sharkey on 19/7/16.
 * In-charge of managing, downloading and uploading data to firebase
 */
public abstract class BaseDataManager extends AsyncTask<Void, Void, Boolean>{
    private String Uid;
    private Context context;

    public BaseDataManager(Context context) {
        this.context = context;
    }

    public BaseDataManager() {
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getProviderId();
    }

    public StorageReference getStorageRef() {
        return FirebaseStorage.getInstance().getReference();
    }

    public DatabaseReference getDatabaseRef() {
        return FirebaseDatabase.getInstance().getReference();
    }
}
