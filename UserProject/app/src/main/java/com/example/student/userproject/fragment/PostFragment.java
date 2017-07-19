package com.example.student.userproject.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.student.userproject.R;
import com.example.student.userproject.model.PostModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PostFragment extends Fragment {

    private List<PostModel> postModelList;
    private RecyclerView postRecyclerView;
    private DatabaseReference mDatabaseRef;
    private String uid;



    public PostFragment() {

    }

    public static PostFragment newInstance() {

        return new PostFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_post, container, false);



        initPostModelList();
        PostModel model = new PostModel();
        uid = model.getUid();


        postRecyclerView = (RecyclerView) rootView.findViewById(R.id.post_recycler_view);

        LinearLayoutManager lm = new LinearLayoutManager(getActivity());
        postRecyclerView.setLayoutManager(lm);
        postRecyclerView.setHasFixedSize(true);

        FirebaseRecyclerAdapter<PostModel, PostHolder> postAdapter = new FirebaseRecyclerAdapter<PostModel, PostHolder>(
                PostModel.class,
                R.layout.post_recycler_row_item,
                PostHolder.class,
                mDatabaseRef
        ) {
            @Override
            protected void populateViewHolder(PostHolder viewHolder, final PostModel model, int position) {
                viewHolder.tvUserName.setText(model.getName());
//                viewHolder.tvLikesCount.setText(model.getLikesCount());
//                viewHolder.tvPostTime.setText(model.getTime().toString());
//                viewHolder.tvPostId.setText(model.getPostId());
                viewHolder.tvPostTitle.setText(model.getTitle());
//                viewHolder.tvLikesCount.setText(model.getLikesCount());
                viewHolder.imgLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateNumLikes(model.getUid());
                        Toast.makeText(getActivity(), "hello", Toast.LENGTH_SHORT).show();
                    }
                });

                Glide.with(getActivity())
                        .load(model.getAvatarUri())
                        .into(viewHolder.imgPost);


            }

        };
        postRecyclerView.setAdapter(postAdapter);

        return rootView;
    }

    private void updateNumLikes(String uid) { // likeri qanakna update anum
        mDatabaseRef.child(uid).child("likesCount")
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        long num = (long) mutableData.getValue();
                        num++;
                        mutableData.setValue(num);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                    }
                });
    }


    private void initPostModelList() {
        postModelList = new ArrayList<>();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("photographs");

        DatabaseReference mDatabaseGalleryRef = mDatabaseRef.child("gallery");
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnepshot : dataSnapshot.getChildren()) {
                    final PostModel info = postSnepshot.getValue(PostModel.class);
                    postModelList.add(info);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static class PostHolder extends RecyclerView.ViewHolder {

        TextView tvUserName;
        TextView tvPostId;
        TextView tvPostTitle;
        TextView tvPostTime;
        TextView tvLikesCount;
        ImageView imgPost;
        ImageView imgLike;

        public PostHolder(View itemView) {
            super(itemView);

            tvUserName = (TextView) itemView.findViewById(R.id.tv_post_username);
            tvPostId = (TextView) itemView.findViewById(R.id.tv_post_id);
            tvPostTitle = (TextView) itemView.findViewById(R.id.tv_post_title);
            tvPostTime = (TextView) itemView.findViewById(R.id.tv_post_time);
            tvLikesCount = (TextView) itemView.findViewById(R.id.tv_likes_count);
            imgPost = (ImageView) itemView.findViewById(R.id.post_image);
            imgLike = (ImageView)itemView.findViewById(R.id.img_like);
        }
    }
}
