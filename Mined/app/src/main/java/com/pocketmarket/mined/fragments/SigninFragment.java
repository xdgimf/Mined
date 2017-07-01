package com.pocketmarket.mined.fragments;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;
import com.pocketmarket.mined.R;
import com.pocketmarket.mined.activity.AssistantActivity;
import com.pocketmarket.mined.data.FacebookManager;
import com.pocketmarket.mined.di.components.DaggerSigninFragmentComponent;
import com.pocketmarket.mined.di.components.SigninFragmentComponent;
import com.pocketmarket.mined.di.modules.SigninFragmentModule;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public class SigninFragment extends Fragment {
    private static final String TAG = "SigninFragment";

    private static final String PUBLIC_PROFILE = "public_profile";
    private static final String EMAIL = "email";

    private static final List<String> PERMISSIONS_READ = Arrays.asList(
            EMAIL, PUBLIC_PROFILE);

    @Inject
    FacebookManager mFacebookManager;

    @BindView(R.id.authButton)
    LoginButton mFacebookButton;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    // callback handler for fb
    private CallbackManager mCallbackManager;

    // DI object handler
    private SigninFragmentComponent mSigninFragmentComponent;

    public SigninFragmentComponent getSigninFragmentComponent(){
        if (mSigninFragmentComponent == null){
            mSigninFragmentComponent = DaggerSigninFragmentComponent
                    .builder()
                    .signinFragmentModule(new SigninFragmentModule(this))
                    .build();
        }

        return mSigninFragmentComponent;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSigninFragmentComponent().inject(this);

        // set the activity to redirect when success
        mFacebookManager.setRedirectSuccessActivity(AssistantActivity.class);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signin, container, false);
        ButterKnife.bind(this, view);

        mFacebookManager.createCallbackManager();

        mFacebookButton.setFragment(this);
        mFacebookButton.setReadPermissions(PERMISSIONS_READ);
        mFacebookButton.registerCallback(mFacebookManager.getCallbackManager(), mFacebookManager.getFacebookCallback());

        mCallbackManager = mFacebookManager.getCallbackManager();

        // Adjust the fbbutton design layout
        float fbIconScale = 1.45F;
        Drawable drawable = getActivity().getResources().getDrawable(
                com.facebook.R.drawable.com_facebook_button_icon);
        drawable.setBounds(0, 0, (int) (drawable.getIntrinsicWidth() * fbIconScale),
                (int) (drawable.getIntrinsicHeight() * fbIconScale));
        mFacebookButton.setCompoundDrawables(drawable, null, null, null);
        mFacebookButton.setCompoundDrawablePadding(getActivity().getResources().
                getDimensionPixelSize(R.dimen.fb_margin_override_textpadding));
        mFacebookButton.setPadding(
                getActivity().getResources().getDimensionPixelSize(
                        R.dimen.fb_margin_override_lr),
                getActivity().getResources().getDimensionPixelSize(
                        R.dimen.fb_margin_override_top),
                0,
                getActivity().getResources().getDimensionPixelSize(
                        R.dimen.fb_margin_override_bottom));

        // set the object controller to fbmanager for behavior handling
        mFacebookManager.setObjects(mFacebookButton, mProgressBar);

        // perform tracking on token and profle for changes
        mFacebookManager.startTrackingFbAccessToken();
        mFacebookManager.startTrackingProfileTracker();

        // get the token and pass it to shared preferences
        // invoke the UI handling
        mFacebookManager.getFBAccessToken();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult requestCode: " + requestCode + ", resultCode: " + resultCode);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mFacebookManager.destroyViewThreads();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mFacebookManager.destroyThreads();
    }

}
