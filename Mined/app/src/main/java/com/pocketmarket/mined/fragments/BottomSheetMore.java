package com.pocketmarket.mined.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;
import android.view.View;

import com.pocketmarket.mined.R;
import com.pocketmarket.mined.activity.ExpenseAnalyticsActivity;
import com.pocketmarket.mined.activity.MinedActivity;
import com.pocketmarket.mined.dto.BalanceAnalyticsDTO;
import com.pocketmarket.mined.fetcher.BalanceAnalyticsGetFetchr;
import com.pocketmarket.mined.utility.Utils;

import java.util.ArrayList;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public class BottomSheetMore extends BottomSheetDialogFragment implements View.OnClickListener {
    private final static String TAG = "BottomSheetMore";
    private BottomSheetBehavior mBehavior;

    ArrayList<String> mCreditList = new ArrayList<String>();
    ArrayList<String> mDebitList = new ArrayList<String>();

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN || newState == BottomSheetBehavior.STATE_COLLAPSED) {
                dismissAllowingStateLoss();
            }

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.fragment_bottom_sheet, null);

        contentView.findViewById(R.id.item).setOnClickListener(this);
        contentView.findViewById(R.id.balance).setOnClickListener(this);
        contentView.findViewById(R.id.trucks).setOnClickListener(this);
        contentView.findViewById(R.id.stocks).setOnClickListener(this);

        contentView.findViewById(R.id.cancel).setOnClickListener(this);

        dialog.setContentView(contentView);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        mBehavior = (BottomSheetBehavior) params.getBehavior();

        if (mBehavior != null && mBehavior instanceof BottomSheetBehavior) {
            mBehavior.setBottomSheetCallback(mBottomSheetBehaviorCallback);
            mBehavior.setPeekHeight(Utils.dpToPx(350));
        }

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.item:
                showProducts();
                break;

            case R.id.balance:
                showAnalytics();
                break;

        }

        mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

    }

    /**
     * show the analytics
     */
    private void showAnalytics(){

        Intent i = new Intent(getActivity(), ExpenseAnalyticsActivity.class);
        i.putStringArrayListExtra("credit", mCreditList);
        i.putStringArrayListExtra("debit", mDebitList);
        startActivity(i);
    }

    private void showProducts(){
        Intent i = new Intent(getActivity(), MinedActivity.class);
        startActivity(i);
    }

    /**
     *  Asynccall for the assistant
     */
    private class BalanceAnalyticsFetchr extends AsyncTask<String, Void, ArrayList<BalanceAnalyticsDTO>> {

        @Override
        protected ArrayList<BalanceAnalyticsDTO> doInBackground(String... url) {
            Log.i(TAG, "SmartReplyTask URL: " + url[0]);
            return new BalanceAnalyticsGetFetchr().fetchItems(url[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<BalanceAnalyticsDTO> balanceAnalyticsList) {
            Log.i(TAG, "onPostExecute BalanceAnalyticsGetFetchr: " + balanceAnalyticsList.size());

            if (getActivity() == null)
                return;

            mCreditList = new ArrayList<String>();
            mDebitList = new ArrayList<String>();
            for (BalanceAnalyticsDTO balanceAnalyticsDTO: balanceAnalyticsList){
                String credit = balanceAnalyticsDTO.getCredit().toString();
                String debit = balanceAnalyticsDTO.getDebit().toString();

                mCreditList.add(credit);
                mDebitList.add(debit);


            }

        }

    }

}

