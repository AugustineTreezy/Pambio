package com.codegreed_devs.pambio.Activities;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codegreed_devs.pambio.R;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class SingleEventActivity extends AppCompatActivity {
    TextView single_event_name, single_event_date, single_event_time, single_event_desc, single_event_venue;
    CardView single_event_card_view;
    FloatingActionButton fab;
    AlertDialog.Builder popDialog;
    ImageView single_event_image;
    String my_event_name,my_event_date,my_event_time,my_event_desc,my_event_venue,my_event_image;
    Integer my_event_id;
    LinearLayout single_items_layout;
    AVLoadingIndicatorView avi;


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
        single_items_layout= (LinearLayout) findViewById(R.id.single_items_layout);
        single_event_name = (TextView) findViewById(R.id.single_event_name);
        single_event_date = (TextView) findViewById(R.id.single_event_date);
        single_event_time = (TextView) findViewById(R.id.single_event_time);
        single_event_desc = (TextView) findViewById(R.id.single_event_desc);
        single_event_venue = (TextView) findViewById(R.id.single_event_venue);
        single_event_card_view = (CardView) findViewById(R.id.single_event_card_view);
        single_event_image= (ImageView) findViewById(R.id.single_event_image);
        avi=(AVLoadingIndicatorView) findViewById(R.id.avi);

        //animate layout
        YoYo.with(Techniques.FadeInUp)
                .duration(900)
                .repeat(0)
                .playOn(single_items_layout);

        //populating data into views
        single_event_name.setText(my_event_name);
        single_event_date.setText(my_event_date);
        single_event_time.setText(my_event_time);
        single_event_desc.setText(my_event_desc);
        single_event_venue.setText(my_event_venue);



        //check if url has image url before populating the image
        if (my_event_image.isEmpty()){
            avi.smoothToHide();
            Toasty.info(SingleEventActivity.this, "No event image provided", Toast.LENGTH_SHORT).show();
        }else {

            Picasso
                    .with(SingleEventActivity.this)
                    .load(my_event_image)
                    .into(single_event_image, new Callback() {
                        @Override
                        public void onSuccess() {
                            avi.smoothToHide();

                        }

                        @Override
                        public void onError() {
                            avi.smoothToHide();
                            Toasty.error(SingleEventActivity.this, "Unable to load event image", Toast.LENGTH_SHORT).show();

                        }
                    });
        }



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm_add_event();

            }
        });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode==100 && grantResults[0]==PackageManager.PERMISSION_GRANTED){


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

        // dt_start
        String start_time;
        start_time = my_event_time.substring(0, 5);
        String cal_start_time=my_event_date+" "+start_time+":00 GMT+03:00";

        //dt_end
        assert my_event_time != null;
        String end_time = my_event_time.substring(my_event_time.length() - 5);
        String cal_end_time=my_event_date+" "+end_time+":00 GMT+03:00";

        //setting the date and time to the required format
        Date localTime = null;
        Date localTime2 = null;
        try {
                localTime = new SimpleDateFormat("dd MMM yyyy HH:mm:ss z", Locale.getDefault()).parse(cal_start_time);
            localTime2 = new SimpleDateFormat("dd MMM yyyy HH:mm:ss z", Locale.getDefault()).parse(cal_end_time);

            String cal_event_desc = my_event_desc.substring(0, 100) + "...";
            ContentResolver contentResolver = SingleEventActivity.this.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(CalendarContract.Events.TITLE, String.valueOf(my_event_name));
            contentValues.put(CalendarContract.Events.DESCRIPTION, cal_event_desc);
            contentValues.put(CalendarContract.Events.EVENT_LOCATION, my_event_venue);
            contentValues.put(CalendarContract.Events.DTSTART, localTime.getTime());
            contentValues.put(CalendarContract.Events.DTEND, localTime2.getTime());
            contentValues.put(CalendarContract.Events.CALENDAR_ID, 1);
            contentValues.put(CalendarContract.Events.ORGANIZER, "pambio.org");
            contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, true);
            contentValues.put(CalendarContract.Events.HAS_ALARM, true);
            contentValues.put(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, true);
            contentValues.put(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS, true);
            ContentResolver cr = getContentResolver();
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Reminders.MINUTES, 15);
            values.put(CalendarContract.Reminders.EVENT_ID, 1);
            values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);

            if (ActivityCompat.checkSelfPermission(SingleEventActivity.this, android.Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            if (ActivityCompat.checkSelfPermission(SingleEventActivity.this, android.Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {

                return;
            }

            try{
                    Uri uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, contentValues);
                    Uri reminder = cr.insert(CalendarContract.Reminders.CONTENT_URI, values);
                    Toasty.success(SingleEventActivity.this, "Event successfully added to calender", Toast.LENGTH_LONG).show();

            }catch (IllegalArgumentException e){
                Toasty.error(SingleEventActivity.this, "An error occured while adding the event", Toast.LENGTH_LONG).show();
            }



        } catch (java.text.ParseException e) {
            e.printStackTrace();
            Toasty.error(this, "Unable to add the event to calender", Toast.LENGTH_LONG).show();
        }


    }
    private void check_permission(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED){

            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                requestPermissions(new String[]{android.Manifest.permission.WRITE_CALENDAR},100);

            }

        }

    }
    public void confirm_add_event(){
        AlertDialog.Builder confirmation=new AlertDialog.Builder(SingleEventActivity.this);
        confirmation.setTitle(Html.fromHtml("<font color='#000000'>Confirm</font>"));
        confirmation.setIcon(R.drawable.ic_date);
        confirmation.setCancelable(true);
        confirmation.setMessage(Html.fromHtml("<font color='#000000'>Add this event to your calender?</font>"));
        confirmation.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                check_permission();
                create_event();

            }
        });
        confirmation.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        confirmation.create().show();
    }

}
