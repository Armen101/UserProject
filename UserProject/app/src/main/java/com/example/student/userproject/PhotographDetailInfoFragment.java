package com.example.student.userproject;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;


public class PhotographDetailInfoFragment extends Fragment {


    private FirebaseDatabase mDtabase;
    private DatabaseReference mDatabaseRef;

    private ImageView detailAvatar;
    private TextView detailName;
    private TextView detailAddress;
    private TextView detailCameraInfo;
    private TextView detailPhone;
    private RecyclerView detailRecyclerView;
    private List<PhotographInfo> detailList;

    public PhotographDetailInfoFragment() {
    }

    public static PhotographDetailInfoFragment newInstance() {
        PhotographDetailInfoFragment fragment = new PhotographDetailInfoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO  set information data to UI elements
        PhotographInfo info = Parcels.unwrap(getArguments().getParcelable("info"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_photograph_detail_info, container, false);
        findViewById(rootView);

        detailList = new ArrayList<>();

        mDtabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mDtabase.getReference().child("photographs");  // TODO get specific fotograph images


        detailRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        detailRecyclerView.setHasFixedSize(true);

        onCreateFirebaseRecyclerAdapter(detailRecyclerView);
        return rootView;
    }

    private void findViewById(View rootView) {
        detailAvatar = (ImageView) rootView.findViewById(R.id.img_detail_avatar);
        detailName = (TextView) rootView.findViewById(R.id.tv_detail_name);
        detailAddress = (TextView) rootView.findViewById(R.id.tv_detail_address);
        detailCameraInfo = (TextView) rootView.findViewById(R.id.tv_detail_camera_info);
        detailPhone = (TextView) rootView.findViewById(R.id.tv_detail_phone);
        detailRecyclerView = (RecyclerView) rootView.findViewById(R.id.detail_recyclerView);


    }

    private void onCreateFirebaseRecyclerAdapter(RecyclerView recyclerView) {
        final FirebaseRecyclerAdapter<PhotographInfo, PhotographDetailInfoFragment.MyViewHolder> adapter = new FirebaseRecyclerAdapter<PhotographInfo,
                PhotographDetailInfoFragment.MyViewHolder>(
                PhotographInfo.class,
                R.layout.detail_recycler_row_item,
                PhotographDetailInfoFragment.MyViewHolder.class,
                mDatabaseRef

        ) {
            @Override
            protected void populateViewHolder(PhotographDetailInfoFragment.MyViewHolder viewHolder, PhotographInfo model, final int position) {
                viewHolder.galleryImageName.setText(model.getImageName());

                Glide.with(getActivity())
                        .load(model.getImageUri())
                        .into(viewHolder.imgGallery);
                detailList.add(model);
            }
        };

        recyclerView.setAdapter(adapter);

    }

    private static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imgGallery;
        TextView galleryImageName;


        public MyViewHolder(View view) {
            super(view);
            galleryImageName = (TextView) view.findViewById(R.id.title_image_gallery);
            imgGallery = (ImageView) view.findViewById(R.id.gallery_img);

        }


    }

    private void detailPhotographInfo() {


        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnepshot : dataSnapshot.getChildren()) {
                    PhotographInfo info = postSnepshot.getValue(PhotographInfo.class);

                    String name = dataSnapshot.child("name").getValue(String.class);
                    String address = dataSnapshot.child("address").getValue(String.class);
                    String cameraInfo = dataSnapshot.child("cameraInfo").getValue(String.class);
                    String phone = dataSnapshot.child("phone").getValue(String.class);
                    detailName.setText(name);
                    detailAddress.setText(address);
                    detailCameraInfo.setText(cameraInfo);
                    detailPhone.setText(phone);


                    detailList.add(info);

                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

}
