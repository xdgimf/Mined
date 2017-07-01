package com.pocketmarket.mined.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.pocketmarket.mined.SingleFragmentActivity;
import com.pocketmarket.mined.fragments.BuyFragment;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public class BuyActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        Bundle bundle = getIntent().getExtras();

        BuyFragment buyFragment = new BuyFragment();
        buyFragment.setArguments(bundle);

        return buyFragment;
    }
}

