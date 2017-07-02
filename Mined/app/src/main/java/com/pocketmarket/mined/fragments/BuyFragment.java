package com.pocketmarket.mined.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.pocketmarket.mined.R;
import com.pocketmarket.mined.controllers.ButtonPlus;
import com.pocketmarket.mined.dto.ItemsDTO;
import com.pocketmarket.mined.thread.ItemsThread;
import com.pocketmarket.mined.utility.AppApi;
import com.pocketmarket.mined.widget.CustomFontTextView;
import com.squareup.picasso.Picasso;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public class BuyFragment extends Fragment {
    private final static String TAG = "BuyFragment";
    private final static String ITEMS_INFO = "itemsinfo";

    private static final int AVATAR_SIZE = 200;

    private String mAccessToken;

    private CustomFontTextView mProductTitle;
    private CustomFontTextView mProductDescription;
    private CustomFontTextView mTotal;
    private ButtonPlus mBuyNow;
    private ImageView mProductAvatar;


    private int mId;
    private String mName;
    private String mDescription;
    private int mPhotoId;
    private String mPhoto;
    private double mAmount;
    private int mType;

    private ItemsThread<String> mItemsThread;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mId = getArguments().getInt("id");
            mAccessToken = getArguments().getString("accessToken");

        }

        Log.i(TAG, "id: " + mId + ", accessToken: " + mAccessToken);

        initItemsInfo();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buy, container, false);

        mTotal = (CustomFontTextView) view.findViewById(R.id.total_value);

        mProductTitle = (CustomFontTextView) view.findViewById(R.id.product_title);

        mProductDescription = (CustomFontTextView) view.findViewById(R.id.product_description);

        mBuyNow = (ButtonPlus) view.findViewById(R.id.btn_buy);
        mBuyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        mProductAvatar = (ImageView) view.findViewById(R.id.product_avatar);

        mItemsThread.queuePost(ITEMS_INFO, getItems(), mId, mAccessToken);

        return view;
    }

    /**
     *
     * @return
     */
    private String getItems(){
        return AppApi.URL_NAME + AppApi.ITEMS;
    }

    private void initItemsInfo(){
        mItemsThread = new ItemsThread<String>(new Handler());
        mItemsThread.setListener(new ItemsThread.Listener<String>(){

            @Override
            public void onItemsThread(String s, ItemsDTO itemsDTO) {

                if (itemsDTO == null){
                    Log.d(TAG, "items is empty....");
                    return;
                }

                mId = itemsDTO.getId();
                mName = itemsDTO.getName();
                mPhotoId = itemsDTO.getPhotoid();
                mPhoto = AppApi.URL_PHOTOS_URL + mPhotoId + "/" + itemsDTO.getPhoto();
                mAmount = itemsDTO.getPrice();

                Log.i(TAG, "initItemsInfo id: " + mId + ", name: " + mName + ", photoId: " + mPhotoId + ", photo: " + mPhoto + ", amount: " + mAmount);

                mProductTitle.setText(mName);
                mTotal.setText("P " + mAmount);
                mProductDescription.setText("");

                mBuyNow.setText(getResources().getString(R.string.buy_now));

                Picasso.with(getContext())
                        .load(mPhoto)
                        .resize(AVATAR_SIZE, AVATAR_SIZE)
                        .centerCrop()
                        .into(mProductAvatar);



            }
        });

        mItemsThread.start();
        mItemsThread.getLooper();
        Log.i(TAG, "Background thread started for userinfo");

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mItemsThread.clearQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mItemsThread.quit();
    }
}

