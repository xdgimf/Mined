package com.pocketmarket.mined.fetcher;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pocketmarket.mined.dto.ChatAssistantDetailsDTO;
import com.pocketmarket.mined.dto.ChatAssistantResultDTO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public class SmartReplyFetchr extends MainFetchr{
    private static final String TAG = "SmartReplyFetchr";
    private static final String MESSAGE = "message";
    private static final String ACCESSTOKEN = "accesstoken";

    public ChatAssistantResultDTO fetchItems(String url, String message, String accesstoken){
        ChatAssistantResultDTO chatAssistantResult = new ChatAssistantResultDTO();

        try {

            HashMap<String, String> params = new HashMap<>();
            params.put(MESSAGE, message);

            String response = getUrl(url, METHOD_TYPE_POST, params, accesstoken);
            Log.i(TAG, "Received json: " + response);

            if (response == null || response.equals(""))
                return null;

            // parse the string result from the parser
            JsonElement jsonElement = new JsonParser().parse(response);

            JsonObject jsonObject = jsonElement.getAsJsonObject();

            String id = jsonObject.get("id").getAsString();
            chatAssistantResult.setId(Integer.parseInt(id));

            if (!jsonObject.get("message").isJsonNull()){
                String message1 = jsonObject.get("message").getAsString();
                chatAssistantResult.setMessage(message1);
            }


            if (!jsonObject.get("message2").isJsonNull()){
                String message2 = jsonObject.get("message2").getAsString();
                chatAssistantResult.setMessage2(message2);
            }

            if (!jsonObject.get("details").isJsonNull()){
                JsonArray jsonArray = jsonObject.get("details").getAsJsonArray();

                List<ChatAssistantDetailsDTO> chatAssistantDetailsList = new ArrayList<ChatAssistantDetailsDTO>();
                for (int i = 0; i < jsonArray.size(); i++){
                    JsonObject detailsObject = jsonArray.get(i).getAsJsonObject();

                    ChatAssistantDetailsDTO chatAssistantDetails = new ChatAssistantDetailsDTO();

                    String detailsid = detailsObject.get("id").getAsString();
                    chatAssistantDetails.setId(Integer.parseInt(detailsid));

                    String name = detailsObject.get("name").getAsString();
                    chatAssistantDetails.setName(name);

                    String description = detailsObject.get("description").getAsString();
                    chatAssistantDetails.setDescription(description);


                    if (!detailsObject.get("image").isJsonNull()){
                        String image = detailsObject.get("image").getAsString();
                        chatAssistantDetails.setImage(image);
                    }

                    if (!detailsObject.get("url").isJsonNull()){
                        String urlLink = detailsObject.get("url").getAsString();
                        chatAssistantDetails.setUrl(urlLink);
                    }


                    if (!detailsObject.get("type").isJsonNull()){
                        String type = detailsObject.get("type").getAsString();
                        chatAssistantDetails.setType(type);
                    }

                    if (!detailsObject.get("productId").isJsonNull()){
                        String productId = detailsObject.get("productId").getAsString();
                        chatAssistantDetails.setProductId(Integer.parseInt(productId));

                    }

                    chatAssistantDetailsList.add(chatAssistantDetails);

                }

                chatAssistantResult.setDetails(chatAssistantDetailsList);

            }

            if (!jsonObject.get("chatType").isJsonNull()){
                String chatTypeValue = jsonObject.get("chatType").getAsString();
                chatAssistantResult.setChatType(Integer.parseInt(chatTypeValue));
            }

            if (!jsonObject.get("status").isJsonNull()){
                String status = jsonObject.get("status").getAsString();
                chatAssistantResult.setStatus(Integer.parseInt(status));
            }


        } catch (IOException e) {
            Log.e(TAG, "Failed to fetch items", e);
        }

        return chatAssistantResult;

    }
}

