package com.pocketmarket.mined;

import android.app.Application;
import android.content.Context;

import com.pocketmarket.mined.di.components.ApplicationComponent;
import com.pocketmarket.mined.di.components.DaggerApplicationComponent;
import com.pocketmarket.mined.di.modules.ApplicationModule;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public class MainApplication extends Application {
    protected ApplicationComponent mApplicationComponent;

    public static MainApplication get(Context context){
        return (MainApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mApplicationComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(this))
                .build();

        mApplicationComponent.inject(this);
    }

    public ApplicationComponent getComponent(){
        return mApplicationComponent;
    }
}
