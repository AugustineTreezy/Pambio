package com.codegreed_devs.pambio.Activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.codegreed_devs.pambio.R;

import es.dmoral.toasty.Toasty;

public class AboutAppActivity extends AppCompatActivity {
    TextView app_version;
    CardView contact_dev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        contact_dev= (CardView) findViewById(R.id.contact_dev);
        app_version= (TextView) findViewById(R.id.app_version);

        /*version name*/
        PackageManager manager = this.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
//            String packageName = info.packageName;
//            int versionCode = info.versionCode;
            String versionName = info.versionName;
            app_version.setText(versionName);
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
        }

        //gmail email sending intent
        final Intent contact = new Intent(Intent.ACTION_SEND);
        contact.setType("plain/text");
        contact.putExtra(Intent.EXTRA_EMAIL  , new String[]{"codegreeddevelopers@gmail.com"});
        contact.putExtra(Intent.EXTRA_TEXT   , "");
        contact.setPackage("com.google.android.gm");

        //other mail sending intent
        final Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("plain/text");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"codegreeddevelopers@gmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "");
        i.putExtra(Intent.EXTRA_TEXT   , "");
        i.setPackage("com.google.android.gm");

        contact_dev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    try{
                        startActivity(contact);
                    }catch (ActivityNotFoundException e){
                        startActivity(Intent.createChooser(i, "Send mail..."));
                    }

                }catch (ActivityNotFoundException e){
                    Toasty.error(AboutAppActivity.this,"There is no email client installed.", Toast.LENGTH_SHORT).show();

                }

            }
        });
    }
}
