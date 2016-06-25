package com.example.sharkey.foodles.FireBaseDemonstration;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sharkey.foodles.MyAdapter;
import com.example.sharkey.foodles.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sharkey on 24/6/16.
 */
public class DisplayActivityAdapter extends RecyclerView.Adapter<DisplayActivityAdapter.ViewHolder> {

    private ArrayList<Object> mDataset;
    private Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView tvDetails;
        public TextView tvUserName;
        public ImageView mImageView;

        public ViewHolder(View v) {
            super(v);
            tvDetails = (TextView) v.findViewById(R.id.tvDetails);
            tvUserName = (TextView) v.findViewById(R.id.tvUserName);
            mImageView = (ImageView) v.findViewById(R.id.ivFoodPicture);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public DisplayActivityAdapter (Context context, ArrayList<Object> myDataset) {
        this.context = context;
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public DisplayActivityAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext().getApplicationContext())
                .inflate(R.layout.cardview, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        String url = mDataset.get(position).toString();
        Log.d("displaying", url);
        holder.tvDetails.setText(url);
        Picasso.with(context).load(url).resize(150,150).centerCrop().into(holder.mImageView);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
