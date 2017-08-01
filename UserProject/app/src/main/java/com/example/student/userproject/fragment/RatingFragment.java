package com.example.student.userproject.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.student.userproject.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RatingFragment extends Fragment {

    private DatabaseReference rDatabaseReference;


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
        RecyclerView ratingRecyclerView = (RecyclerView)rootView.findViewById(R.id.rating_recycler_view);
        LinearLayoutManager rlm = new LinearLayoutManager(getActivity());
        rlm.setReverseLayout(true);
        rlm.setStackFromEnd(true);
        ratingRecyclerView.setLayoutManager(rlm);
        ratingRecyclerView.setHasFixedSize(true);
        rDatabaseReference = FirebaseDatabase.getInstance().getReference();
        return rootView;
    }


}