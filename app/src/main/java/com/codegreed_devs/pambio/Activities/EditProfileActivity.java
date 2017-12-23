package com.codegreed_devs.pambio.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codegreed_devs.pambio.R;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.EmojiTextView;
import com.vanniktech.emoji.ios.IosEmojiProvider;

import net.steamcrafted.loadtoast.LoadToast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

import static com.theartofdev.edmodo.cropper.CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE;

public class EditProfileActivity extends AppCompatActivity {
    ImageView user_dp;
    EmojiEditText user_name;
    TextView email;
    EmojiTextView user_name_text;
    Button update_btn;
    AlertDialog.Builder popDialog;
    CardView profile_card;
    LoadToast lt;
    Uri prof_url = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
    String user_email=FirebaseAuth.getInstance().getCurrentUser().getEmail();
    String result,pic_url,display_name;
    String user_id=FirebaseAuth.getInstance().getCurrentUser().getUid();
    boolean connected = false;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EmojiManager.install(new IosEmojiProvider());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        user_dp=(ImageView) findViewById(R.id.user_dp);
        user_name=(EmojiEditText) findViewById(R.id.user_name);
        update_btn=(Button) findViewById(R.id.update_btn);
        profile_card= (CardView) findViewById(R.id.profile_card);
        email= (TextView) findViewById(R.id.email);
        user_name_text= (EmojiTextView) findViewById(R.id.user_name_text);


        lt = new LoadToast(EditProfileActivity.this);
        lt.setTranslationY(1020);
        lt.setTextColor(Color.WHITE).setBackgroundColor(Color.BLUE);

        //animate layout
        YoYo.with(Techniques.FadeInUp)
                .duration(600)
                .repeat(0)
                .playOn(profile_card);

        LoadPicUrl();
        DisplayInfo();

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
                String name= String.valueOf(user_name_text.getText());
                if (name.equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()) & (result == null)) {
                    Toasty.info(EditProfileActivity.this, "You have not made any changes", Toast.LENGTH_SHORT).show();

                }else {
                    ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                    assert connectivityManager != null;
                    if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                        //we are connected to a network

                        lt.show();
                        if (result == null) {
                            //if the image result is empty, the user has not selected image, so we update username only
                            UpdateName(name);
                            lt.setText("updating...");
                        } else {
                            //The user has updated both name and profile pic
                            UploadImage();
                            lt.setText("updating...");
                        }
                    }else {
                        //no internet connection
                        connected = false;
                        Toasty.error(EditProfileActivity.this, "No internet connection!", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        user_name_text.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= user_name_text.getRight() - user_name_text.getTotalPaddingRight()) {
                        // open change name dialog on drawable click
                        ChangeNameDialog();

                        return true;
                    }
                }
                return true;
            }
        });

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
    public void DisplayInfo(){
        display_name= FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        email.setText(user_email);
        user_name_text.setText(display_name);

    }
    

    /* Choose an image from Gallery */
    void openImageChooser() {
        // start picker to get image for cropping and then use the image in cropping activity
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .setCropMenuCropButtonTitle("Done")
                .start(this);
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (reqCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                try {
                    getRealPathFromURI(resultUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Toast.makeText(this, "Uri"+resultUri, Toast.LENGTH_SHORT).show();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }
    private void getRealPathFromURI(Uri resultUri) throws IOException {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(EditProfileActivity.this, resultUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        result = cursor.getString(column_index);
        cursor.close();


        //get file
        File photo = new File(result);
        String path= photo.getPath();

//        InputStream is = getContentResolver().openInputStream(resultUri);
//        Bitmap bitmap = BitmapFactory.decodeStream(is);

//        is.close();



        //Display the image before uploading
        Picasso
                .with(getBaseContext())
                .load(resultUri)
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
                    Toasty.error(EditProfileActivity.this, "Error updating image", Toast.LENGTH_SHORT).show();
                }

            }
        });

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
                            DisplayInfo();
                            Toasty.success(EditProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                        }else {
                            lt.hide();
                            Toasty.error(EditProfileActivity.this, "Error updating profile", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
    public void ChangeNameDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.edit_prof_custom_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = dialogView.findViewById(R.id.user_name);
        edt.setText(user_name_text.getText());

        dialogBuilder.setTitle(Html.fromHtml("<font color='#000000'>Update name</font>"));
        dialogBuilder.setMessage(Html.fromHtml("<font color='#000000'>Input your new username</font>"));
        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton("Ok", null);
        dialogBuilder.setNegativeButton("Cancel", null);

        final AlertDialog b = dialogBuilder.create();
        b.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name=edt.getText().toString();
                        if (name.isEmpty()){
                            Toasty.error(EditProfileActivity.this, "Username can't be empty", Toast.LENGTH_SHORT).show();
                        }else{
                            user_name_text.setText(name);
                            b.dismiss();

                        }

                    }
                });
            }
        });
        b.show();
    }
    public void UpdateName(String usr_name){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(usr_name)
                .build();


        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            lt.hide();
                            DisplayInfo();
                            LoadPicUrl();
                            Toasty.success(EditProfileActivity.this, "Username updated", Toast.LENGTH_SHORT).show();
                        }else {
                            lt.hide();
                            Toasty.error(EditProfileActivity.this, "Error updating profile", Toast.LENGTH_SHORT).show();
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
            popDialog=new AlertDialog.Builder(EditProfileActivity.this);
            popDialog.setTitle("Permission")
                    .setMessage(Html.fromHtml("<font color='#000000'>Please note that storage permission is needed for you to update your profile image.</font>"))
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
