package com.codegreed_devs.pambio.Activities;

import android.Manifest;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.codegreed_devs.pambio.R;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import net.steamcrafted.loadtoast.LoadToast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class UpdateProfileActivity extends AppCompatActivity {
    ImageView user_dp;
    EditText user_name;
    Button update_btn;
    AlertDialog.Builder popDialog;
    CardView profile_card;
    LoadToast lt;
    Uri prof_url = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
    String result,pic_url,display_name;
    String user_id=FirebaseAuth.getInstance().getCurrentUser().getUid();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        user_dp=(ImageView) findViewById(R.id.user_dp);
        user_name=(EditText) findViewById(R.id.user_name);
        update_btn=(Button) findViewById(R.id.update_btn);
        profile_card= (CardView) findViewById(R.id.profile_card);

        lt = new LoadToast(UpdateProfileActivity.this);
        lt.setTranslationY(1020);
        lt.setTextColor(Color.WHITE).setBackgroundColor(Color.BLUE);

        //animate layout
        YoYo.with(Techniques.FadeInUp)
                .duration(600)
                .repeat(0)
                .playOn(profile_card);

        LoadPicUrl();
        DisplayName();

        user_dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_permission();
                openImageChooser();
            }
        });
        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name= String.valueOf(user_name.getText());

                if (name.isEmpty()){
                    //ensure username is not empty
                    YoYo.with(Techniques.Shake)
                            .duration(900)
                            .repeat(0)
                            .playOn(user_name);
                    Toasty.info(UpdateProfileActivity.this, "Username can't be empty!", Toast.LENGTH_SHORT).show();

                }else {
                    lt.show();
                    if (result==null){
                        //if the image result is empty, the user has not selected image, so we update username only
                        UpdateName();
                        lt.setText("Updating...");
                    }else {
                        //The user has updated both name and profile pic
                        UploadImage();
                        lt.setText("Updating...");
                    }
                }

            }
        });


    }
    /* Choose an image from Gallery */
    void openImageChooser() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 0);
    }
    private void getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(UpdateProfileActivity.this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        result = cursor.getString(column_index);
        cursor.close();


        //get file
        File photo = new File(result);
        String path= photo.getPath();

        //Display the image before is uploaded
        Picasso
                .with(getBaseContext())
                .load(new File(path))
                .transform(new CropCircleTransformation())
                .resize(512, 512)
                .placeholder(R.drawable.profile)
                .centerCrop()
                .into(user_dp);

    }


    public void UploadImage(){
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        long time = cal.getTimeInMillis();
        final String pic_name=user_id+time;
        RequestParams params = new RequestParams();
        try {
            params.put("uploaded_file", new File(result));
            params.put("user_id", user_id);
            params.put("pic_name", pic_name);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        AsyncHttpClient client = new AsyncHttpClient();
        client.post("http://www.kimesh.com/pambio/images/users_profile/update_profile.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                UpdateInfo(pic_name);

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if(i==0) {
                    UpdateInfo(pic_name);
                }else{
                    lt.hide();
                    Toasty.error(UpdateProfileActivity.this, "Error in updating image", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                assert imageUri != null;
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                user_dp.setImageBitmap(selectedImage);
                getRealPathFromURI(imageUri);


            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toasty.error(UpdateProfileActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toasty.info(UpdateProfileActivity.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }
    public void LoadPicUrl(){
        if (prof_url != null) {
            Picasso
                    .with(getBaseContext())
                    .load(prof_url)
                    .transform(new CropCircleTransformation())
                    .resize(512, 512)
                    .centerCrop()
                    .placeholder(R.drawable.profile)
                    .into(user_dp);

        }else {
            Picasso
                    .with(getBaseContext())
                    .load(R.drawable.profile)
                    .transform(new CropCircleTransformation())
                    .resize(512, 512)
                    .centerCrop()
                    .into(user_dp);

            //Else It will display the default dp
        }

    }
    public void DisplayName(){
        display_name= FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        user_name.setText(display_name);

    }
    public void UpdateInfo( String pic_name){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String name= String.valueOf(user_name.getText());

        String extension=result.substring(result.lastIndexOf("."));
        pic_url="http://www.kimesh.com/pambio/images/users_profile/"+pic_name+extension;

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(Uri.parse(pic_url))
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            lt.hide();
                            DisplayName();
                            Toasty.success(UpdateProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
    public void UpdateName(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String name= String.valueOf(user_name.getText());

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();


        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            lt.hide();
                            DisplayName();
                            LoadPicUrl();
                            Toasty.success(UpdateProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                        }
                    }

                });

    }

    private void check_permission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},100);

            }

        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode==100 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            openImageChooser();

        }else {
            popDialog=new AlertDialog.Builder(UpdateProfileActivity.this);
            popDialog.setTitle("Permission")
                    .setMessage(Html.fromHtml("<font color='#000000'>Please note that permission is needed for you to update your profile image.</font>"))
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            check_permission();

                        }
                    });
            popDialog.create().show();



        }
    }
}
