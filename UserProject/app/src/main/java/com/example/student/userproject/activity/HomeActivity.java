package com.example.student.userproject.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.Toast;

import com.example.student.userproject.R;
import com.example.student.userproject.fragment.FavoriteFragment;
import com.example.student.userproject.fragment.MapFragment;
import com.example.student.userproject.fragment.PhotographDetailInfoFragment;
import com.example.student.userproject.fragment.PostFragment;
import com.example.student.userproject.service.LocationService;
import com.example.student.userproject.utility.FavoritAdapterHelper;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.example.student.userproject.utility.Constants.TAG_FAVORITE;
import static com.example.student.userproject.utility.Constants.TAG_MAP;
import static com.example.student.userproject.utility.Constants.TAG_POSTS;

public class HomeActivity extends AppCompatActivity {

    private String tag = TAG_MAP;
    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ImageView btn = (ImageView) findViewById(R.id.img_settings);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v);
            }
        });

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
        } else {
            initFirstFragment(MapFragment.newInstance(), tag);
            initBottomNavigationBar();
        }
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
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("DETAILS_FRAGMENT");
        if (fragment instanceof PhotographDetailInfoFragment) {
            itemMap.setEnabled(true);
            itemPost.setEnabled(true);
            itemFavorite.setEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 10: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    initFirstFragment(MapFragment.newInstance(), tag);
                    initBottomNavigationBar();
                } else {
                    Toast.makeText(this, "Permissions is enabled", Toast.LENGTH_LONG).show();
                }
                return;
            }
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

    public void showPopup(final View v) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View popupView = layoutInflater.inflate(R.layout.popup_menu, null);

        SharedPreferences shared = getSharedPreferences("SWITCH", Context.MODE_PRIVATE);
        final SharedPreferences.Editor edit = shared.edit();

        final boolean swBoolean = shared.getBoolean("SWITCH_TRUE", false);

        final Switch sw = (Switch) popupView.findViewById(R.id.mySwitch);
        sw.setChecked(swBoolean);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isChecked = swBoolean;
                if (isChecked) {
                    popupWindow.dismiss();
                    edit.putBoolean("SWITCH_TRUE", false);
                    edit.apply();
                } else {
                    popupWindow.dismiss();
                    LocationService.changeNetwork();
                    edit.putBoolean("SWITCH_TRUE", true);
                    edit.apply();
                }
            }
        });

        popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.dismiss();
        popupWindow.showAsDropDown(v);
    }
}
