package com.codegreed_devs.pambio;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.internal.NavigationMenu;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.wang.avi.AVLoadingIndicatorView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import cz.msebera.android.httpclient.Header;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

/**
 * Created by augustine on 10/10/17.
 */
public class HomeFragment extends Fragment{
    SliderLayout sliderShow;
    HashMap<String, String> HashMapForURL ;
    HomeItemsAdapter homeItemsAdapter;
    ListView home_items;
    CardView projects_card,pambio_card;
    FrameLayout home_layout,no_network;
    RelativeLayout obstructor;
    AVLoadingIndicatorView avi;
    TextView no_network_text;
    String image,description;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        final View rootView = inflater.inflate(R.layout.home, container, false);
        sliderShow = rootView.findViewById(R.id.slider);
        home_layout=rootView.findViewById(R.id.home_layout);
        no_network=rootView.findViewById(R.id.no_network);
        no_network_text=rootView.findViewById(R.id.no_network_text);
        //initializng the progress bar
        avi=rootView.findViewById(R.id.avi);

        //hiding layouts while progress bar loadings
        pambio_card=rootView.findViewById(R.id.pambio_card);
        projects_card=rootView.findViewById(R.id.projects_card);
        projects_card.setVisibility(View.GONE);
        pambio_card.setVisibility(View.GONE);
        ConnectivityManager connMgr = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {

        AddImagesUrlOnline();
        for(String name : HashMapForURL.keySet()){

            TextSliderView textSliderView = new TextSliderView(getContext());

            textSliderView
                    .description(name)
                    .image(HashMapForURL.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit);

            textSliderView.bundle(new Bundle());

            textSliderView.getBundle()
                    .putString("extra",name);

            sliderShow.addSlider(textSliderView);

        }
        sliderShow.setDuration(3000);
        obstructor=rootView.findViewById(R.id.obstructor);


            //getting the list view from the xml file
            home_items= rootView.findViewById(R.id.home_items);

            //initializing the array_adapter
            ArrayList<HomeItems> arrayofhomeitems=new ArrayList<>();
            homeItemsAdapter=new HomeItemsAdapter(getContext(),0,arrayofhomeitems);

            AsyncHttpClient asyncHttpClient=new AsyncHttpClient();
            asyncHttpClient.post("http://www.kimesh.com/pambio/home_items.php", new TextHttpResponseHandler() {


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
                            t.detach(HomeFragment.this).attach(HomeFragment.this).commitAllowingStateLoss();
                        }
                    });



                }


                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    //animate layout
                    YoYo.with(Techniques.FadeInUp)
                            .duration(900)
                            .repeat(0)
                            .playOn(home_layout);

                    //show elements after loading completes and hide the progress bar
                    pambio_card.setVisibility(View.VISIBLE);
                    avi.smoothToHide();
                    projects_card.setVisibility(View.VISIBLE);


                    try {
                        JSONArray items=new JSONArray(s);

                        JSONObject itemnum=items.getJSONObject(0);

                        ArrayList<HomeItems> home_items=HomeItems.fromJson(items);
                        homeItemsAdapter.addAll(home_items);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //end of getting JSON Data

                }
            });


            home_items.setAdapter(homeItemsAdapter);

        //initialize fab
        FabSpeedDial fabSpeedDial = rootView.findViewById(R.id.fab_speed_dial);
        fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onPrepareMenu(NavigationMenu navigationMenu) {
                // TODO: Do something with your menu items, or return false if you don't want to show them
                //show the obstructor on fab click
                obstructor.setVisibility(View.VISIBLE);

                return true;
            }

            @Override
            public void onMenuClosed() {
                //hide the obstructor on fab click
               obstructor.setVisibility(View.GONE);
                super.onMenuClosed();
            }

            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                // TODO: Start some activity
                obstructor.setVisibility(View.GONE);
                String menuname=menuItem.toString();

                if (menuname.equalsIgnoreCase("Group Chat")){
                    Intent chat=new Intent(getActivity(), ChatActivity.class);
                    startActivity(chat);
                }else if(menuname.equalsIgnoreCase("Join Us")){
                    Intent join=new Intent(getActivity(), JoinUsActivity.class);
                    startActivity(join);

                }else if(menuname.equalsIgnoreCase("Donate")){
                    Intent donate=new Intent(getActivity(), DonateActivity.class);
                    startActivity(donate);

                } else{
                    Toast.makeText(getContext(), menuItem+" not found", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        } else {
            // display error
            avi.smoothToHide();
            no_network.setVisibility(View.VISIBLE);
            no_network.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();
                    t.setAllowOptimization(false);
                    t.detach(HomeFragment.this).attach(HomeFragment.this).commitAllowingStateLoss();
                }
            });

        }

        return rootView;
    }

    public void AddImagesUrlOnline(){
        HashMapForURL = new HashMap<>();

        AsyncHttpClient asyncHttpClient=new AsyncHttpClient();
        asyncHttpClient.post("http://www.kimesh.com/pambio/home_slider.php", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {


            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {


                try {
                    JSONArray jsonArray=new JSONArray(s);
                    for (int a = 0; a <jsonArray.length(); a++) {

                        JSONObject on_mentor = jsonArray.getJSONObject(a);
                        try {
                            image = on_mentor.getString("image");
                            description = on_mentor.getString("description");
                            HashMapForURL.put(description, image);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
    }


    @Override
    public void onStop() {
        sliderShow.stopAutoCycle();
        super.onStop();
    }

}
