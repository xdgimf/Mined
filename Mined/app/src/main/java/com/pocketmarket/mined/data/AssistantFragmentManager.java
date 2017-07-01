package com.pocketmarket.mined.data;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.pocketmarket.mined.RoundedImageView;
import com.pocketmarket.mined.activity.BuyActivity;
import com.pocketmarket.mined.adapter.ChatListViewAdapter;
import com.pocketmarket.mined.adapter.UserPhotoAdapter;
import com.pocketmarket.mined.di.ApplicationActivity;
import com.pocketmarket.mined.di.ApplicationContext;
import com.pocketmarket.mined.di.ApplicationFragment;
import com.pocketmarket.mined.di.SharedReference;
import com.pocketmarket.mined.dto.ChatAssistantDetailsDTO;
import com.pocketmarket.mined.dto.ChatAssistantDetailsSubDTO;
import com.pocketmarket.mined.dto.ChatAssistantResultDTO;
import com.pocketmarket.mined.dto.ChatSuggestionDTO;
import com.pocketmarket.mined.dto.ChatSuggestionDetailsDTO;
import com.pocketmarket.mined.dto.MessageDTO;
import com.pocketmarket.mined.dto.UserDTO;
import com.pocketmarket.mined.fetcher.SmartReplyFetchr;
import com.pocketmarket.mined.fetcher.SuggestFetchr;
import com.pocketmarket.mined.fragments.BottomSheetMore;
import com.pocketmarket.mined.thread.UserThread;
import com.pocketmarket.mined.utility.AppApi;
import com.pocketmarket.mined.utility.MarshMallowPermission;
import com.pocketmarket.mined.utility.Utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.pocketmarket.mined.utility.AppApi.URL_NAME;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */
@Singleton
public class AssistantFragmentManager {
    private final static String TAG = "AssistantFragmentManager";

    private final static String ASSISTANT_ERROR_MESSAGE = "I'm sorry, I don't understand your message.";
    private final static String HERE_ARE_THE_RESULT = "Here are the results";
    private final static String USER_INFO = "userinfo";

    private final static int REQUEST_TAKE_PHOTO = 1;

    // for speech recognition
    private static final int SPEECH_INPUT = 27;

    // enum for the different chat category
    private enum ChatType {
        MERCHANT, MESSAGE, ASSISTANT, ASSISTANT_SELECTION, ASSISTANT_SUGGESTION_IMAGE, MERCHANT_UPLOAD;

        String getName(){
            switch (this){
                case MERCHANT:
                    return "merchant";

                case MESSAGE:
                    return "message";

                case ASSISTANT:
                    return "assistant";

                case ASSISTANT_SELECTION:
                    return "assistant_selection";

                case ASSISTANT_SUGGESTION_IMAGE:
                    return "assistant_suggestion_image";

                case MERCHANT_UPLOAD:
                    return "merchant_upload_image";

                default:
                    return "invalid chat type....";

            }
        }

    }

    private Context mContext;
    private Activity mActivity;
    private Fragment mFragment;
    private SharedPrefReference mSharedPrefReference;

    // permision rules for marshmallow
    private MarshMallowPermission mMarshMallowPermission;

    private UserThread<String> mUserThread;

    private int mId;
    private String mProfilePhoto;
    private String mUserName;
    private String mFirstName;

    // UI objects
    private LinearLayout mChatLayout;
    private RoundedImageView mProductImage;
    private ProgressBar mProgressBarChat;
    private EditText mEnterMessage;
    private ImageView mCustomMessage;
    private ImageView mCamera;
    private ImageView mImageMic;
    private ListView mListView;
    private ImageView mSendMessage;
    private ImageView mMore;
    private LinearLayout mMessageLayout;
    private RecyclerView mImageGallery;
    private TextToSpeech mTts;

    // Chat handling objects for events and behavior
    private ArrayList<MessageDTO> mFirebaseResult;
    private ChatListViewAdapter.onChatPass mOnChatPass;
    private UserPhotoAdapter.onUploadPhotoPass mOnUploadPhotoPass;

    private ChatListViewAdapter mListAdapter;
    private String mMessageSend;

    private boolean isVoice;

    private UserPhotoAdapter userPhotosAdapter;

    private int mUploadImageStatus;

    private String mAccessToken;

    private boolean mIsStateLoad;

    private String mCurrentPhotoPath;

    @Inject
    public AssistantFragmentManager(@ApplicationContext Context context,
                                    @ApplicationActivity Activity activity,
                                    @SharedReference SharedPrefReference sharedPrefReference,
                                    @ApplicationFragment Fragment fragment
    ) {

        mContext = context;
        mActivity = activity;
        mSharedPrefReference = sharedPrefReference;
        mFragment = fragment;

        isVoice = false;
        mIsStateLoad = false;

        // perform marshmallow permission
        mMarshMallowPermission = new MarshMallowPermission(mActivity);

        // Instantiate the firebase result class
        mFirebaseResult = new ArrayList<MessageDTO>();

        // initialize user info
        initUserInfo();

    }

    /**
     * Gets the accesstoken in shared preferences
     * @return
     */
    public String getAccessToken(){
        mAccessToken = mSharedPrefReference.getUserAccessToken();
        return mAccessToken;
    }

    /**
     * The thread initialization for user info.
     * Gets all personal data in the database
     */
    private void initUserInfo(){
        mUserThread = new UserThread<String>(new Handler());
        mUserThread.setListener(new UserThread.Listener<String>(){

            @Override
            public void onUserThread(String s, UserDTO userResult) {

                if (userResult == null){
                    Log.d(TAG, "user is empty....");
                    return;
                }

                mId = userResult.getId();
                mProfilePhoto = userResult.getPhoto();
                mFirstName = userResult.getFirstname();
                mUserName = userResult.getFirstname() + " " + userResult.getLastname();

                Log.i(TAG, "Profile Photo: " + mProfilePhoto + ", firstName: " + mFirstName + ", userName: " + mUserName + ", mId: " + mId);

                // disable the progressbar
                setLayoutProgress(false);

                disableMessage(false);

                setupAdapter(mFirebaseResult);

                // trigger hi
                //mMessageSend = "hi";
                //assistantReply();

            }
        });

        mUserThread.start();
        mUserThread.getLooper();
        Log.i(TAG, "Background thread started for userinfo");
    }

    /**
     * Gets the UI objects from the AssistantFragment.
     * Is used for handling behavior and events triggered from
     * the UI.
     * @param chatLayout
     * @param productImage
     * @param progressBarChat
     * @param enterMessage
     * @param customMessage
     * @param camera
     * @param imageMic
     * @param listView
     * @param sendMessage
     * @param more
     * @param messageLayout
     * @param imageGallery
     * @param tts
     */
    public void setObjects(LinearLayout chatLayout,
                           RoundedImageView productImage,
                           ProgressBar progressBarChat,
                           EditText enterMessage,
                           ImageView customMessage,
                           ImageView camera,
                           ImageView imageMic,
                           ListView listView,
                           ImageView sendMessage,
                           ImageView more,
                           LinearLayout messageLayout,
                           RecyclerView imageGallery,
                           TextToSpeech tts){

        mChatLayout = chatLayout;
        mProductImage = productImage;
        mProgressBarChat = progressBarChat;
        mEnterMessage = enterMessage;
        mCustomMessage = customMessage;
        mCamera = camera;
        mImageMic = imageMic;
        mListView = listView;
        mSendMessage = sendMessage;
        mMore = more;
        mMessageLayout = messageLayout;
        mImageGallery = imageGallery;
        mTts = tts;

    }

    /**
     * Visibility behavior of keyboard and chat. Example Uploading photos or using the camera.
     * @param bValue
     */
    public void setLayoutProgress(boolean bValue){

        if (mActivity == null)
            return;

        if (bValue){
            // Disable the soft keyboard
            AppApi.hideKeyBoard(mActivity);
            mChatLayout.setVisibility(View.GONE);
        }else{
            mChatLayout.setVisibility(View.VISIBLE);
        }


    }

    /**
     * The method to disable the chat box while sending a data
     * @param value
     */
    public void disableMessage(boolean value){

        if (value){
            mEnterMessage.getText().clear();

        }

        mEnterMessage.setEnabled(!value);
        mSendMessage.setEnabled(!value);

    }

    /**
     * Populates the chat adapter and show its features such as: buttons, images, image_text and so on.
     * @param resultList
     */
    public void setupAdapter(ArrayList<MessageDTO> resultList) {
        Log.i(TAG, "setupAdapter...mProfilePhoto: " + mProfilePhoto);

        setLayoutProgressChat(false);
        if (mListView == null) return;

        if (resultList == null) return;

        if (mActivity == null)
            return;

        Log.i(TAG, "populate the adapter...");

        // get the list adapter value
        mListAdapter = new ChatListViewAdapter(mContext, mActivity, mOnChatPass, resultList, null, mProfilePhoto);

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
     * Controls the layout event and behavior of the chat information
     * @param bValue
     */
    private void setLayoutProgressChat(boolean bValue){

        if (bValue){
            // Disable the soft keyboard
            AppApi.hideKeyBoard(mActivity);
            mProgressBarChat.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);

        }else{
            mProgressBarChat.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        }


    }

    /**
     * Reply from the merchant
     * @param message
     * @return
     */
    public MessageDTO addMerchantReply(String message){
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setId(UUID.randomUUID().toString());
        messageDTO.setMessage(message);
        messageDTO.setSender(ChatType.MERCHANT.getName());

        // convert the time into words ago...
        String formatDate = Utils.getChatTime();

        messageDTO.setTime(formatDate);

        messageDTO.setType(ChatType.MESSAGE.getName());

        return messageDTO;

    }

    /**
     * The method to auto reply....
     * Chat bot reply
     */
    public void assistantReply(){
        disableMessage(true);

        smartReply();

    }

    /**
     * Asyccall for chatbot
     */
    private void smartReply(){

        new SmartReplyTask().execute(getSmartReply());

    }

    /**
     * Url for the smart assistant
     * @return
     */
    private String getSmartReply(){
        return URL_NAME + AppApi.CHAT_REPLY;
    }

    /**
     *  Asynccall for the assistant
     */
    private class SmartReplyTask extends AsyncTask<String, Void, ChatAssistantResultDTO> {

        @Override
        protected ChatAssistantResultDTO doInBackground(String... url) {
            Log.i(TAG, "SmartReplyTask URL: " + url[0]);
            return new SmartReplyFetchr().fetchItems(url[0], mMessageSend, mAccessToken);
        }

        @Override
        protected void onPostExecute(ChatAssistantResultDTO chatAssistantResult) {
            Log.i(TAG, "onPostExecute chatAssistantResult: " + chatAssistantResult);

            disableMessage(false);

            if (mActivity == null)
                return;

            // disable the progressbar
            setLayoutProgress(false);

            if (chatAssistantResult == null)
                return;

            // The result assigns to variables that describe the following:
            // message - the first message for reply
            // message2 - additional message for extended description
            // chatType - describes if it's a reserved or general message used.
            // status - used to identify the kind of event or actions returned by the server

            String message = chatAssistantResult.getMessage();
            String message2 = chatAssistantResult.getMessage2();
            int chatType = chatAssistantResult.getChatType();
            int status = chatAssistantResult.getStatus();

            StringBuffer sb = new StringBuffer();
            sb.append(message);

            if (message2 != null){
                sb.append(" ");
                sb.append(mFirstName);
                sb.append(", ");
                sb.append(message2);
            }

            Log.d(TAG, "Assistant message: " + sb.toString());

            String reply = sb.toString();

            if (reply.trim() == null || reply.trim().equals("null") || reply.trim().equals(null)){
                Log.i(TAG, "Assistant reply is null!!!");
                mFirebaseResult.add(addSmartReply(ASSISTANT_ERROR_MESSAGE));
                setupAdapter(mFirebaseResult);

                // assistant speek
                if (isVoice)
                    speak(ASSISTANT_ERROR_MESSAGE);

                return;
            }

            // check if the reply uses images, button or others
            List<ChatAssistantDetailsDTO> chatAssistantDetailsList = chatAssistantResult.getDetails();

            if (chatAssistantDetailsList == null){
                // add the message
                mFirebaseResult.add(addSmartReply(sb.toString()));

            }else{

                int id = chatAssistantResult.getId();

                // Add the message with objects like button and image
                // The chatType definition: 2 - general message, 1 - reserved message and 0 - default.
                if (chatType == 2){
                    mFirebaseResult.add(addSmartReplyObjects(reply, id, HERE_ARE_THE_RESULT, HERE_ARE_THE_RESULT, chatAssistantDetailsList, ChatType.ASSISTANT_SUGGESTION_IMAGE.getName(), status));
                }else{
                    mFirebaseResult.add(addSmartReplyObjects(reply, id, HERE_ARE_THE_RESULT, HERE_ARE_THE_RESULT, chatAssistantDetailsList, ChatType.ASSISTANT_SELECTION.getName(), status));

                }

            }

            setupAdapter(mFirebaseResult);

            // assistant speak
            if (isVoice)
                speak(reply);

        }

    }

    public String getMessageSend() {
        return mMessageSend;
    }

    public void setMessageSend(String messageSend) {
        mMessageSend = messageSend;
    }

    /**
     * The method to insert reply on firebase
     * @param message
     * @return
     */
    public MessageDTO addSmartReply(String message){
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setId(UUID.randomUUID().toString());
        messageDTO.setMessage(message);

        // convert the time into words ago...
        String formatDate = Utils.getChatTime();

        messageDTO.setTime(formatDate);
        messageDTO.setType(ChatType.ASSISTANT.getName());

        return messageDTO;

    }

    /**
     * Identify if voice message is enabled.
     * @return
     */
    public boolean isVoice() {
        return isVoice;
    }

    public void setVoice(boolean voice) {
        isVoice = voice;
    }

    /**
     * Set the text to speak object values information
     * @param text
     */
    private void speak(String text){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mTts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            mTts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }

        isVoice = false;
    }

    /**
     * The method to include objects that are for selection
     */
    private MessageDTO addSmartReplyObjects(String message,
                                            int id,
                                            String name,
                                            String description,
                                            List<ChatAssistantDetailsDTO> chatAssistantDetailsList,
                                            String messageChatType,
                                            int status){

        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setId(UUID.randomUUID().toString());
        messageDTO.setMessage(message);

        //invoice
        StringBuffer sb = new StringBuffer();

        sb.append("{");

        // id
        sb.append("\"id\":");
        sb.append(id);
        sb.append(",");

        // name
        sb.append("\"name\":");
        sb.append("\"" + name + "\"");
        sb.append(",");


        // description
        sb.append("\"description\":");
        sb.append("\"" + description + "\"");
        sb.append(",");

        //"details"
        sb.append("\"details\":");
        sb.append("[");


        for (int i = 0; i< chatAssistantDetailsList.size(); i ++){

            int detailsId = chatAssistantDetailsList.get(i).getId();
            String detailsName = chatAssistantDetailsList.get(i).getName();
            String detailsDescription = chatAssistantDetailsList.get(i).getDescription();
            String image = chatAssistantDetailsList.get(i).getImage();
            String url = chatAssistantDetailsList.get(i).getUrl();
            String type = chatAssistantDetailsList.get(i).getType();
            int productId = chatAssistantDetailsList.get(i).getProductId();


            sb.append("{");

            // id
            sb.append("\"id\":");
            sb.append(detailsId);
            sb.append(",");

            // name
            sb.append("\"name\":");
            sb.append("\"" + detailsName + "\"");
            sb.append(",");

            // description
            sb.append("\"description\":");
            sb.append("\"" + detailsDescription + "\"");
            sb.append(",");

            // image
            sb.append("\"image\":");
            sb.append("\"" + image + "\"");
            sb.append(",");

            // url
            sb.append("\"url\":");
            sb.append("\"" + url + "\"");
            sb.append(",");


            // type
            sb.append("\"type\":");
            sb.append("\"" + type + "\"");
            sb.append(",");

            // productId
            sb.append("\"productId\":");
            sb.append("\"" + productId + "\"");
            sb.append(",");

            //"detailssub"
            sb.append("\"detailsSub\":");
            sb.append("[");
            sb.append("]");


            sb.append("}");

            if (i < (chatAssistantDetailsList.size() -1)){
                sb.append(",");
            }


        }

        sb.append("]");
        sb.append("}");

        Log.d(TAG, "result: " + sb.toString());

        messageDTO.setMessage2(sb.toString());

        // convert the time into words ago...
        String formatDate = Utils.getChatTime();

        messageDTO.setTime(formatDate);
        messageDTO.setType(messageChatType);
        messageDTO.setStatus(status);

        return messageDTO;

    }

    /**
     * Layout design for image gallery
     */
    public void setupImageGallery(){
        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        mImageGallery.setLayoutManager(layoutManager);
        mImageGallery.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                userPhotosAdapter.setLockedAnimations(true);
            }
        });
    }

    public void userQueuePost(String accessToken){
        mUserThread.queuePost(USER_INFO, getUserInfo(accessToken));
    }

    public static String getUserInfo(String accessToken) {
        return URL_NAME + AppApi.USER_INFO + accessToken;
    }

    public ChatListViewAdapter.onChatPass getOnChatPass() {
        return mOnChatPass;
    }

    public void setOnChatPass(ChatListViewAdapter.onChatPass onChatPass) {
        mOnChatPass = onChatPass;
    }

    public void destroy(){
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
        }

        if (mUserThread == null)
            return;

        mUserThread.quit();

    }

    public void destroyView(){
        if (mUserThread == null)
            return;

        mUserThread.clearQueue();

    }


//    public void sendMessage(String name){
//        Log.d(TAG, "name lowercase: " + name.toLowerCase());
//
//        // upload selection
//        if (name.toLowerCase().equals(APPLY_FOR_REGISTRATION)){
//            Log.d(TAG, APPLY_FOR_REGISTRATION);
//
//            mUploadImageStatus = 1;
//        }
//
//        mMessageSend = name;
//
//        // get smart suggestion to provide tips
//        smartSuggest();
//
//    }

    /**
     * Knowing the current status of uploadImage.
     * @return
     */
    public int getUploadImageStatus() {
        return mUploadImageStatus;
    }

    public void setUploadImageStatus(int uploadImageStatus) {
        mUploadImageStatus = uploadImageStatus;

        if (userPhotosAdapter != null){
            userPhotosAdapter.setUploadImageStatus(uploadImageStatus);
        }
    }

    /**
     * When user press the button objects provide suggestion to the user.
     * @param message
     */
    public void sendSmartSuggest(String message){
        mMessageSend = message;

        // get smart suggestion to provide tips
        smartSuggest();

    }

    /**
     *
     * @return
     */
    private String getSuggestChat(){
        return URL_NAME + AppApi.CHAT_SUGGEST;
    }

    /**
     * Method for smart suggestion when user selects a button
     */
    public void smartSuggest(){

        new SuggestTask().execute(getSuggestChat());

    }

    /**
     * Sugges Json respond from the server
     */
    private class SuggestTask extends AsyncTask<String, Void, ChatSuggestionDTO> {

        @Override
        protected ChatSuggestionDTO doInBackground(String... url) {
            Log.i(TAG, "ProductsTask URL: " + url[0]);
            return new SuggestFetchr().fetchItems(url[0], mMessageSend, mAccessToken);
        }

        @Override
        protected void onPostExecute(ChatSuggestionDTO chatSuggestionDTO) {
            Log.i(TAG, "onPostExecute ChatSuggestionDTO: " + chatSuggestionDTO);

            if (mActivity == null)
                return;

            // disable the progressbar
            setLayoutProgress(false);

            if (chatSuggestionDTO == null){
                return;
            }

            Log.d(TAG, "Suggest: " + chatSuggestionDTO.getName());

            mMessageSend = null;

            int id = chatSuggestionDTO.getId();

            if (id < 1){
                String description = chatSuggestionDTO.getName();
                mFirebaseResult.add(addSmartReply(description));
                setupAdapter(mFirebaseResult);

//                for chikka api only
//                if (description.equals(APPOINTMENT_CONFIRMED)){
//                    new GetChikkaTask(APPOINTMENT_RESPOND_CONFIRMED).execute(getChikkaUrl());
//
//                }
                return;
            }

            mFirebaseResult.add(addSmartSuggestionObjects(chatSuggestionDTO.getDescription(), chatSuggestionDTO));

            setupAdapter(mFirebaseResult);


        }

    }

    /**
     * The method to include objects that are for selection.
     * Translate suggestion data into json service
     * @param message
     * @param chatSuggestionDTO
     * @return
     */
    public MessageDTO addSmartSuggestionObjects(String message,
                                                ChatSuggestionDTO chatSuggestionDTO){

        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setId(UUID.randomUUID().toString());
        messageDTO.setMessage(message);

        int id = chatSuggestionDTO.getId();
        String name = chatSuggestionDTO.getName();
        String description = chatSuggestionDTO.getDescription();

        //invoice
        StringBuffer sb = new StringBuffer();

        sb.append("{");

        // id
        sb.append("\"id\":");
        sb.append(id);
        sb.append(",");

        // name
        sb.append("\"name\":");
        sb.append("\"" + name + "\"");
        sb.append(",");


        // description
        sb.append("\"description\":");
        sb.append("\"" + description + "\"");
        sb.append(",");

        //"details"
        sb.append("\"details\":");
        sb.append("[");

        ArrayList<ChatSuggestionDetailsDTO> chatSuggestionDetailsList = chatSuggestionDTO.getDetails();

        if (chatSuggestionDetailsList != null){
            for (int i = 0; i< chatSuggestionDetailsList.size(); i ++){

                int detailsId = chatSuggestionDetailsList.get(i).getId();
                String detailsName = chatSuggestionDetailsList.get(i).getName();
                String detailsDescription = chatSuggestionDetailsList.get(i).getDescription();
                String image = chatSuggestionDetailsList.get(i).getImage();
                String url = chatSuggestionDetailsList.get(i).getUrl();
                String type = chatSuggestionDetailsList.get(i).getType();

                Log.i(TAG, "detailsName: " + detailsName + ", detailsDescription: " + detailsDescription);

                if (detailsId > 0){
                    sb.append("{");

                    // id
                    sb.append("\"id\":");
                    sb.append(detailsId);
                    sb.append(",");

                    // name
                    sb.append("\"name\":");
                    sb.append("\"" + detailsName + "\"");
                    sb.append(",");

                    // description
                    sb.append("\"description\":");
                    sb.append("\"" + detailsDescription + "\"");
                    sb.append(",");

                    // image
                    sb.append("\"image\":");
                    sb.append("\"" + image + "\"");
                    sb.append(",");

                    // url
                    sb.append("\"url\":");
                    sb.append("\"" + url + "\"");
                    sb.append(",");

                    // type
                    sb.append("\"type\":");
                    sb.append("\"" + type + "\"");
                    sb.append(",");


                    //details sub
                    sb.append("\"detailsSub\":");
                    sb.append("[");

                    ArrayList<ChatAssistantDetailsSubDTO> chatAssistantDetailsSubList = chatSuggestionDetailsList.get(i).getSubDetails();

                    if (chatAssistantDetailsSubList != null){
                        for (int i1 = 0; i1< chatAssistantDetailsSubList.size(); i1 ++){

                            int detailsSubId = chatAssistantDetailsSubList.get(i1).getId();
                            String detailsSubName = chatAssistantDetailsSubList.get(i1).getName();
                            String detailsSubDescription = chatAssistantDetailsSubList.get(i1).getDescription();
                            String detailsSubimage = chatAssistantDetailsSubList.get(i1).getImage();
                            String detailsSuburl = chatAssistantDetailsSubList.get(i1).getUrl();
                            String detailsSubtype = chatAssistantDetailsSubList.get(i1).getType();


                            // filter starts here....
                            sb.append("{");

                            // id
                            sb.append("\"id\":");
                            sb.append(detailsSubId);
                            sb.append(",");

                            // name
                            sb.append("\"name\":");
                            sb.append("\"" + detailsSubName + "\"");
                            sb.append(",");

                            // description
                            sb.append("\"description\":");
                            sb.append("\"" + detailsSubDescription + "\"");
                            sb.append(",");

                            // image
                            sb.append("\"image\":");
                            sb.append("\"" + detailsSubimage + "\"");
                            sb.append(",");

                            // url
                            sb.append("\"url\":");
                            sb.append("\"" + detailsSuburl + "\"");
                            sb.append(",");

                            // type
                            sb.append("\"type\":");
                            sb.append("\"" + detailsSubtype + "\"");


                            sb.append("}");

                            // filter ends here....

                            if (i1 < (chatAssistantDetailsSubList.size() -1)){
                                sb.append(",");
                            }

                        }

                    }


                    //details sub end
                    sb.append("]");

                    sb.append("}");

                    if (i < (chatSuggestionDetailsList.size() -1)){
                        sb.append(",");
                    }


                }


            }


        }


        sb.append("]");
        sb.append("}");

        Log.d(TAG, "result: " + sb.toString());

        messageDTO.setMessage2(sb.toString());

        // convert the time into words ago...
        String formatDate = Utils.getChatTime();

        messageDTO.setTime(formatDate);
        messageDTO.setType(ChatType.ASSISTANT_SUGGESTION_IMAGE.getName());

        return messageDTO;

    }

    /**
     * Sends the message of the client to the assistant for response.
     * This method will perform syntaxnet model to identify part of speech in message
     * @param message
     */
    public void fireBaseSendMessage(String message){
        mMessageSend = message;
        mFirebaseResult.add(addMerchantReply(mMessageSend));

        setupAdapter(mFirebaseResult);

        assistantReply();

    }


    /**
     * Check the permission from marshmallow before showing the gallery
     */
    public void showImageUpload(){
        Log.d(TAG, "mMarshMallowPermission.checkPermissionForExternalStorage() " + mMarshMallowPermission.checkPermissionForExternalStorage());

        if (!mMarshMallowPermission.checkPermissionForReadExternalStorage()){
            mMarshMallowPermission.requestPermissionForReadExternalStorage();

            // apply delay for 5 seconds to check for marshmallow authentication
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    // Actions to do after 5 seconds

                    if (mMarshMallowPermission.checkPermissionForReadExternalStorage()){
                        showGallery();
                    }

                }
            }, 5000);


        }else{
            showGallery();

        }

    }

    /**
     * Show the list of pictures from the phone
     */
    private void showGallery(){
        hideAllLayout();
        mImageGallery.setVisibility(View.VISIBLE);

        if (!mIsStateLoad){
            Log.d(TAG, "mUploadImageStatus: " + mUploadImageStatus);
            userPhotosAdapter = new UserPhotoAdapter(mContext, mAccessToken, mFirebaseResult, mListView, mProfilePhoto, mActivity, mOnChatPass, mId, mOnUploadPhotoPass, mUploadImageStatus);
            mImageGallery.setAdapter(userPhotosAdapter);
            mIsStateLoad = true;
        }
    }

    public UserPhotoAdapter.onUploadPhotoPass getOnUploadPhotoPass() {
        return mOnUploadPhotoPass;
    }

    public void setOnUploadPhotoPass(UserPhotoAdapter.onUploadPhotoPass onUploadPhotoPass) {
        mOnUploadPhotoPass = onUploadPhotoPass;
    }

    /**
     * Hides all the interface for chat
     */
    public void hideAllLayout(){
        mMessageLayout.setVisibility(View.GONE);
        mImageGallery.setVisibility(View.GONE);
        AppApi.hideKeyBoard(mActivity);

    }

    /**
     * Allows the user to interact with the bot
     */
    public void showKeyboard(){
        hideAllLayout();
        mMessageLayout.setVisibility(View.VISIBLE);

        if (userPhotosAdapter != null){
            userPhotosAdapter.clearUploadPhoto();
            userPhotosAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Check permissions for camera before showing it.
     */
    public void showCamera(){
        hideAllLayout();

        if (!mMarshMallowPermission.checkPermissionForCamera()){
            mMarshMallowPermission.requestPermissionForCamera();

            // apply delay for 5 seconds to check for marshmallow authentication
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    // Actions to do after 5 seconds

                    if (mMarshMallowPermission.checkPermissionForCamera()){
                        if (!mMarshMallowPermission.checkPermissionForExternalStorage()){
                            mMarshMallowPermission.requestPermissionForExternalStorage();

                            // apply delay for 5 seconds to check for marshmallow authentication
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    // Actions to do after 5 seconds

                                    if (mMarshMallowPermission.checkPermissionForExternalStorage()){
                                        takePictureIntent();
                                    }

                                }
                            }, 5000);
                        }else{
                            takePictureIntent();
                        }

                    }

                }
            }, 5000);

        }else{
            takePictureIntent();
        }

    }

    /**
     * Show the camera and get the image if user perform capture.
     */
    private void takePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(mActivity.getPackageManager()) != null) {

            // Create the File where the photo should go
            File photoFile = null;

            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.d(TAG, "error creating an image file");
                return;
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(mActivity,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                mFragment.startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

            }

        }
    }

    /**
     * Method to create an image file
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = mActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * Addidtion security permission design by Google in marshmallow
     * @return
     */
    public MarshMallowPermission getMarshMallowPermission() {
        return mMarshMallowPermission;
    }

    public void setMarshMallowPermission(MarshMallowPermission marshMallowPermission) {
        mMarshMallowPermission = marshMallowPermission;
    }

    public ArrayList<MessageDTO> getFirebaseResult() {
        return mFirebaseResult;
    }

    public void setFirebaseResult(ArrayList<MessageDTO> firebaseResult) {
        mFirebaseResult = firebaseResult;
    }

    public String getCurrentPhotoPath() {
        return mCurrentPhotoPath;
    }

    public void setCurrentPhotoPath(String currentPhotoPath) {
        mCurrentPhotoPath = currentPhotoPath;
    }

    /**
     * Extended features: possible use case is a plugin.
     */
    public void showMore(){
        BottomSheetMore bottomSheetMore = new BottomSheetMore();
        bottomSheetMore.show(mFragment.getFragmentManager(), bottomSheetMore.getTag());


    }

    /**
     * The method to speak on the mic
     */
    public void showImageMic(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Something !");
        try {
            mFragment.startActivityForResult(intent, SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), "Sorry! Device does not support speech input", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * BuyActivity allows you to preview the product you want to buy.
     * @param id
     */
    public void initPaymentInvoice(int id, String accessToken){

        Intent i = new Intent(mActivity, BuyActivity.class);
        i.putExtra("id", id);
        i.putExtra("accessToken", accessToken);
        mActivity.startActivity(i);

    }

    /**
     * Basic image upload
     * @param photoPath
     */
    public void uploadImage(String photoPath){

        if (userPhotosAdapter != null){
            userPhotosAdapter.uploadImage(photoPath);

        }

    }

    public void uploadImageFaceDetection(String photoPath){
        if (userPhotosAdapter != null){
            userPhotosAdapter.uploadFaceDetection(photoPath);

        }

    }

}
