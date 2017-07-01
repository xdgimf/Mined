package com.pocketmarket.mined.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.pocketmarket.mined.R;
import com.pocketmarket.mined.utility.Utils;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public class FeedContextMenu extends LinearLayout implements View.OnClickListener{
    private static final int CONTEXT_MENU_WIDTH = Utils.dpToPx(240);

    private int feedItem = -1;

    private Button mBuyNow;
    private Button mLocation;
    private Button mCancel;

    private OnFeedContextMenuItemClickListener onItemClickListener;

    public FeedContextMenu(Context context) {
        super(context);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_context_menu, this, true);

        mBuyNow = (Button) view.findViewById(R.id.btnBuyNow);
        mBuyNow.setOnClickListener(this);

        mLocation = (Button) view.findViewById(R.id.btnLocation);
        mLocation.setOnClickListener(this);

        mCancel = (Button) view.findViewById(R.id.btnCancel);
        mCancel.setOnClickListener(this);


        setBackgroundResource(R.drawable.bg_container_shadow);
        setOrientation(VERTICAL);
        setLayoutParams(new LinearLayout.LayoutParams(CONTEXT_MENU_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void bindToItem(int feedItem) {
        this.feedItem = feedItem;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    public void dismiss() {
        ((ViewGroup) getParent()).removeView(FeedContextMenu.this);
    }


    public void onBuyNowClick() {
        if (onItemClickListener != null) {
            onItemClickListener.onBuyNowClick(feedItem);
        }
    }

    public void onLocationClick() {
        if (onItemClickListener != null) {
            onItemClickListener.onLocationClick(feedItem);
        }
    }

    public void onCancelClick() {
        if (onItemClickListener != null) {
            onItemClickListener.onCancelClick(feedItem);
        }
    }

    public void setOnFeedMenuItemClickListener(OnFeedContextMenuItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id){
            case R.id.btnBuyNow:
                onBuyNowClick();
                break;

            case R.id.btnLocation:
                onLocationClick();
                break;

            case R.id.btnCancel:
                onCancelClick();
                break;

        }

    }

    public interface OnFeedContextMenuItemClickListener {

        public void onBuyNowClick(int feedItem);

        public void onLocationClick(int feedItem);

        public void onCancelClick(int feedItem);
    }
}

