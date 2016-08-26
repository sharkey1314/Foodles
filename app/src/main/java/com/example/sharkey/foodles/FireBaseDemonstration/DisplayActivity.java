package com.example.sharkey.foodles.FireBaseDemonstration;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.sharkey.foodles.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class DisplayActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private DatabaseReference mDatabase;

    private String child_path = "gs://project-7957822587339009995.appspot.com/photos/";

    private ArrayList<Object>  dataset = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new DisplayActivityAdapter(getApplicationContext(), dataset);
        mRecyclerView.setAdapter(mAdapter);
        fetchData();
    }


    private void fetchData() {
        Log.d("fetch","fetching");
        Toast.makeText(getApplicationContext(), "fetching datat", Toast.LENGTH_SHORT).show();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("pictures").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d("adding","adding");
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            Log.d("result", child.getValue().toString());
                            dataset.add(child.getValue());
                            mAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );
    }
}
