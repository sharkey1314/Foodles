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


public class AddLocationActivity extends BaseActivity {

    public static final String ARG_TAKEN_PHOTO_URI = "arg_taken_photo_uri";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);
        setupViews();
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_grey600_24dp);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        updateStatusBarColor();

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        try {
            android.location.Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
            currentLoc = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        setupViews();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void updateStatusBarColor() {
        if (Utils.isAndroid5()) {
            getWindow().setStatusBarColor(0xff888888);
        }
    }

    private void setupViews() {
        etAddress = (EditText) findViewById(R.id.etAddress);
        etPlace = (EditText) findViewById(R.id.etPlace);
    }

    private void uploadLocation() {
        String address = etAddress.getText().toString();
        String place = etPlace.getText().toString();
        Coordinates coordinates = new Coordinates(currentLoc.latitude, currentLoc.longitude);
        Location location = new Location(place, address, null, coordinates, null);
        Log.d("Uploading...", "uploading");
        Log.d("Coordinates:", coordinates.latitude + " " + coordinates.longitude);
        DatabaseReference mRef = mDatabase.child("location").child("Singapore");
        String key = mRef.push().getKey();
        location.setLocId(key);
        mRef.child(key).setValue(location);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_publish, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_publish) {
            uploadLocation();
            bringMainActivityToTop();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void bringMainActivityToTop() {
        Intent intent;
        intent = new Intent(this, MapActivity2.class);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ARG_TAKEN_PHOTO_URI, photoUri);
    }
}
