package com.example.student.userproject;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.student.userproject.model.PhotographInfo;

import java.util.List;

public class FullScreenFragment extends DialogFragment {

    private ViewPager fullViewPager;

    ImageView imageFullScreen;
    FrameLayout fullScreenLayout;
    private View rootView;
    private List<PhotographInfo>infoList;

    public FullScreenFragment() {
        // Required empty public constructor
    }

    public static FullScreenFragment newInstance() {

        return new FullScreenFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_full_screen, container, false);

        imageFullScreen =(ImageView)rootView.findViewById(R.id.image_full_screen);
        fullScreenLayout = (FrameLayout) rootView.findViewById(R.id.full_screen_layout);
        fullViewPager = (ViewPager) rootView.findViewById(R.id.view_pager);
        infoList = (List<PhotographInfo>) getArguments().getSerializable("images");
        int mSelectedPosition = getArguments().getInt("position");

        MyViewPagerAdapter mAdapter = new MyViewPagerAdapter();
        fullViewPager.setAdapter(mAdapter);
        fullViewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        setCurrentItem(mSelectedPosition);

        return rootView;
    }

    private void setCurrentItem(int position) {

        fullViewPager.setCurrentItem(position, false);
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {

            fullViewPager.setCurrentItem(position, false);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    public class MyViewPagerAdapter extends PagerAdapter {

        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rootView = layoutInflater.inflate(R.layout.image_fullscreen, container, false);
            ImageView imageViewPreview = (ImageView) rootView.findViewById(R.id.image_full_screen);
            Glide.with(getActivity())
                    .load(infoList.get(position).getImageUri())
                    .into(imageViewPreview);
            container.addView(rootView);

            return rootView;
        }

        @Override
        public int getCount() {
            return infoList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == ((View) obj);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

}
