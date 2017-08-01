package com.example.student.userproject.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.student.userproject.R;
import com.example.student.userproject.model.PhotographInfo;
import com.example.student.userproject.utility.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


public class SplashScreenActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseRef;
    private PhotographInfo info;

    private Map<String, Long> photographersRatings = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabaseRef = database.getReference().child(Constants.PHOTOGRAPHS);

        setLocale();
        photographers();

        ImageView splashScreen = (ImageView) findViewById(R.id.splash_screen);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation);
        splashScreen.setAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                goToActivity();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void setLocale() {
        SharedPreferences shared = getSharedPreferences("localization", MODE_PRIVATE);
        String language = shared.getString(Constants.DEFAULT_LANGUAGE, "");
        if (TextUtils.isEmpty(language)) {
            shared.edit().putString(Constants.DEFAULT_LANGUAGE, Locale.getDefault().getLanguage()).apply();
        }
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }

    private void goToActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void photographers() {
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    info = postSnapshot.getValue(PhotographInfo.class);
                    photographersRatings.put(info.getUid(), Long.valueOf(info.getRating()));
//                    Log.i("ssssssssssssssss", "aaaaaaaaa " + info.getUid() + "    " + info.getRating());
                }

                sortedRating();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void sortedRating() {
        Set<Entry<String, Long>> set = photographersRatings.entrySet();
        List<Entry<String, Long>> list = new ArrayList<Entry<String, Long>>(set);
        Collections.sort(list, new Comparator<Entry<String, Long>>() {
            public int compare(Entry<String, Long> o1,
                               Entry<String, Long> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

//        for (Entry<String, Long> entry : list) {
//            System.out.println(entry.getValue());

            for (int i = 0; i < 5; i++) {
                Log.i("ssssssssss", "  " +list.get(i));
            }

//        }
    }
}
