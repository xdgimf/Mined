package com.pocketmarket.mined.di.components;

import com.pocketmarket.mined.data.AssistantFragmentManager;
import com.pocketmarket.mined.data.SharedPrefReference;
import com.pocketmarket.mined.di.modules.AssistantFragmentModule;
import com.pocketmarket.mined.fragments.AssistantFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */
@Singleton
@Component(modules = AssistantFragmentModule.class)
public interface AssistantFragmentComponent {

    void inject(AssistantFragment assistantFragment);

    AssistantFragmentManager getAssistantFragmentManager();

    SharedPrefReference getSharedPrefReference();


}
