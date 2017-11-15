package com.codegreed_devs.pambio;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class HealthActivity extends AppCompatActivity {
    AVLoadingIndicatorView avi;
    FrameLayout health_layout,no_network;
    TextView activities_desc,about_desc,no_network_text;
    LinearLayout health_items;
    boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_mentor);

        //Initializing the views
        health_layout= (FrameLayout) findViewById(R.id.mentor_layout);
        no_network= (FrameLayout) findViewById(R.id.no_network);
        no_network_text= (TextView) findViewById(R.id.no_network_text);
        health_items= (LinearLayout) findViewById(R.id.mentor_items);
        avi= (AVLoadingIndicatorView) findViewById(R.id.avi);

        //hiding layouts while progress bar loadings
        health_items.setVisibility(View.GONE);

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            AsyncHttpClient asyncHttpClient=new AsyncHttpClient();
            asyncHttpClient.post("http://www.kimesh.com/pambio/what_we_do.php", new TextHttpResponseHandler() {
                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                    avi.smoothToHide();
                    no_network.setVisibility(View.VISIBLE);
                    no_network_text.setText(R.string.server_error);
                    no_network.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent my_class_intent = getIntent();
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(my_class_intent);
                            overridePendingTransition(0, 0);

                        }
                    });

                }


                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    //animate layout
                    YoYo.with(Techniques.FadeInUp)
                            .duration(900)
                            .repeat(0)
                            .playOn(health_layout);
                    avi.smoothToHide();
                    health_items.setVisibility(View.VISIBLE);
                    try {
                        JSONArray contact_items=new JSONArray(s);

                        JSONObject on_mentor=contact_items.getJSONObject(4);
                        try {


                            String on_about=on_mentor.getString("about");
                            String on_activities=on_mentor.getString("activities");

                            activities_desc=(TextView) findViewById(R.id.activities_desc);
                            about_desc=(TextView) findViewById(R.id.about_desc);

                            activities_desc.setText(on_activities);
                            about_desc.setText(on_about);


                        }catch (JSONException e){
                            e.printStackTrace();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            });
        } else {
            //no internet connection
            connected = false;
            avi.smoothToHide();
            no_network.setVisibility(View.VISIBLE);
            no_network.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent my_class_intent = getIntent();
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(my_class_intent);
                    overridePendingTransition(0, 0);

                }
            });
        }



    }



}

