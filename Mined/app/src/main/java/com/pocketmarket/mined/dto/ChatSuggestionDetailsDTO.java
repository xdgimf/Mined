package com.pocketmarket.mined.dto;

import java.util.ArrayList;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public class ChatSuggestionDetailsDTO {
    private int id;
    private String name;
    private String description;
    private String image;
    private String url;
    private String type;
    private int productId;
    private ArrayList<ChatAssistantDetailsSubDTO> mSubDetails;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public ArrayList<ChatAssistantDetailsSubDTO> getSubDetails() {
        return mSubDetails;
    }

    public void setSubDetails(ArrayList<ChatAssistantDetailsSubDTO> subDetails) {
        mSubDetails = subDetails;
    }

    @Override
    public String toString() {
        return "Track [id=" + id + ", description=" + description + ", image=" + image + ", url=" + url + ", type=" + type + ", productId=" + productId + ", mSubDetails=" + mSubDetails + "]";
    }

}
