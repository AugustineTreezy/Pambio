package com.codegreed_devs.pambio;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import java.net.URI;


public class ChatActivity extends AppCompatActivity {
    private FirebaseListAdapter<ChatMessage> adapter;
    TextView messageText,messageUser,messageTime,message_date,my_messageText,my_messageUser,my_messageTime,my_message_date;
    RelativeLayout their_sms,my_sms;
    ImageView sender_pic;
    ListView listOfMessages;
    String photoUrl="http://www.kimesh.com/pambio/images/social_media_icons/facebook.png";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_chat);
        displayChatMessages();

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
                            .setValue(new ChatMessage(input.getText().toString(),
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
            public void notifyDataSetChanged() {
                super.notifyDataSetChanged();
            }

            @Override
            public void notifyDataSetInvalidated() {
                super.notifyDataSetInvalidated();
            }

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
                sender_pic=v.findViewById(R.id.my_image_message_profile);

                //get the name of the user from firebase and the user texting to compare them
                String f_user=FirebaseAuth.getInstance()
                        .getCurrentUser().getDisplayName();
                String user_me=model.getMessageUser();


                //if is from logged in user use the right side layout and set display name to "Me"
                if (user_me.equals(f_user)){

                    their_sms.setVisibility(View.GONE);
                    my_sms.setVisibility(View.VISIBLE);
                    my_messageText.setText(model.getMessageText());
                    my_messageUser.setText("Me");

                    Picasso
                            .with(getBaseContext())
                            .load(photoUrl)
                            .into(sender_pic);

                    //else use the left side layout
                }else {

                    their_sms.setVisibility(View.VISIBLE);
                    my_sms.setVisibility(View.GONE);
                    messageText.setText(model.getMessageText());
                    messageUser.setText(model.getMessageUser());


                }
                // Format the date before showing it
                messageTime.setText(DateFormat.format("hh:mm a",
                        model.getMessageTime()));
                message_date.setText(DateFormat.format("MMM dd,yyyy",
                        model.getMessageTime()));


            }
        };

        listOfMessages.setAdapter(adapter);
        listOfMessages.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                my_messageText.setTextIsSelectable(true);
                my_messageText.setSelectAllOnFocus(true);

                return false;
            }
        });

    }



}
