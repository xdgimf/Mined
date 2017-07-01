package com.pocketmarket.mined.di.components;

import com.pocketmarket.mined.controllers.ErrorMessage;
import com.pocketmarket.mined.data.FacebookManager;
import com.pocketmarket.mined.data.SharedPrefReference;
import com.pocketmarket.mined.di.modules.SigninFragmentModule;
import com.pocketmarket.mined.fragments.SigninFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */
@Singleton
@Component(modules = SigninFragmentModule.class)
public interface SigninFragmentComponent {

    void inject (SigninFragment signinFragment);

    FacebookManager getFacebookManager();

    ErrorMessage getErrorMessage();

    SharedPrefReference getSharedPrefReference();

}
