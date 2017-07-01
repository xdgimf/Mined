package com.pocketmarket.mined.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;

import com.pocketmarket.mined.SingleMainFragmentActivity;
import com.pocketmarket.mined.adapter.GlobalMenuAdapter;
import com.pocketmarket.mined.fragments.AssistantFragment;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public class AssistantActivity extends SingleMainFragmentActivity {
    private final static String TAG = "AssistantActivity";
    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";

    @Override
    protected Fragment createFragment() {
        return new AssistantFragment();
    }

    /**
     * Animation for the navigation bar
     *
     * @param startingLocation
     * @param startingActivity
     */
    public static void startAssistantFromLocation(int[] startingLocation, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, AssistantActivity.class);
        intent.putExtra(ARG_REVEAL_START_LOCATION, startingLocation);
        startingActivity.startActivity(intent);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                Log.d(TAG, "Perform position...." + position);
                closeDrawerOnly();
                break;

            default: // assistant
                Log.d(TAG, "feed....");
                closeDrawerOnly();

                String roomId = null;
                String roomName = null;

                GlobalMenuAdapter.GlobalMenuItem item = ((GlobalMenuAdapter) ((HeaderViewListAdapter) parent.getAdapter()).getWrappedAdapter()).getItem(position - 1);

                if (item != null) {
                    roomId = item.roomId;
                    roomName = item.label;
                }

                showMined(roomId, roomName);

                break;

        }

    }

    /**
     *
     */
    private void showMined(String roomId, String roomName) {
        Intent intent = new Intent(this, MinedActivity.class);
        intent.putExtra(ChatActivity.EXTRA_ROOM_NAME, roomName);
        intent.putExtra(ChatActivity.EXTRA_ROOM_ID, roomId);
        startActivity(intent);
    }
}

