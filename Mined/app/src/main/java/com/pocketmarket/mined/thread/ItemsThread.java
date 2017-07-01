package com.pocketmarket.mined.thread;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.pocketmarket.mined.dto.ItemsDTO;
import com.pocketmarket.mined.fetcher.ItemsPostFetchr;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public class ItemsThread<Token> extends HandlerThread {
    private static final String TAG = "ItemsThread";
    private static final int MESSAGE_POST = 0;

    Handler mHandler;
    Map<Token, String> requestMap = Collections.synchronizedMap(new HashMap<Token, String>());
    Handler mResponseHandler;
    Listener<Token> mListener;
    private int mId;
    private String mAccessToken;

    public interface Listener<Token>{
        void onItemsThread(Token token, ItemsDTO itemsResult);
    }

    public void setListener(Listener<Token> listener){
        mListener = listener;
    }

    public ItemsThread(Handler responseHandler) {
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

    public void queuePost(Token token, String url, int id, String accessToken){
        Log.i(TAG, "Got a URL: " + url);
        requestMap.put(token, url);

        mId = id;

        mAccessToken = accessToken;

        if (mHandler == null)
            return;


        mHandler.obtainMessage(MESSAGE_POST, token).sendToTarget();
    }

    private void handleRequest(final Token token){
        final String url = requestMap.get(token);
        if (url == null)
            return;

        final ItemsDTO itemsDTO = new ItemsPostFetchr().fetchItems(url, mId, mAccessToken);

        mResponseHandler.post(new Runnable() {
            @Override
            public void run() {
                if (requestMap.get(token) != url)
                    return;

                requestMap.remove(token);
                mListener.onItemsThread(token, itemsDTO);
            }
        });

    }

    public void clearQueue(){
        mHandler.removeMessages(MESSAGE_POST);
        requestMap.clear();
    }

}

