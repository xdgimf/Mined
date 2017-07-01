package com.pocketmarket.mined.controllers;

import android.app.Activity;
import android.widget.Toast;

import com.pocketmarket.mined.di.ApplicationActivity;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */
@Singleton
public class ErrorMessage {
    Activity mActivity;

    @Inject
    public ErrorMessage(@ApplicationActivity Activity activity) {
        mActivity = activity;
    }

    public void showMessage(String remarks){
        String error = remarks;
        Toast.makeText(mActivity, error, Toast.LENGTH_LONG).show();

    }
}
