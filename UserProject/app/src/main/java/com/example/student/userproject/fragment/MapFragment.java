package com.example.student.userproject.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arsy.maps_library.MapRipple;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.student.userproject.service.LocationService;
import com.example.student.userproject.model.PhotographInfo;
import com.example.student.userproject.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final long MAP_RADAR_ANIMATION_DURATION = 12000;
    private static final float ZOOM_NUMBER = 13;
    private static final int MAP_ANIMATION_DURATION = 2000;
    private static final float DISTANCE = 5000;

    private DatabaseReference mDatabaseRef;
    private List<PhotographInfo> photograpsList;
    private GoogleMap mMap;
    private MapRipple mapRipple;
    private BroadcastReceiver mBroadcastReceiver;
    private double currentLat;
    private double currentLng;
    private Location location;
    private Location mLocation;
    private Marker currentMarker;
    private Bitmap bmp;
    private LatLng currentPosition;
    private boolean isFirstLocationDetection = true;
    private Intent serviceIntent;

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    public MapFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviceIntent = new Intent(getActivity(), LocationService.class);
        getActivity().startService(serviceIntent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootV = inflater.inflate(R.layout.fragment_map, container, false);
        FirebaseDatabase dtabase = FirebaseDatabase.getInstance();
        mDatabaseRef = dtabase.getReference().child("photographs");
        photograpsList = new ArrayList<>();
        return rootV;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment fragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);
        fragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        listenLocationChanges();
        getAllPhotographsNearly();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mapRipple != null && mapRipple.isAnimationRunning()) {
            mapRipple.stopRippleMapAnimation();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBroadcastReceiver != null) {
            getActivity().unregisterReceiver(mBroadcastReceiver);
        }
        mMap.clear();
        getActivity().stopService(serviceIntent);
    }

    private void initMapRadar(LatLng currentPosition) {
        mapRipple = new MapRipple(mMap, currentPosition, getActivity());
        mapRipple.withNumberOfRipples(1);
        mapRipple.withFillColor(Color.BLUE);
        mapRipple.withStrokeColor(Color.BLACK);
        mapRipple.withStrokewidth(10);
        mapRipple.withDistance(DISTANCE);
        mapRipple.withRippleDuration(MAP_RADAR_ANIMATION_DURATION);
        mapRipple.withTransparency(0.5f);
        mapRipple.startRippleMapAnimation();
    }

    private void startDetailFragment(Marker marker) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("userInfo", Parcels.wrap(marker.getTag()));
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        PhotographDetailInfoFragment fr = new PhotographDetailInfoFragment();
        fr.setArguments(bundle);
        fragmentManager.beginTransaction()
                .replace(R.id.container, fr)
                .addToBackStack(null)
                .commit();
    }

    private void listenLocationChanges() {
        if (mBroadcastReceiver == null) {
            mBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    currentLat = (double) intent.getExtras().get("lat");
                    currentLng = (double) intent.getExtras().get("lng");
                    if (mMap != null) {
                        if (currentMarker != null) {
                            currentMarker.remove();
                        }
                        currentPosition = new LatLng(currentLat, currentLng);
                        currentMarker = mMap.addMarker(
                                new MarkerOptions().position(currentPosition));
                        if (isFirstLocationDetection) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentPosition));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(ZOOM_NUMBER),
                                    MAP_ANIMATION_DURATION, null);
                            isFirstLocationDetection = false;
                        }
                        if (mapRipple == null) {
                            initMapRadar(currentPosition);
                        } else if (!mapRipple.isAnimationRunning()) {
                            mapRipple.startRippleMapAnimation();
                        }
                        mapRipple.withLatLng(currentPosition);
                    }
                }
            };
        }
        getActivity().registerReceiver(mBroadcastReceiver, new IntentFilter("LOCATION_UPDATE"));
    }

    private void getAllPhotographsNearly() {
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnepshot : dataSnapshot.getChildren()) {
                    final PhotographInfo info = postSnepshot.getValue(PhotographInfo.class);
                    final LatLng latLng = getLatLng(info);
                    if (location.distanceTo(mLocation) >= DISTANCE) {
                        final Marker marker = mMap.addMarker(
                                new MarkerOptions().position(latLng).title(info.getName()));
                        marker.setTag(info);
                        mMap.setOnInfoWindowClickListener(
                                new GoogleMap.OnInfoWindowClickListener() {
                                    @Override
                                    public void onInfoWindowClick(Marker marker) {
                                        startDetailFragment(marker);
                                    }
                                });
                        getPhotographAvatar(info, marker);
                    }
                    photograpsList.add(info);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    @NonNull
    private LatLng getLatLng(PhotographInfo info) {
        location = new Location("provider");
        double latitude = info.getLatitude();
        double longitude = info.getLongitude();
        final LatLng latLng = new LatLng(latitude, longitude);
        mLocation = new Location("provider");
        mLocation.setLatitude(currentLat);
        mLocation.setLongitude(currentLng);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return latLng;
    }

    private void getPhotographAvatar(final PhotographInfo info, final Marker marker) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Glide.with(getActivity())
                                .load(info.getAvatarUri())
                                .asBitmap()
                                .fitCenter()
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(Bitmap resource,
                                                                GlideAnimation<? super Bitmap> glideAnimation) {
                                        bmp = Bitmap.createScaledBitmap(resource, 80, 80, false);
                                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(bmp));
                                    }
                                });
                    }
                });
            }
        }).start();


    }

}
