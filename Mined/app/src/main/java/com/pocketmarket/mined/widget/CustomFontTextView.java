package com.pocketmarket.mined.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.pocketmarket.mined.R;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public class CustomFontTextView extends TextView {

    public CustomFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (isInEditMode()){
            return;
        }

        TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.CustomFontTextView);
        String fontName = styledAttrs.getString(R.styleable.CustomFontTextView_customFont);
        styledAttrs.recycle();

        if (fontName != null){
            Typeface typeface = Typeface.createFromAsset(context.getAssets(), fontName);
            setTypeface(typeface);
        }
    }
}

