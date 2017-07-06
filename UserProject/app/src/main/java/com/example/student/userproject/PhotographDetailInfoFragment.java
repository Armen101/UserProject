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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    private DatabaseReference mDatabaseGalleryRef;
    private PhotographInfo info;
    private String uid;

    public PhotographDetailInfoFragment() {
    }

    public static PhotographDetailInfoFragment newInstance() {
        return new PhotographDetailInfoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO  set information data to UI elements
        info = Parcels.unwrap(getArguments().getParcelable("info"));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_photograph_detail_info, container, false);
        findViewById(rootView);


        detailList = new ArrayList<>();
        detailName.setText(info.getName());
        detailCameraInfo.setText(info.getCamera_info());
        detailPhone.setText(info.getPhone());
        detailAddress.setText(info.getAddress());
        uid = info.getUid();

        mDtabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mDtabase.getReference().child("photographs").child(uid);  // TODO get specific fotograph images
        mDatabaseGalleryRef = mDatabaseRef.child("gallery");

        Glide.with(getActivity())
                .load(info.getAvatarUri())
                .into(detailAvatar);

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
        final FirebaseRecyclerAdapter<PhotographInfo, MyViewHolder> adapter = new FirebaseRecyclerAdapter<PhotographInfo, MyViewHolder>(
                PhotographInfo.class,
                R.layout.detail_recycler_row_item,
                MyViewHolder.class,
                mDatabaseGalleryRef

        ) {
            @Override
            protected void populateViewHolder(MyViewHolder viewHolder, PhotographInfo model, final int position) {
                viewHolder.galleryImageTitle.setText(model.getTitle());

                Glide.with(getActivity())
                        .load(model.getImageUri())
                        .into(viewHolder.imgGallery);
                detailList.add(model);
            }
        };
        recyclerView.setAdapter(adapter);
    }

    private static class MyViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgGallery;
        private final TextView galleryImageTitle;

        public MyViewHolder(View view) {
            super(view);
            galleryImageTitle = (TextView) view.findViewById(R.id.title_image_gallery);
            imgGallery = (ImageView) view.findViewById(R.id.gallery_img);
        }
    }
}