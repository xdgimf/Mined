package com.pocketmarket.mined.adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

import com.pocketmarket.mined.R;
import com.pocketmarket.mined.dto.ChatAssistantDetailsDTO;
import com.pocketmarket.mined.dto.ChatSuggestionDetailsDTO;
import com.pocketmarket.mined.dto.MessageDTO;
import com.pocketmarket.mined.dto.UploadPhotoDTO;
import com.pocketmarket.mined.dto.UploadedFormDTO;
import com.pocketmarket.mined.fetcher.UploadPhotoFetchr;
import com.pocketmarket.mined.utility.AppApi;
import com.pocketmarket.mined.utility.Utils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public class UserPhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static String TAG = "UserPhotoAdapter";
    private static final int PHOTO_ANIMATION_DELAY = 600;
    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();

    private final static String[] COLUMNS = {MediaStore.MediaColumns.DATA,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
    private final static String ORDER_BY = MediaStore.Images.Media.DATE_TAKEN;

    private final Context context;
    private final int cellSize;

    private List<String> mPhotoList;

    private boolean lockedAnimations = false;
    private int lastAnimatedItem = -1;

    private Cursor mImagecursor;

    private static ImageView mPhotoUploadCheck = null;

    private String mAccessToken;

    private ArrayList<MessageDTO> mFirebaseResult;

    // the adapter for the chat
    private ChatListViewAdapter mListAdapter;
    private ListView mListView;
    private int mId;
    private String mProfilePhoto;
    private Activity mActivity;
    private ChatListViewAdapter.onChatPass onChatPass;
    private PhotoViewHolder mPhotoViewHolder;
    private int mUploadImageStatus;

    // enum for the different chat category
    private enum ChatType {
        MERCHANT, MERCHANT_UPLOAD;

        String getName(){
            switch (this){
                case MERCHANT:
                    return "merchant";

                case MERCHANT_UPLOAD:
                    return "merchant_upload_image";

                default:
                    return "invalid chat type....";

            }
        }
    }

    public interface onUploadPhotoPass{
        public void onUploadComplete(String message, UploadedFormDTO uploadedFormDTO, ArrayList<ChatSuggestionDetailsDTO> suggestionDetailList);
    }

    UserPhotoAdapter.onUploadPhotoPass mOnUploadPhotoPass;

    public UserPhotoAdapter(Context context, String accessToken, ArrayList<MessageDTO> firebaseResult,
                            ListView listView, String profilePhoto, Activity activity,
                            ChatListViewAdapter.onChatPass onChatPass, int id,
                            onUploadPhotoPass onUploadPhotoPass, int uploadImageStatus) {

        this.context = context;
        this.cellSize = Utils.getScreenWidth(context) / 3;
        mAccessToken = accessToken;

        // objects used to update the chatmode
        mFirebaseResult = firebaseResult;
        mListView = listView;
        mProfilePhoto = profilePhoto;
        mActivity = activity;
        this.onChatPass = onChatPass;
        mId = id;

        // upload image identifier if ocr image will be used...
        mUploadImageStatus = uploadImageStatus;

        mOnUploadPhotoPass = onUploadPhotoPass;

        Log.d(TAG, "profile photo link: " + mProfilePhoto);

        mImagecursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, COLUMNS, null,
                null, ORDER_BY + " DESC");

        Log.i(TAG, "Count: " + mImagecursor.getCount());

        mPhotoList = new ArrayList<String>();


        for (int i = 0; i < mImagecursor.getCount(); i++) {
            mImagecursor.moveToPosition(i);
            int dataColumnIndex = mImagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
            Log.i(TAG, "Data: " + mImagecursor.getString(dataColumnIndex));
            mPhotoList.add(mImagecursor.getString(dataColumnIndex));

        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false);
        StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
        layoutParams.height = cellSize;
        layoutParams.width = cellSize;
        layoutParams.setFullSpan(false);
        view.setLayoutParams(layoutParams);

        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        mPhotoViewHolder = (PhotoViewHolder) holder;

        bindPhoto(mPhotoViewHolder, position);

    }

    private void bindPhoto(final PhotoViewHolder holder, final int position){
        holder.mPhotoUpload.setVisibility(View.GONE);
        holder.mPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mPhotoUploadCheck == null){
                    mPhotoUploadCheck = holder.mPhotoUpload;
                }else{
                    mPhotoUploadCheck.setVisibility(View.GONE);
                    mPhotoUploadCheck = holder.mPhotoUpload;
                }
                holder.mPhotoUpload.setVisibility(View.VISIBLE);
            }
        });

        holder.mPhotoUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.mPhotoUpload.setVisibility(View.GONE);
                clearUploadPhoto();
                uploadImage(position);
            }
        });

        File file = new File(mPhotoList.get(position));

        Picasso.with(context)
                .load(file)
                .resize(cellSize, cellSize)
                .centerCrop()
                .into(holder.mPhoto, new Callback() {
                    @Override
                    public void onSuccess() {
                        animatePhoto(holder);
                    }

                    @Override
                    public void onError() {

                    }
                });

        if (lastAnimatedItem < position) lastAnimatedItem = position;

    }

    private void animatePhoto(PhotoViewHolder viewHolder){
        if (!lockedAnimations) {
            if (lastAnimatedItem == viewHolder.getAdapterPosition()) {
                setLockedAnimations(true);
            }

            long animationDelay = PHOTO_ANIMATION_DELAY + viewHolder.getAdapterPosition() * 30;

            viewHolder.mRootPhoto.setScaleY(0);
            viewHolder.mRootPhoto.setScaleX(0);

            viewHolder.mRootPhoto.animate()
                    .scaleY(1)
                    .scaleX(1)
                    .setDuration(200)
                    .setInterpolator(INTERPOLATOR)
                    .setStartDelay(animationDelay)
                    .start();
        }
    }

    @Override
    public int getItemCount() {
        return mPhotoList.size();
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        FrameLayout mRootPhoto;
        ImageView mPhoto;
        ImageView mPhotoUpload;

        public PhotoViewHolder(View view) {
            super(view);

            mRootPhoto = (FrameLayout) view.findViewById(R.id.rootPhoto);
            mPhoto = (ImageView) view.findViewById(R.id.photo);

            mPhotoUpload = (ImageView) view.findViewById(R.id.image_upload);

        }

    }

    public void setLockedAnimations(boolean lockedAnimations) {
        this.lockedAnimations = lockedAnimations;
    }

    /**
     * Method to upload the image with processing equivalent
     * 1 - for ocr
     * 2 - for face recognition
     * others - normal upload
     * @param position
     */
    private void uploadImage(int position){
        String photo = mPhotoList.get(position);

        Log.d(TAG, "Upload Image status: " + mUploadImageStatus);

        switch (mUploadImageStatus){
            case 0: // upload image using inception from tensorflow
                new UploadPhotoTask(photo).execute(getUploadImageInception());
                break;

            case 1: // upload registration form for ocr
                new UploadPhotoTask(photo).execute(getUploadImageOCR());
                break;

            case 2: // upload selfie and detect face
                uploadFaceDetection(photo);
                break;

            case 3: // government id images
                uploadImageId(photo);
                break;

            default:
                uploadImage(photo);
                break;
        }


    }

    private String getUploadImage(){
        return AppApi.URL_NAME + AppApi.UPLOAD_PHOTO;
    }

    private String getUploadImageOCR(){
        return AppApi.URL_NAME + AppApi.UPLOAD_PHOTO_OCR_TEXT;
    }

    private String getUploadImageFaceDetection(){
        return AppApi.URL_NAME + AppApi.UPLOAD_PHOTO_FACE_DETECTION;
    }

    private String getUploadImageInception(){
        return AppApi.URL_NAME + AppApi.UPLOAD_PHOTO_INCEPTION;
    }

    private String getUploadImageId(){
        return AppApi.URL_NAME + AppApi.UPLOAD_PHOTO_ID;
    }

    /**
     *  Asynccall for the assistant
     */
    private class UploadPhotoTask extends AsyncTask<String, Void, UploadPhotoDTO> {
        private String mPhoto;

        public UploadPhotoTask(String photo) {
            mPhoto = photo;
        }

        @Override
        protected UploadPhotoDTO doInBackground(String... url) {
            Log.i(TAG, "SmartReplyTask URL: " + url[0]);
            return new UploadPhotoFetchr().fetchItems(url[0], mPhoto, mAccessToken, "file");
        }

        @Override
        protected void onPostExecute(UploadPhotoDTO uploadPhotoDTO) {
            Log.i(TAG, "onPostExecute uploadPhotoDTO: " + uploadPhotoDTO);

            if (uploadPhotoDTO == null)
                return;

            int id = uploadPhotoDTO.getId();
            String imageLink = uploadPhotoDTO.getImageLink();
            String message = uploadPhotoDTO.getMessage();
            // check if the reply uses images, button or others 
            List<ChatAssistantDetailsDTO> detailsList = uploadPhotoDTO.getDetails();

            if (id > 0){
                Log.i(TAG, "id: " + id + ", imageLink: " + imageLink + ", message: " + message + ", detailsList: " + detailsList);

                mFirebaseResult.add(addMerchantUpload(id, imageLink));

                setupAdapter(mFirebaseResult);

                ArrayList<ChatSuggestionDetailsDTO> suggestionDetailsList = new ArrayList<ChatSuggestionDetailsDTO>();

                if (detailsList != null){
                    for (ChatAssistantDetailsDTO details: detailsList){
                        ChatSuggestionDetailsDTO chatSuggestionDetailsDTO = new ChatSuggestionDetailsDTO();
                        chatSuggestionDetailsDTO.setId(details.getId());
                        chatSuggestionDetailsDTO.setName(details.getName());
                        chatSuggestionDetailsDTO.setDescription(details.getDescription());
                        chatSuggestionDetailsDTO.setImage(details.getImage());
                        chatSuggestionDetailsDTO.setUrl(details.getUrl());
                        chatSuggestionDetailsDTO.setType(details.getType());

                        suggestionDetailsList.add(chatSuggestionDetailsDTO);


                    }

                }

                mOnUploadPhotoPass.onUploadComplete(message, uploadPhotoDTO.getUploadedForm(), suggestionDetailsList);


            }

        }

    }

    /**
     * Reply from the merchant using upload
     * @param imageLink
     * @return
     */
    private MessageDTO addMerchantUpload(int id, String imageLink){
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setId(Integer.toString(id));
        messageDTO.setMessage(imageLink);
        messageDTO.setSender(ChatType.MERCHANT.getName());

        // convert the time into words ago...
        String formatDate = Utils.getChatTime();

        messageDTO.setTime(formatDate);

        messageDTO.setType(ChatType.MERCHANT_UPLOAD.getName());

        return messageDTO;

    }

    /**
     * The adapter for chating
     * @param resultList
     */
    void setupAdapter(ArrayList<MessageDTO> resultList) {

        if (mListView == null) return;

        if (resultList == null) return;

        if (context == null)
            return;

        if (mActivity == null)
            return;

        Log.i(TAG, "populate the adapter...");

        // get the list adapter value
        mListAdapter = new ChatListViewAdapter(context, mActivity, onChatPass, resultList, null, mProfilePhoto);

        mListView.setAdapter(mListAdapter);
        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });
        mListView.setSelection(mListView.getCount() -1);
    }

    /**
     * The method to set the default of the photo
     */
    public void clearUploadPhoto(){
        mPhotoUploadCheck = null;
        mPhotoViewHolder.mPhotoUpload.setVisibility(View.GONE);
    }

    public void setUploadImageStatus(int uploadImageStatus) {
        mUploadImageStatus = uploadImageStatus;
    }

    public void uploadFaceDetection(String photo){
        new UploadPhotoTask(photo).execute(getUploadImageFaceDetection());
    }

    public void uploadImageId(String photo){
        new UploadPhotoTask(photo).execute(getUploadImageId());
    }

    public void uploadImage(String photo){
        new UploadPhotoTask(photo).execute(getUploadImage());
    }
}

