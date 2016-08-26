package com.example.sharkey.foodles.UI.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.sharkey.foodles.UI.API.model.Recommendation;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sharkey on 20/7/16.
 */
public abstract  class FirebaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public List<Recommendation> feedItems;
    public List<String> mKeys;
    public DatabaseReference mRef;
    private final String TAG = "FirebaseAdapter";
    private ChildEventListener childEventListener;

    public FirebaseAdapter() {
    }

    public FirebaseAdapter(DatabaseReference mRef) {
        this.mRef = mRef;
        feedItems = new ArrayList<>();
        mKeys = new ArrayList<>();
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
                Recommendation recommendation = dataSnapshot.getValue(Recommendation.class);
                String key = dataSnapshot.getKey();
                if (s == null) {
                    // means first node
                    feedItems.add(0, recommendation);
                    mKeys.add(0, key);
                } else {
                    int previousIndex = mKeys.indexOf(s);
                    int nextIndex = previousIndex + 1;
                    if (nextIndex == feedItems.size()) {
                        feedItems.add(recommendation);
                        mKeys.add(key);
                    } else {
                        feedItems.add(nextIndex, recommendation);
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
                Recommendation recommendation = dataSnapshot.getValue(Recommendation.class);
                int index = mKeys.indexOf(key);
                feedItems.set(index, recommendation);

                notifyDataSetChanged();

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());
                String key = dataSnapshot.getKey();
                int index = mKeys.indexOf(key);

                mKeys.remove(index);
                feedItems.remove(index);

                notifyDataSetChanged();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                String key = dataSnapshot.getKey();
                Recommendation recommendation = dataSnapshot.getValue(Recommendation.class);
                int index = mKeys.indexOf(key);
                feedItems.remove(index);
                mKeys.remove(index);
                if (s == null) {
                    mKeys.add(0, key);
                    feedItems.add(0, recommendation);
                } else {
                    int previousIndex = mKeys.indexOf(s);
                    int nextIndex = previousIndex + 1;
                    if (nextIndex == feedItems.size()) {
                        feedItems.add(recommendation);
                        mKeys.add(key);
                    } else {
                        feedItems.add(nextIndex, recommendation);
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
        feedItems.clear();
    }

    @Override
    public int getItemCount() {
        return feedItems.size();
    }

    public void updateLikesCount(int position, long count) {
        String key = mKeys.get(position);
        Log.d("key", key);
        mRef.child(key).child("likes_count").setValue(count);
    }


}
