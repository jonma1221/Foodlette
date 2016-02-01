package com.example.jonathan.cs499foodlette;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;

public class result extends AppCompatActivity {
    TextView infoView;
    TextView nameBiz;
    TextView snippetView;
    ImageView view;
    ImageButton load;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Business business = (Business)getIntent().getSerializableExtra("biz");
        boolean rouletteMode = getIntent().getBooleanExtra("rouletteMode", false);
        String name = business.getName();
        String address = business.getLocation().getAddress().toString().substring(1, business.getLocation().getAddress().toString().length() - 1);
        String city = business.getLocation().getCity();
        String state = business.getLocation().getState_code();
        String zip = business.getLocation().getPostal_code();
        String phone = business.getDisplay_phone();
        String rating = "Rating: " + business.getRating();
        String url = business.getImage_url().substring(0, business.getImage_url().length() - 6) + "o.jpg";
        String mobileUrl = business.getMobile_url();
        String ratingUrl = business.getRating_img_url_large();
        String snippet = business.getSnippet_text();

        if(phone == null) phone = "No number";
        String locationInfo =  address + "\n" + city + "\n" + state + ", " + zip + "\n" + phone + "\n";

        nameBiz = (TextView) findViewById(R.id.name);
        if(name != null){
            nameBiz.setText(name);
        }

        infoView = (TextView) findViewById(R.id.info);
        infoView.setText(locationInfo);

        if(snippet == null) snippet= "No review available";
        snippetView = (TextView) findViewById(R.id.snippet);
        snippetView.setText(Html.fromHtml("\"" + snippet + "\"" +
                "<a href=mobileUrl> \n more info...</a>"));


        load = (ImageButton)findViewById(R.id.load);
        if(rouletteMode)
            load.setVisibility(View.VISIBLE);
        else load.setVisibility(View.GONE);

        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(result.this,Foodlette.class);
                intent.putExtra("loaded",true);
                intent.putExtra("biz",business);
                Toast.makeText(getApplicationContext(), R.string.loaded, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        new DownloadImageTask((ImageView)findViewById(R.id.ratingImg))
                .execute(ratingUrl);

        if(url != null){
            new DownloadImageTask((ImageView)findViewById(R.id.image))
                    .execute(url);
        }

    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}