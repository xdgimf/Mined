package com.pocketmarket.mined.dto;

import java.util.List;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public class UploadPhotoDTO {
    private int id;

    private String mImageLink;

    private String mMessage;

    private UploadedFormDTO mUploadedForm;

    private int mUserId;

    private List<ChatAssistantDetailsDTO> details;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageLink() {
        return mImageLink;
    }

    public void setImageLink(String imageLink) {
        mImageLink = imageLink;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public UploadedFormDTO getUploadedForm() {
        return mUploadedForm;
    }

    public void setUploadedForm(UploadedFormDTO uploadedForm) {
        mUploadedForm = uploadedForm;
    }

    public int getUserId() {
        return mUserId;
    }

    public void setUserId(int userId) {
        mUserId = userId;
    }

    public List<ChatAssistantDetailsDTO> getDetails() {
        return details;
    }

    public void setDetails(List<ChatAssistantDetailsDTO> details) {
        this.details = details;
    }

}
