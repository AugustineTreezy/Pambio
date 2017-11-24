package com.codegreed_devs.pambio.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codegreed_devs.pambio.R;
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

public class DonateActivity extends AppCompatActivity {
    AVLoadingIndicatorView avi;
    FrameLayout donate_layout,no_network;
    TextView mobile_money_desc,ac_name_desc,ac_no_desc,bank_desc,no_network_text;
    LinearLayout donate_items;
    boolean connected = false;
    Button donate_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_donate);

        //Initializing the views
        donate_layout= (FrameLayout) findViewById(R.id.donate_layout);
        no_network= (FrameLayout) findViewById(R.id.no_network);
        no_network_text= (TextView) findViewById(R.id.no_network_text);
        mobile_money_desc= (TextView) findViewById(R.id.mobile_money_desc);
        ac_name_desc= (TextView) findViewById(R.id.ac_name_desc);
        ac_no_desc= (TextView) findViewById(R.id.ac_no_desc);
        bank_desc= (TextView) findViewById(R.id.bank_desc);
        donate_items= (LinearLayout) findViewById(R.id.donate_items);
        donate_btn= (Button) findViewById(R.id.donate_btn);
        avi= (AVLoadingIndicatorView) findViewById(R.id.avi);

        //hiding layouts while progress bar loadings
        donate_items.setVisibility(View.GONE);

        donate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DonateActivity.this, "Coming soon..", Toast.LENGTH_SHORT).show();
            }
        });

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            AsyncHttpClient asyncHttpClient=new AsyncHttpClient();
            asyncHttpClient.post("http://www.kimesh.com/pambio/donate.php", new TextHttpResponseHandler() {
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
                            .playOn(donate_layout);
                    avi.smoothToHide();
                    donate_items.setVisibility(View.VISIBLE);
                    try {
                        JSONArray donate_items=new JSONArray(s);

                        JSONObject on_mobile_money=donate_items.getJSONObject(0);
                        JSONObject on_cheque=donate_items.getJSONObject(1);
                        try {

                            String mobile_money=on_mobile_money.getString("procedure");
                            String ac_name=on_cheque.getString("ac_name");
                            String ac_no=on_cheque.getString("ac_no");
                            String bank=on_cheque.getString("bank");

                            mobile_money_desc.setText(mobile_money);
                            ac_name_desc.setText(ac_name);
                            ac_no_desc.setText(ac_no);
                            bank_desc.setText(bank);

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

