package com.pocketmarket.mined.activity;

import android.support.v4.app.Fragment;

import com.pocketmarket.mined.SingleSigninFragmentActivity;
import com.pocketmarket.mined.fragments.SigninFragment;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public class SigninActivity extends SingleSigninFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new SigninFragment();
    }
}
