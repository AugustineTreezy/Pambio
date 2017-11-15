package com.codegreed_devs.pambio;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.icu.text.TimeZoneFormat;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class SingleEventActivity extends AppCompatActivity {
    TextView single_event_name, single_event_date, single_event_time, single_event_desc, single_event_venue;
    CardView single_event_card_view;
    FloatingActionButton fab;
    AlertDialog.Builder popDialog;
    ImageView single_event_image;
    String my_event_name,my_event_date,my_event_time,my_event_desc,my_event_venue,my_event_image;
    Integer my_event_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_event);

        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //receiving the data of clicked item from upcoming event fragment
        Bundle bundle = getIntent().getExtras();
        my_event_id=bundle.getInt("event_id");
        my_event_name = bundle.getString("event_name");
        my_event_date = bundle.getString("event_date");
        my_event_time = bundle.getString("event_time");
        my_event_desc = bundle.getString("event_desc");
        my_event_venue = bundle.getString("event_venue");
        my_event_image = bundle.getString("event_image");

        //intializing views
        fab= (FloatingActionButton) findViewById(R.id.fab);
        single_event_name = (TextView) findViewById(R.id.single_event_name);
        single_event_date = (TextView) findViewById(R.id.single_event_date);
        single_event_time = (TextView) findViewById(R.id.single_event_time);
        single_event_desc = (TextView) findViewById(R.id.single_event_desc);
        single_event_venue = (TextView) findViewById(R.id.single_event_venue);
        single_event_card_view = (CardView) findViewById(R.id.single_event_card_view);
        single_event_image= (ImageView) findViewById(R.id.single_event_image);

        //populating data into views
        single_event_name.setText(my_event_name);
        single_event_date.setText(my_event_date);
        single_event_time.setText(my_event_time);
        single_event_desc.setText(my_event_desc);
        single_event_venue.setText(my_event_venue);

        //check if url has image url before populating the image
        if (my_event_image.isEmpty()){
            Toast.makeText(SingleEventActivity.this, "No event image provided", Toast.LENGTH_SHORT).show();
        }else {

            Picasso
                    .with(SingleEventActivity.this)
                    .load(my_event_image)
                    .into(single_event_image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Toast.makeText(SingleEventActivity.this, "Unable to load event image", Toast.LENGTH_SHORT).show();

                        }
                    });
        }



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_permission();
                create_event();

            }
        });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode==100 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            create_event();


        }else {
            popDialog=new AlertDialog.Builder(SingleEventActivity.this);
            popDialog.setTitle("Permission")
                    .setMessage(Html.fromHtml("<font color='#000000'>Please note that permission is needed so that the event can be added to your calender.</font>"))
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            check_permission();

                        }
                    });
            popDialog.create().show();



        }
    }
    private void create_event() {
        Toast.makeText(this, "my event id is: "+my_event_id, Toast.LENGTH_SHORT).show();
        //The date/time to be used on calender

        // dt_start
        String start_time;
        start_time = my_event_time.substring(0, 5);
        String cal_start_time=my_event_date+" "+start_time+":00 GMT";

        //dt_end
        assert my_event_time != null;
        String end_time = my_event_time.substring(my_event_time.length() - 5);
        String cal_end_time=my_event_date+" "+end_time+":00 GMT";

        //setting the date and time to the required format
        Date localTime = null;
        Date localTime2 = null;
        try {
            localTime = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.getDefault()).parse(cal_start_time);
            localTime2 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.getDefault()).parse(cal_end_time);

            String cal_event_desc = my_event_desc.substring(0, 100) + "...";
            ContentResolver contentResolver = SingleEventActivity.this.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(CalendarContract.Events.TITLE, String.valueOf(my_event_name));
            contentValues.put(CalendarContract.Events.DESCRIPTION, cal_event_desc);
            contentValues.put(CalendarContract.Events.EVENT_LOCATION, my_event_venue);
            contentValues.put(CalendarContract.Events.DTSTART, localTime.getTime());
            contentValues.put(CalendarContract.Events.DTEND, localTime2.getTime());
            contentValues.put(CalendarContract.Events.CALENDAR_ID, my_event_id);
            contentValues.put(CalendarContract.Events.ORGANIZER, "pambio.org");
            contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, true);
            contentValues.put(CalendarContract.Events.HAS_ALARM, true);
            contentValues.put(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, true);

            if (ActivityCompat.checkSelfPermission(SingleEventActivity.this, android.Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            if (ActivityCompat.checkSelfPermission(SingleEventActivity.this, android.Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            try{
                    Uri uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, contentValues);
                    Toast.makeText(SingleEventActivity.this, "Event successfully added to calender", Toast.LENGTH_LONG).show();

            }catch (IllegalArgumentException e){
                Toast.makeText(SingleEventActivity.this, "Your calender is not supported, Install latest version of google calender", Toast.LENGTH_LONG).show();
            }



        } catch (java.text.ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Unable to add the event to calender", Toast.LENGTH_LONG).show();
        }


    }
    private void check_permission(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED){

            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                requestPermissions(new String[]{android.Manifest.permission.WRITE_CALENDAR},100);

            }

        }

    }

}
