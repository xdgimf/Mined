package com.pocketmarket.mined.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.androidplot.xy.XYPlot;
import com.pocketmarket.mined.R;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public class ExpenseAnalyticsActivity extends AppCompatActivity {
    private final static String TAG = "ExpenseAnalyticsActivity";
    private XYPlot mPlot;
//    private String mAccessToken;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_expense_analytics);

//        // get the shared preferences user value
//        mAccessToken = Utils.getAccessToken(this);
//
//        ArrayList<String> sCreditList = getIntent().getStringArrayListExtra("credit");
//        ArrayList<String> sDebitList = getIntent().getStringArrayListExtra("debit");
//
//
//        ArrayList<Double> creditList = new ArrayList<Double>();
//        for (String credit:sCreditList){
//            Log.i(TAG, "Credit: " + credit);
//            creditList.add(Double.parseDouble(credit));
//        }
//
//        ArrayList<Double> debitList = new ArrayList<Double>();
//        for (String debit:sDebitList){
//            Log.i(TAG, "Debit: " + debit);
//            debitList.add(Double.parseDouble(debit));
//        }
//
//
//        mPlot = (XYPlot) findViewById(R.id.expense_plot);
//
//        XYSeries credit = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Credit", creditList.toArray(new Double[creditList.size()]));
//
//        XYSeries debit = new SimpleXYSeries(SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Debit", debitList.toArray(new Double[debitList.size()]));
//
//        mPlot.addSeries(debit, new LineAndPointFormatter(Color.RED, Color.RED, null, null));
//        mPlot.addSeries(credit, new LineAndPointFormatter(Color.GREEN, Color.GREEN, null, null));

    }

}

