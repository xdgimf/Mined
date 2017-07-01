package com.pocketmarket.mined.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.pocketmarket.mined.R;
import com.pocketmarket.mined.activity.BuyActivity;
import com.pocketmarket.mined.adapter.FeedProductsAdapter;
import com.pocketmarket.mined.dto.ProductsDTO;
import com.pocketmarket.mined.thread.ProductsThread;
import com.pocketmarket.mined.utility.AppApi;
import com.pocketmarket.mined.view.FeedContextMenu;
import com.pocketmarket.mined.view.FeedContextMenuManager;

import java.util.ArrayList;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public class MinedFragment extends Fragment implements FeedProductsAdapter.OnFeedItemClickListener,
        FeedContextMenu.OnFeedContextMenuItemClickListener{
    private final static String TAG = "MinedFragment";
    private final static String PRODUCTS = "products";

    private LinearLayout mProductLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar mProgressBar;


    //    private String mAccessToken;
    private ArrayList<ProductsDTO> mProductItemList;

    private boolean mManualSyncRequest;

    // Cards
    private RecyclerView mRvFeed;
    private FeedProductsAdapter mFeedProductsAdapter;

    private LinearLayoutManager mLayoutManager;

    private ProductsThread<String> mProductsThread;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        // get the shared preferences user value
//        mAccessToken = Utils.getAccessToken(getActivity());
//
//        Log.d(TAG, "accessToken: " + mAccessToken);
//
//        mManualSyncRequest = false;
//
//        initProducts();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mined, container, false);

//        mRvFeed = (RecyclerView) view.findViewById(R.id.rvFeed);
//
//        mProductLayout = (LinearLayout) view.findViewById(R.id.product_layout);
//
//        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
//
//        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
//        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
//                android.R.color.holo_green_light,
//                android.R.color.holo_orange_light,
//                android.R.color.holo_red_light);
//
//        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                mManualSyncRequest = true;
//
//                // disable the progressbar
//                showProgressBar(false);
//
//                // disable the blank statements
//                setProductVisible(false);
//
//                //Trigger content provider refresh
//                refreshProduct();
//
//            }
//        });
//
//        refreshProduct();

        return view;
    }

    /**
     * Populate the list of cards in invoice
     *
     */
    private void setupFeed() {
        mLayoutManager = new LinearLayoutManager(getActivity()) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }
        };

        mRvFeed.setLayoutManager(mLayoutManager);

        mFeedProductsAdapter = new FeedProductsAdapter(getActivity());
        mFeedProductsAdapter.setOnFeedItemClickListener(this);

        mRvFeed.setAdapter(mFeedProductsAdapter);

        mFeedProductsAdapter.updateItems(mProductItemList);

    }

    private String getProducts(String accessToken){
        return AppApi.URL_NAME + AppApi.PRODUCTS + accessToken;
    }


    private void initProducts(){
        mProductsThread = new ProductsThread<String>(new Handler());
        mProductsThread.setListener(new ProductsThread.Listener<String>(){
            @Override
            public void onProductsThread(String s, ArrayList<ProductsDTO> productsList) {

                showProgressBar(false);
                setProductVisible(true);
                setRefereshing(false);

                if (productsList == null){
                    Log.i(TAG, "Products list is null");
                    showToast(getString(R.string.error_no_internet));
                    return;
                }
                Log.d(TAG, "products size: " + productsList.size());

                int size = productsList.size();
                if (size > 0) {
                    mProductItemList = productsList;

                    showProgressBar(false);

                    // disable the blank statements
                    setProductVisible(false);

                    // populate the feed cards
                    setupFeed();

                }else {
                    if (mManualSyncRequest) {
                        clearFeed();
                    }
                }

                mManualSyncRequest = false;



            }

        });

        mProductsThread.start();
        mProductsThread.getLooper();
        Log.i(TAG, "Background thread started for products");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mProductsThread == null)
            return;

        mProductsThread.clearQueue();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mProductsThread == null)
            return;

        mProductsThread.quit();

        Log.i(TAG, "Background thread destroyed");
    }

    /**
     * Empty state status
     *
     * @param value
     */
    private void setProductVisible(boolean value) {

        if (mProductLayout == null)
            return;

        if (value) {
            mProductLayout.setVisibility(View.VISIBLE);

        } else {
            mProductLayout.setVisibility(View.GONE);

        }

    }

    /**
     * Loading status of products
     *
     * @param value
     */
    private void showProgressBar(boolean value) {

        if (mProgressBar == null)
            return;

        if (value) {
            mProgressBar.setVisibility(View.VISIBLE);

        } else {
            mProgressBar.setVisibility(View.GONE);

        }
    }

    @Override
    public void onMoreClick(View v, int position) {
        FeedContextMenuManager.getInstance().toggleContextMenuFromView(v, position, this);

    }

    @Override
    public void onBuyNowClick(int feedItem) {
        FeedContextMenuManager.getInstance().hideContextMenu();
        showBuyNow(feedItem);
    }

    @Override
    public void onLocationClick(int feedItem) {
        FeedContextMenuManager.getInstance().hideContextMenu();
    }

    @Override
    public void onCancelClick(int feedItem) {
        FeedContextMenuManager.getInstance().hideContextMenu();

    }

//    /**
//     * The method to refresh the list of items shown
//     */
//    private void refreshProduct() {
//        mProductsThread.queuePost(PRODUCTS, getProducts(mAccessToken));
//    }

    /**
     * @param message The method to display an error message
     */
    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    /**
     *
     */
    private void clearFeed() {
        if (mFeedProductsAdapter == null)
            return;

        mFeedProductsAdapter.clearAllItems();
    }

    /**
     * The loader for refreshing in product list
     *
     * @param value
     */
    private void setRefereshing(boolean value) {
        mSwipeRefreshLayout.setRefreshing(value);
    }

    private void showBuyNow(int feedItem){
        Log.i(TAG, "feedItem: " + feedItem);

        ProductsDTO products = mProductItemList.get(feedItem);

        Intent i = new Intent(getActivity(), BuyActivity.class);
        i.putExtra("id", products.getId());
        i.putExtra("name", products.getName());
        i.putExtra("description", products.getDescription());
        i.putExtra("photo", products.getPhoto());
        i.putExtra("amount", products.getAmount());
        i.putExtra("type", products.getType());
        startActivity(i);
    }

}

