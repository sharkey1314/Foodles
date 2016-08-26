package com.example.sharkey.foodles.UI.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Toast;

import com.example.sharkey.foodles.Main2Activity;
import com.example.sharkey.foodles.MainActivity;
import com.example.sharkey.foodles.PermissionUtils.PermissionUtils;
import com.example.sharkey.foodles.R;
import com.example.sharkey.foodles.UI.API.model.Coordinates;
import com.example.sharkey.foodles.UI.API.model.Restaurant;
import com.example.sharkey.foodles.UI.Adapter.MapAdapter;
import com.example.sharkey.foodles.UI.Views.RevealBackgroundView;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapActivity2 extends BaseDrawerActivity implements RevealBackgroundView.OnStateChangeListener,
        NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener, ActivityCompat.OnRequestPermissionsResultCallback ,
        MapAdapter.OnRestaurantClickListener{

    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";

    private static final int USER_OPTIONS_ANIMATION_DELAY = 300;
    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;


    private boolean mPermissionDenied = false;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;

    private Location mLastLocation;


    private RecyclerView mRecyclerView;
    private MapAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private List<String> friends = new ArrayList<>();
    private LatLng currentLoc;
    private List<Marker> markers = new ArrayList<>();
    public List<Restaurant> restaurants;
    private List<com.example.sharkey.foodles.UI.API.model.Location> locations;
    private List<String> locationsID;
    private String currentLocationID;
    private List<String> restaurantsKey = new ArrayList<>();
    private List<List<Restaurant>> restaurantsList = new ArrayList<>();
    private List<List<String>> restaurantsKeyList = new ArrayList<>();

    private DatabaseReference mDatabase;

    private List<List<Restaurant>> dataset = new ArrayList<>();

    RevealBackgroundView vRevealBackground;

    public static void startMapActivityFromLocation(int[] startingLocation, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, MapActivity2.class);
        intent.putExtra(ARG_REVEAL_START_LOCATION, startingLocation);
        startingActivity.startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_inbox: {
                Intent intent = new Intent(this, AddLocationActivity.class);
                startActivity(intent);
            }
        }
        return true;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (FacebookSdk.isInitialized()) {
            ;
        } else {
            FacebookSdk.sdkInitialize(getApplicationContext());
        }
        setContentView(R.layout.activity_map);
        vRevealBackground = (RevealBackgroundView) findViewById(R.id.vRevealBackground);
        //setupRevealBackground(savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        locations = new ArrayList<>();
        restaurants = new ArrayList<>();

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.NETWORK_PROVIDER;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null)
                mMap.setMyLocationEnabled(true);
            enableMyLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }

        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        currentLoc = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
        Log.d("latlng", currentLoc.toString());
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // get nearby locations by filtering
        setupMapFeed();
        setUpFbFriends();
        // getNearbyLocations();
        fetchAllLocations();
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) throws SecurityException{

        try {
            switch (requestCode) {
                case 100: {
                    if (mMap != null)
                        mMap.setMyLocationEnabled(true);
                    enableMyLocation();
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void fetchAllLocations() {
        Log.d("FetchAllLocations", "Fetching locations");
        /*// fetch all locations
        mDatabase.child("location").child("Singapore").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                locationsID = new ArrayList<String>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    locationsID.add(child.getKey());
                    locations.add(child.getValue(com.example.sharkey.foodles.UI.API.model.Location.class));
                }
                locationFilterTask locationFilterTask = new locationFilterTask();
                locationFilterTask.execute(locations);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
        mDatabase.child("location").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                locationsID = new ArrayList<String>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Log.d("keyeeyeyey", child.getKey());
                    locationsID.add(child.getKey());
                    locations.add(child.getValue(com.example.sharkey.foodles.UI.API.model.Location.class));
                }
                locationFilterTask locationFilterTask = new locationFilterTask();
                locationFilterTask.execute(locations);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getNearbyLocations() {
        // based on current location, filter and get the nearby locationsLog.d("fetch","fetching");
        Toast.makeText(getApplicationContext(), "getting nearby locations", Toast.LENGTH_SHORT).show();
        mDatabase.child("location").child("Singapore").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d("adding","adding");
                        locationsID = new ArrayList<String>();
                        locations = new ArrayList<com.example.sharkey.foodles.UI.API.model.Location>();
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            Log.d("result", child.getValue().toString());
                            Log.d("resultt", child.getKey());
                            locationsID.add(child.getKey());
                            locations.add(child.getValue(com.example.sharkey.foodles.UI.API.model.Location.class));
                            // filter the locations first
                            //mAdapter.notifyDataSetChanged();
                        }
                        locationFilterTask locationFilterTask = new locationFilterTask();
                        locationFilterTask.execute(locations);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );

    }
    /*
    * Update the list of friends the user have
     */
    private void setUpFbFriends() {
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends/",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        Log.d("fb id",response.toString());
                    }
                }
        ).executeAsync();
    }

    private void setupMapFeed() {
        mRecyclerView = (RecyclerView) findViewById(R.id.rvMapFeed);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        Log.d("recommendations", restaurants.toString());
        mAdapter = new MapAdapter(this, restaurants);
        mAdapter.setOnRestaurantClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                mAdapter.setLockedAnimations(true);
            }
        });
        mAdapter.notifyDataSetChanged();
    }



    @Override
    public void onRestaurantClick(View v, int position) {
        Log.d("position", position+"");
        Log.d("restaurantsize", restaurants.size()+"");
        if (position == restaurants.size()-1 || restaurants.size() == 0) {
            // ADD A NEW RESTAURANT
            Log.d("POSITION IS ", position+"");int[] startingLocation = new int[2];
            v.getLocationOnScreen(startingLocation);
            startingLocation[0] += v.getWidth() / 2;
            String id;
            int adjustedPos = restaurants.size()-position-1;
            if (restaurantsKey.size() == 0) {
                id = "lol";
            } else {
                id = restaurantsKey.get(adjustedPos);
            }
            TakePhotoActivity.startCameraFromLocation(startingLocation, this, currentLocationID, "Add", position+"", id);
            overridePendingTransition(0, 0);
        } else {
            // OPEN RESTAURANT DETAILS
            int adjustedPos = restaurants.size()-position-1;
            final Intent intent = new Intent(this, RestaurantActivity.class);
            intent.putExtra("Position", adjustedPos+"");
            intent.putExtra("Location",currentLocationID);
            intent.putExtra("RestaurantKey", restaurantsKey.get(position));
            intent.putExtra("pictureUrl", restaurants.get(position).getRestaurantPicUri());
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
    }

    private void setupRevealBackground(Bundle savedInstanceState) {
        vRevealBackground.setOnStateChangeListener(this);
        if (savedInstanceState == null) {
            final int[] startingLocation = getIntent().getIntArrayExtra(ARG_REVEAL_START_LOCATION);
            vRevealBackground.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    vRevealBackground.getViewTreeObserver().removeOnPreDrawListener(this);
                    vRevealBackground.startFromLocation(startingLocation);
                    return true;
                }
            });
        } else {
            vRevealBackground.setToFinishedFrame();
            mAdapter.setLockedAnimations(true);
        }
    }
/*
    private void fetchData() {
        Log.d("fetch","fetching");
        Toast.makeText(getApplicationContext(), "fetching data", Toast.LENGTH_SHORT).show();
        locations = new ArrayList<>();
        mDatabase.child("NearbyLocations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("fetching?!", "fetching...");
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Log.d("result", child.getValue().toString());
                    locations.add(child.getValue(com.example.sharkey.foodles.UI.API.model.Location.class));
                    // filter the locations first
                    //mAdapter.notifyDataSetChanged();
                    Log.d("testing", locations.get(0).toString());
                }
                filterLocation(locations);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }*/
/*

    private void filterLocation(List<com.example.sharkey.foodles.UI.API.model.Location> locations) {
        // constructor with list of friends
        filterFriendManager filterFriendManager =  new filterFriendManager(friends);
        // execute with the list of locations fetched from database
        filterFriendManager.execute(locations);
    }
*/

    @Override
    public void onStateChange(int state) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (RevealBackgroundView.STATE_FINISHED == state) {
            fragmentTransaction.hide(fragmentManager.findFragmentById(R.id.map));
            //animateUserProfileOptions();
            //animateUserProfileHeader();
        } else {
            fragmentTransaction.show(fragmentManager.findFragmentById(R.id.map));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
/*
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

        float zoomLevel = 16.0f; //This goes up to 21
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, zoomLevel));
        addMarkersToMap();
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // get the index of the marker being clicked
                Log.d("marker", marker.toString());
                int index = markers.indexOf(marker);
                Log.d("index", index+"");
                // fetch the relevant location
                currentLocationID = locationsID.get(index);
                restaurants = dataset.get(index);
                restaurantsKey = restaurantsKeyList.get(index);
                Log.d("dataset", dataset.size()+"");
                Log.d("recommendationsmarkers", restaurants.toString());
                mAdapter.refresh(restaurants);
                return false;
            }
        });
        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();

    }

    private void addMarkersToMap() {
        // for loop
        for (int i = 0; i < locations.size(); i++) {
            // add a marker for each individual location
            Log.d("location size", locations.size()+"");
            com.example.sharkey.foodles.UI.API.model.Location location = locations.get(i);
            markers.add(mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(location.getCoordinates().latitude,
                            location.getCoordinates().longitude))
                    .title(location.getName())
                    .snippet(location.getAddress())
                    .draggable(false)));
        }
    }


    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            Log.d("nono","nono");
            PermissionUtils.requestPermission((AppCompatActivity)this.getParent(), LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    public class locationFilterTask extends AsyncTask<List<com.example.sharkey.foodles.UI.API.model.Location>
            , Void, List<com.example.sharkey.foodles.UI.API.model.Location>> {

        @Override
        protected List<com.example.sharkey.foodles.UI.API.model.Location> doInBackground(
                List<com.example.sharkey.foodles.UI.API.model.Location>... params) {
            List<com.example.sharkey.foodles.UI.API.model.Location> loc = params[0];
            List<com.example.sharkey.foodles.UI.API.model.Location> finalLoc = new ArrayList<>();
            double startlat = currentLoc.latitude;
            double startlong = currentLoc.longitude;
            for (int i = 0; i<loc.size(); i++) {
                com.example.sharkey.foodles.UI.API.model.Location current = loc.get(i);
                Coordinates coord = current.getCoordinates();
                float[] results = new float[1];
                Location.distanceBetween(startlat, startlong, coord.latitude, coord.longitude, results);
                if (results[0] < 100000) {
                    // within circle of 1km radius
                    // add to finalloc
                    Log.d("adding location", "adding location");
                    Log.d("added", current.toString());
                    finalLoc.add(current);
                }

                // extract restaurants out...
                HashMap<String, Restaurant> map = current.getRestaurantMap();
                List<String> keys = new ArrayList<>();
                List<Restaurant> list = new ArrayList<>();
                if (map != null) {
                    for (String key : map.keySet()) {
                        keys.add(key);
                        list.add(map.get(key));
                    }
                }
                locationsID.add(current.getLocId());
                restaurantsList.add(list);
                restaurantsKeyList.add(keys);
            }
            return finalLoc;
        }

        @Override
        protected void onPostExecute(List<com.example.sharkey.foodles.UI.API.model.Location> locations) {
            mDatabase.child("NearbyLocations").setValue(locations);
            if (mMap != null) {
                Log.d("adding markers", "adding markers");
                addMarkersToMap();
            }
            dataset = restaurantsList;
            restaurants = dataset.get(0);
            restaurantsKey = restaurantsKeyList.get(0);
            mAdapter.refresh(dataset.get(0));
            currentLocationID = locationsID.get(0);
            Log.d("recommendations", restaurants.toString());

        }
    }

    /*public class filterFriendManager extends AsyncTask<List<com.example.sharkey.foodles.UI.API.model.Location>, Void,
            List<List<Restaurant>>> {
        List<String> friends;

        public filterFriendManager(List<String> friends) {
            this.friends = friends;
        }
        @Override
        protected List<List<Restaurant>> doInBackground(List<com.example.sharkey.foodles.UI.API.model.Location>... params) {
            List<com.example.sharkey.foodles.UI.API.model.Location> passed = params[0];
            // iterate through the passed locations
            List<List<Restaurant>> sortedData = new ArrayList<>();
            for (int i = 0; i<passed.size(); i++) {
                Log.d("passed size", passed.size()+"");
                com.example.sharkey.foodles.UI.API.model.Location location = passed.get(i);
                sortedData.add(i, getRestaurants(location));
                Log.d("locationsort", location.toString());
            }
            Log.d("sorteddatasize", sortedData.size()+"");
            Log.d("sortedata", sortedData.toString());
            return sortedData;

        }

        private List<Restaurant> getRestaurants(com.example.sharkey.foodles.UI.API.model.Location location) {
            final HashMap<String, String> restaurants = location.getRestaurantMap();
            final List<Restaurant> restaurantList = new ArrayList<>();
            if (restaurants == null) { return restaurantList;}
            mDatabase.child("restaurants").child("Singapore").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Log.d("result", child.getValue().toString());
                        Log.d("sigh", restaurants.size()+"");
                        Log.d("WTF", restaurants.toString());
                        for (int i = 0; i<restaurants.size(); i++) {
                            if (restaurants.get(i).equals(child.getKey())) {
                                restaurantList.add(child.getValue(Restaurant.class));
                                restaurantsKey.add(child.getKey());
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            return restaurantList;
        }
*//*
        private List<Restaurant> sortLocationRecommendation(com.example.sharkey.foodles.UI.API.model.Location location) {
            HashMap<String,Restaurant> restaurants = location.getRestaurantMap();
            List<Restaurant> friends = new ArrayList<>();
            List<Restaurant> notFriends = new ArrayList<>();

            *//**//*for (String key: restaurants.keySet()) {
                Restaurant restaurant = restaurants.get(key);
                // to-do add userid in Recommendation class **
                if (friends.indexOf(recommendation.getUserName()) == -1) {
                    // recommendation not part of friend
                    // put in notFriends array
                    notFriends.add(recommendation);
                } else {
                    friends.add(recommendation);
                }
            }*//**//*
            friends.addAll(notFriends);
            return friends;
            // return maybe two arrays : friends and strangers
            // or return 1 full array with friends starting first
        }*//*

        @Override
        protected void onPostExecute(List<List<Restaurant>> sortedData) {
            for (int i = 0; i<sortedData.size(); i++) {
                Log.d("dataset", sortedData.get(i).toString());
            }
            dataset = sortedData;
            mAdapter.refresh(sortedData.get(0));
            currentLocationID = locationsID.get(0);
            Log.d("recommendations", restaurants.toString());

        }
    }*/

}
