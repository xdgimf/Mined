package com.pocketmarket.mined.dto;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public class MessageDTO {
    private String mId;
    private String mMessage;
    private String mMessage2;
    private String mSender;
    private String mType;
    private int mSuggestion;
    private String mTime;
    private int mStatus;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public String getMessage2() {
        return mMessage2;
    }

    public void setMessage2(String message2) {
        mMessage2 = message2;
    }

    public String getSender() {
        return mSender;
    }

    public void setSender(String sender) {
        mSender = sender;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public int getSuggestion() {
        return mSuggestion;
    }

    public void setSuggestion(int suggestion) {
        mSuggestion = suggestion;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        mStatus = status;
    }
}
