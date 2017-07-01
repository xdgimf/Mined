package com.pocketmarket.mined.model;

import java.util.Date;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public class FriendlyMessage {

    private String id;
    private String text;
    private String name;
    private String photoUrl;
    private Date dateCreated;

    public FriendlyMessage() {
    }

    public FriendlyMessage(String text, String name, String photoUrl, Date dateCreated) {
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
        this.dateCreated = dateCreated;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }
}

