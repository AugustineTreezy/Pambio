package com.codegreed_devs.pambio.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.codegreed_devs.pambio.R;
import com.wang.avi.AVLoadingIndicatorView;

public class JoinUsActivity extends AppCompatActivity {
    AVLoadingIndicatorView avi;
    WebView mWebView;
    FrameLayout no_network;
    TextView no_network_text;
    boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_donate);

        //Initializing the views
        mWebView= (WebView) findViewById(R.id.mWebView);
        no_network= (FrameLayout) findViewById(R.id.no_network);
        no_network_text= (TextView) findViewById(R.id.no_network_text);
        avi= (AVLoadingIndicatorView) findViewById(R.id.avi);

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            final JoinUsActivity.MyWebClient myWebViewClient = new JoinUsActivity.MyWebClient();
            mWebView.setWebViewClient(myWebViewClient);
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.getSettings().setBuiltInZoomControls(false);
            mWebView.loadUrl("http://pambio.org/join-us/");
            mWebView.setVisibility(View.INVISIBLE);



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


    public class MyWebClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            view.loadUrl("javascript:(function() { " +
                    "document.getElementById('masthead').style.display='none'; " +
                    "document.getElementById('slideshow').style.display='none'; " +
                    "document.getElementById('sidebar-footer').style.display='none'; " +
                    "document.getElementById('colophon').style.display='none'; " +
                    "document.getElementById('footer_copyright-3').style.display='none'; " +

                    "})()");
            avi.smoothToHide();
            mWebView.setVisibility(View.VISIBLE);

        }
        public void onReceivedError(WebView webView, int errorCode, String description, String failingUrl) {
            try {
                webView.stopLoading();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (webView.canGoBack()) {
                webView.goBack();
            }

            webView.loadUrl("about:blank");
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
            super.onReceivedError(webView, errorCode, description, failingUrl);
        }


    }
    public class myJavaScriptInterface {
        @JavascriptInterface
        public void setVisible() {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    mWebView.setVisibility(View.VISIBLE);
                }
            });
        }
    }

}

