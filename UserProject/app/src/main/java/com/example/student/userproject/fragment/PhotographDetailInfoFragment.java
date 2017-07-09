package com.example.student.userproject.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.student.userproject.CarouselPagerAdapter;
import com.example.student.userproject.CarouselTransformer;
import com.example.student.userproject.utility.NetworkHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.example.student.userproject.model.PhotographInfo;
import com.example.student.userproject.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class PhotographDetailInfoFragment extends Fragment implements View.OnClickListener {
    private ImageView detailAvatar;
    private ImageView imgPhone;
    private ImageView imgCamera;

    private Button sendNotification;
    private TextView detailName;
    private TextView detailAddress;
    private TextView detailCameraInfo;
    private TextView detailPhone;
    private List<PhotographInfo> detailList;
    private boolean isFavorite;

    private DatabaseReference mDatabaseGalleryRef;
    private PhotographInfo infoMap;
    private PhotographInfo infoFav;
    private String uid;
    private ImageButton imgFavorite;
    private SharedPreferences sheredPref;

    public static final String MYPREF = "my_pref";
    public static final String FAVORITE_KEY = "fav_key";

    private ViewPager viewpagerCar;
    private FrameLayout pagerLayout;
    public static final int ADAPTER_TYPE_TOP = 1;
    private FirebaseDatabase mDtabase;
    private DatabaseReference mDatabaseRef;

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
        mDatabaseGalleryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnepshot : dataSnapshot.getChildren()) {
                    final PhotographInfo info = postSnepshot.getValue(PhotographInfo.class);
                    detailList.add(info);
                }
                setupViewPager();
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        init(rootView);
        getFavoriteStatus();

        sendNotification.setOnClickListener(this);
        imgFavorite.setOnClickListener(this);

        return rootView;
    }

    private void init(View rootView) {

        viewpagerCar = (ViewPager) rootView.findViewById(R.id.view_pager_car);
        pagerLayout = (FrameLayout) rootView.findViewById(R.id.pager_layout);
        viewpagerCar.setClipChildren(false);
        viewpagerCar.setPageMargin(getResources().getDimensionPixelOffset(R.dimen.pager_margin));
        viewpagerCar.setOffscreenPageLimit(3);
        viewpagerCar.setPageTransformer(false, new CarouselTransformer(getContext()));// Set transformer);
    }

    private void setupViewPager() {
        CarouselPagerAdapter adapter = new CarouselPagerAdapter(getContext(), detailList, ADAPTER_TYPE_TOP);
        viewpagerCar.setAdapter(adapter);
        viewpagerCar.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private int index = 0;

            @Override
            public void onPageSelected(int position) {
                index = position;
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
    private void getFavoriteStatus() {
        String getFavoriteStatus = sheredPref.getString(FAVORITE_KEY + uid, "");
        isFavorite = !getFavoriteStatus.equals("");
        if(isFavorite) imgFavorite.setImageResource(R.drawable.ic_favorite_black_24dp);
        else imgFavorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
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
        sendNotification = (Button) rootView.findViewById(R.id.send_notification);
        detailAvatar = (ImageView) rootView.findViewById(R.id.img_detail_avatar);
        detailName = (TextView) rootView.findViewById(R.id.tv_detail_name);
        detailAddress = (TextView) rootView.findViewById(R.id.tv_detail_address);
        detailCameraInfo = (TextView) rootView.findViewById(R.id.tv_detail_camera_info);
        detailPhone = (TextView) rootView.findViewById(R.id.tv_detail_phone);
        imgFavorite = (ImageButton) rootView.findViewById(R.id.btn_favorite);
        imgPhone = (ImageView) rootView.findViewById(R.id.phone_img);
        imgCamera = (ImageView) rootView.findViewById(R.id.camera_img);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.send_notification:{
                NetworkHelper networkHelper = new NetworkHelper();
                networkHelper.sendNotificationRequest();
            }
            case R.id.btn_favorite:{
                if(isFavorite){
                    Log.i("==== ","Favorite --> is  to not");
                    imgFavorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    SharedPreferences.Editor editor = sheredPref.edit();
                    editor.putString(FAVORITE_KEY + uid, "");
                    isFavorite = false;
                    editor.apply();

                }else{
                    Log.i("==== ","Favorite --> not to is");
                    imgFavorite.setImageResource(R.drawable.ic_favorite_black_24dp);
                    SharedPreferences.Editor editor = sheredPref.edit();
                    editor.putString(FAVORITE_KEY + uid, uid);
                    isFavorite = true;
                    editor.apply();
                }
            }
        }
    }
}