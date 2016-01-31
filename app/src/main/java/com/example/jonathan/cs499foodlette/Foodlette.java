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

public class Foodlette extends AppCompatActivity {
    public  final static String SER_KEY ="com.example.jonathan.cs499foodlette.ser";
    final String CONSUMER_KEY = "XxsQ0t52VIaAtbW9y6o8TA";
    final String CONSUMER_SECRET = "88THviX8OapVOhAF01OLxnqNV_c";
    final String TOKEN = "gCPhJx8GzdO88RsHkbvv5eWozqLASiMB";
    final String TOKEN_SECRET = "NOuj5IY2gfLpBNz5mg9h0HVfBYg";

    ImageButton searchButton;
    EditText editLocation;
    SeekBar distanceSeekBar;

    Business business,savedBusiness;
    String location;
    String category;
    String msg = "";
    String id,name,address,city,state,zip,phone,rating,url,mobileUrl,ratingImg,snippet,snippetImg,menu;
    double distance = 0;
    boolean loaded = false, toggle = false;

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
                v.startAnimation(rotateAnim);
                loaded = getIntent().getBooleanExtra("loaded",false);

                toggle = true ? false : true;
                location = editLocation.getText().toString();
                Log.i("info",location);
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            OAuthService service = new ServiceBuilder().provider(YelpV2API.class).apiKey(CONSUMER_KEY).apiSecret(CONSUMER_SECRET).build();
                            Token accessToken = new Token(TOKEN, TOKEN_SECRET);
                            OAuthRequest request = new OAuthRequest(Verb.GET, "http://api.yelp.com/v2/search");
                            request.addQuerystringParameter("location", location);
                            request.addQuerystringParameter("category_filter", category);
                            if(distance !=0 ){
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
                                YelpSearchResult places = new Gson().fromJson(rawData, YelpSearchResult.class);
                                if(loaded){
                                    Log.i("info", "Loaded is true");
                                    Business biz = (Business)getIntent().getSerializableExtra("biz");
                                    Log.i("info",biz.getName());
                                    places.getBusinesses().add(biz);
                                }
                                else Log.i("info", "Loaded is false");
                                Log.i("info", "Your search found " + places.getTotal() + " results.\n");
                                Log.i("info","Yelp returned " + places.getBusinesses().size() + " businesses in this request.\n");
                                Random rand = new Random();
                                int randomNumber = rand.nextInt(places.getBusinesses().size());
                                business = places.getBusinesses().get(randomNumber);
                                id = business.getId();
                                name = business.getName();
                                address = business.getLocation().getAddress().toString().substring(1, business.getLocation().getAddress().toString().length() - 1);
                                city = business.getLocation().getCity();
                                state = business.getLocation().getState_code();
                                zip = business.getLocation().getPostal_code();
                                phone = business.getDisplay_phone();
                                rating = "Rating: " + business.getRating();
                                url = business.getImage_url().substring(0, business.getImage_url().length() - 6) + "o.jpg";
                                mobileUrl = business.getMobile_url();
                                ratingImg = business.getRating_img_url_large();
                                snippet = business.getSnippet_text();
                                snippetImg = business.getSnippet_image_url();
                                Log.i("info", id);

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
                        intent.putExtra("biz",business);
                        /*intent.putExtra("name", name);
                        intent.putExtra("address", address);
                        intent.putExtra("city", city);
                        intent.putExtra("state", state);
                        intent.putExtra("zip", zip);
                        intent.putExtra("phone", phone);
                        intent.putExtra("rating", rating);
                        intent.putExtra("url", url);
                        intent.putExtra("mobileUrl", mobileUrl);
                        intent.putExtra("ratingImg", ratingImg);
                        intent.putExtra("snippet", snippet);*/
                        startActivity(intent);
                    }
                }, 1001);
            }
        });

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


        Switch mySwitch = (Switch)findViewById(R.id.roullete);
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(getApplicationContext(), R.string.rouletteOn, Toast.LENGTH_SHORT).show();
                    searchButton.setBackgroundResource(R.drawable.rr);
                } else{
                    Toast.makeText(getApplicationContext(), R.string.rouletteOff, Toast.LENGTH_SHORT).show();
                    searchButton.setBackgroundResource(R.drawable.roullete);
                }

            }

        });

        // Retrieve selected category
        Spinner mySpinner = (Spinner)findViewById(R.id.category);
        String[] items = new String[]{"bagels","bakeries","beer_and_wine","breweries",
                                      "bubbletea","burgers","bbq","buffets","churros","coffee","cupcakes",
                                      "delicatessen","desserts","foodtrucks",
                                      "pizza","pretzels", "ramen","streetvendors",
                                      "tea","restaurants"};
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
