package com.example.student.userproject.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.student.userproject.R;
import com.example.student.userproject.model.PhotographInfo;

import java.util.List;

public class RatingRecyclerAdapter extends RecyclerView.Adapter<RatingRecyclerAdapter.MyViewHolder> {

    private List<PhotographInfo> list;
    private Context context;

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public RatingRecyclerAdapter(List<PhotographInfo> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public RatingRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row_rating, parent, false);

        return new RatingRecyclerAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RatingRecyclerAdapter.MyViewHolder holder, final int position) {

        holder.tvRatingName.setText(list.get(position).getName());
//                viewHolder.tvRatingRating.setText(model.getRating());
        holder.countRating.setProgress(list.get(position).getRating());
//
        Glide.with(context)
                .load(list.get(position).getAvatarUri())
                .into(holder.avatarRating);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvRatingName;
        private TextView tvRatingRating;
        private ImageView avatarRating;
        private RatingBar countRating;

        MyViewHolder(View view) {
            super(view);
            tvRatingName = (TextView) itemView.findViewById(R.id.tv_rating_name);
            tvRatingRating = (TextView) itemView.findViewById(R.id.tv_rating_rating);
            avatarRating = (ImageView) itemView.findViewById(R.id.avatar_rating);
            countRating = (RatingBar) itemView.findViewById(R.id.rb_rating_count);
        }
    }
}
