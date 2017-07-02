package com.example.student.userproject;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.arsy.maps_library.MapRipple;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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

    private DatabaseReference mDatabaseRef;
    private List<PhotographInfo> photograpsList;
    private FirebaseDatabase mDtabase;
    private GoogleMap mMap;
    private MapRipple mapRipple;
    private BroadcastReceiver mBroadcastReceiver;
    private double currentLat;
    private double currentLng;
    private Location location;
    private Location mLocation;
    private Marker currentMarker;

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    public MapFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 10);
            }
            return;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootV= inflater.inflate(R.layout.fragment_map, container, false);
        mDtabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mDtabase.getReference().child("photographs");
        photograpsList = new ArrayList<>();
        Intent i = new Intent(getActivity(), LocationService.class);
        getActivity().startService(i);

        return rootV;
    }

    private void allPhotographs() {
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnepshot : dataSnapshot.getChildren()) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                            android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    }
                    final PhotographInfo info = postSnepshot.getValue(PhotographInfo.class);
                    location = new Location("provider");
                    double latitude = info.getLatitude();
                    double longitude = info.getLongitude();
                    LatLng latLng = new LatLng(latitude, longitude);

                    mLocation = new Location("provider");
                    mLocation.setLatitude(currentLat);
                    mLocation.setLongitude(currentLng);

                    location.setLatitude(latitude);
                    location.setLongitude(longitude);

                    if (location.distanceTo(mLocation) >= 5000) {
                        Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title(info.getName()));
                        marker.setTag(info);
                        // TODO add  avartars to the map like a marker
                        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                            @Override
                            public void onInfoWindowClick(Marker marker) {
                                // TODO start detail fragment
                                Bundle bundle = new Bundle();
                                bundle.putParcelable("info",  Parcels.wrap(marker.getTag()));
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                PhotographDetailInfoFragment fr = new PhotographDetailInfoFragment();
                                fr.setArguments(bundle);
                                fragmentManager.beginTransaction()
                                        .replace(R.id.container,fr)
                                        .addToBackStack(null)
                                        .commit();
                            }
                        });
//                        Bitmap theBitmap = null;
//                        try {
//                            theBitmap = Glide.
//                                    with(getActivity()).
//                                    load(info.getAvatarUri()).
//                                    asBitmap().
//                                    into(100, 100). // Width and height
//                                    get();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        } catch (ExecutionException e) {
//                            e.printStackTrace();
//                        }
//                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(theBitmap));
                    }
                    photograpsList.add(info);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    @Override
     public void onResume() {
        super.onResume();
        if(mBroadcastReceiver == null) {
            mBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    currentLat = (double) intent.getExtras().get("lat");
                    currentLng = (double) intent.getExtras().get("lng");
                    if (mMap != null) {
                        if (currentMarker != null) {
                            currentMarker.remove();
                        }
                        LatLng currentPosition = new LatLng(currentLat, currentLng);
                        currentMarker = mMap.addMarker(new MarkerOptions().position(currentPosition).title("Marker in Sydney"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentPosition));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(13), 2000, null);
                        mapRipple = new MapRipple(mMap, currentPosition, getActivity());
                        mapRipple.withNumberOfRipples(3);
                        mapRipple.withFillColor(Color.BLUE);
                        mapRipple.withStrokeColor(Color.BLACK);
                        mapRipple.withStrokewidth(10);      // 10dp
                        mapRipple.withDistance(5000);      // 2000 metres radius
                        mapRipple.withRippleDuration(12000);    //12000ms
                        mapRipple.withTransparency(0.5f);
                        mapRipple.startRippleMapAnimation();
                    }
                }
            };
        }
        getActivity().registerReceiver(mBroadcastReceiver, new IntentFilter("LOCATION_UPDATE"));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment fragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);
        fragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        allPhotographs();
        mMap = googleMap;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mBroadcastReceiver != null) {
            getActivity().unregisterReceiver(mBroadcastReceiver);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 10: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    Toast.makeText(getActivity(), "Permissions is enabled", Toast.LENGTH_LONG).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
