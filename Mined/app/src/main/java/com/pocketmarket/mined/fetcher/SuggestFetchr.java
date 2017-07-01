package com.pocketmarket.mined.fetcher;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pocketmarket.mined.dto.ChatAssistantDetailsSubDTO;
import com.pocketmarket.mined.dto.ChatSuggestionDTO;
import com.pocketmarket.mined.dto.ChatSuggestionDetailsDTO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public class SuggestFetchr extends MainFetchr{

    private static final String TAG = "SuggestFetchr";
    private static final String MESSAGE = "message";

    public ChatSuggestionDTO fetchItems(String url, String message, String accessToken){
        ChatSuggestionDTO chatSuggestionDTO = new ChatSuggestionDTO();

        try {

            HashMap<String, String> params = new HashMap<>();
            params.put(MESSAGE, message);

            String response = getUrl(url, METHOD_TYPE_POST, params, accessToken);
            Log.i(TAG, "Received json: " + response);

            if (response == null)
                return null;

            // parse the string result from the parser
            JsonElement jsonElement = new JsonParser().parse(response);


            // get the json object response of the string
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (jsonObject == null)
                return null;

            String id = jsonObject.get("id").getAsString();
            chatSuggestionDTO.setId(Integer.parseInt(id));


            String name = jsonObject.get("name").getAsString();
            chatSuggestionDTO.setName(name);

            String description = jsonObject.get("description").getAsString();
            chatSuggestionDTO.setDescription(description);

            if (!jsonObject.get("details").isJsonNull()){
                if (!jsonObject.getAsJsonArray("details").isJsonNull()){
                    JsonArray detailsList = jsonObject.getAsJsonArray("details");

                    ArrayList<ChatSuggestionDetailsDTO> chatSuggestionList = new ArrayList<ChatSuggestionDetailsDTO>();
                    for (int i = 0; i < detailsList.size(); i++){

                        ChatSuggestionDetailsDTO chatSuggestion = new ChatSuggestionDetailsDTO();

                        JsonObject chatObject = detailsList.get(i).getAsJsonObject();

                        String detailsId = chatObject.get("id").getAsString();
                        chatSuggestion.setId(Integer.parseInt(detailsId));

                        String detailsName = chatObject.get("name").getAsString();
                        chatSuggestion.setName(detailsName);

                        String detailsDescription = chatObject.get("description").getAsString();
                        chatSuggestion.setDescription(detailsDescription);


                        if (!chatObject.get("image").isJsonNull()){
                            String image = chatObject.get("image").getAsString();
                            chatSuggestion.setImage(image);
                        }


                        if (!chatObject.get("url").isJsonNull()){
                            String imageUrl = chatObject.get("url").getAsString();
                            chatSuggestion.setUrl(imageUrl);
                        }

                        if (!chatObject.get("type").isJsonNull()){
                            String type = chatObject.get("type").getAsString();
                            chatSuggestion.setType(type);
                        }


                        // get the details sub information
                        if (!chatObject.get("detailsSub").isJsonNull()) {
                            JsonArray jsonSubArray = chatObject.get("detailsSub").getAsJsonArray();

                            ArrayList<ChatAssistantDetailsSubDTO> chatAssistantDetailsSubList = new ArrayList<ChatAssistantDetailsSubDTO>();
                            for (int i1 = 0; i1 < jsonSubArray.size(); i1++) {
                                JsonObject detailsSubObject = jsonSubArray.get(i1).getAsJsonObject();

                                ChatAssistantDetailsSubDTO chatAssistantDetailsSub = new ChatAssistantDetailsSubDTO();

                                String subId = detailsSubObject.get("id").getAsString();
                                chatAssistantDetailsSub.setId(Integer.parseInt(subId));

                                String subName = detailsSubObject.get("name").getAsString();
                                chatAssistantDetailsSub.setName(subName);

                                String subDescription = detailsSubObject.get("description").getAsString();
                                chatAssistantDetailsSub.setDescription(subDescription);

                                if (!detailsSubObject.get("image").isJsonNull()) {
                                    String subImage = detailsSubObject.get("image").getAsString();
                                    chatAssistantDetailsSub.setImage(subImage);

                                }

                                if (!detailsSubObject.get("url").isJsonNull()) {
                                    String subRurl = detailsSubObject.get("url").getAsString();
                                    chatAssistantDetailsSub.setUrl(subRurl);
                                }

                                if (!detailsSubObject.get("type").isJsonNull()) {
                                    String subType = detailsSubObject.get("type").getAsString();
                                    chatAssistantDetailsSub.setType(subType);
                                }

                                chatAssistantDetailsSubList.add(chatAssistantDetailsSub);


                            }

                            chatSuggestion.setSubDetails(chatAssistantDetailsSubList);
                        }

                        chatSuggestionList.add(chatSuggestion);

                    }

                    chatSuggestionDTO.setDetails(chatSuggestionList);
                }


            }



        } catch (IOException e) {
            Log.e(TAG, "Failed to fetch items", e);
        }

        return chatSuggestionDTO;

    }
}

