package com.pocketmarket.mined.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.pocketmarket.mined.R;
import com.pocketmarket.mined.SingleFragmentActivity;
import com.pocketmarket.mined.fragments.MinedFragment;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public class MinedActivity extends SingleFragmentActivity {
    private final static String TAG = "MinedActivity";

    @Override
    protected Fragment createFragment() {
        return new MinedFragment();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.chat_menu:
                startActivity(new Intent(this, ChatActivity.class).putExtras(getIntent()));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

