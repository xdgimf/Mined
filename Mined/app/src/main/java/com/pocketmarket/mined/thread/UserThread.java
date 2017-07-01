package com.pocketmarket.mined.thread;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.pocketmarket.mined.dto.UserDTO;
import com.pocketmarket.mined.fetcher.UserFetchr;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public class UserThread<Token> extends HandlerThread {
    private static final String TAG = "UserInfoThread";
    private static final int MESSAGE_POST = 0;

    Handler mHandler;
    Map<Token, String> requestMap = Collections.synchronizedMap(new HashMap<Token, String>());
    Handler mResponseHandler;
    Listener<Token> mListener;

    public interface Listener<Token>{
        void onUserThread(Token token, UserDTO userResult);
    }

    public void setListener(Listener<Token> listener){
        mListener = listener;
    }

    public UserThread(Handler responseHandler) {
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

    public void queuePost(Token token, String url){
        Log.i(TAG, "Got a URL: " + url);
        requestMap.put(token, url);

        if (mHandler == null)
            return;


        mHandler.obtainMessage(MESSAGE_POST, token).sendToTarget();
    }

    private void handleRequest(final Token token){
        final String url = requestMap.get(token);
        if (url == null)
            return;

        final UserDTO userDTO = new UserFetchr().fetchItems(url);

        mResponseHandler.post(new Runnable() {
            @Override
            public void run() {
                if (requestMap.get(token) != url)
                    return;

                requestMap.remove(token);
                mListener.onUserThread(token, userDTO);
            }
        });

    }

    public void clearQueue(){
        mHandler.removeMessages(MESSAGE_POST);
        requestMap.clear();
    }

}

