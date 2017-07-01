package com.pocketmarket.mined.activity;

import android.support.v4.app.Fragment;

import com.pocketmarket.mined.SingleFragmentActivity;
import com.pocketmarket.mined.fragments.FeedsFragment;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public class FeedsActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new FeedsFragment();
    }
}

