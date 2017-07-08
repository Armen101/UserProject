package com.example.student.userproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    private ImageView imgPhone;
    private ImageView imgCamera;

    private TextView detailName;
    private TextView detailAddress;
    private TextView detailCameraInfo;
    private TextView detailPhone;
    private RecyclerView detailRecyclerView;
    private List<PhotographInfo> detailList;

    private DatabaseReference mDatabaseGalleryRef;
    private PhotographInfo infoMap;
    private PhotographInfo infoFav;
    private String uid;
    private ImageButton imgFavorite;
    private SharedPreferences sheredPref;

    public static final String MYPREF = "my_pref";
    public static final String UID = "user_uid";


    public PhotographDetailInfoFragment() {
    }

    public static PhotographDetailInfoFragment newInstance() {
        return new PhotographDetailInfoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        infoMap = Parcels.unwrap(getArguments().getParcelable("infoMap"));
        infoFav = Parcels.unwrap(getArguments().getParcelable("infoFav"));

        sheredPref = this.getActivity().getSharedPreferences(MYPREF, Context.MODE_PRIVATE);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_photograph_detail_info, container, false);
        findViewById(rootView);

        if (infoMap != null) {
            photographerInfo(infoMap);
        } else {
            photographerInfo(infoFav);
        }

        mDtabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mDtabase.getReference().child("photographs").child(uid);
        mDatabaseGalleryRef = mDatabaseRef.child("gallery");

        detailRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        detailRecyclerView.setHasFixedSize(true);

        imgFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imgFavorite.setImageResource(R.drawable.ic_favorite_black_24dp);
                SharedPreferences.Editor editor = sheredPref.edit();
                editor.putString(UID, uid);
                editor.apply();
                Toast.makeText(getActivity(), "hello ", Toast.LENGTH_SHORT).show();

            }
        });
        onCreateFirebaseRecyclerAdapter(detailRecyclerView);
        return rootView;
    }

    private void photographerInfo(PhotographInfo photographInfo) {
        detailList = new ArrayList<>();
        detailName.setText(photographInfo.getName());
        detailCameraInfo.setText(photographInfo.getCamera_info());
        detailPhone.setText(photographInfo.getPhone());
        detailAddress.setText(photographInfo.getAddress());
        uid = photographInfo.getUid();
        Glide.with(getActivity())
                .load(photographInfo.getAvatarUri())
                .into(detailAvatar);
    }

    private void findViewById(View rootView) {

        detailAvatar = (ImageView) rootView.findViewById(R.id.img_detail_avatar);
        detailName = (TextView) rootView.findViewById(R.id.tv_detail_name);
        detailAddress = (TextView) rootView.findViewById(R.id.tv_detail_address);
        detailCameraInfo = (TextView) rootView.findViewById(R.id.tv_detail_camera_info);
        detailPhone = (TextView) rootView.findViewById(R.id.tv_detail_phone);
        detailRecyclerView = (RecyclerView) rootView.findViewById(R.id.detail_recyclerView);
        imgFavorite = (ImageButton) rootView.findViewById(R.id.btn_favorite);
        imgPhone = (ImageView) rootView.findViewById(R.id.phone_img);
        imgCamera = (ImageView) rootView.findViewById(R.id.camera_img);
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