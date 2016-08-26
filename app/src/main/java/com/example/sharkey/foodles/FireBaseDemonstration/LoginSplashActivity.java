package com.example.sharkey.foodles.FireBaseDemonstration;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.sharkey.foodles.R;
import com.example.sharkey.foodles.UI.Activity.MainActivity3;
import com.example.sharkey.foodles.UI.Activity.MapActivity2;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginSplashActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_splash);

        FacebookSdk.sdkInitialize(getApplicationContext());

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        Log.d("splash", "splashing");


        if (mUser != null) {
            // user is logged in; go to homeActivity
            intent = new Intent(this, MapActivity2.class);
        } else {
            // user is not logged in; go to FireBaseLogin
            Log.d("wtf", "what is happening?!");
            intent = new Intent(this, FireBaseLogin.class);
        }
        startActivity(intent);
    }
}
