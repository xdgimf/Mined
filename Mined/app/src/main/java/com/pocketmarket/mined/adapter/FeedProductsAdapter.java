package com.pocketmarket.mined.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.pocketmarket.mined.R;
import com.pocketmarket.mined.dto.ProductsDTO;
import com.pocketmarket.mined.utility.Utils;
import com.pocketmarket.mined.view.SendingProgressView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public class FeedProductsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private static final String TAG = "FeedProductsAdapter";

    private static final DecelerateInterpolator DECCELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);

    private static final int AVATAR_SIZE = 200;

    private static final int ANIMATED_ITEMS_COUNT = 2;
    private static final int VIEW_TYPE_DEFAULT = 1;
    private static final int VIEW_TYPE_LOADER = 2;

    private Context mContext;
    private int mLastAnimatedPosition = -1;
    private int mItemsCount = 0;

    private final Map<Integer, Integer> mLikesCount = new HashMap<>();
    private final Map<RecyclerView.ViewHolder, AnimatorSet> mLikeAnimations = new HashMap<>();
    private final ArrayList<Integer> mLikedPositions = new ArrayList<>();

    private boolean showLoadingView = false;
    private int loadingViewSize = Utils.dpToPx(200);

    private ArrayList<ProductsDTO> mProductsList;

    public interface OnFeedItemClickListener {
        public void onMoreClick(View v, int position);
    }

    private OnFeedItemClickListener onFeedItemClickListener;


    public FeedProductsAdapter(Context context) {
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.item_feed, parent, false);
        final CellFeedViewHolder cellFeedViewHolder = new CellFeedViewHolder(view);
        if (viewType == VIEW_TYPE_DEFAULT) {
            cellFeedViewHolder.btnMore.setOnClickListener(this);
            cellFeedViewHolder.feedCenter.setOnClickListener(this);
            cellFeedViewHolder.btnLike.setOnClickListener(this);

        }else if (viewType == VIEW_TYPE_LOADER) {
            View bgView = new View(mContext);
            bgView.setLayoutParams(new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            ));
            bgView.setBackgroundColor(0x77ffffff);
            cellFeedViewHolder.imageRoot.addView(bgView);
            cellFeedViewHolder.progressBg = bgView;

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(loadingViewSize, loadingViewSize);
            params.gravity = Gravity.CENTER;
            SendingProgressView sendingProgressView = new SendingProgressView(mContext);
            sendingProgressView.setLayoutParams(params);
            cellFeedViewHolder.imageRoot.addView(sendingProgressView);
            cellFeedViewHolder.sendingProgress = sendingProgressView;

        }

        return cellFeedViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        runEnterAnimation(viewHolder.itemView, position);
        final CellFeedViewHolder holder = (CellFeedViewHolder) viewHolder;
        if (getItemViewType(position) == VIEW_TYPE_DEFAULT) {
            bindDefaultFeedItem(position, holder);
        } else if (getItemViewType(position) == VIEW_TYPE_LOADER) {
            bindLoadingFeedItem(position, holder);
        }


    }

    /**
     * Method animation effect when like
     * @param view
     * @param position
     */
    private void runEnterAnimation(View view, int position){
        if (position >= ANIMATED_ITEMS_COUNT - 1) {
            return;
        }

        if (position > mLastAnimatedPosition) {
            mLastAnimatedPosition = position;
            view.setTranslationY(Utils.getScreenHeight(mContext));
            view.animate()
                    .translationY(0)
                    .setInterpolator(new DecelerateInterpolator(3.f))
                    .setDuration(700)
                    .start();
        }

    }

    /**
     * Defualt effects on cards
     * @param position
     * @param holder
     */
    private void bindDefaultFeedItem(int position, CellFeedViewHolder holder) {
        setupContents(holder, position);
        updateLikesCounter(holder, false);
        updateHeartButton(holder, false);

        holder.btnMore.setTag(position);
        holder.feedCenter.setTag(holder);
        holder.btnLike.setTag(holder);

        if (mLikeAnimations.containsKey(holder)) {
            mLikeAnimations.get(holder).cancel();
        }
        resetLikeAnimationState(holder);

    }

    /**
     * Binding animation if animation exisit
     * @param position
     * @param holder
     */
    private void bindLoadingFeedItem(int position, final CellFeedViewHolder holder) {
        setupContents(holder, position);
        holder.sendingProgress.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                holder.sendingProgress.getViewTreeObserver().removeOnPreDrawListener(this);
                holder.sendingProgress.simulateProgress();
                return true;
            }
        });
        holder.sendingProgress.setOnLoadingFinishedListener(new SendingProgressView.OnLoadingFinishedListener() {
            @Override
            public void onLoadingFinished() {
                holder.sendingProgress.animate().scaleY(0).scaleX(0).setDuration(200).setStartDelay(100);
                holder.progressBg.animate().alpha(0.f).setDuration(200).setStartDelay(100)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                holder.sendingProgress.setScaleX(1);
                                holder.sendingProgress.setScaleY(1);
                                holder.progressBg.setAlpha(1);
                                showLoadingView = false;
                                notifyItemChanged(0);
                            }
                        })
                        .start();
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if (showLoadingView && position == 0) {
            return VIEW_TYPE_LOADER;
        } else {
            return VIEW_TYPE_DEFAULT;
        }
    }

    @Override
    public int getItemCount() {
        return mItemsCount;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id){

            case R.id.btn_more:
                if (onFeedItemClickListener != null) {
                    onFeedItemClickListener.onMoreClick(view, (Integer) view.getTag());
                }
                break;

            case R.id.btn_like:
                CellFeedViewHolder holder = (CellFeedViewHolder) view.getTag();
                if (!mLikedPositions.contains(holder.getPosition())) {
                    mLikedPositions.add(holder.getPosition());
                    updateLikesCounter(holder, true);
                    updateHeartButton(holder, true);
                }
                break;

            case R.id.feed_center:
                CellFeedViewHolder feedHolder = (CellFeedViewHolder) view.getTag();
                if (!mLikedPositions.contains(feedHolder.getPosition())) {
                    mLikedPositions.add(feedHolder.getPosition());
                    updateLikesCounter(feedHolder, true);
                    animatePhotoLike(feedHolder);
                    updateHeartButton(feedHolder, false);
                }
                break;

        }

    }

    /**
     * The method to populate the data needed in cards
     * @param holder
     * @param position
     */
    private void setupContents(CellFeedViewHolder holder, int position){

        ProductsDTO products = mProductsList.get(position);

        String title = products.getName();
        String description = products.getDescription();

        StringBuffer sb = new StringBuffer();
        sb.append(title);
        sb.append("\n");
        sb.append(description);

        holder.feedBottom.setText(sb.toString());

        String imageUrl = products.getPhoto();

        Picasso.with(mContext)
                .load(imageUrl)
                .resize(AVATAR_SIZE, AVATAR_SIZE)
                .centerCrop()
                .into(holder.feedCenter);


    }

    /**
     * Method for the likes handling
     * @param holder
     * @param animated
     */
    private void updateLikesCounter(CellFeedViewHolder holder, boolean animated) {
        int currentLikesCount = mLikesCount.get(holder.getPosition()) + 1;
        String likesCountText = mContext.getResources().getQuantityString(
                R.plurals.likes_count, currentLikesCount, currentLikesCount
        );

        if (animated) {
            holder.likesCounter.setText(likesCountText);
        } else {
            holder.likesCounter.setCurrentText(likesCountText);
        }

        mLikesCount.put(holder.getPosition(), currentLikesCount);
    }

    /**
     * The method to animate the photo when like
     * @param holder
     */
    private void animatePhotoLike(final CellFeedViewHolder holder) {
        if (!mLikeAnimations.containsKey(holder)) {
            holder.bgLike.setVisibility(View.VISIBLE);
            holder.like.setVisibility(View.VISIBLE);

            holder.bgLike.setScaleY(0.1f);
            holder.bgLike.setScaleX(0.1f);
            holder.bgLike.setAlpha(1f);
            holder.like.setScaleY(0.1f);
            holder.like.setScaleX(0.1f);

            AnimatorSet animatorSet = new AnimatorSet();
            mLikeAnimations.put(holder, animatorSet);

            ObjectAnimator bgScaleYAnim = ObjectAnimator.ofFloat(holder.bgLike, "scaleY", 0.1f, 1f);
            bgScaleYAnim.setDuration(200);
            bgScaleYAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
            ObjectAnimator bgScaleXAnim = ObjectAnimator.ofFloat(holder.bgLike, "scaleX", 0.1f, 1f);
            bgScaleXAnim.setDuration(200);
            bgScaleXAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
            ObjectAnimator bgAlphaAnim = ObjectAnimator.ofFloat(holder.bgLike, "alpha", 1f, 0f);
            bgAlphaAnim.setDuration(200);
            bgAlphaAnim.setStartDelay(150);
            bgAlphaAnim.setInterpolator(DECCELERATE_INTERPOLATOR);

            ObjectAnimator imgScaleUpYAnim = ObjectAnimator.ofFloat(holder.like, "scaleY", 0.1f, 1f);
            imgScaleUpYAnim.setDuration(300);
            imgScaleUpYAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
            ObjectAnimator imgScaleUpXAnim = ObjectAnimator.ofFloat(holder.like, "scaleX", 0.1f, 1f);
            imgScaleUpXAnim.setDuration(300);
            imgScaleUpXAnim.setInterpolator(DECCELERATE_INTERPOLATOR);

            ObjectAnimator imgScaleDownYAnim = ObjectAnimator.ofFloat(holder.like, "scaleY", 1f, 0f);
            imgScaleDownYAnim.setDuration(300);
            imgScaleDownYAnim.setInterpolator(ACCELERATE_INTERPOLATOR);
            ObjectAnimator imgScaleDownXAnim = ObjectAnimator.ofFloat(holder.like, "scaleX", 1f, 0f);
            imgScaleDownXAnim.setDuration(300);
            imgScaleDownXAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

            animatorSet.playTogether(bgScaleYAnim, bgScaleXAnim, bgAlphaAnim, imgScaleUpYAnim, imgScaleUpXAnim);
            animatorSet.play(imgScaleDownYAnim).with(imgScaleDownXAnim).after(imgScaleUpYAnim);

            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    resetLikeAnimationState(holder);
                }
            });
            animatorSet.start();
        }
    }

    /**
     * Method for the heart object
     * @param holder
     * @param animated
     */
    private void updateHeartButton(final CellFeedViewHolder holder, boolean animated) {
        if (animated) {
            if (!mLikeAnimations.containsKey(holder)) {
                AnimatorSet animatorSet = new AnimatorSet();
                mLikeAnimations.put(holder, animatorSet);

                ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(holder.btnLike, "rotation", 0f, 360f);
                rotationAnim.setDuration(300);
                rotationAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

                ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(holder.btnLike, "scaleX", 0.2f, 1f);
                bounceAnimX.setDuration(300);
                bounceAnimX.setInterpolator(OVERSHOOT_INTERPOLATOR);

                ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(holder.btnLike, "scaleY", 0.2f, 1f);
                bounceAnimY.setDuration(300);
                bounceAnimY.setInterpolator(OVERSHOOT_INTERPOLATOR);
                bounceAnimY.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        holder.btnLike.setImageResource(R.mipmap.ic_heart_red);
                    }
                });

                animatorSet.play(rotationAnim);
                animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);

                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        resetLikeAnimationState(holder);
                    }
                });

                animatorSet.start();
            }
        } else {
            if (mLikedPositions.contains(holder.getPosition())) {
                holder.btnLike.setImageResource(R.mipmap.ic_heart_red);
            } else {
                holder.btnLike.setImageResource(R.mipmap.ic_heart_outline_grey);
            }
        }
    }

    /**
     * set the animation to default...
     * @param holder
     */
    private void resetLikeAnimationState(CellFeedViewHolder holder) {
        mLikeAnimations.remove(holder);
        holder.bgLike.setVisibility(View.GONE);
        holder.like.setVisibility(View.GONE);
    }

    public static class CellFeedViewHolder extends RecyclerView.ViewHolder {
        ImageView feedCenter;
        TextView feedBottom;
        ImageButton btnLike;
        ImageButton btnMore;
        View bgLike;
        ImageView like;
        TextSwitcher likesCounter;
        FrameLayout imageRoot;

        SendingProgressView sendingProgress;
        View progressBg;

        public CellFeedViewHolder(View view) {
            super(view);

            feedCenter = (ImageView) view.findViewById(R.id.feed_center);

            feedBottom = (TextView) view.findViewById(R.id.feed_bottom);

            btnLike = (ImageButton) view.findViewById(R.id.btn_like);

            btnMore = (ImageButton) view.findViewById(R.id.btn_more);

            bgLike = view.findViewById(R.id.bg_like);

            like = (ImageView) view.findViewById(R.id.like);

            likesCounter = (TextSwitcher) view.findViewById(R.id.likes_counter);

            imageRoot = (FrameLayout) view.findViewById(R.id.image_root);
        }
    }

    /**
     * Method to update all cards
     */
    public void updateItems(ArrayList<ProductsDTO> productsList) {
        mProductsList = productsList;
        mItemsCount = mProductsList.size();

        fillLikesWithRandomValues();
        notifyDataSetChanged();
    }

    /**
     * method to create likes
     */
    private void fillLikesWithRandomValues() {
        for (int i = 0; i < getItemCount(); i++) {
            mLikesCount.put(i, new Random().nextInt(100));
        }
    }

    public void setOnFeedItemClickListener(OnFeedItemClickListener onFeedItemClickListener){
        this.onFeedItemClickListener = onFeedItemClickListener;

    }

    public void clearAllItems(){
        if (mProductsList == null)
            return;

        mItemsCount = 0;
        mProductsList.clear();
        notifyDataSetChanged();

    }

}

