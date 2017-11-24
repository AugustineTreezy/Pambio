package com.codegreed_devs.pambio.Activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.codegreed_devs.pambio.Models.ChatMessage;
import com.codegreed_devs.pambio.R;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.EnumSet;

import es.dmoral.toasty.Toasty;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;


public class ChatActivity extends AppCompatActivity {
    private FirebaseListAdapter<ChatMessage> adapter;
    private DatabaseReference mDatabase;
    TextView messageText,messageUser,messageTime,message_date,my_messageText,my_messageUser,my_messageTime,my_message_date;
    RelativeLayout their_sms,my_sms;
    ImageView receivers_pic,full_dp;
    ListView listOfMessages;
    String photoUrl="http://www.kimesh.com/pambio/images/social_media_icons/facebook.png";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_chat);
        displayChatMessages();
//        UpdateUserInfo();

        FloatingActionButton fab =
                (FloatingActionButton)findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText) findViewById(R.id.input);
                //check if the input is empty
                if (input.getText().toString().isEmpty()) {
                    Toast.makeText(ChatActivity.this, "please type something", Toast.LENGTH_SHORT).show();

                } else {

                    // Read the input field and push a new instance
                    // of ChatMessage to the Firebase database

                    FirebaseDatabase.getInstance()
                            .getReference()
                            .push()
                            .setValue(new ChatMessage(FirebaseAuth.getInstance()
                                    .getCurrentUser().getUid(), FirebaseAuth.getInstance()
                                    .getCurrentUser().getPhotoUrl().toString(), input.getText().toString(),
                                    FirebaseAuth.getInstance()
                                            .getCurrentUser()
                                            .getDisplayName())
                            );

                    // Clear the input
                    input.setText("");
                }
            }
        });

     }


    private void displayChatMessages() {
        listOfMessages = (ListView)findViewById(R.id.list_of_messages);

        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
                R.layout.message, FirebaseDatabase.getInstance().getReference()) {

            @Override
            protected void populateView(View v, ChatMessage model, int position) {

                // Get references to the views of message.xml
                messageText = v.findViewById(R.id.message_text);
                messageUser = v.findViewById(R.id.message_user);
                messageTime = v.findViewById(R.id.message_time);
                message_date=v.findViewById(R.id.message_date);
                my_messageText=v.findViewById(R.id.my_message_text);
                my_messageUser = v.findViewById(R.id.my_message_user);
                my_messageTime = v.findViewById(R.id.my_message_time);
                my_message_date=v.findViewById(R.id.my_message_date);
                their_sms=v.findViewById(R.id.their_sms);
                my_sms=v.findViewById(R.id.my_sms);
                receivers_pic=v.findViewById(R.id.receivers_pic);

                //get the id of the user from firebase and the current user to compare them
                String f_user= FirebaseAuth.getInstance()
                        .getCurrentUser().getUid();
                String user_me=model.getUserId();

                //if is from logged in user use the right side layout and set display name to "Me"
                if (user_me.equals(f_user)){
                    Uri f=FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();

                    their_sms.setVisibility(View.GONE);
                    my_sms.setVisibility(View.VISIBLE);
                    my_messageText.setText(model.getMessageText());
                    my_messageUser.setText(R.string.me);

                    my_messageTime.setText(DateFormat.format("hh:mm a",
                            model.getMessageTime()));
                    my_message_date.setText(DateFormat.format("MMM dd,yyyy",
                            model.getMessageTime()));

                }else {

                    their_sms.setVisibility(View.VISIBLE);
                    my_sms.setVisibility(View.GONE);
                    messageText.setText(model.getMessageText());
                    messageUser.setText(model.getMessageUser());
                    // Format the date before showing it
                    messageTime.setText(DateFormat.format("hh:mm a",
                            model.getMessageTime()));
                    message_date.setText(DateFormat.format("MMM dd,yyyy",
                            model.getMessageTime()));
                    String profile=model.getPhotoUrl();
                    final Uri prof_url=Uri.parse(profile);
                    receivers_pic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showImage(prof_url);

                        }
                    });

                    if (profile != null) {

                        Picasso
                                .with(getBaseContext())
                                .load(prof_url)
                                .transform(new CropCircleTransformation())
                                .resize(1024, 1024)
                                .placeholder(R.drawable.ic_dp)
                                .into(receivers_pic);

                    }


                }


            }
        };


        listOfMessages.setAdapter(adapter);
        listOfMessages.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                my_messageText.setTextIsSelectable(true);
                my_messageText.setSelectAllOnFocus(true);
                messageText.setTextIsSelectable(true);
                messageText.setSelectAllOnFocus(true);

                return false;
            }
        });

    }

    public void showImage(Uri prof_url) {
        Dialog builder = new Dialog(this);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //nothing;
            }
        });

        ImageView imageView = new ImageView(this);
        Picasso
                .with(getBaseContext())
                .load(prof_url)
                .resize(512, 512)
                .into(imageView);
        builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        builder.show();

    }



}
