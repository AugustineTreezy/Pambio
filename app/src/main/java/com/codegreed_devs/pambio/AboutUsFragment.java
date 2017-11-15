package com.codegreed_devs.pambio;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.HashMap;
import cz.msebera.android.httpclient.Header;



public class AboutUsFragment extends Fragment implements BaseSliderView.OnSliderClickListener {
    public SliderLayout sliderShow;
    HashMap<String, String> HashMapForURL ;
    AboutUsAdapter aboutUsAdapter;
    ListView about_items;
    CardView card_social,card_partnes;
    View rootView;
    FrameLayout about_us_layout,no_network;
    TextView no_network_text;
    ImageView ig,twitter,fb;
    AVLoadingIndicatorView avi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        rootView = inflater.inflate(R.layout.activity_about_us, container, false);
        avi=rootView.findViewById(R.id.avi);
        no_network=rootView.findViewById(R.id.no_network);
        no_network_text=rootView.findViewById(R.id.no_network_text);

        //hiding elements while loading
        card_partnes=rootView.findViewById(R.id.card_partners);
        card_social=rootView.findViewById(R.id.card_social);
        card_partnes.setVisibility(View.GONE);
        card_social.setVisibility(View.GONE);

        //start of the slideshow
        sliderShow = rootView.findViewById(R.id.slider);
        ConnectivityManager connMgr = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
        AddImagesUrlOnline();
        social_media();
        for(String name : HashMapForURL.keySet()){

            TextSliderView textSliderView = new TextSliderView(getContext());

            textSliderView
                    .description(name)
                    .image(HashMapForURL.get(name))
                    .setOnSliderClickListener(this)
                    .setScaleType(BaseSliderView.ScaleType.Fit);


            textSliderView.bundle(new Bundle());

            textSliderView.getBundle()
                    .putString("extra",name);

            sliderShow.addSlider(textSliderView);

        }
        sliderShow.setDuration(10300);



        about_us_layout=rootView.findViewById(R.id.about_us_layout);


        //getting the list view from the xml file
        about_items= rootView.findViewById(R.id.about_items);


        //initializing the arrayadapter
        ArrayList<AboutUsItems> arrayofaboutusitems=new ArrayList<>();
        aboutUsAdapter=new AboutUsAdapter(getContext(),0,arrayofaboutusitems);

        AsyncHttpClient asyncHttpClient=new AsyncHttpClient();
        asyncHttpClient.post("http://www.kimesh.com/pambio/about_us.php", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                avi.smoothToHide();
                no_network.setVisibility(View.VISIBLE);
                no_network_text.setText(R.string.server_error);
                no_network.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();
                        t.setAllowOptimization(false);
                        t.detach(AboutUsFragment.this).attach(AboutUsFragment.this).commitAllowingStateLoss();
                    }
                });

            }


            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                //animate layout
                YoYo.with(Techniques.FadeInUp)
                        .duration(900)
                        .repeat(0)
                        .playOn(about_us_layout);

                //show elements after loading completes and hide the progress bar
                card_partnes.setVisibility(View.VISIBLE);
                card_social.setVisibility(View.VISIBLE);
                avi.smoothToHide();

                try {
                    JSONArray items=new JSONArray(s);
                    ArrayList<AboutUsItems> about_us=AboutUsItems.fromJson(items);
                    aboutUsAdapter.addAll(about_us);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //end of getting JSON Data


            }
        });


        about_items.setAdapter(aboutUsAdapter);
        } else {
            // display error
            avi.smoothToHide();
            no_network.setVisibility(View.VISIBLE);
            no_network.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();
                    t.setAllowOptimization(false);
                    t.detach(AboutUsFragment.this).attach(AboutUsFragment.this).commitAllowingStateLoss();
                }
            });
        }


        return rootView;
    }

    //inserting the url for the image sliders
    public void AddImagesUrlOnline(){
        HashMapForURL = new HashMap<>();

        HashMapForURL.put("Khetia Supermarket", "http://www.kimesh.com/pambio/images/sliders/partners/6.png");
        HashMapForURL.put("American Friends Service Committee", "http://www.kimesh.com/pambio/images/sliders/partners/2.png");
        HashMapForURL.put("Kenya National Blood Transfusion Service", "http://www.kimesh.com/pambio/images/sliders/partners/3.png");
        HashMapForURL.put("Global Peace Foundation Kenya", "http://www.kimesh.com/pambio/images/sliders/partners/4.png");
        HashMapForURL.put("Noverty International kenya", "http://www.kimesh.com/pambio/images/sliders/partners/5.png");
        HashMapForURL.put("Administrative Police Service", "http://www.kimesh.com/pambio/images/sliders/partners/1.png");


    }


    @Override
    public void onStop() {
        sliderShow.stopAutoCycle();
        super.onStop();
    }

    @Override
    //on click listener for "our projects" slider
    public void onSliderClick(BaseSliderView slider) {
        String clicked= (String) slider.getBundle().get("extra");
        assert clicked != null;
        switch (clicked) {
            case "Khetia Supermarket":
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.khetia.com")));

                break;
            case "American Friends Service Committee":
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.afsc.org")));

                break;
            case "Kenya National Blood Transfusion Service":
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.nbtskenya.or.ke")));

                break;
            case "Global Peace Foundation Kenya":
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.globalpeacekenya.org")));

                break;
            case "Novelty International kenya":
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.noveltyik.or.ke")));

                break;
            default:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.administrationpolice.go.ke")));
                break;
        }

    }
    //getting social media icons online
    public void social_media(){
        String ig_url="http://www.kimesh.com/pambio/images/social_media_icons/instagram.png";
        String twitter_url="http://www.kimesh.com/pambio/images/social_media_icons/twitter.png";
        String facebook_url="http://www.kimesh.com/pambio/images/social_media_icons/facebook.png";
        ig=rootView.findViewById(R.id.ig);
        fb=rootView.findViewById(R.id.fb);
        twitter=rootView.findViewById(R.id.twitter);
        Picasso
                .with(getContext())
                .load(ig_url)
                .into(ig);
        Picasso
                .with(getContext())
                .load(twitter_url)
                .into(twitter);
        Picasso
                .with(getContext())
                .load(facebook_url)
                .into(fb);
        ig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://instagram.com/_u/peaceambassadorskenya");
                Intent instagram = new Intent(Intent.ACTION_VIEW, uri);

                instagram.setPackage("com.instagram.android");

                try {
                    startActivity(instagram);
                } catch (ActivityNotFoundException e) {
                    //if instagram not found, opens in browser
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://instagram.com/peaceambassadorskenya")));
                }
                //end

            }
        });
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String YourPageURL = "https://www.facebook.com/n/?PeaceAmbassadorsKE";
                Intent fb = new Intent(Intent.ACTION_VIEW, Uri.parse(YourPageURL));

                try {
                    //facebook
                    fb.setPackage("com.facebook.katana");
                    startActivity(fb);


                }catch (ActivityNotFoundException e){
                    //browser
                    Uri browser = Uri.parse("https://www.facebook.com/n/?PeaceAmbassadorsKE");
                    Intent connect_browser = new Intent(Intent.ACTION_VIEW, browser);
                    startActivity(connect_browser);
                }

            }
        });
        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //twitter intent
                Uri real_twitter = Uri.parse("https://twitter.com/PeaceAmbsKenya");
                Intent twitter = new Intent(Intent.ACTION_VIEW, real_twitter);
                twitter.setPackage("com.twitter.android");

                try {

                    startActivity(twitter);

                }catch (ActivityNotFoundException e){
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://twitter.com/PeaceAmbsKenya")));
                }

            }
        });
    }


}
