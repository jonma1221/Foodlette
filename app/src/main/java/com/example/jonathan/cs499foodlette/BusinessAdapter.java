package com.example.jonathan.cs499foodlette;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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
                notifyDataSetChanged();
            }
        });
        Picasso.with(getContext()).load(business.get(position).getImage_url()).into(imgIcon);
        txtTitle.setText(business.get(position).getName());
        return row;
    }
}
