package com.example.student.userproject;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.student.userproject.activity.HomeActivity;
import com.example.student.userproject.model.PhotographInfo;

import java.io.Serializable;
import java.util.List;

import static com.example.student.userproject.fragment.PhotographDetailInfoFragment.ADAPTER_TYPE_TOP;

public class CarouselPagerAdapter extends PagerAdapter {

    private Context context;
    private List<PhotographInfo> infoList;
    private int adapterType;

    public CarouselPagerAdapter() {
    }

    public CarouselPagerAdapter(Context context, List<PhotographInfo> infoList, int adapterType) {
        this.context = context;
        this.infoList = infoList;
        this.adapterType = adapterType;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item, null);
        try {

            RelativeLayout relMain = (RelativeLayout) view.findViewById(R.id.rel_main);
            ImageView image = (ImageView) view.findViewById(R.id.image);
            relMain.setTag(position);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment f = new FullScreenFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("images", (Serializable) infoList);
                    bundle.putInt("position", position);
                    f.setArguments(bundle);
                    ((HomeActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.container, f).commit();
                    Toast.makeText(context, "click on item " + position, Toast.LENGTH_LONG).show();
                }
            });

            switch (adapterType) {
                case ADAPTER_TYPE_TOP:
                    relMain.setBackgroundResource(R.drawable.shadow);
                    break;
            }

            Glide.with(context)
                    .load(infoList.get(position).getImageUri())
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

        return infoList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {

        return (view == object);
    }


}