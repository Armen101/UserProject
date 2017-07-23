package com.example.student.userproject.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arsy.maps_library.MapRipple;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.student.userproject.R;
import com.example.student.userproject.activity.HomeActivity;
import com.example.student.userproject.model.PhotographInfo;
import com.example.student.userproject.service.LocationService;
import com.example.student.userproject.utility.Constants;
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
    private Intent serviceIntent;
    private SupportMapFragment fragmentMap;
    private Location location;
    private Location mLocation;
    private LatLng currentPosition;
    private double currentLat;
    private double currentLng;
    private Marker currentMarker;
    private Bitmap bmp;
    private Marker marker;
    private boolean isFirstLocationDetection = true;
    private boolean ok = false;
    private SharedPreferences okey;
    private SharedPreferences shared;
    private String uid;

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
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        FirebaseDatabase dtabase = FirebaseDatabase.getInstance();
        mDatabaseRef = dtabase.getReference().child(Constants.PHOTOGRAPHS);
        photograpsList = new ArrayList<>();

        okey = getActivity().getSharedPreferences("OK", Context.MODE_PRIVATE);
        ok = okey.getBoolean("OK", false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentMap = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);
        fragmentMap.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        listenLocationChanges();
        if (ok) {
            getPhotographer();
        } else {
            getAllPhotographsNearly();
        }
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
        if (shared != null && okey != null) {
            shared.edit().remove(uid).apply();
            okey.edit().putBoolean("OK", false).apply();
        }
        super.onDestroy();
        if (mBroadcastReceiver != null) {
            getActivity().unregisterReceiver(mBroadcastReceiver);
        }
        mMap.clear();
        getActivity().stopService(serviceIntent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            FragmentTransaction ft = getActivity().getSupportFragmentManager()
                    .beginTransaction();
            ft.remove(fragmentMap);
            ft.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        bundle.putDouble("lat", currentLat);
        bundle.putDouble("lng", currentLng);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        PhotographDetailInfoFragment fr = new PhotographDetailInfoFragment();
        fr.setArguments(bundle);
        fragmentManager.beginTransaction()
                .replace(R.id.container, fr, "DETAIL_FRAGMENT")
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
                mMap.clear();
                for (DataSnapshot postSnepshot : dataSnapshot.getChildren()) {
                    final PhotographInfo info = postSnepshot.getValue(PhotographInfo.class);
                    final LatLng latLng = getLatLng(info);
                    if (location.distanceTo(mLocation) >= DISTANCE) {
                        marker = mMap.addMarker(
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

    private void getPhotographer() {
        shared = getActivity().getSharedPreferences("PHOTOGRAPHER_UID", Context.MODE_PRIVATE);
        uid = shared.getString("uid", "");
        mDatabaseRef.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mMap.clear();
                PhotographInfo info = dataSnapshot.getValue(PhotographInfo.class);
                LatLng latLng = getLatLng(info);
                marker = mMap.addMarker(new MarkerOptions().position(latLng).title(info.getName()));
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
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
