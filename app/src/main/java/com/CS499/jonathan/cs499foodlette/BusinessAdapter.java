package com.CS499.jonathan.cs499foodlette;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import YelpParseClasses.Business;

/**
 * Created by Jonathan on 2/7/2016.
 */
public class BusinessAdapter extends ArrayAdapter<Business>{

    Context context;
    int layoutResource;
    ArrayList<Business> business;

    public BusinessAdapter(Context context, int layoutResource, ArrayList<Business> business){
        super(context, layoutResource, business);
        this.layoutResource = layoutResource;
        this.context = context;
        this.business = business;
    }

    public View getView(final int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(layoutResource, parent, false);
        ImageView imgIcon = (ImageView)row.findViewById(R.id.businessImg);
        TextView txtTitle = (TextView)row.findViewById(R.id.businessName);
        ImageButton deleteButton = (ImageButton) row.findViewById(R.id.deleteFavorite);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                business.remove(position);
                saveFavorites();
                notifyDataSetChanged();
            }
        });

        Picasso.with(getContext()).load(business.get(position).getImage_url().substring(0,
                    business.get(position).getImage_url().length() - 6) + "o.jpg").into(imgIcon);
        String locationInfo =
            business.get(position).getName() + "\n" +
            business.get(position).getLocation().getAddress().toString().substring(1, business.get(position).
                                   getLocation().getAddress().toString().length() - 1) + "\n" +
            business.get(position).getLocation().getCity() + "\n" +
            business.get(position).getLocation().getState_code() + "," +
            business.get(position).getLocation().getPostal_code();
        txtTitle.setText(locationInfo);
        return row;
    }
    private void saveFavorites() {
        SharedPreferences preferences = context.getSharedPreferences("favorites", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(business);
        editor.putString("jsonFavorites",jsonFavorites);
        editor.commit();

    }
}
