package com.pocketmarket.mined.fetcher;

import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pocketmarket.mined.dto.UserDTO;

import java.io.IOException;
import java.math.BigInteger;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public class UserFetchr extends MainFetchr{
    private static final String TAG = "UserFetchr";

    public UserDTO fetchItems(String sUrl) {
        UserDTO user = new UserDTO();

        try {

            String response = getUrl(sUrl, METHOD_TYPE_GET, null);
            Log.i(TAG, "Received json: " + response);

            if (response == null)
                return null;

            // parse the string result from the parser
            JsonElement jsonElement = new JsonParser().parse(response);


            // get the json object response of the string
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (jsonObject == null)
                return null;

            // get the id
            String id = jsonObject.get("id").getAsString();
            user.setId(Integer.parseInt(id));

            String firstName = jsonObject.get("firstname").getAsString();
            user.setFirstname(firstName);

            String lastName = jsonObject.get("lastname").getAsString();
            user.setLastname(lastName);

            String email = jsonObject.get("email").getAsString();
            user.setEmail(email);

            String photo = jsonObject.get("photo").getAsString();
            user.setPhoto(photo);

            String fbuid = jsonObject.get("fbuid").getAsString();
            user.setFbuid(new BigInteger(fbuid));

            String fbAccessToken = jsonObject.get("fb_accesstoken").getAsString();
            user.setFb_accesstoken(fbAccessToken);

            String accessToken = jsonObject.get("accesstoken").getAsString();
            user.setAccesstoken(accessToken);


        } catch (IOException e) {
            Log.e(TAG, "Failed to fetch items", e);

        }

        return user;

    }

}

