package com.example.sharkey.foodles.UI.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sharkey.foodles.R;
import com.example.sharkey.foodles.UI.API.model.Location;
import com.example.sharkey.foodles.UI.API.model.Recommendation;
import com.example.sharkey.foodles.UI.API.model.Restaurant;
import com.example.sharkey.foodles.UI.Views.LoadingFeedItemView;
import com.example.sharkey.foodles.UI.utils.RoundedTransformation;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.greenfrvr.hashtagview.HashtagView;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by sharkey on 17/8/16.
 */
public class RestaurantActivity extends BaseActivity implements ObservableScrollViewCallbacks {


    public static final String ACTION_SHOW_LOADING_ITEM = "action_show_loading_item";

    private static final float MAX_TEXT_SCALE_DELTA = 0.3f;
    private Location location;
    private int drawingStartLocation;
    private View mImageView;
    private View mOverlayView;
    private ObservableScrollView mScrollView;
    private TextView mTitleView;
    private View mFab;
    private TextView tvPrice;
    private TextView tvAddress;
    private ImageView ivFeedCenter;
    private TextView ivFeedBottom;
    private ImageView ivUserProfile;
    private TextView ivUserName;
    private FrameLayout vImageRoot;
    private String id;
    private String restaurantKey;
    private String displayUrl;
    private int mActionBarSize;
    private int mFlexibleSpaceShowFabOffset;
    private int mFlexibleSpaceImageHeight;
    private int mFabMargin;
    private String pos;
    private boolean mFabIsShown;
    private DatabaseReference mDatabase;
    private Restaurant restaurant;
    private LoadingFeedItemView loadingFeedItemView;

    private boolean showLoadingView;
    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";

    public static final int VIEW_TYPE_DEFAULT = 1;
    public static final int VIEW_TYPE_LOADER = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        Intent intent = getIntent();
        pos = intent.getStringExtra("Position");
        id = intent.getStringExtra("Location");
        restaurantKey = intent.getStringExtra("RestaurantKey");
        displayUrl = intent.getStringExtra("pictureUrl");

        mFlexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mFlexibleSpaceShowFabOffset = getResources().getDimensionPixelSize(R.dimen.flexible_space_show_fab_offset);
        mActionBarSize = getActionBarSize();


        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        tvAddress = (TextView) findViewById(R.id.tvAddress);
        tvPrice = (TextView) findViewById(R.id.tvPrice);
        mImageView = findViewById(R.id.image);
        mOverlayView = findViewById(R.id.overlay);
        mScrollView = (ObservableScrollView) findViewById(R.id.scroll);
        mScrollView.setScrollViewCallbacks(this);
        mTitleView = (TextView) findViewById(R.id.title);

        ivFeedCenter = (ImageView) findViewById(R.id.ivFeedCenter);
        ivFeedBottom = (TextView) findViewById(R.id.ivFeedBottom);
        ivFeedCenter.setRotation(90);
        ivUserProfile = (ImageView) findViewById(R.id.ivUserProfile);
        vImageRoot = (FrameLayout) findViewById(R.id.vImageRoot);
        ivUserName = (TextView)findViewById(R.id.ivUserName);
        CardView cv = (CardView) findViewById(R.id.moreRecommendation);
        cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MainActivity3.class);
                intent.putExtra("restaurantID", restaurantKey);
                startActivity(intent);
            }
        });
        //mTitleView.setText(getTitle());
        final Activity activity = this;
        setTitle(null);
        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RestaurantActivity.this, "FAB is clicked", Toast.LENGTH_SHORT).show();
                int[] startingLocation = new int[2];
                mFab.getLocationOnScreen(startingLocation);
                startingLocation[0] += mFab.getWidth() / 2;
                TakePhotoActivity.startCameraFromLocation(startingLocation, activity, id, "recommend", getIntent().getStringExtra("Position"), restaurantKey);
                overridePendingTransition(0, 0);
            }
        });
        mFabMargin = getResources().getDimensionPixelSize(R.dimen.margin_standard);
        ViewHelper.setScaleX(mFab, 0);
        ViewHelper.setScaleY(mFab, 0);

        ScrollUtils.addOnGlobalLayoutListener(mScrollView, new Runnable() {
            @Override
            public void run() {
                //mScrollView.scrollTo(0, mFlexibleSpaceImageHeight - mActionBarSize);

                // If you'd like to start from scrollY == 0, don't write like this:
                //mScrollView.scrollTo(0, 0);
                // The initial scrollY is 0, so it won't invoke onScrollChanged().
                // To do this, use the following:
                //onScrollChanged(0, false, false);

                // You can also achieve it with the following codes.
                // This causes scroll change from 1 to 0.
                mScrollView.scrollTo(0, 1);
                mScrollView.scrollTo(0, 0);
            }
        });

        getNearbyLocations();
    }

    private void getNearbyLocations() {
        // based on current location, filter and get the nearby locationsLog.d("fetch","fetching");
        Toast.makeText(getApplicationContext(), "getting nearby locations", Toast.LENGTH_SHORT).show();
        mDatabase.child("location").child("Singapore").child(id).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        location = dataSnapshot.getValue(Location.class);
                        getRestaurant(location, restaurantKey);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );

    }

    private void getRestaurant(final Location location, String key) {
        Log.d("keyyy", key);
        mDatabase.child("restaurants").child("Singapore").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Restaurant meh = dataSnapshot.getValue(Restaurant.class);
                Log.d("restaurantdetails", meh.toString());
                restaurant = meh;
                Log.d("meh", restaurant.restaurantName);
                updateDetails(location);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateDetails(Location location) {
        String address = location.getAddress();
        String name = restaurant.getRestaurantName();
        String price = restaurant.getAvgPrice();
        Picasso.with(getBaseContext())
                .load(restaurant.restaurantPicUri)
                .fit()
                .rotate(90f)
                .into((ImageView) mImageView);
        if (restaurant.getRecommendationsMap() == null) {
            ;
        } else {
            HashMap<String, Recommendation> map = restaurant.getRecommendationsMap();
            List<Recommendation> recommendations = new ArrayList<>();
            for(String key : map.keySet()) {
                recommendations.add(map.get(key));
            }
            bindView(recommendations.get(0));
        }

        mTitleView.setText(name);
        tvAddress.setText(address);
        tvPrice.setText(price);
    }

    public void bindView(final Recommendation feedItem) {
        // code to parse feedItem and update the views respectively
        if (showLoadingView) {
            loadingFeedItemView.setOnLoadingFinishedListener(new LoadingFeedItemView.OnLoadingFinishedListener() {
                @Override
                public void onLoadingFinished() {
                    showLoadingView = false;
                }
            });
            loadingFeedItemView.startLoading();
            showLoadingView = false;
        }
        Picasso.with(getBaseContext())
                .load(feedItem.getPhotoUrl())
                .fit()
                .into(ivFeedCenter, new Callback() {
                    @Override
                    public void onSuccess() {
                        //Log.d("likescount", feedItem.getLikes_count().toString());
                        //btnLike.setImageResource(feedItem.isLiked() ? R.drawable.ic_heart_red : R.drawable.ic_heart_outline_grey);
                            /*tsLikesCounter.setCurrentText(vImageRoot.getResources().getQuantityString(
                                    R.plurals.likes_count, feedItem.getLikes_count().intValue(), feedItem.getLikes_count().intValue()
                            ));*/
                        //tsLikesCounter.setText(feedItem.getLikes_count().toString());
                        String userName = feedItem.getUserName() + "\n";
                        ivUserName.setText(userName);
                        if (feedItem.caption != null) {
                            ivFeedBottom.setText(feedItem.caption);
                        } else {
                            ivFeedBottom.setVisibility(View.GONE);
                        }
                        Picasso.with(getBaseContext())
                                .load(feedItem.getDisplayPicUrl())
                                .centerCrop()
                                .resize(56, 56)
                                .transform(new RoundedTransformation())
                                .into(ivUserProfile);
                        ivUserProfile.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError() {

                    }
                });
        // is like does not mean like count > 0 but whether you like it or not
        // need check against the like users if your id is there

    }
    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        // Translate overlay and image
        float flexibleRange = mFlexibleSpaceImageHeight - mActionBarSize;
        int minOverlayTransitionY = mActionBarSize - mOverlayView.getHeight();
        ViewHelper.setTranslationY(mOverlayView, ScrollUtils.getFloat(-scrollY, minOverlayTransitionY, 0));
        ViewHelper.setTranslationY(mImageView, ScrollUtils.getFloat(-scrollY / 2, minOverlayTransitionY, 0));

        // Change alpha of overlay
        ViewHelper.setAlpha(mOverlayView, ScrollUtils.getFloat((float) scrollY / flexibleRange, 0, 1));

        // Scale title text
        float scale = 1 + ScrollUtils.getFloat((flexibleRange - scrollY) / flexibleRange, 0, MAX_TEXT_SCALE_DELTA);
        ViewHelper.setPivotX(mTitleView, 0);
        ViewHelper.setPivotY(mTitleView, 0);
        ViewHelper.setScaleX(mTitleView, scale);
        ViewHelper.setScaleY(mTitleView, scale);

        // Translate title text
        int maxTitleTranslationY = (int) (mFlexibleSpaceImageHeight - mTitleView.getHeight() * scale);
        int titleTranslationY = maxTitleTranslationY - scrollY;
        ViewHelper.setTranslationY(mTitleView, titleTranslationY);

        // Translate FAB
        int maxFabTranslationY = mFlexibleSpaceImageHeight - mFab.getHeight() / 2;
        float fabTranslationY = ScrollUtils.getFloat(
                -scrollY + mFlexibleSpaceImageHeight - mFab.getHeight() / 2,
                mActionBarSize - mFab.getHeight() / 2,
                maxFabTranslationY);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            // On pre-honeycomb, ViewHelper.setTranslationX/Y does not set margin,
            // which causes FAB's OnClickListener not working.
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mFab.getLayoutParams();
            lp.leftMargin = mOverlayView.getWidth() - mFabMargin - mFab.getWidth();
            lp.topMargin = (int) fabTranslationY;
            mFab.requestLayout();
        } else {
            ViewHelper.setTranslationX(mFab, mOverlayView.getWidth() - mFabMargin - mFab.getWidth());
            ViewHelper.setTranslationY(mFab, fabTranslationY);
        }

        // Show/hide FAB
        if (fabTranslationY < mFlexibleSpaceShowFabOffset) {
            hideFab();
        } else {
            showFab();
        }
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }

    private void showFab() {
        if (!mFabIsShown) {
            ViewPropertyAnimator.animate(mFab).cancel();
            ViewPropertyAnimator.animate(mFab).scaleX(1).scaleY(1).setDuration(200).start();
            mFabIsShown = true;
        }
    }

    private void hideFab() {
        if (mFabIsShown) {
            ViewPropertyAnimator.animate(mFab).cancel();
            ViewPropertyAnimator.animate(mFab).scaleX(0).scaleY(0).setDuration(200).start();
            mFabIsShown = false;
        }
    }


}
