package com.pocketmarket.mined.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.pocketmarket.mined.R;
import com.pocketmarket.mined.adapter.GlobalMenuAdapter;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public class GlobalMenuView extends ListView implements View.OnClickListener {

    private OnHeaderClickListener mOnHeaderClickListener;
    private GlobalMenuAdapter mGlobalMenuAdapter;

    private int mChannelId = 0;

    private Activity mActivity;
    public interface OnHeaderClickListener {
        public void onGlobalMenuHeaderClick(View v, int channelId);
    }

    public GlobalMenuView(Context context, Activity activity) {
        super(context);
        mActivity = activity;

        init();

    }

    private void init() {
        setChoiceMode(CHOICE_MODE_SINGLE);

        setDivider(ContextCompat.getDrawable(getContext(), android.R.color.transparent));
        setDividerHeight(0);
        setBackgroundColor(Color.WHITE);

        setupHeader();

    }

    private void setupAdapter(int channelId) {
        mGlobalMenuAdapter = new GlobalMenuAdapter(getContext(), mActivity, channelId);
        setAdapter(mGlobalMenuAdapter);

    }

    private void setupHeader() {

        setHeaderDividersEnabled(true);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_global_menu_header, null);

        addHeaderView(view);
        view.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        if (mOnHeaderClickListener != null) {
            mOnHeaderClickListener.onGlobalMenuHeaderClick(v, mChannelId);
        }
    }

    public void setOnHeaderClickListener(OnHeaderClickListener onHeaderClickListener) {
        this.mOnHeaderClickListener = onHeaderClickListener;
    }

}

