package com.pocketmarket.mined.fetcher;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pocketmarket.mined.dto.UserDTO;
import com.pocketmarket.mined.utility.Utils;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public class SigninPostFetchr extends MainFetchr {

    private static final String TAG = "SigninPostFetchr";
    private static final String FB_UID = "fbuid";
    private static final String FIRSTNAME = "firstname";
    private static final String LASTNAME = "lastname";
    private static final String EMAIL = "email";
    private static final String PHOTO = "photo";
    private static final String ACCESSTOKEN = "accesstoken";

    public UserDTO fetchItems(String sUrl, UserDTO user) throws IOException {

        String response = null;

        HashMap<String, String> params = new HashMap<>();
        params.put(FB_UID, String.valueOf(user.getFbuid()));
        params.put(FIRSTNAME, user.getFirstname());
        params.put(LASTNAME, user.getLastname());
        params.put(EMAIL, user.getEmail());
        params.put(PHOTO, user.getPhoto());
        params.put(ACCESSTOKEN, user.getFb_accesstoken());

        // request of http post
        response = getUrl(sUrl, METHOD_TYPE_POST, params);

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

        user = new UserDTO();

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

        return user;

    }
}

