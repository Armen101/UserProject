package com.example.student.userproject.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.student.userproject.R;
import com.example.student.userproject.activity.HomeActivity;
import com.example.student.userproject.fragment.FullScreenFragment;
import com.example.student.userproject.model.PhotographInfo;

import java.io.Serializable;
import java.util.List;

import static com.example.student.userproject.fragment.PhotographDetailInfoFragment.ADAPTER_TYPE_TOP;

public class CarouselPagerAdapter extends PagerAdapter {

    private Context mContext;
    private List<PhotographInfo> mInfoList;
    private int mAdapterType;

    public CarouselPagerAdapter(Context context, List<PhotographInfo> mInfoList, int adapterType) {
        this.mContext = context;
        this.mInfoList = mInfoList;
        this.mAdapterType = adapterType;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.full_screen_item, null);
        try {

            RelativeLayout relMain = (RelativeLayout) view.findViewById(R.id.rel_main);
            ImageView image = (ImageView) view.findViewById(R.id.image);
            relMain.setTag(position);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("images", (Serializable) mInfoList);
                    bundle.putInt("position", position);

                    FragmentTransaction ft = ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction();
                    FullScreenFragment newFragment = FullScreenFragment.newInstance();
                    newFragment.setArguments(bundle);
                    newFragment.show(ft, "CaruselPagerAdapter");

                    Toast.makeText(mContext, "click on full_screen_item " + position, Toast.LENGTH_LONG).show();
                }
            });

            switch (mAdapterType) {
                case ADAPTER_TYPE_TOP:
                    relMain.setBackgroundResource(R.drawable.shadow);
                    break;
            }

            Glide.with(mContext)
                    .load(mInfoList.get(position).getImageUri())
                    .into(image);
            container.addView(view);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mInfoList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

}