package com.example.sharkey.foodles.UI.Adapter;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sharkey.foodles.R;
import com.example.sharkey.foodles.UI.API.model.Location;
import com.example.sharkey.foodles.UI.API.model.Recommendation;
import com.example.sharkey.foodles.UI.API.model.Restaurant;
import com.example.sharkey.foodles.UI.Utils;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sharkey on 21/7/16.
 */
public class MapAdapter extends LocationAdapter {
    private static final int PHOTO_ANIMATION_DELAY = 600;
    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();


    private OnRestaurantClickListener onRestaurantClickListener;
    private final Context context;
    private final int cellSize;


    private List<Restaurant> mDataset;
    private boolean lockedAnimations = false;
    private int lastAnimatedItem = -1;

    public MapAdapter (Context context, List<Restaurant> myDataset) {
        this.context = context;
        this.cellSize = Utils.getScreenWidth(context) / 3;
        this.mDataset = myDataset;
    }

    public MapAdapter(Context context, DatabaseReference mRef) {
        super(mRef);
        this.context = context;
        this.cellSize = Utils.getScreenWidth(context) / 3;
        this.mRef = mRef;
    }

    private void setupClickableViews(final View view, final ViewHolder viewHolder) {
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRestaurantClickListener.onRestaurantClick(view, viewHolder.getAdapterPosition());
            }
        });
    }
    public void setOnRestaurantClickListener(OnRestaurantClickListener onRestaurantClickListener) {
        this.onRestaurantClickListener = onRestaurantClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount()-1) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            view = LayoutInflater.from(context).inflate(R.layout.cardview, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.cardview2, parent, false);
        }
        ViewHolder viewHolder = new ViewHolder(view);
        setupClickableViews(view, viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == getItemCount()-1) {
            return;
        } else {
            bindPhoto((ViewHolder) holder, position);
        }
    }

    private void bindPhoto(final ViewHolder holder, int position) {
        Log.d("adapter", mDataset.size()+"");
        String url = mDataset.get(position).getRestaurantPicUri();
        Picasso.with(context)
                .load(url)
                .rotate(90f)
                .resize(150, 150)
                .centerCrop()
                .into(holder.mImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        animatePhoto(holder);
                    }

                    @Override
                    public void onError() {

                    }
                });
        if (lastAnimatedItem < position) lastAnimatedItem = position;
    }

    private void animatePhoto(ViewHolder viewHolder) {
        if (!lockedAnimations) {
            if (lastAnimatedItem == viewHolder.getPosition()) {
                setLockedAnimations(true);
            }

            long animationDelay = PHOTO_ANIMATION_DELAY + viewHolder.getPosition() * 30;

            viewHolder.cardView.setScaleY(0);
            viewHolder.cardView.setScaleX(0);

            viewHolder.cardView.animate()
                    .scaleY(1)
                    .scaleX(1)
                    .setDuration(200)
                    .setInterpolator(INTERPOLATOR)
                    .setStartDelay(animationDelay)
                    .start();
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void refresh(List<Restaurant> items) {
        this.mDataset = items;
        Log.d("items?!!?", items.toString());
        if (items.size() == 0) {
            this.mDataset.add(new Restaurant());
            notifyDataSetChanged();
            return;
        }
        if (items.get(items.size()-1).avgPrice == null) {
            notifyDataSetChanged();
        } else {
            this.mDataset.add(new Restaurant());
            notifyDataSetChanged();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView tvDetails;
        public TextView tvUserName;
        public ImageView mImageView;
        public CardView cardView;

        public ViewHolder(View v) {
            super(v);
            tvDetails = (TextView) v.findViewById(R.id.tvDetails);
            tvUserName = (TextView) v.findViewById(R.id.tvUserName);
            mImageView = (ImageView) v.findViewById(R.id.ivFoodPicture);
            cardView = (CardView) v.findViewById(R.id.card);
        }
    }


    public void setLockedAnimations(boolean lockedAnimations) {
        this.lockedAnimations = lockedAnimations;
    }

    public interface OnRestaurantClickListener {
        void onRestaurantClick(View view, int position);
    }
}
