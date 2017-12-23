package com.codegreed_devs.pambio.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.MotionEvent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.EmojiTextView;
import com.vanniktech.emoji.ios.IosEmojiProvider;

import es.dmoral.toasty.Toasty;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import uk.co.senab.photoview.PhotoViewAttacher;


public class ChatActivity extends AppCompatActivity {
    TextView messageTime,message_date,my_messageTime,my_message_date;
    EmojiTextView messageText,my_messageText,my_messageUser,messageUser;
    RelativeLayout their_sms,my_sms,activity_chat;
    ImageView receivers_pic;
    ListView listOfMessages;
    EmojiEditText emojiEditText;
    EmojiPopup emojiPopup;
    String usr_id,usr_name,usr_photoUrl;
    FloatingActionButton fab;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EmojiManager.install(new IosEmojiProvider()); // This line needs to be executed before any usage of EmojiTextView, EmojiEditText or EmojiButton.
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_chat);
        displayChatMessages();

        fab = (FloatingActionButton)findViewById(R.id.fab);
        emojiEditText = (EmojiEditText) findViewById(R.id.emojiEditText);
        activity_chat= (RelativeLayout) findViewById(R.id.activity_chat);

        emojiEditText.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                int start=emojiEditText.getSelectionStart();
                int end=emojiEditText.getSelectionEnd();
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() <= (emojiEditText.getLeft() + emojiEditText.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width())) {
                        //Do your action here
                        emojiPopup = EmojiPopup.Builder.fromRootView(activity_chat).build(emojiEditText);
                        emojiPopup.toggle(); // Toggles visibility of the Popup.

                        return true;
                    }

                }

                return false;
            }

        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //check if the input is empty
                if (emojiEditText.getText().toString().isEmpty()) {
                    Toasty.info(ChatActivity.this, "please type something", Toast.LENGTH_SHORT).show();

                } else {

                    // Read the input field and push a new instance
                    // of ChatMessage to the Firebase database
                    usr_id=FirebaseAuth.getInstance().getCurrentUser().getUid();
                    usr_name=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                    try {
                        usr_photoUrl=FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString();
                    }catch (NullPointerException e){
                        usr_photoUrl="";

                    }


                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child("messages")
                            .push()
                            .setValue(new ChatMessage(usr_id, usr_photoUrl, emojiEditText.getText().toString(),
                                    usr_name
                            ));

                    // Clear the input
                    emojiEditText.setText("");
                }
            }
        });

     }



    private void displayChatMessages() {
        listOfMessages = (ListView)findViewById(R.id.list_of_messages);

        FirebaseListAdapter<ChatMessage> adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
                R.layout.message, FirebaseDatabase.getInstance().getReference().child("messages")) {

            @Override
            protected void populateView(View v, ChatMessage model, int position) {



                    // Get references to the views of message.xml
                messageText = v.findViewById(R.id.message_text);
                messageUser = v.findViewById(R.id.message_user);
                messageTime = v.findViewById(R.id.message_time);
                message_date = v.findViewById(R.id.message_date);
                my_messageText = v.findViewById(R.id.my_message_text);
                my_messageUser = v.findViewById(R.id.my_message_user);
                my_messageTime = v.findViewById(R.id.my_message_time);
                my_message_date = v.findViewById(R.id.my_message_date);
                their_sms = v.findViewById(R.id.their_sms);
                my_sms = v.findViewById(R.id.my_sms);
                receivers_pic = v.findViewById(R.id.receivers_pic);


                //get the id of the user from firebase and the current user to compare them
                String f_user = FirebaseAuth.getInstance()
                        .getCurrentUser().getUid();
                String user_me = model.getUserId();

                //if is from logged in user use the right side layout
                if (user_me.equals(f_user)) {

                    their_sms.setVisibility(View.GONE);
                    my_sms.setVisibility(View.VISIBLE);
                    my_messageText.setText(model.getMessageText());
                    my_messageUser.setText(R.string.you);

                    my_messageTime.setText(DateFormat.format("hh:mm a",
                            model.getMessageTime()));
                    my_message_date.setText(DateFormat.format("MMM dd,yyyy",
                            model.getMessageTime()));

                } else {
                    their_sms.setVisibility(View.VISIBLE);
                    my_sms.setVisibility(View.GONE);
                    messageText.setText(model.getMessageText());
                    messageUser.setText(model.getMessageUser());
                    // Format the date before showing it
                    messageTime.setText(DateFormat.format("hh:mm a",
                            model.getMessageTime()));
                    message_date.setText(DateFormat.format("MMM dd,yyyy",
                            model.getMessageTime()));
                    String profile = model.getPhotoUrl();
                    final Uri prof_url = Uri.parse(profile);
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
                                .resize(256, 256)
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

    private void setTimeTextVisibility(long messageTime, long previousTs) {
        Toast.makeText(this, "Current: "+messageTime, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Previous: "+previousTs, Toast.LENGTH_SHORT).show();
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
        imageView.setScaleType(ImageView.ScaleType.MATRIX);
        Picasso
                .with(getBaseContext())
                .load(prof_url)
                .into(imageView);
        builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        builder.setCancelable(true);
        builder.show();
        PhotoViewAttacher photoAttacher;
        photoAttacher= new PhotoViewAttacher(imageView);
        photoAttacher.update();

    }


}
