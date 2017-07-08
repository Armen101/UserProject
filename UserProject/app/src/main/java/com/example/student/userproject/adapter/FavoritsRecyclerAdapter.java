package com.example.student.userproject.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.student.userproject.R;
import com.example.student.userproject.model.PhotographInfo;

import java.util.List;


public class FavoritsRecyclerAdapter extends RecyclerView.Adapter<FavoritsRecyclerAdapter.MyViewHolder>{

    private List<PhotographInfo> list;
    private Fragment usageFragment;
    private OnItemClickFavorite mListener;
    private Context context;

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if(mListener == null){
            mListener = (OnItemClickFavorite) usageFragment;
        }
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if(mListener != null){
            mListener = null;
        }
    }

    public FavoritsRecyclerAdapter(List<PhotographInfo> list, Fragment usageFragment, Context context) {
        this.list = list;
        this.usageFragment = usageFragment;
        this.context = context;
        Log.i("=== FavRecAdapter", " list size = "+ list.size());
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.tvName.setText(list.get(position).getName());
        holder.tvPhone.setText(list.get(position).getPhone());

        Glide.with(context)
                .load(list.get(position).getAvatarUri())
                .into(holder.imgAvatar);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.getModel(list.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvPhone;
        private final ImageView imgAvatar;

        private TextView tvName;

        MyViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.person_name);
            tvPhone = (TextView) view.findViewById(R.id.person_phone);
            imgAvatar = (ImageView) view.findViewById(R.id.person_photo);
        }
    }

    public interface OnItemClickFavorite{
        void getModel(PhotographInfo model);
    }
}
