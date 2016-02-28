package com.CS499.jonathan.cs499foodlette;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import YelpParseClasses.Business;

public class result extends AppCompatActivity {
    private TextView infoView;
    private TextView mapInfoView;
    private TextView nameBiz;
    private TextView snippetView;
    private TextView reviewCount;
    private ImageButton load;
    private ImageButton favorite;
    private WebView myWebView;
    private String locationInfo;
    private boolean landed = false;

    //Intent
    private Intent myIntent;

    //Google Map
    private GoogleMap googleMap;
    private Business business;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*** Tab Stuff ***/
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("foodInfo");
        tabSpec.setIndicator("Location",getResources().getDrawable(R.drawable.ic_drive_eta_white_24dp));
        tabSpec.setContent(R.id.foodInfo);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("webInfo");
        tabSpec.setContent(R.id.webview);
        tabSpec.setIndicator("Yelp Page");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("mapInfo");
        tabSpec.setContent(R.id.mapInfo);
        tabSpec.setIndicator("Map info");
        tabHost.addTab(tabSpec);

        //Intent
        myIntent = getIntent();

        business = (Business) getIntent().getSerializableExtra("biz");
        final boolean rouletteMode = getIntent().getBooleanExtra("rouletteMode", false);

        locationInfo = myIntent.getStringExtra("_locationInfo");
        String name = myIntent.getStringExtra("_name");
        String snippet = myIntent.getStringExtra("_snippet");
        String url = myIntent.getStringExtra("_url");
        String ratingUrl = myIntent.getStringExtra("_ratingUrl");
        String mobileUrl = myIntent.getStringExtra("_mobileUrl");
        landed = myIntent.getBooleanExtra("bulletLanded",false);
        if(landed){
            Toast.makeText(getApplicationContext(), "You landed on the restaurant!", Toast.LENGTH_SHORT).show();
            LinearLayout rootView = (LinearLayout)findViewById(R.id.rootView);
            rootView.setBackgroundColor(Color.RED);
        }
        nameBiz = (TextView) findViewById(R.id.name);
        if(name != null){
            nameBiz.setText(name);
        }

        infoView = (TextView) findViewById(R.id.info);
        infoView.setText(locationInfo);

        mapInfoView = (TextView)findViewById(R.id.mapFragmentInfo);
        mapInfoView.setText(locationInfo);

        //Snippet text
        snippetView = (TextView) findViewById(R.id.snippet);
        if (snippet == null)
            snippet = "No review available";
        else
            snippetView.setText("\"" + snippet + "...\"");

        load = (ImageButton)findViewById(R.id.load);
        if(rouletteMode)
            load.setVisibility(View.VISIBLE);
        else load.setVisibility(View.GONE);

        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(result.this, Foodlette.class);
                intent.putExtra("loaded",true);
                intent.putExtra("loadedBusiness",business);
                Toast.makeText(getApplicationContext(), R.string.loaded, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        favorite = (ImageButton) findViewById(R.id.favorite);
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean loaded = myIntent.getBooleanExtra("loaded",false);
                Intent intent = new Intent(result.this, Foodlette.class);
                if(rouletteMode){
                    if(loaded) {
                        intent.putExtra("alreadyLoaded",true);
                    }
                }
                if(landed){
                    intent.putExtra("emptyChamber",true);
                }
                intent.putExtra("favorited", true);
                intent.putExtra("favoritedBusiness", business);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        //Loading Web Url
        myWebView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.loadUrl("" + mobileUrl);

        loadImage(url, ratingUrl);

        initializeMap();

    }

    private void initializeMap() {

        Marker marker;

        if (googleMap == null) {
            googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment)).getMap();

            double lat = myIntent.getExtras().getDouble("_lat");
            double lng = myIntent.getExtras().getDouble("_lng");
            LatLng latlng = new LatLng(lat, lng);

            if (googleMap != null) {
                marker = googleMap.addMarker(new MarkerOptions()
                        .position(latlng)
                        .title(locationInfo));

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));
                marker.showInfoWindow();
            }
        }
    }

    private void loadImage(String url, String ratingUrl) {
        //Initialize ImageView
        ImageView ratingPic = (ImageView) findViewById(R.id.ratingImg);
        ImageView urlPic = (ImageView) findViewById(R.id.image);

        //Loading image from below url into imageView
        Picasso.with(this).load(ratingUrl).into(ratingPic);
        Picasso.with(this).load(url).into(urlPic);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_result, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_back) {
            if(landed){
                Intent intent = new Intent();
                intent.putExtra("emptyChamber",true);
                setResult(RESULT_OK, intent);
            }
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}