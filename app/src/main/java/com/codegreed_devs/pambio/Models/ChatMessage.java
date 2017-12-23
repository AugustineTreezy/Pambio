package com.codegreed_devs.pambio.Models;

import java.util.Date;

/**
 * Created by Augustine on 10/15/2017.
 */


public class ChatMessage {

    private String messageText;
    private String messageUser;
    private long messageTime;
    private String photoUrl;
    private String userId;

    public ChatMessage(String userId, String photoUrl, String messageText, String messageUser) {
        this.userId = userId;
        this.photoUrl=photoUrl;
        this.messageText = messageText;
        this.messageUser = messageUser;




        // Initialize to current date and time
        messageTime = new Date().getTime();

    }

    public ChatMessage(){

    }


    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public String setMessageUser(String messageUser) {
        this.messageUser = messageUser;
        return messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {

        this.messageTime = messageTime;
    }
    public void setPhotoUrl(String photoUrl) {

        this.photoUrl = photoUrl;
    }
    public String getPhotoUrl() {
        return photoUrl;
    }


}