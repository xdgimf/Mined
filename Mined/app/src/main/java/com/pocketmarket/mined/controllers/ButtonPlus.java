package com.pocketmarket.mined.controllers;

import android.content.Context;
import android.util.AttributeSet;

import com.pocketmarket.mined.widget.CustomFontHelper;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public class ButtonPlus extends android.support.v7.widget.AppCompatButton {

    public ButtonPlus(Context context) {
        super(context);
    }

    public ButtonPlus(Context context, AttributeSet attrs) {
        super(context, attrs);
        CustomFontHelper.setCustomFont(this, context, attrs);
    }

    public ButtonPlus(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        CustomFontHelper.setCustomFont(this, context, attrs);
    }
}

