package com.CS499.jonathan.cs499foodlette;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import YelpParseClasses.Business;

/**
 * Created by Jonathan on 3/3/2016.
 */
public class SwipeAdapter extends PagerAdapter{
    Context context;
    ArrayList<Business> businesses;

    public SwipeAdapter(Context context, ArrayList<Business> businesses){
        this.context = context;
        this.businesses = businesses;
    }
    @Override
    public int getCount() {
        return businesses.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.history_layout,container,false);

        ImageView historyImage = (ImageView) view.findViewById(R.id.historyImage);
        TextView historyName = (TextView) view.findViewById(R.id.historyName);

        // set image
        Picasso.with(context).load(businesses.get(position).getImage_url().substring(0,
                businesses.get(position).getImage_url().length() - 6) + "o.jpg").into(historyImage);

        // set text
        historyName.setText(businesses.get(position).getName());

        container.addView(view);
        return view;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object){
        container.removeView((View) object);
    }
}
