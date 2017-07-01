package com.pocketmarket.mined.fetcher;

import android.util.Log;

import com.pocketmarket.mined.utility.AppApi;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public class DeviceFCMFetchr extends MainFetchr {
    private static final String TAG = "DeviceFCMFetchr";
    private static final String USER_ACCESSTOKEN = "userAccessToken";
    private static final String ACCESSTOKEN = "accesstoken";
    private static String sUrl = AppApi.URL_NAME + AppApi.DEVICES;
    private static final String PRODUCTID = "productId";
    private static final String AMOUNT = "amount";


    public String fetchItems(String accessToken, String deviceToken) {

        String response = null;

        try {
            HashMap<String, String> params = new HashMap<>();
            params.put(USER_ACCESSTOKEN, accessToken);
            params.put(ACCESSTOKEN, deviceToken);

            // request of http post
            response = getUrl(sUrl, METHOD_TYPE_POST, params);

            if (response == null)
                return null;


        } catch (IOException e) {
            Log.e(TAG, "Failed to fetch items", e);

        }

        return response;

    }
}

