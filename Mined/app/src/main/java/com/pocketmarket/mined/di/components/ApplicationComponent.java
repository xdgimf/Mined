package com.pocketmarket.mined.di.components;

import android.app.Application;
import android.content.Context;

import com.pocketmarket.mined.MainApplication;
import com.pocketmarket.mined.data.SharedPrefReference;
import com.pocketmarket.mined.di.ApplicationContext;
import com.pocketmarket.mined.di.modules.ApplicationModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */
@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    void inject(MainApplication mainApplication);

    @ApplicationContext
    Context getContext();

    Application getApplication();

    SharedPrefReference getSharedPreference();


}
