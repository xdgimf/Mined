package com.pocketmarket.mined.utility;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.facebook.login.LoginManager;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public class AppApi {
    public static final String TAG = "AppApi";

    public static final String URL_NAME = "http://54f15015.ngrok.io/MinedRest";

    // URL for the photos
    public final static String URL_PHOTOS_URL = "http://kinvo-staging.s3.amazonaws.com/uploads/photo/image/";

    public static final String LOGIN = "/u/auth/token";
    public static final String FIREBASE_SERVICE = "/u/firebaseservice";
    public static final String USER_INFO = "/u/user?accesstoken=";

    // ai
    public static final String CHAT_REPLY = "/u/chatreplyservice";
    public static final String CHAT_SUGGEST = "/u/chatsuggestionservice";

    // photo upload
    public static final String UPLOAD_PHOTO = "/u/uploadphoto";
    public static final String UPLOAD_PHOTO_FACE_DETECTION = "/u/uploadphotoface";

    // photo upload ocr text
    public static final String UPLOAD_PHOTO_OCR_TEXT = "/u/uploadphotoocrtext";

    // photo upload inception
    public static final String UPLOAD_PHOTO_INCEPTION = "/u/uploadphotoinception";

    // photo upload government id
    public static final String UPLOAD_PHOTO_ID = "/u/uploadphotoid";

    // items product
    public static final String ITEMS = "/u/itemsservice";


    // firebase
    public static final String PRODUCTS = "/u/products?accesstoken=";
    public static final String BALANCE_ANALYTICS = "/u/balanceanalytics?accesstoken=";

    // chikka
    public static final String CHIKKA = "https://post.chikka.com/smsapi/request";

    // Paypal
    public static final String PAYPAL_PAYMENT = "/u/paypal/payment";

    public static final String DEVICES = "/u/devices/devicefcm";

    // Application validation
    public static final String APPLICATION_VALIDATION = "/u/applicationvalidationservice";

    public static final String SHARED_PREFERENCE_NAME = "mined-prefs";

    public static void addFacebookTokenPreferences(Activity activity, String accessToken){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("fbAccessToken", accessToken);
        editor.apply();

    }

    // The method to logout facebook but not clear your shared preferences
    public static void facebookLogout() {
        Log.e(TAG, "facebookLogout....");
        LoginManager.getInstance().logOut();

    }

    public static void hideKeyBoard(Activity activity){
        View view = activity.getCurrentFocus();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

    public static String getUserInfo(String accessToken) {
        return URL_NAME + USER_INFO + accessToken;
    }

}

