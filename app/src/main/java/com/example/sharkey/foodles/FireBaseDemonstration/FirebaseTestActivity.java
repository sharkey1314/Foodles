package com.example.sharkey.foodles.FireBaseDemonstration;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sharkey.foodles.R;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.squareup.picasso.Picasso;

public class FirebaseTestActivity extends AppCompatActivity {

    private String facebookID;
    private Uri profilePicURL;
    private String Uid;
    private String providerId;
    private String profileName;
    private String email;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_test);
        FacebookSdk.sdkInitialize(getApplicationContext());
        createNewUser();
        updateUI();
    }

    public boolean createNewUser() {
        // new user create a "new account" for them
        facebookID = Profile.getCurrentProfile().getId();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            Log.d("no null", "not null");
            // fetch relevant data
            profileName = firebaseUser.getDisplayName();
            email = firebaseUser.getEmail();
            profilePicURL = firebaseUser.getPhotoUrl();

            for (UserInfo profile : firebaseUser.getProviderData()) {
                // Id of the provider (ex: google.com)
                providerId = profile.getProviderId();

                // UID specific to the provider
                Uid = profile.getUid();
            };
        }
        return true;
    }

    public void updateUI() {
        TextView tvProfileName = (TextView) findViewById(R.id.tvProfileName);
        TextView tvFacebookID = (TextView) findViewById(R.id.tvFacebookID);
        TextView tvUid = (TextView) findViewById(R.id.tvUid);
        TextView tvProviderId = (TextView) findViewById(R.id.tvProviderId);
        TextView tvProfilePicURL = (TextView) findViewById(R.id.tvProfilePicURL);
        TextView tvEmail = (TextView) findViewById(R.id.tvEmail);
        ImageView ivProfilePic = (ImageView) findViewById(R.id.ivProfilePic);

        tvProfileName.setText(profileName);
        tvFacebookID.setText(facebookID);
        tvUid.setText(Uid);
        tvProviderId.setText(providerId);
        tvProfilePicURL.setText(profilePicURL.toString());
        tvEmail.setText(email);

        Picasso.with(getBaseContext())
                .load(profilePicURL)
                .into(ivProfilePic);

    }

    public void createDatabase() {

    }
}
