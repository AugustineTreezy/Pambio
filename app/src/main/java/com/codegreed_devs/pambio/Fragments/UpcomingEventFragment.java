package com.codegreed_devs.pambio.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.codegreed_devs.pambio.Adapters.UpcomingEventsAdapter;
import com.codegreed_devs.pambio.R;
import com.codegreed_devs.pambio.Activities.SingleEventActivity;
import com.codegreed_devs.pambio.Models.UpcomingEventsItems;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.wang.avi.AVLoadingIndicatorView;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import cz.msebera.android.httpclient.Header;

public class UpcomingEventFragment extends Fragment {
    UpcomingEventsAdapter upcomingEventsAdapter;
    ListView event_items;
    View rootView;
    FrameLayout upcoming_events_layout,no_network;
    AVLoadingIndicatorView avi;
    TextView no_network_text;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        rootView = inflater.inflate(R.layout.activity_upcoming_event, container, false);
        avi=rootView.findViewById(R.id.avi);
        no_network=rootView.findViewById(R.id.no_network);
        no_network_text=rootView.findViewById(R.id.no_network_text);

        //hiding elements while loading


        ConnectivityManager connMgr = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connMgr != null;
        NetworkInfo networkInfo;
        networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {


            upcoming_events_layout=rootView.findViewById(R.id.upcoming_events_layout);

            //getting the list view from the xml file
            event_items= rootView.findViewById(R.id.event_items);


            //initializing the array_adapter
            final ArrayList<UpcomingEventsItems> array_of_upcoming_events=new ArrayList<>();
            upcomingEventsAdapter=new UpcomingEventsAdapter(getContext(),0,array_of_upcoming_events);

            AsyncHttpClient asyncHttpClient=new AsyncHttpClient();
            asyncHttpClient.post("http://www.kimesh.com/pambio/upcoming_events.php", new TextHttpResponseHandler() {
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
                            t.detach(UpcomingEventFragment.this).attach(UpcomingEventFragment.this).commitAllowingStateLoss();
                        }
                    });

                }


                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    //animate layout
                    YoYo.with(Techniques.FadeInUp)
                            .duration(900)
                            .repeat(0)
                            .playOn(upcoming_events_layout);
                    avi.smoothToHide();
                    event_items.setVisibility(View.VISIBLE);

                    try {
                        JSONArray items=new JSONArray(s);
                        ArrayList<UpcomingEventsItems> events=UpcomingEventsItems.fromJson(items);
                        upcomingEventsAdapter.addAll(events);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //end of getting JSON Data
                    avi.smoothToHide();

                }
            });


            event_items.setAdapter(upcomingEventsAdapter);

            event_items.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String single_event_name=array_of_upcoming_events.get(position).event_name;
                    Integer single_id=array_of_upcoming_events.get(position).event_id;
                    String single_date=array_of_upcoming_events.get(position).date;
                    String single_time=array_of_upcoming_events.get(position).time;
                    String single_desc=array_of_upcoming_events.get(position).desc;
                    String single_venue=array_of_upcoming_events.get(position).venue;
                    String single_image=array_of_upcoming_events.get(position).image;
                    Intent intent = new Intent(getContext(), SingleEventActivity.class);
                    intent.putExtra("event_id", single_id);
                    intent.putExtra("event_name", single_event_name);
                    intent.putExtra("event_date", single_date);
                    intent.putExtra("event_time", single_time);
                    intent.putExtra("event_desc", single_desc);
                    intent.putExtra("event_venue", single_venue);
                    intent.putExtra("event_image",single_image);
                    startActivity(intent);

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
                    t.detach(UpcomingEventFragment.this).attach(UpcomingEventFragment.this).commitAllowingStateLoss();
                }
            });

        }

        return rootView;
    }
}
