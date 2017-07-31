package com.example.student.userproject.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.student.userproject.R;
import com.example.student.userproject.model.PhotographInfo;
import com.example.student.userproject.utility.Constants;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;

public class RatingFragment extends Fragment {

    private DatabaseReference mDatabaseRef;
    private List<PhotographInfo> photograpsList;
    private List<String> photograpsDetailList;
    private PhotographInfo info;
    private ArrayList<String> xAxis;

    private Map<String, Long> photographersRatings = new HashMap<>();
    private String name;

    public RatingFragment() {
        // Required empty public constructor
    }

    public static RatingFragment newInstance() {
        return new RatingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_rating, container, false);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabaseRef = database.getReference().child(Constants.PHOTOGRAPHS);
        photograpsList = new ArrayList<>();
        photograpsDetailList = new ArrayList<>();
        photographers();
        HorizontalBarChart chart = (HorizontalBarChart) rootView.findViewById(R.id.bar_chart_horizontal);

        BarData data = new BarData(getXAxisValues(), getDataSet());
        chart.setData(data);
        chart.animateXY(2000, 2000);
        chart.invalidate();

        return rootView;
    }

    private ArrayList<BarDataSet> getDataSet() {
        ArrayList<BarDataSet> dataSets = null;

        ArrayList<BarEntry> valueSet1 = new ArrayList<>();
        BarEntry v1e1 = new BarEntry(100.000f, 4);
        valueSet1.add(v1e1);
        BarEntry v1e2 = new BarEntry(80.000f, 3); // Feb
        valueSet1.add(v1e2);
        BarEntry v1e3 = new BarEntry(60.000f, 2); // Mar
        valueSet1.add(v1e3);
        BarEntry v1e4 = new BarEntry(40.000f, 1); // Apr
        valueSet1.add(v1e4);
        BarEntry v1e5 = new BarEntry(20.000f, 0); // May
        valueSet1.add(v1e5);

        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "Brand 1");
        barDataSet1.setColor(Color.rgb(0, 155, 0));

//        ArrayList<BarDataSet> dataSets = new ArrayList<>();
//        dataSets.add(barDataSet1);dataSets = new ArrayList<>();
        dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);

        return dataSets;
    }

    private ArrayList<String> getXAxisValues() {
        try {
            Thread.sleep(10000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.i("sssssssssssssss", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        xAxis = new ArrayList<>();
        xAxis.add("aasasasas");
        xAxis.add("aasasasas");
        xAxis.add("aasasasas");
        xAxis.add("aasasasas");
        xAxis.add("aasasasas");
        return xAxis;

    }
//    private ArrayList<String> getXAxisValues() {
//        ArrayList<String> xAxis = new ArrayList<>();
//        int n = 5;
//        List<Map.Entry<String, Long>> greatest = findGreatest(photographersRatings, n);
//        System.out.println("Top " + n + " entries:");
//
//        for (Map.Entry<String, Long> entry : greatest) {
//            xAxis.add(entry.getKey());
//            getUserByUid(entry.getKey());
//            Log.i("sssssssss", "   aaaaaa     " + name);
//        }
//        xAxis.add("jann");
//        xAxis.add("jann");
//        xAxis.add("jann");
//        xAxis.add("jann");
//        xAxis.add("jann");
//        return xAxis;
//    }

    private String getUserByUid(final String uid) {
        mDatabaseRef.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("name").getValue(String.class);
                Log.i("sssssssss", "   aaaaaa     " + name + "     " + uid);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return name;
    }

    private void photographers() {
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    info = postSnapshot.getValue(PhotographInfo.class);
                    photographersRatings.put(info.getUid(), Long.valueOf(info.getRating()));
                    Log.i("ssssssssssssssss", "aaaaaaaaa " + info.getUid() + "    " + info.getRating());
                    getUserByUid(info.getUid());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private <K, V extends Comparable<? super V>> List<Map.Entry<K, V>> findGreatest(Map<K, V> map, int n) {
        Comparator<? super Map.Entry<K, V>> comparator =
                new Comparator<Map.Entry<K, V>>() {
                    @Override
                    public int compare(Map.Entry<K, V> e0, Map.Entry<K, V> e1) {
                        V v0 = e0.getValue();
                        V v1 = e1.getValue();
                        return v0.compareTo(v1);
                    }
                };
        PriorityQueue<Map.Entry<K, V>> highest =
                new PriorityQueue<Map.Entry<K, V>>(n, comparator);
        for (Map.Entry<K, V> entry : map.entrySet()) {
            highest.offer(entry);
            while (highest.size() > n) {
                highest.poll();
            }
        }

        List<Map.Entry<K, V>> result = new ArrayList<Map.Entry<K, V>>();
        while (highest.size() > 0) {
            result.add(highest.poll());
        }
        return result;
    }
}