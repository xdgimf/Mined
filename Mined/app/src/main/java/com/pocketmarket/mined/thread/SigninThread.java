package com.pocketmarket.mined.thread;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.pocketmarket.mined.dto.UserDTO;
import com.pocketmarket.mined.fetcher.SigninPostFetchr;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public class SigninThread <Token> extends HandlerThread {

    private static final String TAG = "SigninThread";
    private static final int MESSAGE_POST = 0;

    Handler mHandler;
    Map<Token, String> requestMap = Collections.synchronizedMap(new HashMap<Token, String>());
    Handler mResponseHandler;
    Listener<Token> mListener;
    private UserDTO mUser;

    public interface Listener<Token>{
        void onSigninThread(Token token, UserDTO userDTO);
    }

    public void setListener(Listener<Token> listener){
        mListener = listener;
    }

    public SigninThread(Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;
    }

    @Override
    protected void onLooperPrepared() {
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_POST){
                    Token token = (Token) msg.obj;
                    Log.i(TAG, "Got a request for url: " + requestMap.get(token));
                    handleRequest(token);
                }
            }
        };

    }

    public void queuePost(Token token, String url, UserDTO user){
        Log.i(TAG, "Got a URL: " + url);
        requestMap.put(token, url);

        mUser = user;

        if (mHandler == null)
            return;


        mHandler.obtainMessage(MESSAGE_POST, token).sendToTarget();
    }

    private void handleRequest(final Token token){

        final String url = requestMap.get(token);
        if (url == null)
            return;

        try {
            final UserDTO userDTO = new SigninPostFetchr().fetchItems(url, mUser);

            mResponseHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (requestMap.get(token) != url)
                        return;

                    requestMap.remove(token);
                    mListener.onSigninThread(token, userDTO);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void clearQueue(){
        mHandler.removeMessages(MESSAGE_POST);
        requestMap.clear();
    }
}

