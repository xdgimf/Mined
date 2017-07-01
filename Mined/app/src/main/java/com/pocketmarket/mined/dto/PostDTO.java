package com.pocketmarket.mined.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public class PostDTO {
    public String uid;
    public String author;
    public String title;
    public String body;
    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();

    public PostDTO() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public PostDTO(String uid, String author, String title, String body) {
        this.uid = uid;
        this.author = author;
        this.title = title;
        this.body = body;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("title", title);
        result.put("body", body);
        result.put("starCount", starCount);
        result.put("stars", stars);

        return result;
    }

}
