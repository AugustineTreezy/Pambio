package com.codegreed_devs.pambio.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.codegreed_devs.pambio.R;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import es.dmoral.toasty.Toasty;

public class SettingsActivity extends AppCompatActivity {
    ListView settings_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settings_list= (ListView) findViewById(R.id.settings_list);
        //animate layout
        YoYo.with(Techniques.FadeInUp)
                .duration(600)
                .repeat(0)
                .playOn(settings_list);


        String[] items={"Update Profile","Log Out","About App"};
        ArrayAdapter<String> adapter= new ArrayAdapter<>(this, R.layout.list_style, items);

        settings_list.setAdapter(adapter);
        settings_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case(0):
                        Intent update=new Intent(SettingsActivity.this,UpdateProfileActivity.class);
                        startActivity(update);
                        break;
                    case 2:
                        Intent about=new Intent(SettingsActivity.this,AboutAppActivity.class);
                        startActivity(about);
                        break;
                    default:
                        confirm_log_out();
                }
            }
        });


    }

    public void confirm_log_out(){
        AlertDialog.Builder confirmation=new AlertDialog.Builder(SettingsActivity.this);
        confirmation.setTitle(Html.fromHtml("<font color='#000000'>Confirm</font>"));
        confirmation.setIcon(R.drawable.ic_dp);
        confirmation.setCancelable(true);
        confirmation.setMessage(Html.fromHtml("<font color='#000000'>Are You Sure You want to Log Out?</font>"));
        confirmation.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SignOut();

            }
        });
        confirmation.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        confirmation.create().show();
    }
    public void SignOut(){
        AuthUI.getInstance().signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toasty.normal(SettingsActivity.this,
                                "You have been signed out.",
                                Toast.LENGTH_LONG)
                                .show();

                        // Return to sign in
                        Intent intent=new Intent(SettingsActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

    }
}
