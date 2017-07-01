package com.pocketmarket.mined;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public abstract class SingleSigninFragmentActivity extends FragmentActivity {
    protected abstract Fragment createFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signin);

        // Disable the Animation
        getWindow().setWindowAnimations(0);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.container);

        if (fragment == null){
            fragment = createFragment();
            fm.beginTransaction()
                    .replace(R.id.main_content, fragment)
                    .commit();
        }
    }

}

