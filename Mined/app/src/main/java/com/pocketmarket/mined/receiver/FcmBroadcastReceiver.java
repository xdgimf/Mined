package com.pocketmarket.mined.receiver;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.pocketmarket.mined.service.FcmNotificationService;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public class FcmBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Explicitly specify that FcmInstanceService will handle the intent.
        ComponentName comp = new ComponentName(context.getPackageName(),
                FcmNotificationService.class.getName());
        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}

