package com.pocketmarket.mined.di.modules;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.pocketmarket.mined.di.ApplicationContext;
import com.pocketmarket.mined.utility.AppApi;

import dagger.Module;
import dagger.Provides;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */
@Module
public class ApplicationModule {
    private final Application mApplication;

    public ApplicationModule(Application application) {
        mApplication = application;
    }

    @Provides
    @ApplicationContext
    Context provideContext(){
        return mApplication;
    };

    @Provides
    Application provideApplication(){
        return mApplication;
    }

    @Provides
    SharedPreferences provideSharedPreferences(){
        return mApplication.getSharedPreferences(AppApi.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);

    }

}
