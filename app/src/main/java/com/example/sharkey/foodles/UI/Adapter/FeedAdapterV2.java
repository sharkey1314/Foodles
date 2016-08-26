package com.example.sharkey.foodles.UI.Adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.example.sharkey.foodles.R;
import com.example.sharkey.foodles.UI.API.model.Recommendation;
import com.example.sharkey.foodles.UI.Activity.MainActivity3;
import com.example.sharkey.foodles.UI.Views.LoadingFeedItemView;
import com.example.sharkey.foodles.UI.utils.RoundedTransformation;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sharkey on 20/7/16.
 */
public class FeedAdapterV2 extends FirebaseAdapter {

    public static final String ACTION_LIKE_BUTTON_CLICKED = "action_like_button_button";
    public static final String ACTION_LIKE_IMAGE_CLICKED = "action_like_image_button";

    public static final int VIEW_TYPE_DEFAULT = 1;
    public static final int VIEW_TYPE_LOADER = 2;

    //private final List<FeedItem> feedItems = new ArrayList<>();

    public Context context;
    private OnFeedItemClickListener onFeedItemClickListener;

    private boolean showLoadingView = false;


    public FeedAdapterV2 (Context context, DatabaseReference mRef) {
        super(mRef);
        this.context = context;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_DEFAULT) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_feed2, parent, false);
            CellFeedViewHolder cellFeedViewHolder = new CellFeedViewHolder(view);
            setupClickableViews(view, cellFeedViewHolder);
            return cellFeedViewHolder;
        } else if (viewType == VIEW_TYPE_LOADER) {
            LoadingFeedItemView view = new LoadingFeedItemView(context);
            view.setLayoutParams(new LinearLayoutCompat.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            );
            return new LoadingCellFeedViewHolder(view);
        }

        return null;
    }

    private void setupClickableViews(final View view, final CellFeedViewHolder cellFeedViewHolder) {
        cellFeedViewHolder.btnComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFeedItemClickListener.onCommentsClick(view, cellFeedViewHolder.getAdapterPosition());
            }
        });
        cellFeedViewHolder.btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFeedItemClickListener.onMoreClick(v, cellFeedViewHolder.getAdapterPosition());
            }
        });
        /*cellFeedViewHolder.ivFeedCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = cellFeedViewHolder.getAdapterPosition();
                long count = feedItems.get(adapterPosition).likes_count;
                feedItems.get(feedItems.size()-adapterPosition-1).likes_count = count + 1;
                updateLikesCount(adapterPosition, count+1);
                notifyItemChanged(adapterPosition, ACTION_LIKE_IMAGE_CLICKED);
                if (context instanceof MainActivity3) {
                    ((MainActivity3) context).showLikedSnackbar();
                }
            }
        });*/
        /*cellFeedViewHolder.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = cellFeedViewHolder.getAdapterPosition();
                long count = feedItems.get(adapterPosition).likes_count;
                feedItems.get(feedItems.size()-adapterPosition-1).likes_count = count + 1;
                updateLikesCount(adapterPosition, count+1);
                notifyItemChanged(adapterPosition, ACTION_LIKE_BUTTON_CLICKED);
                if (context instanceof MainActivity3) {
                    ((MainActivity3) context).showLikedSnackbar();
                }
            }
        });*/
        cellFeedViewHolder.ivUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFeedItemClickListener.onProfileClick(view);
            }
        });
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ((CellFeedViewHolder) viewHolder).bindView(feedItems.get(feedItems.size()-position-1));

        if (getItemViewType(position) == VIEW_TYPE_LOADER) {
            bindLoadingFeedItem((LoadingCellFeedViewHolder) viewHolder);
        }
    }



    public void setOnFeedItemClickListener(OnFeedItemClickListener onFeedItemClickListener) {
        this.onFeedItemClickListener = onFeedItemClickListener;
    }


    private void bindLoadingFeedItem(final LoadingCellFeedViewHolder holder) {
        holder.loadingFeedItemView.setOnLoadingFinishedListener(new LoadingFeedItemView.OnLoadingFinishedListener() {
            @Override
            public void onLoadingFinished() {
                showLoadingView = false;
                notifyItemChanged(0);
            }
        });
        holder.loadingFeedItemView.startLoading();
    }

    @Override
    public int getItemViewType(int position) {
        if (showLoadingView && position == 0) {
            return VIEW_TYPE_LOADER;
        } else {
            return VIEW_TYPE_DEFAULT;
        }
    }

    public void updateItems(boolean animated) {
        if (animated) {
            notifyItemRangeInserted(0, feedItems.size());
        } else {
            notifyDataSetChanged();
        }
    }

    public void showLoadingView() {
        showLoadingView = true;
        notifyItemChanged(0);
    }

    public class CellFeedViewHolder extends RecyclerView.ViewHolder {

        ImageView ivFeedCenter;
        TextView ivFeedBottom;
        ImageButton btnComments;
        //ImageButton btnLike;
        ImageButton btnMore;
        View vBgLike;
        //ImageView ivLike;
        //TextSwitcher tsLikesCounter;
        ImageView ivUserProfile;
        FrameLayout vImageRoot;
        TextView ivUserName;

        Recommendation feedItem;

        public CellFeedViewHolder(View view) {
            super(view);
            ivFeedCenter = (ImageView) view.findViewById(R.id.ivFeedCenter);
            ivFeedCenter.setRotation(90);
            // replace with caption textview
            ivFeedBottom = (TextView) view.findViewById(R.id.ivFeedBottom);
            btnComments = (ImageButton) view.findViewById(R.id.btnComments);
            //btnLike = (ImageButton) view.findViewById(R.id.btnLike);
            btnMore = (ImageButton) view.findViewById(R.id.btnMore);
            vBgLike = view.findViewById(R.id.vBgLike);
            //ivLike = (ImageView) view.findViewById(R.id.ivLike);
            //tsLikesCounter = (TextSwitcher) view.findViewById(R.id.tsLikesCounter);
            ivUserProfile = (ImageView) view.findViewById(R.id.ivUserProfile);
            vImageRoot = (FrameLayout) view.findViewById(R.id.vImageRoot);
            ivUserName = (TextView) view.findViewById(R.id.ivUserName);
            ivUserProfile.setVisibility(View.GONE);
        }

        public void bindView(final Recommendation feedItem) {
            // code to parse feedItem and update the views respectively
            this.feedItem = feedItem;
            Picasso.with(context)
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
                            Picasso.with(context)
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
        public Recommendation getFeedItem() {
            return feedItem;
        }
    }

    public class LoadingCellFeedViewHolder extends CellFeedViewHolder {

        LoadingFeedItemView loadingFeedItemView;

        public LoadingCellFeedViewHolder(LoadingFeedItemView view) {
            super(view);
            this.loadingFeedItemView = view;
        }

        @Override
        public void bindView(Recommendation feedItem) {
            super.bindView(feedItem);
        }
    }


    public interface OnFeedItemClickListener {
        void onCommentsClick(View v, int position);

        void onMoreClick(View v, int position);

        void onProfileClick(View v);
    }
}
