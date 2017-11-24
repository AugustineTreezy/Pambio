package com.codegreed_devs.pambio.Fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codegreed_devs.pambio.R;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.wang.avi.AVLoadingIndicatorView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;


/**
 * Created by rufflez on 6/21/15.
 */
public class ContactUsFragment extends Fragment implements OnMapReadyCallback {
    AVLoadingIndicatorView avi;
    FrameLayout contact_us_layout,no_network;
    TextView info,phone1,phone2,email,address,location;
    GoogleMap mGoogleMap;
    MapView mMapView;
    View mView;
    TextView no_network_text;
    LinearLayout contact_us_items;
    private EditText editTextSubject;
    private EditText editTextMessage;
    private Button buttonSend;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mView = inflater.inflate(R.layout.activity_contact_us, container, false);
        //initializng the progress bar
        avi=mView .findViewById(R.id.avi);

        //Initializing the views
        no_network=mView.findViewById(R.id.no_network);
        no_network_text=mView.findViewById(R.id.no_network_text);
        editTextSubject = mView.findViewById(R.id.editTextSubject);
        editTextMessage = mView.findViewById(R.id.editTextMessage);
        contact_us_items=mView.findViewById(R.id.contact_us_items);
        buttonSend = mView.findViewById(R.id.buttonSend);
        contact_us_layout=mView.findViewById(R.id.contact_us_layout);

        //hiding layouts while progress bar loadings
        contact_us_items.setVisibility(View.GONE);

        ConnectivityManager connMgr = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        assert connMgr != null;
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
            AsyncHttpClient asyncHttpClient=new AsyncHttpClient();
        asyncHttpClient.post("http://www.kimesh.com/pambio/contact_us.php", new TextHttpResponseHandler() {
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
                        t.detach(ContactUsFragment.this).attach(ContactUsFragment.this).commitAllowingStateLoss();
                    }
                });

            }


            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                //animate layout
                YoYo.with(Techniques.FadeInUp)
                        .duration(900)
                        .repeat(0)
                        .playOn(contact_us_layout);
                avi.smoothToHide();
                contact_us_items.setVisibility(View.VISIBLE);
                try {
                    JSONArray contact_items=new JSONArray(s);
                    JSONObject item_one=contact_items.getJSONObject(0);
                    JSONObject item_two=contact_items.getJSONObject(1);
                    try {

                        String on_title=item_one.getString("info");
                        String on_phone1=item_two.getString("phone1");
                        String on_phone2=item_two.getString("phone2");
                        String on_email=item_two.getString("email");
                        String on_address=item_two.getString("address");
                        String on_location=item_two.getString("location");
                        info=mView .findViewById(R.id.info);
                        phone1=mView .findViewById(R.id.phone1);
                        phone2=mView .findViewById(R.id.phone2);
                        email=mView .findViewById(R.id.email);
                        address=mView .findViewById(R.id.address);
                        location=mView.findViewById(R.id.location);

                        info.setText(on_title);
                        phone1.setText(on_phone1);
                        phone2.setText(on_phone2);
                        email.setText(on_email);
                        address.setText(on_address);
                        location.setText(on_location);

                    }catch (JSONException e){
                        e.printStackTrace();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


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
                    t.detach(ContactUsFragment.this).attach(ContactUsFragment.this).commitAllowingStateLoss();
                }
            });
        }
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Getting content for email

                String subject = editTextSubject.getText().toString().trim();
                String message = editTextMessage.getText().toString().trim();
                if (message.isEmpty()){
                    Toasty.info(getContext(), "Please type a message", Toast.LENGTH_SHORT).show();
                    YoYo.with(Techniques.Shake)
                            .duration(900)
                            .repeat(0)
                            .playOn(editTextMessage);
                }else {
                final Intent send = new Intent(Intent.ACTION_SEND);
                send.setType("plain/text");
                send.putExtra(Intent.EXTRA_EMAIL  , new String[]{"info@pambio.com"});
                send.putExtra(Intent.EXTRA_SUBJECT   , subject);
                send.putExtra(Intent.EXTRA_TEXT   , message);

                try {
                    send.setPackage("com.google.android.gm");
                    startActivity(send);

                }catch (ActivityNotFoundException e){
                    // need this to prompts email client only
                    send.setType("message/rfc822");
                    startActivity(Intent.createChooser(send, "Choose an Email client"));
                }

                }



            }
        });




        return mView;
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView= mView.findViewById(R.id.map);
        if (mMapView!=null){
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        mGoogleMap=googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setAllGesturesEnabled(true);
        googleMap.getUiSettings().isZoomControlsEnabled();
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.addMarker(new MarkerOptions().position(new LatLng(0.284477,34.755286)).title("Peace Ambassadors Kenya Integration Organization. (Pambio)").snippet("Our location"));
        CameraPosition pambio=CameraPosition.builder().target(new LatLng(0.284477,34.755286)).zoom(16).bearing(0).tilt(45).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(pambio));

    }
}
