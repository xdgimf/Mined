package com.pocketmarket.mined.dto;

import java.util.ArrayList;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public class ChatSuggestionDTO {
    private int id;
    private String name;
    private String description;
    private ArrayList<ChatSuggestionDetailsDTO> details;

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

    public ArrayList<ChatSuggestionDetailsDTO> getDetails() {
        return details;
    }
    public void setDetails(ArrayList<ChatSuggestionDetailsDTO> details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "Track [id=" + id + ", description=" + description + ", details=" + details + "]";
    }
}

