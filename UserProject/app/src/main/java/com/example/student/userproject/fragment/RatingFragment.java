package com.example.student.userproject.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.student.userproject.R;
import com.example.student.userproject.adapter.RatingRecyclerAdapter;
import com.example.student.userproject.model.PhotographInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.student.userproject.utility.Constants.PHOTOGRAPHS;
import static com.example.student.userproject.utility.Constants.RATING_UID;
import static com.example.student.userproject.utility.Constants.R_UID1;
import static com.example.student.userproject.utility.Constants.R_UID2;
import static com.example.student.userproject.utility.Constants.R_UID3;
import static com.example.student.userproject.utility.Constants.R_UID4;
import static com.example.student.userproject.utility.Constants.R_UID5;

public class RatingFragment extends Fragment {

    private List<PhotographInfo> ratingList;
    private String uid1;
    private String uid2;
    private String uid3;
    private String uid4;
    private String uid5;
    private PhotographInfo info;


    public RatingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(RATING_UID, Context.MODE_PRIVATE);
        uid1 = sharedPreferences.getString(R_UID1, "");
        uid2 = sharedPreferences.getString(R_UID2, "");
        uid3 = sharedPreferences.getString(R_UID3, "");
        uid4 = sharedPreferences.getString(R_UID4, "");
        uid5 = sharedPreferences.getString(R_UID5, "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_rating, container, false);
        RecyclerView ratingRecyclerView = (RecyclerView) rootView.findViewById(R.id.rating_recycler_view);
        LinearLayoutManager rlm = new LinearLayoutManager(getActivity());
        ratingRecyclerView.setLayoutManager(rlm);
        ratingRecyclerView.setHasFixedSize(true);
        DatabaseReference rDatabaseReference = FirebaseDatabase.getInstance().getReference().child(PHOTOGRAPHS);
        ratingList = new ArrayList<>();


        rDatabaseReference.child(uid1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                info = dataSnapshot.getValue(PhotographInfo.class);
                Log.i("sssssssssssssss",info.getName());
                Log.i("sssssssssssssss", String.valueOf(info.getRating()));
                Log.i("sssssssssssssss",info.getAvatarUri());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ratingRecyclerView.setAdapter(new RatingRecyclerAdapter(ratingList, getActivity()));
        return rootView;
    }
}