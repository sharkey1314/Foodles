package com.example.sharkey.foodles.UI.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.sharkey.foodles.UI.API.model.Location;
import com.example.sharkey.foodles.UI.API.model.Recommendation;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sharkey on 22/7/16.
 */
public abstract class LocationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // listen to updates in nearby locations database reference
    // meant to be used for MapAdapter and also autosuggest location use
    public List<Location> locations;
    public List<String> mKeys;

    public DatabaseReference mRef;
    private final String TAG = "FirebaseAdapter";
    private ChildEventListener childEventListener;

    public LocationAdapter() {
    }

    public LocationAdapter(DatabaseReference mRef) {
        this.mRef = mRef;
        locations = new ArrayList<>();
        mKeys = new ArrayList<>();
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
                Location location = dataSnapshot.getValue(Location.class);
                String key = dataSnapshot.getKey();
                if (s == null) {
                    // means first node
                    locations.add(0, location);
                    mKeys.add(0, key);
                } else {
                    int previousIndex = mKeys.indexOf(s);
                    int nextIndex = previousIndex + 1;
                    if (nextIndex == locations.size()) {
                        locations.add(location);
                        mKeys.add(key);
                    } else {
                        locations.add(nextIndex, location);
                        mKeys.add(key);
                    }
                }
                notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                // child change maybe it has been updated in terms of likes or name
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
                String key = dataSnapshot.getKey();
                Location location = dataSnapshot.getValue(Location.class);
                int index = mKeys.indexOf(key);
                locations.set(index, location);

                notifyDataSetChanged();

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());
                String key = dataSnapshot.getKey();
                int index = mKeys.indexOf(key);

                mKeys.remove(index);
                locations.remove(index);

                notifyDataSetChanged();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                String key = dataSnapshot.getKey();
                Location location = dataSnapshot.getValue(Location.class);
                int index = mKeys.indexOf(key);
                locations.remove(index);
                mKeys.remove(index);
                if (s == null) {
                    mKeys.add(0, key);
                    locations.add(0, location);
                } else {
                    int previousIndex = mKeys.indexOf(s);
                    int nextIndex = previousIndex + 1;
                    if (nextIndex == locations.size()) {
                        locations.add(location);
                        mKeys.add(key);
                    } else {
                        locations.add(nextIndex, location);
                        mKeys.add(nextIndex, key);
                    }
                }

                notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());

                Log.e("FirebaseListAdapter", "Listen was cancelled, no more updates will occur");

            }
        };
        mRef.addChildEventListener(childEventListener);
    }

    public void cleanup() {
        mRef.removeEventListener(childEventListener);
        mKeys.clear();
        locations.clear();
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

}
