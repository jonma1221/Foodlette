package com.example.jonathan.cs499foodlette;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import java.util.ArrayList;
import java.util.Random;

import YelpParseClasses.Business;
import YelpParseClasses.Search;
import YelpParseClasses.YelpV2API;

public class Foodlette extends AppCompatActivity {
    private final String CONSUMER_KEY = "XxsQ0t52VIaAtbW9y6o8TA";
    private final String CONSUMER_SECRET = "88THviX8OapVOhAF01OLxnqNV_c";
    private final String TOKEN = "gCPhJx8GzdO88RsHkbvv5eWozqLASiMB";
    private final String TOKEN_SECRET = "NOuj5IY2gfLpBNz5mg9h0HVfBYg";

    private ImageButton searchButton;
    private EditText editLocation;
    private SeekBar distanceSeekBar;

    Search places;
    // Fields
    private Business business,loadedBusiness, favoritedBusiness;
    private String name;
    private String address;
    private String city;
    private String state;
    private String zip;
    private String phone;
    private String rating;
    private String url;
    private String mobileUrl;
    private String ratingUrl;
    private String snippet;
    private String locationInfo;
    private double latitude, longitude;

    //filters
    private String location;
    private String category;
    private double distance = 0;

    // Food russian roulette
    private boolean loaded = false, rouletteMode, toggle = false;

    // Favorites drawer
    private ListView mDrawerList;
    private ArrayList<Business> favoriteList = new ArrayList<Business>();
    private BusinessAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodlette);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        search();

        distanceBar();

        russianRouletteMode();

        foodCategoryFilter();

        favorites();

    }

    private void search() {
        final Animation rotateAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
        // Initial location
        editLocation = (EditText) findViewById(R.id.location);
        // Search for restaurant
        searchButton = (ImageButton)findViewById(R.id.search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editLocation.getText().length() != 0) {
                    location = editLocation.getText().toString();
                    Log.i("info", "Initial location: " + location);
                } else {
                    Log.i("info", "Empty");
                    Toast.makeText(getApplicationContext(), R.string.verifyLocation, Toast.LENGTH_SHORT).show();
                    return;
                }
                v.startAnimation(rotateAnim);
                toggle = true ? false : true;

                new Thread() {
                    @Override
                    public void run() {
                        try {
                            OAuthService service = new ServiceBuilder().provider(YelpV2API.class).
                                    apiKey(CONSUMER_KEY).apiSecret(CONSUMER_SECRET).build();
                            Token accessToken = new Token(TOKEN, TOKEN_SECRET);
                            OAuthRequest request = new OAuthRequest(Verb.GET,
                                    "http://api.yelp.com/v2/search");

                            request.addQuerystringParameter("location", location);
                            request.addQuerystringParameter("category_filter", category);
                            if (distance != 0) {
                                request.addQuerystringParameter("radius_filter", "" + distance);
                            }
                            request.addQuerystringParameter("sort", "1");
                            if (toggle) {
                                request.addQuerystringParameter("offset", "20");
                            }

                            request.addQuerystringParameter("limit", "20");
                            service.signRequest(accessToken, request);
                            Response response = request.send();
                            String rawData = response.getBody();
                            try {
                                places = new Gson().fromJson(rawData, Search.class);
                                if (loaded) {
                                    Log.i("info", "Loaded is true");
                                    Log.i("info", loadedBusiness.getName());
                                    places.getBusinesses().add(loadedBusiness);
                                } else Log.i("info", "Loaded is false");
                                Log.i("info", "Your search found " + places.getTotal() + " results.\n");
                                Log.i("info", "Yelp returned " + places.getBusinesses().size() + " businesses in this request.\n");
                                Random rand = new Random();
                                int randomNumber = rand.nextInt(places.getBusinesses().size());
                                business = places.getBusinesses().get(randomNumber);

                            } catch (Exception e) {
                                System.out.println("Error, could not parse returned data!");
                                System.out.println(rawData);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendBusinessInfo(business);
                    }
                }, 1001);
            }
        });
    }

    private void distanceBar() {
        // Filter for radius
        distanceSeekBar = (SeekBar)findViewById(R.id.distance);
        distanceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            TextView textView = (TextView) findViewById(R.id.distanceCovered);

            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {

                textView.setText("Distance within: " + progressValue + " miles");
                distance = progressValue / (0.00062137);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void russianRouletteMode() {
        // Food Russian Roulette mode
        Switch mySwitch = (Switch)findViewById(R.id.roullete);
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                TextView instruction1 = (TextView) findViewById(R.id.instruction);
                TextView instruction2 = (TextView) findViewById(R.id.instructionContinued);
                ImageView loadImg = (ImageView) findViewById(R.id.loadImg);
                if (isChecked) {
                    Toast.makeText(getApplicationContext(), R.string.rouletteOn, Toast.LENGTH_SHORT).show();
                    searchButton.setBackgroundResource(R.drawable.rr_empty);
                    instruction1.setVisibility(View.VISIBLE);
                    loadImg.setVisibility(View.VISIBLE);
                    instruction2.setVisibility(View.VISIBLE);
                    rouletteMode = true;
                } else {
                    Toast.makeText(getApplicationContext(), R.string.rouletteOff, Toast.LENGTH_SHORT).show();
                    searchButton.setBackgroundResource(R.drawable.roullete);
                    instruction1.setVisibility(View.INVISIBLE);
                    loadImg.setVisibility(View.INVISIBLE);
                    instruction2.setVisibility(View.INVISIBLE);
                    rouletteMode = false;
                    loaded = false;
                }
            }
        });
    }

    private void foodCategoryFilter() {
        // Filter for food category
        Spinner mySpinner = (Spinner)findViewById(R.id.category);
        String[] items = new String[]{"bagels","bakeries","beer_and_wine",
                                      "bubbletea", "burgers", "bbq", "buffets", "cheesesteaks",
                                      "coffee","cupcakes","delicatessen","desserts","donuts",
                                      "hotdog","hotpot","icecream","pizza","pretzels","ramen",
                                      "restaurants","salad","sandwiches","seafood","soup","steak",
                                      "sushi","tea"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        mySpinner.setAdapter(adapter);
        mySpinner.setSelection(adapter.getPosition("restaurants"));
        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void favorites() {
        // Navigation Drawer for favorites
        View header = (View)getLayoutInflater().inflate(R.layout.layout_favorite_header, null);
        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerList.addHeaderView(header);
        mAdapter = new BusinessAdapter(this, R.layout.layout_favorite, favoriteList);
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (position == 0) {
                    return;
                }
                Toast.makeText(Foodlette.this, favoriteList.get(position - 1).getName(), Toast.LENGTH_SHORT).show();
                sendBusinessInfo(favoriteList.get(position - 1));
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        boolean loadedResult,favorited, emptyChamber;
        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                loadedResult = data.getBooleanExtra("loaded", false);
                favorited = data.getBooleanExtra("favorited", false);
                emptyChamber = data.getBooleanExtra("emptyChamber",false);

                if(loadedResult){
                    loaded = true;
                    loadedBusiness = (Business) data.getSerializableExtra("loadedBusiness");
                    searchButton.setBackgroundResource(R.drawable.rr);
                    Log.i("info:","Loaded");
                }

                if(favorited){
                    favoritedBusiness = (Business) data.getSerializableExtra("favoritedBusiness");
                    boolean alreadyLoaded = data.getBooleanExtra("alreadyLoaded",false);
                    if(rouletteMode) {
                        if (!alreadyLoaded) searchButton.setBackgroundResource(R.drawable.rr_empty);
                    }
                    addDrawerItems(favoritedBusiness);
                }

                if(emptyChamber){
                    loaded = false;
                    searchButton.setBackgroundResource(R.drawable.rr_empty);
                }
            }
        }
    }
    private void addDrawerItems(Business business) {
        boolean found = false;
        for(int i = 0; i < favoriteList.size(); i++){
            if(favoriteList.get(i).getId().equals(business.getId())){
                Toast.makeText(Foodlette.this, "Already in your favorite drawer", Toast.LENGTH_SHORT).show();
                found = true;
            }
        }
        if(!found){
            Toast.makeText(Foodlette.this, "Added to your favorite's !", Toast.LENGTH_SHORT).show();
            favoriteList.add(business);
            mAdapter.notifyDataSetChanged();
        }
    }
    private void sendBusinessInfo(Business business) {
        name = business.getName();
        address = business.getLocation().getAddress().toString().substring(1, business.getLocation().getAddress().toString().length() - 1);
        city = business.getLocation().getCity();
        state = business.getLocation().getState_code();
        zip = business.getLocation().getPostal_code();
        phone = business.getDisplay_phone();
        rating = "Rating: " + business.getRating();
        url = business.getImage_url().substring(0, business.getImage_url().length() - 6) + "o.jpg";
        mobileUrl = business.getMobile_url();
        ratingUrl = business.getRating_img_url_large();
        snippet = business.getSnippet_text();

        latitude = business.getLocation().getCoordinate().getLatitude();
        longitude = business.getLocation().getCoordinate().getLongitude();

        if (phone == null) phone = "No number";
        locationInfo = address + "\n" + city + "\n" + state + ", " + zip + "\n" + phone + "\n";

        Intent intent = new Intent(Foodlette.this, result.class);
        intent.putExtra("biz", business);
        intent.putExtra("_name", name);
        intent.putExtra("_locationInfo", locationInfo);
        intent.putExtra("_url", url);
        intent.putExtra("_ratingUrl", ratingUrl);
        intent.putExtra("_lat", latitude);
        intent.putExtra("_lng", longitude);
        intent.putExtra("_snippet", snippet);
        intent.putExtra("_mobileUrl", mobileUrl);
        intent.putExtra("rouletteMode", rouletteMode);
        intent.putExtra("loaded", loaded);
        if(loaded){
            if(business.getId().equals(places.getBusinesses().get(places.getBusinesses().size()-1).getId())){
                intent.putExtra("bulletLanded", true);
            }
        }
        startActivityForResult(intent, 1);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_foodlette, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_forward) {
            if(business != null){
                sendBusinessInfo(business);
            }
        }

        return super.onOptionsItemSelected(item);
    }
}