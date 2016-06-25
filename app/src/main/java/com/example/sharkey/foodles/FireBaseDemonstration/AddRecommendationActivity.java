package com.example.sharkey.foodles.FireBaseDemonstration;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.camera2.CameraManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.sharkey.foodles.Manifest;
import com.example.sharkey.foodles.R;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddRecommendationActivity extends AppCompatActivity implements View.OnClickListener{
    // this activity allows the user to add their recommendations to the database
    // we allow them to upload/capture a photo and fill in some descriptions
    // then we display them accordingly
    private AccessToken accessToken;
    // References to views
    EditText etName;
    EditText etCaption;
    ImageView imageView;
    Button btCamera;
    Button btGallery;
    Button btCont;

    private static final String BITMAP_STORAGE_KEY = "viewbitmap";
    private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";

    Bitmap imageBitmap;

    String mCurrentPhotoPath;

    String[] perms = {"android.permission.CAMERA", "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};
    static final int permsRequestCode = 200;
    static final int REQUEST_IMAGE_CAPTURE = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_add_recommendation);
        accessToken = AccessToken.getCurrentAccessToken();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl("gs://project-7957822587339009995.appspot.com");

        // get references
        etName = (EditText) findViewById(R.id.etName);
        etCaption = (EditText) findViewById(R.id.etCaption);
        imageView = (ImageView) findViewById(R.id.foodPic);
        btCamera = (Button) findViewById(R.id.btUseCamera);
        btGallery = (Button) findViewById(R.id.btAddGallery);
        btCont = (Button) findViewById(R.id.btCont);

        btCamera.setOnClickListener(this);
        btCont.setOnClickListener(this);

        if (ContextCompat.checkSelfPermission(this, "android.permission.CAMERA")
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, perms, permsRequestCode);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btUseCamera :
                dispatchTakePictureIntent();
                break;

            case R.id.btCont:
                Intent intent = new Intent(this, uploadActivity.class);
                intent.putExtra("imageBitMap", imageBitmap);
                startActivity(intent);

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();  // Always call the superclass method first

    }

    @Override
    protected void onRestart() {
        super.onRestart();  // Always call the superclass method first
        // Activity being restarted from stopped state
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case permsRequestCode: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // do what you need to do after having access to camera
                    // finish implementation here
                    Log.d("permissions granted", "true");
                }
            }
        }
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                Log.d("Error", "Cannot create file");
            }

            if (photoFile != null) {
                // only continue if the image file is not null
            /*    Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);*//*
                if (((BitmapDrawable)imageView.getDrawable()).getBitmap() != null) {
                    imageView.setImageResource(0);
                }*/
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                Log.d("Error", "test");

            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d("PlacePickerTest", "Activity onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("PlacePickerTest", "Activity onPause");
    }

    @Override
    protected void onDestroy() {
        Log.d("PlacePickerTest", "Activity onDestroy");
        super.onDestroy();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("result code is :", "" + resultCode);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Log.d("Error", "picture adding");
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            MediaStore.Images.Media.insertImage(getContentResolver(), imageBitmap, null, null);
            imageView.setImageBitmap(imageBitmap);
            imageView.invalidate();
            // galleryAddPic();
        }

        if (resultCode == RESULT_CANCELED) {
            Log.d("stupid", "sigh");
        } else {
            Log.d("SOMETHING BAD HAPPEN", "something bad did happened");
        }
    }



    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(BITMAP_STORAGE_KEY, imageBitmap);
        outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY, (imageBitmap != null) );
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        imageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
        imageView.setImageBitmap(imageBitmap);
        imageView.setVisibility(
                savedInstanceState.getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ?
                        ImageView.VISIBLE : ImageView.INVISIBLE
        );
    }



}
