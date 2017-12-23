package com.codegreed_devs.pambio.Models;

/**
 * Created by Augustine on 12/10/2017.
 */

public class UserInfo {
    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
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

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    private String photoUrl;
    private String userId;
    private String messageUser;


    public UserInfo(String photoUrl,String userId,String messageUser) {
        this.userId = userId;
        this.photoUrl=photoUrl;
        this.messageUser = messageUser;
    }

    public UserInfo() {
    }
}


