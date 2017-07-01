package com.pocketmarket.mined;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;

import com.pocketmarket.mined.utility.DrawerLayoutInstaller;
import com.pocketmarket.mined.utility.Utils;
import com.pocketmarket.mined.view.GlobalMenuView;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public abstract class SingleMainFragmentActivity extends AppCompatActivity
        implements GlobalMenuView.OnHeaderClickListener, AdapterView.OnItemClickListener {

    protected abstract Fragment createFragment();

    private final static String TAG = "SingleMainFragmentActivity";
    public Toolbar mToolbar;
    private DrawerLayout drawerLayout;

    private GlobalMenuView mMenuView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mined);

        // Disable the Animation
        getWindow().setWindowAnimations(0);

        // set the topbar for material design
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.mipmap.icon_burger_menu);

        setupDrawer();

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.container);

        if (fragment == null){
            fragment = createFragment();
            fm.beginTransaction()
                    .replace(R.id.mainContent, fragment)
                    .commit();
        }

    }

    private void setupDrawer() {
        mMenuView = new GlobalMenuView(this, this);
        mMenuView.setOnHeaderClickListener(this);
        mMenuView.setOnItemClickListener(this);

        drawerLayout = DrawerLayoutInstaller.from(this)
                .drawerRoot(R.layout.drawer_root)
                .drawerLeftView(mMenuView)
                .drawerLeftWidth(Utils.dpToPx(300))
                .withNavigationIconToggler(getToolbar())
                .build();
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    public void onGlobalMenuHeaderClick(final View v, final int channelId) {
        drawerLayout.closeDrawer(Gravity.LEFT);

//        int id = v.getId();

    }

    /**
     *
     */
    public void closeDrawerOnly(){
        drawerLayout.closeDrawer(Gravity.LEFT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK)
            return;

    }
}
