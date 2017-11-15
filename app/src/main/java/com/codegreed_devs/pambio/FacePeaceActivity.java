package com.codegreed_devs.pambio;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class FacePeaceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_face_peace);
    }
}
