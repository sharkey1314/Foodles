package com.example.sharkey.foodles.UI.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sharkey.foodles.R;
import com.example.sharkey.foodles.UI.Activity.PublishActivity;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by froger_mcs on 11.11.14.
 */
public class PhotoFiltersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<String> data;
    private static OnItemClickListener listener;
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public PhotoFiltersAdapter(Context context, List<String> data, OnItemClickListener listener) {
        this.context = context;
        this.data = data;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_location_suggestion, parent, false);
        PhotoFilterViewHolder holder = new PhotoFilterViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        String location = data.get(position);
        ((PhotoFilterViewHolder) viewHolder).tvLocation.setText(location);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class PhotoFilterViewHolder extends RecyclerView.ViewHolder{
        public TextView tvLocation;

        public PhotoFilterViewHolder(View view) {
            super(view);
            tvLocation = (TextView) view.findViewById(R.id.textView4);
            tvLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ( listener != null) {
                        listener.onItemClick(itemView, getLayoutPosition());
                    }
                }
            });
        }
    }

    public void refresh(List<String> data) {
        this.data = data;
    }
}
