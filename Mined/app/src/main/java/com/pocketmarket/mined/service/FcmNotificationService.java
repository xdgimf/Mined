package com.pocketmarket.mined.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.pocketmarket.mined.R;
import com.pocketmarket.mined.activity.FeedsActivity;
import com.pocketmarket.mined.receiver.FcmBroadcastReceiver;
import com.pocketmarket.mined.utility.Utils;

import java.util.Map;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public class FcmNotificationService extends FirebaseMessagingService {
    private final static String TAG = "FcmNotificationService";
    public static int mNumMessages = 0;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String accessToken = preferences.getString("accessToken","");
        Log.i(TAG, "Accesstoken: " + accessToken);

        if (accessToken == null || accessToken.equals(""))
            return;

        SharedPreferences sp = getSharedPreferences(Utils.PREF_KEY, MODE_PRIVATE);
        String deviceToken = sp.getString(Utils.PREF_FCM_TOKEN, "");
        Log.i(TAG, "DeviceToken: " + deviceToken);

        if (deviceToken == null || deviceToken.equals(""))
            return;



        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Map<String, String> data = remoteMessage.getData();
            Log.d(TAG, "Message Received: " + data);

            String message = data.get("message");
            Log.i(TAG, "Your message is : " + message);
            sendNotification(this, message);
        }else{
            // Check if message contains a notification payload.

            String notificationBody = remoteMessage.getNotification().getBody();
            Log.d(TAG, "Message Notification Body: " + notificationBody);
            sendNotification(this, notificationBody);

        }


    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    public static void sendNotification(Context context, String messageBody) {

        Intent intent = new Intent(context, FeedsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.status_bar_icon)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        notificationBuilder.mNumber = mNumMessages;

        notificationBuilder.setNumber(1);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

        FcmBroadcastReceiver.completeWakefulIntent(intent);

    }

}

