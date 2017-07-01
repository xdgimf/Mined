package com.pocketmarket.mined.dto;

import java.util.List;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public class ChatAssistantResultDTO {
    private int id;

    private String message;

    private String message2;

    private List<ChatAssistantDetailsDTO> details;

    private int chatType;

    private int status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage2() {
        return message2;
    }

    public void setMessage2(String message2) {
        this.message2 = message2;
    }

    public List<ChatAssistantDetailsDTO> getDetails() {
        return details;
    }

    public void setDetails(List<ChatAssistantDetailsDTO> details) {
        this.details = details;
    }

    public int getChatType() {
        return chatType;
    }

    public void setChatType(int chatType) {
        this.chatType = chatType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

