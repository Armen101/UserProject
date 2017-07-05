package com.example.student.userproject;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
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


// TODO correct card item UI (chases im emulyatrov lava)
public class FavoriteFragment extends Fragment {

    private DatabaseReference mDatabaseRef;
    private List<PhotographInfo> photograpsList;
    private RecyclerView recyclerView;
    private FirebaseDatabase mDtabase;


    public FavoriteFragment() {
    }

    public static FavoriteFragment newInstance() {
        FavoriteFragment fragment = new FavoriteFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_favorite, container, false);

        mDtabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mDtabase.getReference().child("photographs");
        photograpsList = new ArrayList<>();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv);
        LinearLayoutManager lm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(lm);

        recyclerView.setHasFixedSize(true);

        onCreateFirebaseRecyclerAdapter(recyclerView);
        recyclerView.addOnItemTouchListener(new RecyclerViewHelper(getActivity().getApplicationContext(), recyclerView,
                new RecyclerViewHelper.RecyclerViewClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("info", Parcels.wrap(photograpsList.get(position)));

                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        PhotographDetailInfoFragment fr = new PhotographDetailInfoFragment();
                        fr.setArguments(bundle);
                        fragmentManager.beginTransaction()
                                .replace(R.id.container,fr)
                                .addToBackStack(null)
                                .commit();
                    }

                    @Override
                    public void onLongClick(View view, int position) {
                        photograpsList.remove(position);

                    }
                }));

        return rootView;
    }


    private void onCreateFirebaseRecyclerAdapter(RecyclerView recyclerView) {
        final FirebaseRecyclerAdapter<PhotographInfo, MyViewHolder> adapter = new FirebaseRecyclerAdapter<PhotographInfo, MyViewHolder>(
                PhotographInfo.class,
                R.layout.recycler_row_item,
                MyViewHolder.class,
                mDatabaseRef

        ) {
            @Override
            protected void populateViewHolder(MyViewHolder viewHolder, PhotographInfo model, final int position) {
                viewHolder.tvName.setText(model.getName());
                viewHolder.tvPhone.setText(model.getPhone());

                Glide.with(getActivity())
                        .load(model.getAvatarUri())
                        .into(viewHolder.imgAvatar);
                photograpsList.add(model);
            }
        };

        recyclerView.setAdapter(adapter);

    }

    private static class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvPhone;
        private final ImageView imgAvatar;

        private TextView tvName;

        public MyViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.person_name);
            tvPhone = (TextView) view.findViewById(R.id.person_phone);
            imgAvatar = (ImageView) view.findViewById(R.id.person_photo);
        }

    }

}




