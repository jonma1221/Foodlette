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

import java.util.Random;

import YelpParseClasses.Business;
import YelpParseClasses.Search;
import YelpParseClasses.YelpV2API;

public class Foodlette extends AppCompatActivity {
    final String CONSUMER_KEY = "XxsQ0t52VIaAtbW9y6o8TA";
    final String CONSUMER_SECRET = "88THviX8OapVOhAF01OLxnqNV_c";
    final String TOKEN = "gCPhJx8GzdO88RsHkbvv5eWozqLASiMB";
    final String TOKEN_SECRET = "NOuj5IY2gfLpBNz5mg9h0HVfBYg";

    ImageButton searchButton;
    EditText editLocation;
    SeekBar distanceSeekBar;

    Business business, loadedbusiness;

    //filters
    String location;
    String category;
    double distance = 0;

    // Food rushian roulette
    boolean loaded = false, rouletteMode, toggle = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodlette);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editLocation = (EditText) findViewById(R.id.location);

        final Animation rotateAnim = AnimationUtils.loadAnimation(this,R.anim.rotate);

        searchButton = (ImageButton)findViewById(R.id.search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editLocation.getText().length() != 0){
                    location = editLocation.getText().toString();
                    Log.i("info","Initial location: " + location);
                }
                else{
                    Log.i("info","Empty");
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
                                Search places = new Gson().fromJson(rawData, Search.class);
                                if (loaded) {
                                    Log.i("info", "Loaded is true");
                                    Log.i("info", loadedbusiness.getName());
                                    places.getBusinesses().add(loadedbusiness);
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
                        Intent intent = new Intent(Foodlette.this, result.class);
                        intent.putExtra("biz", business);
                        intent.putExtra("rouletteMode", rouletteMode);
                        startActivityForResult(intent, 1);
                    }
                }, 1001);
            }
        });

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

        // Food Rushian Roulette mode
        Switch mySwitch = (Switch)findViewById(R.id.roullete);
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(getApplicationContext(), R.string.rouletteOn, Toast.LENGTH_SHORT).show();
                    searchButton.setBackgroundResource(R.drawable.rr_empty);
                    rouletteMode = true;
                } else{
                    Toast.makeText(getApplicationContext(), R.string.rouletteOff, Toast.LENGTH_SHORT).show();
                    searchButton.setBackgroundResource(R.drawable.roullete);
                    rouletteMode = false;
                    loaded = false;
                }
            }
        });

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                loaded = data.getBooleanExtra("loaded", false);
                loadedbusiness  = (Business) data.getSerializableExtra("biz");
                searchButton.setBackgroundResource(R.drawable.rr);
                Log.i("info:","Loaded");
            }
        }
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
