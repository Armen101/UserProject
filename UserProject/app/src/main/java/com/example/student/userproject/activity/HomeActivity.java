package com.example.student.userproject.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.student.userproject.R;
import com.example.student.userproject.fragment.AboutFragment;
import com.example.student.userproject.fragment.FavoriteFragment;
import com.example.student.userproject.fragment.MapFragment;
import com.example.student.userproject.fragment.PhotographDetailInfoFragment;
import com.example.student.userproject.fragment.PostFragment;
import com.example.student.userproject.service.LocationService;
import com.example.student.userproject.utility.Constants;
import com.example.student.userproject.utility.FavoritAdapterHelper;

import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.example.student.userproject.utility.Constants.TAG_FAVORITE;
import static com.example.student.userproject.utility.Constants.TAG_MAP;
import static com.example.student.userproject.utility.Constants.TAG_POSTS;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private String tag = TAG_MAP;
    private PopupWindow popupWindow;
    private TextView tvAbout;
    private TextView btnLanguage;
    private AlertDialog dialog;
    private SharedPreferences shared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ImageView btn = (ImageView) findViewById(R.id.img_settings);

        shared = getSharedPreferences("localization", MODE_PRIVATE);

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

        findViewPopup(popupView);

        popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.dismiss();
        popupWindow.showAsDropDown(v);
    }

    private void findViewPopup(View popupView) {
        tvAbout = (TextView) popupView.findViewById(R.id.tv_about);
        btnLanguage = (TextView) popupView.findViewById(R.id.tv_language);
        btnLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                }
        });


//        tvAbout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        SharedPreferences shared = getSharedPreferences("SWITCH", Context.MODE_PRIVATE);
        final SharedPreferences.Editor edit = shared.edit();
        final boolean swBoolean = shared.getBoolean("SWITCH_TRUE", false);

        TextView tvLlanguage = (TextView) popupView.findViewById(R.id.tv_language);
        TextView tvRating = (TextView) popupView.findViewById(R.id.tv_rating);
        TextView tvAbout = (TextView) popupView.findViewById(R.id.tv_about);
        final Switch sw = (Switch) popupView.findViewById(R.id.mySwitch);

        tvLlanguage.setOnClickListener(this);
        tvRating.setOnClickListener(this);
        tvAbout.setOnClickListener(this);

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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_language:{
                LayoutInflater inflater = getLayoutInflater();
                View dialogLayout = inflater.inflate(R.layout.languages, null);
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(HomeActivity.this);
                dialogBuilder.setView(dialogLayout);
                dialog = dialogBuilder.create();
                dialog.show();

                Button btnOk = (Button) dialogLayout.findViewById(R.id.btn_confirm);
                final RadioGroup rgLanguage = (RadioGroup) dialogLayout.findViewById(R.id.language_group);
                RadioButton rbEnglish = (RadioButton) dialogLayout.findViewById(R.id.rb_english);
                RadioButton rbArmenian = (RadioButton) dialogLayout.findViewById(R.id.rb_armenian);
                RadioButton rbSpanish = (RadioButton) dialogLayout.findViewById(R.id.rb_spanish);
                RadioButton rbGermany = (RadioButton) dialogLayout.findViewById(R.id.rb_german);
                RadioButton rbRussian = (RadioButton) dialogLayout.findViewById(R.id.rb_russian);

                String language = shared.getString(Constants.DEFAULT_LANGUAGE, "");
                switch (language) {
                    case "en": {
                        rbEnglish.setChecked(true);
                        break;
                    }
                    case "ru": {
                        rbRussian.setChecked(true);
                        break;
                    }
                    case "hy": {
                        rbArmenian.setChecked(true);
                        break;
                    }
                    case "de": {
                        rbGermany.setChecked(true);
                        break;
                    }
                    case "es": {
                        rbSpanish.setChecked(true);
                        break;
                    }
                }

                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int cheked = rgLanguage.getCheckedRadioButtonId();
                        switch (cheked) {
                            case R.id.rb_english: {
                                Locale english = new Locale("en");
                                changeLocale(english);
                                break;
                            }
                            case R.id.rb_armenian: {
                                Locale armenian = new Locale("hy");
                                changeLocale(armenian);
                                break;
                            }
                            case R.id.rb_spanish: {
                                Locale spanish = new Locale("es");
                                changeLocale(spanish);
                                break;
                            }
                            case R.id.rb_german: {
                                Locale german = new Locale("de");
                                changeLocale(german);
                                break;
                            }
                            case R.id.rb_russian: {
                                Locale russian = new Locale("ru");
                                changeLocale(russian);
                                break;
                            }
                        }
                        shared.edit().putString(Constants.DEFAULT_LANGUAGE, Locale.getDefault().getLanguage()).apply();
                        Toast.makeText(HomeActivity.this, shared.getString(Constants.DEFAULT_LANGUAGE, ""), Toast.LENGTH_SHORT).show();

                    }

                    private void changeLocale(Locale language) {
                        Locale.setDefault(language);
                        Configuration config = new Configuration();
                        config.locale = language;
                        getBaseContext().getResources().updateConfiguration(config,
                                getBaseContext().getResources().getDisplayMetrics());
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                        dialog.dismiss();
                    }

                });

                break;
            }
            case R.id.tv_rating: {
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.container, RatingFragment.newInstance()).commit();
                break;
            }
            case R.id.tv_about: {
                FragmentManager manager = getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.container, AboutFragment.newInstance())
                        .addToBackStack(null).commit();
                break;
            }
        }
    }

}
