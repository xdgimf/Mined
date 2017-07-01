package com.pocketmarket.mined.data;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.pocketmarket.mined.R;
import com.pocketmarket.mined.activity.SigninActivity;
import com.pocketmarket.mined.controllers.ErrorMessage;
import com.pocketmarket.mined.di.ActivityErrorMessage;
import com.pocketmarket.mined.di.ApplicationActivity;
import com.pocketmarket.mined.di.ApplicationContext;
import com.pocketmarket.mined.di.SharedReference;
import com.pocketmarket.mined.dto.UserDTO;
import com.pocketmarket.mined.thread.SigninThread;
import com.pocketmarket.mined.utility.AppApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */
@Singleton
public class FacebookManager {
    private final static String TAG = "FacebookManager";

    private final static String SIGNIN_FACEBOOK = "signinFacebook";

    private Context mContext;
    private Activity mActivity;

    private SigninThread<String> mSigninThread;

    private String mAccessToken;
    private String mEmail;

    private ErrorMessage mErrorMessage;

    private Class<? extends Object> mRedirectSuccessActivity;

    private SharedPrefReference mSharedPrefReference;

    private CallbackManager mCallbackManager;

    private LoginButton mFacebookButton;
    private ProgressBar mProgressBar;

    private AccessTokenTracker mAccessTokenTracker;
    private ProfileTracker mProfileTracker;

    private boolean isQuery = false;

    @Inject
    public FacebookManager(@ApplicationContext Context context,
                           @ApplicationActivity Activity activity,
                           @ActivityErrorMessage ErrorMessage errorMessage,
                           @SharedReference SharedPrefReference sharedPrefReference)
    {
        mContext = context;
        mActivity = activity;
        mErrorMessage = errorMessage;
        mSharedPrefReference = sharedPrefReference;

        Log.i(TAG, "mContext: " + mContext + ", mSharedPrefReference: " + mSharedPrefReference);

        // perform facebook sdk initialize
        FacebookSdk.sdkInitialize(mContext);

        initSignin();
    }

    private void initSignin(){
        mSigninThread = new SigninThread<String>(new Handler());
        mSigninThread.setListener(new SigninThread.Listener<String>() {

            @Override
            public void onSigninThread(String s, UserDTO userDTO) {
                Log.i(TAG, "onPostExecute UserDTO: " + userDTO);

                if (mActivity == null) {
                    Log.i(TAG, "getActivity is null");
                    return;

                }

                if (userDTO == null) {
                    mSharedPrefReference.facebookLogout();
                    showSignin();
                    return;
                }

                // get the generated access token
                mAccessToken = userDTO.getAccesstoken();

                String name = userDTO.getFirstname() + " " + userDTO.getLastname();

                String photo = userDTO.getPhoto();

                Log.d(TAG, "Signin: " + userDTO.getFbuid() + ", Name: " + name + ", photo: " + photo);

                // if the accesstoken from the backend is null go back to signin
                if (mAccessToken == null) {
                    checkError("Access token error!!!");
                    return;
                }

                // add the token to sharedpreferences
                Log.d(TAG, "Debug accessToken: " + mAccessToken + ", activity: " + mActivity);
                mSharedPrefReference.addUserPreferences(mAccessToken);

                redirectSuccessActivity();

            }
        });

        mSigninThread.start();
        mSigninThread.getLooper();
        Log.i(TAG, "Background thread started for signin");

    }


    public void showTest(){
        Log.d(TAG, "This is a test for FacebookManager");
    }


    /**
     * Signin intent
     */
    private void showSignin() {
        Intent i = new Intent(mActivity, SigninActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);

        mActivity.startActivity(i);
        mActivity.finish();
    }


    public void destroyViewThreads(){
        if (mSigninThread == null)
            return;

        mSigninThread.clearQueue();


    }

    public void destroyThreads(){
        if (mSigninThread == null)
            return;

        mSigninThread.quit();

    }

    private void checkError(String remarks){
        if (remarks == null || !remarks.equals("")) {
            mSharedPrefReference.facebookLogout();

            if (remarks == null) {
                mErrorMessage.showMessage(mActivity.getResources().getString(R.string.facebook_conflict_key));
            } else {
                mErrorMessage.showMessage(remarks);
            }

        }
        showSignin();

    }

    public void setRedirectSuccessActivity(Class<? extends Object> redirectSuccessActivity){
        mRedirectSuccessActivity = redirectSuccessActivity;

    }

    /**
     * The method to redirect to assign activity
     */
    public void redirectSuccessActivity(){

        if (mRedirectSuccessActivity == null){
            mErrorMessage.showMessage("Redirect activity is empty!");
            return;
        }

        Intent i = new Intent(mActivity, mRedirectSuccessActivity);
        mActivity.startActivity(i);
        mActivity.finish();

    }

    public void createCallbackManager(){
        mCallbackManager = CallbackManager.Factory.create();
    }

    public CallbackManager getCallbackManager(){
        return mCallbackManager;
    }

    public FacebookCallback getFacebookCallback(){
        return new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "onSuccess");
                onSuccessFacebookLogin(loginResult.getAccessToken());
                signinProcess(true);

            }

            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel");

            }

            @Override
            public void onError(FacebookException e) {
                Log.e(TAG, "onError exception: " + e.toString());
                mErrorMessage.showMessage("Error on: " + e.toString());

            }

        };
    }

    /**
     * Facbook login success method
     *
     * @param accessToken
     */
    private void onSuccessFacebookLogin(AccessToken accessToken) {
        Log.i(TAG, "Logged in..., accessToken: " + accessToken.getToken());


        // get the shared preferences user value
        mAccessToken = mSharedPrefReference.getUserAccessToken();

        // get the shared preference email
        mEmail = mSharedPrefReference.getFacebookEmail();


        Log.i(TAG, "mAccessToken: " + mAccessToken + ", email: " + mEmail + ", mAccessToken boolean: " + mAccessToken.equalsIgnoreCase(""));

        if (mAccessToken == null || mAccessToken.equalsIgnoreCase("")) {
            Log.i(TAG, "mAccessToken is empty");

            isQuery = false;
            GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                    if (jsonObject == null)
                        return;

                    try {
                        String id = jsonObject.getString("id");
                        String firstName = jsonObject.getString("first_name");
                        String lastName = jsonObject.getString("last_name");
                        String picture = jsonObject.getJSONObject("picture").getJSONObject("data").getString("url");

                        Log.d(TAG, "Email: " + jsonObject.getString("email"));

                        String email = jsonObject.getString("email");

                        mEmail = email;

                        mAccessToken = AccessToken.getCurrentAccessToken().getToken();
                        handleFacebookAccessToken(AccessToken.getCurrentAccessToken());

                        Log.d(TAG, "id: " + id + ", firstName: " + firstName + ", lastName: " + lastName
                                + ", email: " + email + ", accessToken: " + mAccessToken + ", picture: " + picture
                                + ", isQuery: " + isQuery);

                        UserDTO user = new UserDTO();
                        user.setFbuid(new BigInteger(id));
                        user.setFirstname(firstName);
                        user.setLastname(lastName);
                        user.setEmail(email);
                        user.setFb_accesstoken(mAccessToken);
                        user.setPhoto(picture);

                        if (!isQuery){
                            authenticateAccount(user);
                            isQuery = true;
                        }



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id, first_name, last_name, email, picture.type(large)");
            request.setParameters(parameters);
            request.executeAsync();

        } else {
            Log.d(TAG, "User is a returning customer");

            redirectSuccessActivity();

        }

    }

    /**
     * The method to handle signin authentication with facebook
     * @param token
     */
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            mErrorMessage.showMessage("Authentication failed.");

                        }
                    }
                });
    }

    /**
     * Perform authentication for the signin
     *
     * @param user
     */
    private void authenticateAccount(UserDTO user) {
        mSigninThread.queuePost(SIGNIN_FACEBOOK, getSignin(), user);
    }

    /**
     * The url for the signin api
     *
     * @return
     */
    private String getSignin() {
        return AppApi.URL_NAME + AppApi.LOGIN;
    }

    /**
     * The method to set fb object controls
     * @param facebookButton
     * @param progressBar
     */
    public void setObjects(LoginButton facebookButton, ProgressBar progressBar){
        mFacebookButton = facebookButton;
        mProgressBar = progressBar;
    }

    /**
     * Method for wait loading during the signin for fb
     *
     * @param visible
     */
    private void signinProcess(boolean visible) {
        if (visible) {
            mFacebookButton.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mFacebookButton.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        }

    }

    /**
     * The method to track fb access token changes
     */
    public void startTrackingFbAccessToken(){
        mAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

                if (currentAccessToken == null) {
                    return;
                }

                String fbAuthToken = currentAccessToken.getToken();
                String fbUserID = currentAccessToken.getUserId();

                Log.d(TAG, "User id: " + fbUserID);
                Log.d(TAG, "Access token is: " + fbAuthToken);

                Log.d(TAG, "FacebookSdk client token: " + AccessToken.getCurrentAccessToken());

                // incase the fb hangs up
                getFBAccessToken();

            }

        };

        mAccessTokenTracker.startTracking();

    }

    public void startTrackingProfileTracker(){
        mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {

                if (currentProfile == null) {
                    Log.d(TAG, "currentProfile is null");
                    return;
                }

                String firstName = currentProfile.getFirstName();
                String lastName = currentProfile.getLastName();

                Log.d(TAG, "FirstName: " + firstName + ", lastName: " + lastName);

            }
        };

        mProfileTracker.startTracking();
    }

    /**
     * Add the generated token to facebook
     */
    public void getFBAccessToken() {
        if (AccessToken.getCurrentAccessToken() == null) {
            Log.e(TAG, "AccessToken: null");
        } else {
            Log.d(TAG, "AccessToken: " + AccessToken.getCurrentAccessToken().getToken());
            AccessToken accessToken = AccessToken.getCurrentAccessToken();

            mSharedPrefReference.addFacebookTokenPreferences(accessToken.getToken());
            onSuccessFacebookLogin(accessToken);

            signinProcess(true);

        }
    }


}
