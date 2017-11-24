package com.codegreed_devs.pambio.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import com.codegreed_devs.pambio.R;
import com.firebase.ui.auth.AuthUI;
import java.util.Arrays;
import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity {
    private int SIGN_IN_REQUEST_CODE=10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Start sign in/sign up activity
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.FirebaseLoginTheme)
                        .setLogo(R.drawable.logo)
                        .setProviders(Arrays.asList(
                                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build()))
                        .setIsSmartLockEnabled(false)
                        .build(),
                SIGN_IN_REQUEST_CODE
        );

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SIGN_IN_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                //if successfully sign in, toast welcome and direct to main activity
                Toasty.success(this,
                        "Successfully signed in. Welcome!",
                        Toast.LENGTH_LONG)
                        .show();
                Intent intent=new Intent(this,MainActivity.class);
                startActivity(intent);
                finish();

            } else {
                Toasty.error(this,
                        "We couldn't sign you in. Please try again later.",
                        Toast.LENGTH_LONG)
                        .show();

                // Close the app
                finish();
            }
        }
    }


}
