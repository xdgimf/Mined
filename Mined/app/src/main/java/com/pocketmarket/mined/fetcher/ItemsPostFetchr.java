package com.pocketmarket.mined.fetcher;

import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pocketmarket.mined.dto.ItemsDTO;
import com.pocketmarket.mined.utility.Utils;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public class ItemsPostFetchr extends MainFetchr{
    private static final String TAG = "ItemsFetchr";
    private static final String ID = "id";
    private static final String ACCESS_TOKEN = "accesstoken";

    public ItemsDTO fetchItems(String sUrl, int id, String accessToken) {

        String response = null;
        ItemsDTO items = null;

        try {

            HashMap<String, String> params = new HashMap<>();
            params.put(ID, Integer.toString(id));

            // request of http post
            response = getUrl(sUrl, METHOD_TYPE_POST, params, accessToken);


            if (response == null)
                return null;

            if (!Utils.isJSONValid(response))
                return null;


            // parse the string result from the parser
            JsonElement jsonElement = new JsonParser().parse(response);


            // get the json object response of the string
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (jsonObject == null)
                return null;

            items = new ItemsDTO();

            // get the id
            String itemId = jsonObject.get("id").getAsString();
            items.setId(Integer.parseInt(itemId));

            String name = jsonObject.get("name").getAsString();
            items.setName(name);

            String photoid = jsonObject.get("photoid").getAsString();
            items.setPhotoid(Integer.parseInt(photoid));

            String photo = jsonObject.get("photo").getAsString();
            items.setPhoto(photo);

            String price = jsonObject.get("price").getAsString();
            items.setPrice(Double.parseDouble(price));


        } catch (IOException e) {
            Log.e(TAG, "Failed to fetch items", e);

        }

        return items;

    }

}

