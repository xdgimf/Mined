package com.pocketmarket.mined;

import android.content.Context;
import android.widget.ImageView;

import com.pocketmarket.mined.utility.TCImageLoader;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public class ImageCache {
    private static ImageCache instance;
    private TCImageLoader mTCImageLoader;

    public ImageCache(Context context) {
        mTCImageLoader = new TCImageLoader(context);

    }

    public static ImageCache getInstance(Context context) {

        if (instance == null) {
            instance = new ImageCache(context);
        }

        return instance;
    }

    public void display(String url, ImageView imageview, int defaultresource) {
        mTCImageLoader.display(url, imageview, defaultresource);
    }

    public void display(String url, ImageView imageview, int defaultresource, boolean isEnabled) {
        mTCImageLoader.display(url, imageview, defaultresource, isEnabled);
    }

    public static void clearInstance() {
        instance = null;
    }
}


