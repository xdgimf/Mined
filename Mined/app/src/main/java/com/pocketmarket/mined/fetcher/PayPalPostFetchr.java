package com.pocketmarket.mined.fetcher;

import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public class PayPalPostFetchr extends MainFetchr {

    private static final String TAG = "PayPalPostFetchr";
    private static final String USER_ACCESSTOKEN = "userAccessToken";
    private static final String PRODUCTID = "productId";
    private static final String AMOUNT = "amount";


    public String fetchItems(String sUrl, String accessToken, int productId, String amount) {

        String response = null;

        try {
            HashMap<String, String> params = new HashMap<>();
            params.put(USER_ACCESSTOKEN, accessToken);
            params.put(PRODUCTID, Integer.toString(productId));
            params.put(AMOUNT, amount);

            // request of http post
            response = getUrl(sUrl, METHOD_TYPE_POST, params);

            if (response == null)
                return null;


            // parse the string result from the parser
            JsonElement jsonElement = new JsonParser().parse(response);


            // get the json object response of the string
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (jsonObject == null)
                return null;


            response = jsonObject.get("href").getAsString();
        } catch (IOException e) {
            Log.e(TAG, "Failed to fetch items", e);

        }

        return response;

    }
}

