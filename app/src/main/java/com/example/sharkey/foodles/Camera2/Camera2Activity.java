package com.example.sharkey.foodles.Camera2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.sharkey.foodles.R;

public class Camera2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);
        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, CameraFragment.newInstance())
                    .commit();
        }
    }
}
