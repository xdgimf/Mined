package com.pocketmarket.mined.fetcher;

import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pocketmarket.mined.dto.FirebaseDTO;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public class FirebasePostFetchr extends MainFetchr {

    private static final String TAG = "SigninPostFetchr";
    private static final String UID = "uid";

    public FirebaseDTO fetchItems(String sUrl, String uid) {

        FirebaseDTO firebaseDTO = new FirebaseDTO();


        try{

            HashMap<String, String> params = new HashMap<>();
            params.put(UID, uid);

            // request of http get
            String response = getUrl(sUrl, METHOD_TYPE_POST, params);

            if (response == null)
                return null;

            // parse the string result from the parser
            JsonElement jsonElement = new JsonParser().parse(response);


            // get the json object response of the string
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (jsonObject == null)
                return null;

            String accessToken = jsonObject.get("accessToken").getAsString();
            firebaseDTO.setAccessToken(accessToken);


        }catch (IOException e) {
            Log.e(TAG, "Failed to fetch items", e);
        }

        return firebaseDTO;

    }
}

