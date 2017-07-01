package com.pocketmarket.mined.service;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.pocketmarket.mined.fetcher.DeviceFCMFetchr;
import com.pocketmarket.mined.utility.Utils;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public class FcmInstanceIdService extends FirebaseInstanceIdService {
    private final static String TAG = "FcmInstanceIdService";

    @Override
    public void onTokenRefresh() {
        Log.d(TAG, "Im here.");
        // Get updated InstanceId Token
        String refreshToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refresh Token: " + refreshToken);

        // Code for saving device token in user prefs
        // When user logged in or open app with session retrieve token on user prefs and call the API for device token.
        SharedPreferences sp = getSharedPreferences(Utils.PREF_KEY, MODE_PRIVATE);
        sp.edit().putString(Utils.PREF_FCM_TOKEN, refreshToken).commit();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String accessToken = preferences.getString("accessToken","");

        Log.i(TAG, "accessToken: " + accessToken);

        if (accessToken != null && !accessToken.equals("")) {
            new DeviceFCMFetchr().fetchItems(accessToken, refreshToken);
        }

    }
}


