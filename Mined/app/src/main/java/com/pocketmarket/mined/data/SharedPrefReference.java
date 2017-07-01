package com.pocketmarket.mined.data;

import android.content.SharedPreferences;
import android.util.Log;

import com.facebook.login.LoginManager;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */
@Singleton
public class SharedPrefReference {
    private final static String TAG = "SharedPrefReference";

    private SharedPreferences mSharedPrefReferences;

    @Inject
    public SharedPrefReference(SharedPreferences sharedPreferences) {
        mSharedPrefReferences = sharedPreferences;
    }

    /**
     *
     * @param accessToken
     */
    public void addFacebookTokenPreferences(String accessToken){
        SharedPreferences.Editor editor = mSharedPrefReferences.edit();
        editor.putString("fbAccessToken", accessToken);
        editor.apply();

    }

    /**
     * The method to logout facebook but not clear your shared preferences
     */
    public void facebookLogout() {
        Log.e(TAG, "facebookLogout....");
        LoginManager.getInstance().logOut();

    }

    public void test(){
        Log.d(TAG, "This is a test for SharedPrefReference");
    }

    /**
     * The shared preference for accesstoken
     * @param accessToken
     */
    public void addUserPreferences(String accessToken){
        SharedPreferences.Editor editor = mSharedPrefReferences.edit();
        editor.putString("accessToken", accessToken);
        editor.apply();

    }

    /**
     *
     * @return
     */
    public String getUserAccessToken(){
        String accessToken = mSharedPrefReferences.getString("accessToken","");

        Log.i(TAG, "User accessToken: " + accessToken);

        return accessToken;
    }

    /**
     * get the user email from facebook
     * @return
     */
    public String getFacebookEmail(){
        String email = mSharedPrefReferences.getString("email", "");

        Log.i(TAG, "User email: " + email);

        return email;

    }
}
