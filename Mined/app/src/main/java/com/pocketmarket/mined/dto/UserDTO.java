package com.pocketmarket.mined.dto;

import java.math.BigInteger;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public class UserDTO {
    private int id;

    private String firstname;

    private String lastname;

    private String email;

    private BigInteger fbuid;

    private String fb_accesstoken;

    private String accesstoken;

    private String photo;

    public UserDTO(){

    }

    public UserDTO(String firstname, String lastname, String email, String photo, BigInteger fbuid, String fb_accesstoken, String accesstoken) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.photo = photo;
        this.fbuid = fbuid;
        this.fb_accesstoken = fb_accesstoken;
        this.accesstoken = accesstoken;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BigInteger getFbuid() {
        return fbuid;
    }

    public void setFbuid(BigInteger fbuid) {
        this.fbuid = fbuid;
    }

    public String getFb_accesstoken() {
        return fb_accesstoken;
    }

    public void setFb_accesstoken(String fb_accesstoken) {
        this.fb_accesstoken = fb_accesstoken;
    }

    public String getAccesstoken() {
        return accesstoken;
    }

    public void setAccesstoken(String accesstoken) {
        this.accesstoken = accesstoken;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
