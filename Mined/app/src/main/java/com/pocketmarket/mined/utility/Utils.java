package com.pocketmarket.mined.utility;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public class Utils {
    public static final String PREF_KEY = "shared_pref";
    public static final String PREF_STORE_NAME = "store_name";
    public static final String PREF_AVATAR_URL = "avatar_url";
    public static final String PREF_FCM_TOKEN = "fcm_token";

    private final static String NON_THIN = "[^iIl1\\.,']";

    private static int screenWidth = 0;
    private static int screenHeight = 0;

    /** used for time conversion **/
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    /**
     * The method used in webservice parameters
     * @param params
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    // Ellipsis
    private static int textWidth(String str) {
        return (int) (str.length() - str.replaceAll(NON_THIN, "").length() / 2);
    }

    public static String ellipsize(String text, int max) {

        if (textWidth(text) <= max)
            return text;

        // Start by chopping off at the word before max
        // This is an over-approximation due to thin-characters...
        int end = text.lastIndexOf(' ', max - 3);

        // Just one long word. Chop it off.
        if (end == -1)
            return text.substring(0, max-3) + "...";

        // Step forward as long as textWidth allows.
        int newEnd = end;
        do {
            end = newEnd;
            newEnd = text.indexOf(' ', end + 1);

            // No more spaces.
            if (newEnd == -1)
                newEnd = text.length();

        } while (textWidth(text.substring(0, newEnd) + "...") < max);

        return text.substring(0, end) + "...";
    }

    /**
     * Method to animate on cards
     * @param c
     * @return
     */
    public static int getScreenHeight(Context c) {
        if (screenHeight == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenHeight = size.y;
        }

        return screenHeight;
    }

    public static String getChatTime(){
        //If you want the current timestamp :
        Calendar c = Calendar.getInstance();
        long timestamp = c.getTimeInMillis();


        Date dateTime = new Date(timestamp);

        //used for checking the right timedate
        // invoice date used in chat after title
        SimpleDateFormat dfInvoice = new SimpleDateFormat("MMMM dd, yyyy");
        String invoiceDate = dfInvoice.format(dateTime);

        SimpleDateFormat dfPromote = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        // convert the date to string
        String promoteDate = dfPromote.format(dateTime);

        try {
            //Here you set to your timezone
            dfPromote.setTimeZone(TimeZone.getTimeZone("UTC"));
            dateTime = dfPromote.parse(promoteDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        promoteDate = dfPromote.format(dateTime);

        // convert the time into words ago...
        String formatDate = Utils.getTimeAgo(timestamp, promoteDate);

        return formatDate;

    }

    public static String getTimeAgo(long time, String dateTime) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        Log.i("TAG", "Time: " + time);
        long now = getCurrentTime();

        Log.i("TAG", "Time: " + time + ", now: " + now);

//        if (time > now || time <= 0) {
//            return null;
//        }

        if (time <=0){
            return null;
        }

        // TODO: localize
        final long diff = now - time;

        Log.i("TAG", "Differemce: " + diff + ", HOUR_MILLIS: " + HOUR_MILLIS + ", MINUTE_MILLIS: " + MINUTE_MILLIS);

        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            long result = diff / HOUR_MILLIS;

            if (result == 1){
                return "an hour ago";
            }else{
                return result + " hours ago";
            }

        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else if (diff < 31 * DAY_MILLIS) {
            return diff / DAY_MILLIS + " days ago";
        }else{

            return Utils.getTimeAgoString(dateTime);
        }
    }

    private static long getCurrentTime(){
        return System.currentTimeMillis();


    }

    public static String getTimeAgoString(String dateTime){
        long timePromoted = getMilliSeconds(dateTime);
        long now = System.currentTimeMillis();

        return DateUtils.getRelativeTimeSpanString(timePromoted, now, DateUtils.DAY_IN_MILLIS).toString();
    }

    public static long getMilliSeconds(String sDate){
        long milliseconds = 0;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            Date rDate = df.parse(sDate);
            Log.i("TAG", "rDate: " + rDate + ", rDate milliseconds: " + rDate.getTime());

            milliseconds = rDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return milliseconds;
    }

    public static boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    public static int getScreenWidth(Context c) {
        if (screenWidth == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenWidth = size.x;
        }

        return screenWidth;
    }

}
