package com.example.student.userproject.activity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.student.userproject.R;
import com.example.student.userproject.fragment.FavoriteFragment;
import com.example.student.userproject.fragment.MapFragment;
import com.example.student.userproject.fragment.PostFragment;
import com.example.student.userproject.utility.FavoritAdapterHelper;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HomeActivity extends AppCompatActivity {

    public static final String TAG_MAP = "MAP_FRAGMENT";
    public static final String TAG_POSTS = "POSTS_FRAGMENT";
    public static final String TAG_FAVORITE = "FAVORITE_FRAGMENT";
    private String tag = TAG_MAP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("choco_cooky.ttf")
                .build());
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION}, 10);
            }
            return;
        }
        initFirstFragment(MapFragment.newInstance(), tag);
        initBottomNavigationBar();
    }

    private void initFirstFragment(Fragment fragment, String tag) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment, tag)
                .addToBackStack(null)
                .commit();
    }

    private void initBottomNavigationBar() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.navigation);
        Menu menu = bottomNavigationView.getMenu();
        final MenuItem itemMap = menu.findItem(R.id.action_map);
        final MenuItem itemPost = menu.findItem(R.id.action_post);
        final MenuItem itemFavorite = menu.findItem(R.id.action_config);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.action_config: {
                        FavoritAdapterHelper.initFavoritList(HomeActivity.this);
                        selectedFragment = FavoriteFragment.newInstance();
                        tag = TAG_FAVORITE;
                        break;
                    }
                    case R.id.action_post: {
                        selectedFragment = PostFragment.newInstance();
                        tag = TAG_POSTS;
                        break;
                    }
                    case R.id.action_map: {
                        selectedFragment = MapFragment.newInstance();
                        tag = TAG_MAP;
                        break;
                    }
                }
                enableMenuItems(itemMap, itemPost, itemFavorite);
                initFirstFragment(selectedFragment, tag);
                return true;
            }
        });
    }

    private void enableMenuItems(MenuItem itemMap, MenuItem itemPost, MenuItem itemFavorite) {
        if (tag.equals(TAG_MAP)) {
            itemMap.setEnabled(false);
        } else itemMap.setEnabled(true);

        if (tag.equals(TAG_POSTS)) {
            itemPost.setEnabled(false);
        } else itemPost.setEnabled(true);

        if (tag.equals(TAG_FAVORITE)) {
            itemFavorite.setEnabled(false);
        } else itemFavorite.setEnabled(true);
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
                    Toast.makeText(this, "Permissions is enabled", Toast.LENGTH_LONG).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentById(R.id.container) != null) {
            super.onBackPressed();
            if (getSupportFragmentManager().findFragmentById(R.id.container) == null) {
                finish();
            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
