package com.example.sharkey.foodles.UI.Activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.OvershootInterpolator;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.example.sharkey.foodles.R;
import com.example.sharkey.foodles.UI.API.model.Coordinates;
import com.example.sharkey.foodles.UI.API.model.Location;
import com.example.sharkey.foodles.UI.API.model.Recommendation;
import com.example.sharkey.foodles.UI.API.model.Restaurant;
import com.example.sharkey.foodles.UI.Adapter.PhotoFiltersAdapter;
import com.example.sharkey.foodles.UI.DataManager.UploadRecommendationManager;
import com.example.sharkey.foodles.UI.Views.Utils;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class PublishActivity extends BaseActivity{

    public static final String ARG_TAKEN_PHOTO_URI = "arg_taken_photo_uri";

    @Bind(R.id.ivPhoto)
    ImageView ivPhoto;
    EditText etDescription;
    private AutoCompleteTextView textView;
    private RecyclerView rvFilters;
    private EditText etName;
    private  EditText etAddress;
    private boolean propagatingToggleState = false;
    private Uri photoUri;
    private int photoSize;
    private PhotoFiltersAdapter photoFiltersAdapter;
    private ViewSwitcher vLocation;
    private RatingBar ratingBar;
    private EditText etPrice;
    private EditText etPlace;
    private CardView cardView;
    private TextView tvLocation;
    private String restaurantID;

    private Recommendation recommendation;

    private String currentState;

    private LatLng currentLoc;
    private Boolean locationSelected = false;
    private List<String> dataset = new ArrayList<>();
    private List<String> finalKeys = new ArrayList<>();
    private int locId;
    private String locid;
    private String type;
    private String pos;
    private DatabaseReference mDatabase;
    private Uri mDownloadUrl;
    private StorageReference mStorageRef;

    public static void openWithPhotoUri(Activity openingActivity, Uri photoUri, String locationId, String type, String pos) {
        Intent intent = new Intent(openingActivity, PublishActivity.class);
        intent.putExtra(ARG_TAKEN_PHOTO_URI, photoUri);
        intent.putExtra("Position", pos);
        intent.putExtra("Type", type);
        intent.putExtra("Location", locationId);
        openingActivity.startActivity(intent);
    }


    public static void openWithPhotoUri(Activity openingActivity, Uri photoUri, String locationId, String type, String pos, String resID) {
        Intent intent = new Intent(openingActivity, PublishActivity.class);
        intent.putExtra(ARG_TAKEN_PHOTO_URI, photoUri);
        intent.putExtra("Position", pos);
        intent.putExtra("Type", type);
        intent.putExtra("Location", locationId);
        intent.putExtra("restaurantID", resID);
        openingActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pos = getIntent().getStringExtra("Position");
        type = getIntent().getStringExtra("Type");
        restaurantID = getIntent().getStringExtra("restaurantID");
        Log.d("restaurantID at publish", restaurantID);
        if (type.equals("Add")) {
            setContentView(R.layout.activity_add_restaurant);
        } else {
            setContentView(R.layout.activity_publish2);
        }
        setupViews();
        locid = getIntent().getStringExtra("Location");
        Log.d("locationsssss", locid);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_grey600_24dp);
        photoSize = getResources().getDimensionPixelSize(R.dimen.publish_photo_thumbnail_size);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        if (savedInstanceState == null) {
            photoUri = getIntent().getParcelableExtra(ARG_TAKEN_PHOTO_URI);
        } else {
            photoUri = savedInstanceState.getParcelable(ARG_TAKEN_PHOTO_URI);
        }
        updateStatusBarColor();

        if (type.equals("Add")) {
            // add restaurant

        } else {
            setupLocation();
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            String locationProvider = LocationManager.NETWORK_PROVIDER;
            try {
                android.location.Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
                currentLoc = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            } catch (SecurityException e) {
                e.printStackTrace();
            }
            Log.d("executinlocationfilter", "location fitler");

            locationFilterTask locationFilterTask = new locationFilterTask();
            locationFilterTask.execute();
            if (dataset != null) {
                photoFiltersAdapter.refresh(dataset);
                photoFiltersAdapter.notifyDataSetChanged();
            }
        }

        ivPhoto.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                ivPhoto.getViewTreeObserver().removeOnPreDrawListener(this);
                loadThumbnailPhoto();
                return true;
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void updateStatusBarColor() {
        if (Utils.isAndroid5()) {
            getWindow().setStatusBarColor(0xff888888);
        }
    }

    private void setupViews() {
        if (type.equals("Add")) {
            // set up views for add_restaurant
            etDescription = (EditText) findViewById(R.id.etDescription);
            etAddress = (EditText) findViewById(R.id.etAddress);
            etPlace = (EditText) findViewById(R.id.etPlace);
            etPrice = (EditText) findViewById(R.id.etPrice);
        } else {
            etDescription = (EditText) findViewById(R.id.etDescription);
            rvFilters = (RecyclerView) findViewById(R.id.rvFilters);
            etName = (EditText) findViewById(R.id.etName);
            etPrice = (EditText) findViewById(R.id.etPrice);
            ratingBar = (RatingBar) findViewById(R.id.ratingBar);
            tvLocation = (TextView) findViewById(R.id.tvLocation);
        }
    }
    private void loadThumbnailPhoto() {
        ivPhoto.setScaleX(0);
        ivPhoto.setScaleY(0);
        Picasso.with(this)
                .load(photoUri)
                .centerCrop()
                .resize(photoSize, photoSize)
                .into(ivPhoto, new Callback() {
                    @Override
                    public void onSuccess() {
                        ivPhoto.animate()
                                .scaleX(1.f).scaleY(1.f)
                                .setInterpolator(new OvershootInterpolator())
                                .setDuration(400)
                                .setStartDelay(200)
                                .start();
                    }

                    @Override
                    public void onError() {
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_publish, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_publish) {
            if (type.equals("Add")) {
                // add restaurant
                uploadRestaurant();
                bringMainActivityToTop();
                return true;
            } else {
                uploadRecommendation();
                bringMainActivityToTop();
                return true;
            }
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void bringMainActivityToTop() {
        Intent intent;
        if (type.equals("Add")) {
            intent = new Intent(this, MapActivity2.class);
            intent.putExtra("refresh", "refresh");
            intent.putExtra("Position", pos);
            intent.putExtra("Location", locid);
            intent.putExtra("restaurantID", restaurantID);
        } else {
            intent = new Intent(this, RestaurantActivity.class);
            intent.putExtra("Position", pos);
            intent.putExtra("Location", locid);
            intent.putExtra("RestaurantKey", restaurantID);
        }
        startActivity(intent);
    }

    private void uploadRestaurant() {
        String caption = etDescription.getText().toString();
        String name, address;/*
        Double ratings = Double.parseDouble(ratingBar.getRating()+"");*/
        Double ratings = 0.0;
        String price = etPrice.getText().toString();
        address = etAddress.getText().toString();
        name = etPlace.getText().toString();/*
        DatabaseReference mRef = mDatabase.child("location").child("Singapore")
                .child(locid).child("restaurantMap");*/
        // update restaurant
        // update location map as well
        final DatabaseReference mRef = mDatabase.child("restaurants").child("Singapore");
        final String key = mRef.push().getKey();
        final Restaurant restaurant = new Restaurant(price, ratings, caption, name, null);


        StorageReference photoRef = FirebaseStorage.getInstance().getReference()
                .child("restaurant/").child(photoUri.getLastPathSegment());
        photoRef.putFile(photoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // success in uploading the image so update the remote database of the recommendation
                Log.d("upload", "uploadFromUri:onSuccess");
                mDownloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                restaurant.setRestaurantPicUri(mDownloadUrl.toString());
                Log.d("logid", locid);
                mRef.child(key).setValue(restaurant);
                mDatabase.child("location").child("Singapore").child(locid).child("restaurantMap").child(key).setValue(restaurant);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // upload fail
                Log.d("uploadFailure", "fail uploading");
            }
        });
    }

    private void uploadRecommendation() {
        String caption = etDescription.getText().toString();
        String name, address;
        String key = null;
        Double ratings = Double.parseDouble(ratingBar.getRating()+"");
        String price = etPrice.getText().toString();
        address = dataset.get(locId);
        String id;
        // restaurant id
        if (locationSelected) {
            id = finalKeys.get(locId);
        } else {
            id = key;
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String profileName = user.getDisplayName();
        String profilePicURL = user.getPhotoUrl().toString();
        String userid = FirebaseAuth.getInstance().getCurrentUser().getProviderId();
        String recKey = mDatabase.child("users").child(userid).child("recommendations").push().getKey();
        recommendation = new Recommendation(address, caption, profilePicURL, locid, price, ratings, profileName);
        UploadRecommendationManager task = new UploadRecommendationManager(getBaseContext(),recommendation, photoUri, id, restaurantID, recKey);
        task.execute();
    }


    private void setupLocation() {
        photoFiltersAdapter = new PhotoFiltersAdapter(getBaseContext(), dataset, new PhotoFiltersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                locId = position;
                locationSelected = true;
                Toast.makeText(PublishActivity.this, locId + "was selected", Toast.LENGTH_LONG).show();
            }
        });
        rvFilters.setHasFixedSize(true);
        rvFilters.setAdapter(photoFiltersAdapter);
        rvFilters.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ARG_TAKEN_PHOTO_URI, photoUri);
    }
    public class locationFilterTask extends AsyncTask<Void
            , Void, Void> {
    
        @Override
        protected Void doInBackground(Void... params) {
            final List<Location> locations = new ArrayList<>();
            final List<String> keys = new ArrayList<>();
            dataset = new ArrayList<>();
            DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("location")
                    .child("Singapore");
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Log.d("result", child.getValue().toString());
                        keys.add(child.getKey());
                        locations.add(child.getValue(com.example.sharkey.foodles.UI.API.model.Location.class));
                        // filter the locations first
                        //mAdapter.notifyDataSetChanged();
                    }
                    for (int i = 0; i<locations.size(); i++) {
                        Location current = locations.get(i);
                        Log.d("locations", keys.get(i));
                        Log.d("name", current.getName());
                        finalKeys.add(keys.get(i));
                        dataset.add(current.getName());
                    }
                    photoFiltersAdapter.refresh(dataset);
                    photoFiltersAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            double startlat = currentLoc.latitude;
            double startlong = currentLoc.longitude;
            for (int i = 0; i<locations.size(); i++) {
                Location current = locations.get(i);
                Coordinates coord = current.getCoordinates();
                float[] results = new float[1];
                android.location.Location.distanceBetween(startlat, startlong, coord.latitude, coord.longitude, results);
                /*if (results[0] < 1000) {
                    // within circle of 1km radius
                    // add to finalloc
                    Log.d("adding location", "adding location");
                    Log.d("added", current.toString());
                    finalKeys.add(keys.get(i));
                    dataset.add(current.getName());
                }*/
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d("refreshing", "refreshing");
            Log.d("dataset", dataset.toString());
            photoFiltersAdapter.refresh(dataset);
            photoFiltersAdapter.notifyDataSetChanged();
        }
    }
}
