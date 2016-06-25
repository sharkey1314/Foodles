package com.example.sharkey.foodles.FireBaseDemonstration;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.sharkey.foodles.R;

public class uploadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        Intent intent = getIntent();
        Bitmap bitmap = (Bitmap) intent.getParcelableExtra("imageBitMap");

        ImageView imageView = (ImageView) findViewById(R.id.imageView2);
        imageView.setImageBitmap(bitmap);


    }
}
