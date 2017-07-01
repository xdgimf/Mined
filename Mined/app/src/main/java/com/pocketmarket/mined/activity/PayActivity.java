package com.pocketmarket.mined.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.pocketmarket.mined.SingleFragmentActivity;
import com.pocketmarket.mined.fragments.PayFragment;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public class PayActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        Bundle bundle = getIntent().getExtras();

        PayFragment payFragment = new PayFragment();
        payFragment.setArguments(bundle);

        return payFragment;
    }
}

