package com.codegreed_devs.pambio.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import es.dmoral.toasty.Toasty;

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        super.onCreate(savedInstanceState);
        int i=0;
        while (i<3000) {

            i++;
        }

        //initialize toasty
        Toasty.Config.getInstance()
        .apply(); // required


        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            //direct user to login activity
            Intent intent=new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();

        } else {
            // User is already signed in. Therefore, display
            // a welcome Toast with his/her name

            Toasty.success(this,
                    "Welcome " + FirebaseAuth.getInstance()
                            .getCurrentUser()
                            .getDisplayName(),
                    Toast.LENGTH_LONG)
                    .show();


            //direct user to main activity
            Intent intent=new Intent(this,MainActivity.class);
            startActivity(intent);
            finish();

        }

    }


}
