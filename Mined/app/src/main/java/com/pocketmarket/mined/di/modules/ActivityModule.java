package com.pocketmarket.mined.di.modules;

import android.app.Activity;
import android.content.Context;

import com.pocketmarket.mined.di.ApplicationContext;

import dagger.Module;
import dagger.Provides;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */
@Module
public class ActivityModule {
    private Activity mActivity;

    public ActivityModule(Activity activity) {
        mActivity = activity;
    }

    @Provides
    @ApplicationContext
    Context provideContext(){
        return mActivity;
    }

    @Provides
    Activity provideActivity(){
        return mActivity;
    }

}
