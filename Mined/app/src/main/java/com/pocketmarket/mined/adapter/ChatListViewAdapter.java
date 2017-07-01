package com.pocketmarket.mined.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pocketmarket.mined.ImageCache;
import com.pocketmarket.mined.R;
import com.pocketmarket.mined.RoundedImageView;
import com.pocketmarket.mined.controllers.ButtonPlus;
import com.pocketmarket.mined.dto.ChatAssistantDetailsDTO;
import com.pocketmarket.mined.dto.ChatAssistantDetailsSubDTO;
import com.pocketmarket.mined.dto.ChatSuggestionDTO;
import com.pocketmarket.mined.dto.ChatSuggestionDetailsDTO;
import com.pocketmarket.mined.dto.MessageDTO;
import com.pocketmarket.mined.utility.Utils;
import com.pocketmarket.mined.widget.CustomFontTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public class ChatListViewAdapter extends ArrayAdapter<MessageDTO> {
    private static String TAG = "ChatListViewAdapter";


    private enum ChatType {
        MERCHANT, MESSAGE, ASSISTANT, ASSISTANT_SELECTION, ASSISTANT_SUGGESTION_IMAGE, MERCHANT_UPLOAD;

        String getName() {
            switch (this) {
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

    // alignment of chat layout
    private static final String LEFT_ALIGN = "Left";
    private static final String RIGHT_ALIGN = "Right";
    private static final String CENTER_ALIGN = "center";


    private Activity mActivity;
    private String mCustomerPhoto;
    private String mProfilePhoto;

    private enum ObjectsType {
        BUTTON, IMAGE, TEXT, TEXT_IMAGE, IMAGE_BUTTON;

    }

    public interface onChatPass {
        public void onButtonClick(int id, String name, int status, int productId);

        public void onImageClick(int id, String name, String url);

    }

    onChatPass mOnChatPass;

    public ChatListViewAdapter(Context context,
                               Activity activity,
                               onChatPass onChatPass,
                               ArrayList<MessageDTO> objects,
                               String customerPhoto,
                               String profilePhoto) {
        super(context, 0, objects);

        mOnChatPass = onChatPass;

        mActivity = activity;
        mCustomerPhoto = customerPhoto;
        mProfilePhoto = profilePhoto;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mActivity.getLayoutInflater().inflate(R.layout.list_item_chat, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mTextTitle = (CustomFontTextView) convertView.findViewById(R.id.text_title);
            viewHolder.mRoundedImage = (RoundedImageView) convertView.findViewById(R.id.image_round);
            viewHolder.mTextDate = (CustomFontTextView) convertView.findViewById(R.id.text_date);
            viewHolder.mImageGallery = (RelativeLayout) convertView.findViewById(R.id.image_gallery);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MessageDTO message = getItem(position);

        StringBuffer sbMessage = new StringBuffer();
        String messageTitle = null;

        // The sender is null if the bot is the one who automatically responds to the user message.
        // No sender is use since it uses machine intelligence like syntaxnet to show results
        if (message.getSender() == null) {
            // messages are all buyer
            messageTitle = message.getMessage();
            sbMessage.append(messageTitle);

            if (message.getType().equals(ChatType.MESSAGE.getName())) {

                // show the left user icon and message
                chatLeftReply(viewHolder, mCustomerPhoto, false);

            } else if (message.getType().equals(ChatType.ASSISTANT.getName())) {

                // show the left user icon and message
                chatLeftReply(viewHolder, "", true);

            } else if (message.getType().equals(ChatType.ASSISTANT_SELECTION.getName())) {

                // show the assistant reply with options
                chatAssistantLeftCenterReply(viewHolder, "", true, message.getMessage2(), message.getStatus());


            } else if (message.getType().equals(ChatType.ASSISTANT_SUGGESTION_IMAGE.getName())) {

                // This method is used when uploading an image and allow the use of inception model
                // It will show a gallery type images where you can scroll to the right
                // show the assistant reply with options
                chatSuggestionLeftCenterReply(viewHolder, "", true, message.getMessage2(), message.getStatus());


            } else if (message.getType().equals(ChatType.MERCHANT_UPLOAD.getName())) {

                // show the merchant upload image
                chatMerchantUploadImage(viewHolder, mCustomerPhoto, message.getId(), message.getMessage());

            } else {

                // no message
                String messageProduct = message.getMessage();

                sbMessage.append(messageProduct);

                viewHolder.mRoundedImage.setVisibility(View.GONE);
                viewHolder.mImageGallery.setVisibility(View.GONE);


                setLayoutAlignment(CENTER_ALIGN, viewHolder);

                viewHolder.mTextTitle.setBackgroundResource(R.drawable.btn_white);

            }


        } else if (message.getSender().equals(ChatType.MERCHANT.getName())) {
            // If the sender is the merchant or user of the product get the type value
            // and display the objects needed.

            if (message.getType().equals(ChatType.MESSAGE.getName())) {
                sbMessage.append("<font color='#ffffff'>");
                messageTitle = message.getMessage();

                sbMessage.append(messageTitle);
                sbMessage.append("</font>");

                // show the right user icon and message
                chatRightReply(viewHolder, mProfilePhoto, false);
            } else if (message.getType().equals(ChatType.ASSISTANT.getName())) {
                sbMessage.append("<font color='#ffffff'>");

                messageTitle = message.getMessage();

                sbMessage.append(messageTitle);
                sbMessage.append("</font>");

                // show the right user icon and message
                chatRightReply(viewHolder, "", true);

            } else if (message.getType().equals(ChatType.ASSISTANT_SELECTION.getName())) {

                // show the assistant reply with options
                chatCenterReply(viewHolder, message.getMessage());


            } else if (message.getType().equals(ChatType.MERCHANT_UPLOAD.getName())) {
                // show the merchant upload image
                chatMerchantUploadImage(viewHolder, mProfilePhoto, message.getId(), message.getMessage());
            }
        }

        Spanned htmlSpan = Html.fromHtml(sbMessage.toString());
        viewHolder.mTextTitle.setText(htmlSpan);

        StringBuffer sbDate = new StringBuffer();
        sbDate.append("<font color='#a7afb8'>");
        sbDate.append(message.getTime());
        sbDate.append("</font>");

        viewHolder.mTextDate.setText(Html.fromHtml(sbDate.toString()));

        return convertView;

    }

    /**
     * The method to show reply on the left side
     *
     * @param viewHolder
     * @param photo
     * @param isAssistant
     * @return
     */
    private void chatLeftReply(ViewHolder viewHolder, String photo, boolean isAssistant) {

        setLayoutAlignment(LEFT_ALIGN, viewHolder);
        viewHolder.mTextTitle.setVisibility(View.VISIBLE);
        viewHolder.mTextTitle.setBackgroundResource(R.mipmap.chat_bubble_white);
        viewHolder.mTextTitle.setCompoundDrawables(null, null, null, null);
        viewHolder.mTextTitle.setCompoundDrawablePadding(0);

        viewHolder.mImageGallery.setVisibility(View.GONE);
        viewHolder.mRoundedImage.setVisibility(View.VISIBLE);

        ImageCache.getInstance(mActivity).display(photo, viewHolder.mRoundedImage, (isAssistant) ? R.mipmap.mined_assistant : R.mipmap.blank);

    }

    /**
     * The method to show reply on the right side
     *
     * @param viewHolder
     * @param photo
     * @param isAssistant
     */
    private void chatRightReply(ViewHolder viewHolder, String photo, boolean isAssistant) {

        setLayoutAlignment(RIGHT_ALIGN, viewHolder);

        viewHolder.mTextTitle.setVisibility(View.VISIBLE);
        viewHolder.mTextTitle.setBackgroundResource(R.mipmap.chat_bubble_green);
        viewHolder.mTextTitle.setCompoundDrawables(null, null, null, null);
        viewHolder.mTextTitle.setCompoundDrawablePadding(0);

        viewHolder.mImageGallery.setVisibility(View.GONE);
        viewHolder.mRoundedImage.setVisibility(View.VISIBLE);

        ImageCache.getInstance(mActivity).display(photo, viewHolder.mRoundedImage, (isAssistant) ? R.mipmap.mined_assistant : R.mipmap.blank);

    }

    /**
     * The method to show the center reply
     *
     * @param viewHolder
     * @param message
     */
    private void chatCenterReply(ViewHolder viewHolder, String message) {
        viewHolder.mTextTitle.setVisibility(View.INVISIBLE);
        viewHolder.mRoundedImage.setVisibility(View.INVISIBLE);

        viewHolder.mImageGallery.setVisibility(View.VISIBLE);
        viewHolder.mImageGallery.setBackgroundResource(R.drawable.btn_white);

        int childCount = viewHolder.mImageGallery.getChildCount();

        if (childCount < 1) {
            createImageGallery(viewHolder, message);
        }
    }

    /**
     * The method to show the center reply
     *
     * @param viewHolder
     * @param message
     */
    private void chatAssistantLeftCenterReply(ViewHolder viewHolder,
                                              String photo,
                                              boolean isAssistant,
                                              String message,
                                              int status) {

        setLayoutAlignment(LEFT_ALIGN, viewHolder);
        viewHolder.mTextTitle.setVisibility(View.VISIBLE);
        viewHolder.mTextTitle.setBackgroundResource(R.mipmap.chat_bubble_white);
        viewHolder.mTextTitle.setCompoundDrawables(null, null, null, null);
        viewHolder.mTextTitle.setCompoundDrawablePadding(0);

        viewHolder.mRoundedImage.setVisibility(View.VISIBLE);

        ImageCache.getInstance(mActivity).display(photo, viewHolder.mRoundedImage, (isAssistant) ? R.mipmap.mined_assistant : R.mipmap.blank);


        viewHolder.mImageGallery.setVisibility(View.VISIBLE);
        //viewHolder.mImageGallery.setBackgroundResource(R.drawable.btn_white);

        // create the assistant objects
        createAssistantObjects(viewHolder, message, status);
    }

    /**
     * The method to show the center reply
     *
     * @param viewHolder
     * @param message
     */
    private void chatSuggestionLeftCenterReply(ViewHolder viewHolder,
                                               String photo,
                                               boolean isAssistant,
                                               String message,
                                               int status) {
        setLayoutAlignment(LEFT_ALIGN, viewHolder);
        viewHolder.mTextTitle.setVisibility(View.VISIBLE);
        viewHolder.mTextTitle.setBackgroundResource(R.mipmap.chat_bubble_white);
        viewHolder.mTextTitle.setCompoundDrawables(null, null, null, null);
        viewHolder.mTextTitle.setCompoundDrawablePadding(0);

        viewHolder.mRoundedImage.setVisibility(View.VISIBLE);

        ImageCache.getInstance(mActivity).display(photo, viewHolder.mRoundedImage, (isAssistant) ? R.mipmap.mined_assistant : R.mipmap.blank);


        viewHolder.mImageGallery.setVisibility(View.VISIBLE);
        //viewHolder.mImageGallery.setBackgroundResource(R.drawable.btn_white);

        // create the suggestion objects
        createSuggestionObjects(viewHolder, message, status);
    }

    /**
     * The method to show reply on the left side with image upload
     *
     * @param viewHolder
     * @param photo
     * @param id
     * @return
     */
    private void chatMerchantUploadImage(final ViewHolder viewHolder, String photo, String id, String imageLink) {

        Log.d(TAG, "Photo: " + photo);

        //setLayoutAlignment(LEFT_ALIGN, viewHolder);
        setLayoutAlignment(RIGHT_ALIGN, viewHolder);
        viewHolder.mTextTitle.setVisibility(View.GONE);
        viewHolder.mRoundedImage.setVisibility(View.GONE);

        viewHolder.mImageGallery.setVisibility(View.VISIBLE);
        //viewHolder.mImageGallery.setBackgroundResource(R.drawable.btn_white);

        createUploadImage(viewHolder, id, imageLink, 0);

    }

    /**
     * @param alignment
     * @param viewHolder
     */
    private void setLayoutAlignment(String alignment, ViewHolder viewHolder) {

        if (alignment.equals(LEFT_ALIGN)) {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(130, 0, 150, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

            viewHolder.mTextTitle.setLayoutParams(lp);


            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100,
                    100);

            params.setMargins(20, 20, 20, 0);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

            viewHolder.mRoundedImage.setLayoutParams(params);

        } else if (alignment.equals(RIGHT_ALIGN)) {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(150, 0, 130, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

            viewHolder.mTextTitle.setLayoutParams(lp);


            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100,
                    100);

            params.setMargins(10, 20, 20, 0);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);


            viewHolder.mRoundedImage.setLayoutParams(params);


        } else if (alignment.equals(CENTER_ALIGN)) {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 30, 0, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

            viewHolder.mTextTitle.setLayoutParams(lp);

        }

    }


    /**
     * The assistant result to filter
     *
     * @param viewHolder
     * @param body
     */
    private void createAssistantObjects(ViewHolder viewHolder, String body, int status) {

        Log.i(TAG, "createAssistantObjects body: " + body);

        if (!isValidJson(body)) {
            Log.i(TAG, "Invalid json format!!!");
            return;
        }

        List<ChatAssistantDetailsDTO> chatAssistantDetailsList = parseMessageObject(body);

        if (chatAssistantDetailsList == null || chatAssistantDetailsList.size() < 1) {
            Log.i(TAG, "null object result");
            return;
        }


        Log.i(TAG, "Continue....createAssistantObjects");

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, mActivity.getResources().getDisplayMetrics());

        LinearLayout linearMain = new LinearLayout(getContext());
        linearMain.setLayoutParams(params);
        linearMain.setOrientation(LinearLayout.VERTICAL);
        linearMain.setPadding(20, 20, 20, 20);

        // scrollview for multiple selection
        LinearLayout choicesLayout = new LinearLayout(getContext());
        choicesLayout.setLayoutParams(params);
        choicesLayout.setOrientation(LinearLayout.VERTICAL);
        choicesLayout.setPadding(20, 20, 20, 20);

        // Layout for the object gallery
        LinearLayout bgLayout = new LinearLayout(getContext());
        bgLayout.setLayoutParams(params);
        bgLayout.setOrientation(LinearLayout.VERTICAL);
        bgLayout.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams paramsObject = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        for (ChatAssistantDetailsDTO chatAssistantDetails : chatAssistantDetailsList) {
            int id = chatAssistantDetails.getId();
            String type = chatAssistantDetails.getType();
            String name = chatAssistantDetails.getName();
            String description = chatAssistantDetails.getDescription();
            String image = chatAssistantDetails.getImage();
            String url = chatAssistantDetails.getUrl();
            int productId = chatAssistantDetails.getProductId();

            bgLayout.addView(getObjectType(id, type, name, description,image, url, paramsObject, size, status, productId));

        }

        choicesLayout.addView(bgLayout);

        linearMain.addView(choicesLayout);
        // gallery image till here....


        viewHolder.mImageGallery.removeAllViews();
        viewHolder.mImageGallery.addView(linearMain);

        // set the gallery layout below the text title
        RelativeLayout.LayoutParams galleryLayout = (RelativeLayout.LayoutParams) viewHolder.mImageGallery.getLayoutParams();
        galleryLayout.addRule(RelativeLayout.BELOW, R.id.text_title);

        viewHolder.mImageGallery.setLayoutParams(galleryLayout);

        // set the text date bellow the gallery
        RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) viewHolder.mTextDate.getLayoutParams();
        p.addRule(RelativeLayout.BELOW, R.id.image_gallery);

        viewHolder.mTextDate.setLayoutParams(p);

    }

    /**
     * This is the method to identify the type of objects to display in chatmode.
     * The following are the list:
     * Button - shows button only option
     * Image - shows the image of the items
     * Text - shows the message
     * Text_Image - shows the name of the object with name
     * Image_Button - shows the image and button
     * @param id
     * @param type
     * @param name
     * @param description
     * @param image
     * @param url
     * @param paramsObject
     * @param size
     * @return
     */
    private View getObjectType(int id, String type, String name, String description, String image,
                               String url, LinearLayout.LayoutParams paramsObject, int size, int status, int productId) {

        if (type.toLowerCase().equals(ObjectsType.BUTTON.toString().toLowerCase())) {
            return getObjectButton(id, name, description, paramsObject, status, productId);

        } else if (type.toLowerCase().equals(ObjectsType.IMAGE.toString().toLowerCase())) {
            return getObjectImage(id, name, image, url, paramsObject, size);

        } else if (type.toLowerCase().equals(ObjectsType.TEXT.toString().toLowerCase())) {
            return getObjectText(description, paramsObject);

        } else if (type.toLowerCase().equals(ObjectsType.TEXT_IMAGE.toString().toLowerCase())) {
            return getObjectTextImage(id, name, description, image, url, paramsObject, size);

        } else if (type.toLowerCase().equals(ObjectsType.IMAGE_BUTTON.toString().toLowerCase())) {
            return getObjectImageButton(id, name, description, image, url, paramsObject, size, status, productId);

        } else {
            return null;

        }

    }


    /**
     * The method to show the button on chat
     * @param id
     * @param name
     * @param description
     * @param paramsObject
     * @return
     */
    private Button getObjectButton(final int id, String name, final String description, LinearLayout.LayoutParams paramsObject, final int status, final int productId) {

        ButtonPlus button = new ButtonPlus(getContext());
        button.setLayoutParams(paramsObject);
        button.setText(name);
        button.setTextColor(Color.BLUE);
        button.setBackgroundResource(R.drawable.button_milestone);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getButtonClick(id, description, status, productId);
            }
        });
        int fifteen = Utils.dpToPx(15);
        button.setPadding(fifteen, 0, fifteen, 0);
        button.setAllCaps(false);
        return button;
    }

    /**
     * The method to show an image in chat
     *
     * @param id
     * @param name
     * @param image
     * @param paramsImage
     * @param size
     * @return
     */
    private ImageView getObjectImage(final int id, final String name, String image,
                                     final String url, LinearLayout.LayoutParams paramsImage, int size) {

        ImageView imageView = new ImageView(getContext());
        imageView.setLayoutParams(paramsImage);
        imageView.getLayoutParams().height = size;
        imageView.getLayoutParams().width = size;
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageClick(id, name, url);
            }
        });

        ImageCache.getInstance(mActivity).display(image, imageView, R.mipmap.blank);

        return imageView;
    }


    /**
     * The method to show text message on chat
     * @param description
     * @param paramsImage
     * @return
     */
    private TextView getObjectText(String description, LinearLayout.LayoutParams paramsImage) {

        TextView textView = new TextView(getContext());
        textView.setLayoutParams(paramsImage);
        textView.setText(description);
        textView.setPadding(20, 20, 20, 20);

        return textView;
    }


    /**
     * The method to show a combination of text and image inside chat
     * @param id
     * @param name
     * @param description
     * @param image
     * @param url
     * @param params
     * @param size
     * @return
     */
    private LinearLayout getObjectTextImage(final int id, final String name, String description, String image,
                                            final String url, LinearLayout.LayoutParams params, int size) {


        // Layout for the object gallery
        LinearLayout bgLayout = new LinearLayout(getContext());
        bgLayout.setLayoutParams(params);
        bgLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams paramsText = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsText.gravity = Gravity.CENTER;

        TextView textView = new TextView(getContext());
        textView.setLayoutParams(paramsText);
        textView.setText(description);
        textView.setPadding(20, 20, 20, 20);

        bgLayout.addView(textView);

        LinearLayout.LayoutParams paramsImage = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsImage.gravity = Gravity.CENTER;

        ImageView imageView = new ImageView(getContext());
        imageView.setLayoutParams(paramsImage);
        imageView.getLayoutParams().height = size;
        imageView.getLayoutParams().width = size + 50;

        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageClick(id, name, url);
            }
        });

        ImageCache.getInstance(mActivity).display(image, imageView, R.mipmap.blank);

        bgLayout.addView(imageView);

        return bgLayout;
    }

    /**
     * The method to show an image and button inside chat
     * @param id
     * @param name
     * @param description
     * @param image
     * @param url
     * @param params
     * @param size
     * @return
     */
    private LinearLayout getObjectImageButton(final int id, final String name, final String description, String image,
                                              final String url, LinearLayout.LayoutParams params, int size, final int status, final int productId) {


        // Layout for the object gallery
        LinearLayout bgLayout = new LinearLayout(getContext());
        bgLayout.setLayoutParams(params);
        bgLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams paramsImage = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        ImageView imageView = new ImageView(getContext());
        imageView.setLayoutParams(paramsImage);
        imageView.getLayoutParams().height = size;
        imageView.getLayoutParams().width = size * 2;
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageClick(id, name, url);
            }
        });
        // To have space below the image and button
        imageView.setPadding(0,0,0,20);

        ImageCache.getInstance(mActivity).display(image, imageView, R.mipmap.blank);

        bgLayout.addView(imageView);


        LinearLayout.LayoutParams paramsText = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsText.gravity = Gravity.CENTER;

        ButtonPlus button = new ButtonPlus(getContext());
        button.setLayoutParams(paramsText);
        button.setText(name);
        button.setBackgroundResource(R.drawable.button_milestone);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getButtonClick(id, description, status, productId);
            }
        });
        // for the text alignment in button
        button.setPadding(20, 0, 20, 0);
        button.setAllCaps(false);

        bgLayout.addView(button);

        return bgLayout;
    }

    /**
     * The method when the button is clicked in chat
     * @param id
     * @param name
     */
    private void getButtonClick(int id, String name, int status, int productId) {
        Log.d(TAG, "Button ID: " + id + ", name: " + name + ", status: " + status + ", productId: " + productId);

        mOnChatPass.onButtonClick(id, name, status, productId);

    }

    /**
     * The method when image is click
     * @param id
     * @param name
     * @param url
     */
    private void getImageClick(int id, String name, String url) {
        Log.d(TAG, "Image ID: " + id + ", name: " + name + ", url: " + url);

        mOnChatPass.onImageClick(id, name, url);

    }



    /**
     * @param viewHolder
     */

    /**
     * CreateImageGallery is a method that contains images and behaves like image gallery where
     * you can scroll using swipe left or right
     * @param viewHolder
     * @param body
     */
    private void createImageGallery(ViewHolder viewHolder, String body) {

        Log.i(TAG, "body: " + body);

        if (!isValidJson(body)) {
            Log.i(TAG, "Invalid json format!!!");
            return;
        }

        ChatSuggestionDTO chatSuggestionDTO = parseMessageImage(body);

        if (chatSuggestionDTO == null) {
            Log.i(TAG, "null object result");
            return;
        }


        Log.i(TAG, "Continue....createImageGallery");

        String description = chatSuggestionDTO.getDescription();


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        LinearLayout linearMain = new LinearLayout(getContext());
        linearMain.setLayoutParams(params);
        linearMain.setOrientation(LinearLayout.VERTICAL);

        TextView textView = new TextView(getContext());
        textView.setText(description);

        linearMain.addView(textView);


        // Layout for the image gallery
        LinearLayout imageLayout = new LinearLayout(getContext());
        imageLayout.setLayoutParams(params);
        imageLayout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams paramsImage = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, mActivity.getResources().getDisplayMetrics());


        ArrayList<ChatSuggestionDetailsDTO> chatDetails = chatSuggestionDTO.getDetails();
        for (int i = 0; i < chatDetails.size(); i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setLayoutParams(paramsImage);
            imageView.getLayoutParams().height = size;
            imageView.getLayoutParams().width = size;
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

            ImageCache.getInstance(mActivity).display(chatDetails.get(i).getImage(), imageView, R.mipmap.blank);

            imageLayout.addView(imageView);

        }

        linearMain.addView(imageLayout);

        viewHolder.mImageGallery.addView(linearMain);

        RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) viewHolder.mTextDate.getLayoutParams();
        p.addRule(RelativeLayout.BELOW, R.id.image_gallery);

        viewHolder.mTextDate.setLayoutParams(p);


    }

    /**
     * Parsing for the chat assistant reply
     *
     * @param body
     * @return
     */
    private List<ChatAssistantDetailsDTO> parseMessageObject(String body) {
        JsonElement jsonElement = new JsonParser().parse(body);

        // get the json object response of the string
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        if (jsonObject.isJsonNull()) {
            Log.i(TAG, "Failed!!!");
            return null;
        }

        List<ChatAssistantDetailsDTO> chatAssistantDetailsDTOList = new ArrayList<ChatAssistantDetailsDTO>();

        JsonArray detailsList = jsonObject.getAsJsonArray("details");
        if (detailsList.isJsonNull())
            return null;


        for (int i = 0; i < detailsList.size(); i++) {
            ChatAssistantDetailsDTO chatAssistantDetails = new ChatAssistantDetailsDTO();

            JsonObject chatObject = detailsList.get(i).getAsJsonObject();

            String detailsId = chatObject.get("id").getAsString();
            chatAssistantDetails.setId(Integer.parseInt(detailsId));

            String detailsName = chatObject.get("name").getAsString();
            chatAssistantDetails.setName(detailsName);

            String detailsDescription = chatObject.get("description").getAsString();
            chatAssistantDetails.setDescription(detailsDescription);

            String image = chatObject.get("image").getAsString();
            chatAssistantDetails.setImage(image);

            String url = chatObject.get("url").getAsString();
            chatAssistantDetails.setUrl(url);

            String detailsType = chatObject.get("type").getAsString();
            chatAssistantDetails.setType(detailsType);

            int productId = chatObject.get("productId").getAsInt();
            chatAssistantDetails.setProductId(productId);


            chatAssistantDetailsDTOList.add(chatAssistantDetails);

        }

        return chatAssistantDetailsDTOList;

    }

    /**
     * @param body
     * @return
     */
    private ChatSuggestionDTO parseMessageImage(String body) {
        JsonElement jsonElement = new JsonParser().parse(body);

        // get the json object response of the string
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        if (jsonObject.isJsonNull()) {
            Log.i(TAG, "Failed!!!");
            return null;
        }

        ChatSuggestionDTO chatSuggestionDTO = new ChatSuggestionDTO();

        String id = jsonObject.get("id").getAsString();
        chatSuggestionDTO.setId(Integer.parseInt(id));


        String name = jsonObject.get("name").getAsString();
        chatSuggestionDTO.setName(name);

        String description = jsonObject.get("description").getAsString();
        chatSuggestionDTO.setDescription(description);

        JsonArray detailsList = jsonObject.getAsJsonArray("details");
        ArrayList<ChatSuggestionDetailsDTO> chatSuggestionList = new ArrayList<ChatSuggestionDetailsDTO>();
        for (int i = 0; i < detailsList.size(); i++) {

            ChatSuggestionDetailsDTO chatSuggestion = new ChatSuggestionDetailsDTO();

            JsonObject chatObject = detailsList.get(i).getAsJsonObject();

            String detailsId = chatObject.get("id").getAsString();
            chatSuggestion.setId(Integer.parseInt(detailsId));

            String detailsName = chatObject.get("name").getAsString();
            chatSuggestion.setName(detailsName);

            String detailsDescription = chatObject.get("description").getAsString();
            chatSuggestion.setDescription(detailsDescription);

            String image = chatObject.get("image").getAsString();
            chatSuggestion.setImage(image);

            String imageUrl = chatObject.get("url").getAsString();
            chatSuggestion.setUrl(imageUrl);

            String type = chatObject.get("type").getAsString();
            chatSuggestion.setType(type);

            if (chatObject.get("productId")!= null){
                int productId = chatObject.get("productId").getAsInt();
                chatSuggestion.setProductId(productId);
            }

            if (!chatObject.get("detailsSub").isJsonNull()) {

                JsonArray detailsSubList = chatObject.get("detailsSub").getAsJsonArray();
                ArrayList<ChatAssistantDetailsSubDTO> chatSuggestionSubList = new ArrayList<ChatAssistantDetailsSubDTO>();

                for (int i1 = 0; i1 < detailsSubList.size(); i1++) {

                    ChatAssistantDetailsSubDTO chatAssistantDetailsSubDTO = new ChatAssistantDetailsSubDTO();

                    JsonObject chatSubObject = detailsSubList.get(i1).getAsJsonObject();

                    if (!chatSubObject.get("id").isJsonNull()) {
                        String detailsSubId = chatSubObject.get("id").getAsString();
                        chatAssistantDetailsSubDTO.setId(Integer.parseInt(detailsSubId));
                    }

                    if (!chatSubObject.get("name").isJsonNull()) {
                        String detailsSubName = chatSubObject.get("name").getAsString();
                        chatAssistantDetailsSubDTO.setName(detailsSubName);
                    }

                    if (!chatSubObject.get("description").isJsonNull()) {
                        String detailsSubDescription = chatSubObject.get("description").getAsString();
                        chatAssistantDetailsSubDTO.setDescription(detailsSubDescription);
                    }

                    if (!chatSubObject.get("image").isJsonNull()) {
                        String detailsSubImage = chatSubObject.get("image").getAsString();
                        chatAssistantDetailsSubDTO.setImage(detailsSubImage);
                    }

                    if (!chatSubObject.get("url").isJsonNull()) {
                        String detailsSubUrl = chatSubObject.get("url").getAsString();
                        chatAssistantDetailsSubDTO.setUrl(detailsSubUrl);
                    }

                    if (!chatSubObject.get("type").isJsonNull()) {
                        String detailsSubType = chatSubObject.get("type").getAsString();
                        chatAssistantDetailsSubDTO.setType(detailsSubType);
                    }

                    chatSuggestionSubList.add(chatAssistantDetailsSubDTO);


                }

                chatSuggestion.setSubDetails(chatSuggestionSubList);

            }


            chatSuggestionList.add(chatSuggestion);


        }

        chatSuggestionDTO.setDetails(chatSuggestionList);

        return chatSuggestionDTO;

    }

    /**
     * Inspect for a valid json returned
     *
     * @param json
     * @return
     */
    public boolean isValidJson(String json) {
        try {
            // parse json
            JsonElement jsonElement = new JsonParser().parse(json);

            // get the json object response of the string
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (jsonObject == null)
                return true;

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static class ViewHolder {
        public CustomFontTextView mTextTitle;
        public RoundedImageView mRoundedImage;
        public CustomFontTextView mTextDate;
        public RelativeLayout mImageGallery;
    }

    /**
     * The assistant result to filter
     *
     * @param viewHolder
     * @param body
     */
    private void createSuggestionObjects(ViewHolder viewHolder, String body, int status) {

        Log.i(TAG, "createSuggestionObjects body: " + body);

        if (!isValidJson(body)) {
            Log.i(TAG, "Invalid json format!!!");
            return;
        }

        // parse the string body
        ChatSuggestionDTO chatSuggestionDTO = parseMessageImage(body);

        if (chatSuggestionDTO == null) {
            Log.i(TAG, "null object result");
            return;
        }


        Log.i(TAG, "Continue....createSuggestionObjects");

        // Create a scrollview and add the linear layout inside

        // create the property of the linear layout
        HorizontalScrollView scrollView = new HorizontalScrollView(getContext());
        //scrollView.setBackgroundColor(getContext().getColor(android.R.color.transparent));
//        scrollView.setBackgroundColor(android.R.color.transparent);
        scrollView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // create the property of the linear layout
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        LinearLayout linearMain = new LinearLayout(getContext());
        linearMain.setLayoutParams(params);
        linearMain.setOrientation(LinearLayout.VERTICAL);
        linearMain.setPadding(0,20,0,0);

        scrollView.addView(linearMain);

        // Layout for the object gallery
        LinearLayout bgLayout = new LinearLayout(getContext());
        bgLayout.setLayoutParams(params);
        bgLayout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams paramsObject = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, mActivity.getResources().getDisplayMetrics());


        LinearLayout.LayoutParams paramsObjectSub = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int sizeSub = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, mActivity.getResources().getDisplayMetrics());

        ArrayList<ChatSuggestionDetailsDTO> chatDetailsList = chatSuggestionDTO.getDetails();

        for (ChatSuggestionDetailsDTO chatDetails : chatDetailsList) {

            // Layout for the object gallery
            LinearLayout bgDetailsLayout = new LinearLayout(getContext());
            bgDetailsLayout.setLayoutParams(params);
            bgDetailsLayout.setOrientation(LinearLayout.VERTICAL);

            int id = chatDetails.getId();
            String type = chatDetails.getType();
            String name = chatDetails.getName();
            String description = chatDetails.getDescription();
            String image = chatDetails.getImage();
            String url = chatDetails.getUrl();
            int productId = chatDetails.getProductId();

            Log.i(TAG, "createSuggestionObjects id: " + id + ", type: " + type + ", name: " + name + ", description: " + description + ", image: " + image + ", url: " + url + ", productId: " + productId);

            bgDetailsLayout.addView(getObjectType(id, type, name, description, image, url, paramsObject, size, status, productId));

            // Layout for the object gallery
            LinearLayout bgDetailsSubLayout = new LinearLayout(getContext());
            bgDetailsSubLayout.setLayoutParams(params);
            bgDetailsSubLayout.setOrientation(LinearLayout.HORIZONTAL);

            // get the sub details and show it
            ArrayList<ChatAssistantDetailsSubDTO> subDetailsList = chatDetails.getSubDetails();
            for (ChatAssistantDetailsSubDTO subDetails : subDetailsList) {
                int subid = subDetails.getId();
                String subtype = subDetails.getType();
                String subname = subDetails.getName();
                String subdescription = subDetails.getDescription();
                String subimage = subDetails.getImage();
                String suburl = subDetails.getUrl();

                bgDetailsSubLayout.addView(getObjectType(subid, subtype, subname, subdescription, subimage, suburl, paramsObjectSub, sizeSub, status, 0));

            }

            bgDetailsLayout.addView(bgDetailsSubLayout);

            bgLayout.addView(bgDetailsLayout);


        }

        linearMain.addView(bgLayout);

        // Layout buttons for suggestion
        LinearLayout bgButtonLayout = new LinearLayout(getContext());
        bgButtonLayout.setLayoutParams(params);
        bgButtonLayout.setOrientation(LinearLayout.HORIZONTAL);
        bgButtonLayout.setPadding(0,20,0,0);

        LinearLayout.LayoutParams paramsButton = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);


        for (ChatSuggestionDetailsDTO chatDetails : chatDetailsList) {

            int id = chatDetails.getId();
            String type = chatDetails.getType();
            String name = chatDetails.getName();
            String description = chatDetails.getDescription();
            String image = chatDetails.getImage();
            String url = chatDetails.getUrl();
            int productId = chatDetails.getProductId();

            Log.i(TAG, "createSuggestionObjects for button id: " + id + ", type: " + type + ", name: " + name + ", description: " + description + ", image: " + image + ", url: " + url + ", productId: " + productId);

            // Do not add button if it is a text message
            //if (!type.toLowerCase().equals(ObjectsType.TEXT.toString().toLowerCase()) || !type.toLowerCase().equals(ObjectsType.TEXT_IMAGE.toString().toLowerCase())){
            //if (!type.toLowerCase().equals(ObjectsType.TEXT_IMAGE.toString().toLowerCase())){
//            if (!type.toLowerCase().equals(ObjectsType.TEXT.toString().toLowerCase())) {
            if (type.toLowerCase().equals(ObjectsType.BUTTON.toString().toLowerCase()) || type.toLowerCase().equals(ObjectsType.TEXT_IMAGE.toString().toLowerCase())
                    || type.toLowerCase().equals(ObjectsType.IMAGE_BUTTON.toString().toLowerCase())) {
                bgButtonLayout.addView(getObjectType(id, ObjectsType.BUTTON.toString(), name, description, image, url, paramsButton, size, status, productId));
            }


        }

        linearMain.addView(bgButtonLayout);

        viewHolder.mImageGallery.removeAllViews();
        viewHolder.mImageGallery.addView(scrollView);
        // till here...

        // set the gallery layout below the text title
        RelativeLayout.LayoutParams galleryLayout = (RelativeLayout.LayoutParams) viewHolder.mImageGallery.getLayoutParams();
        galleryLayout.addRule(RelativeLayout.BELOW, R.id.text_title);

        viewHolder.mImageGallery.setLayoutParams(galleryLayout);

        // set the text date bellow the gallery
        RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) viewHolder.mTextDate.getLayoutParams();
        p.addRule(RelativeLayout.BELOW, R.id.image_gallery);

        viewHolder.mTextDate.setLayoutParams(p);

    }

    /**
     * The method to upload merchant selected pic
     *
     * @param viewHolder
     */
    private void createUploadImage(ViewHolder viewHolder, String nId, String imageLink, int status) {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        LinearLayout linearMain = new LinearLayout(getContext());
        linearMain.setLayoutParams(params);
        linearMain.setOrientation(LinearLayout.VERTICAL);


        // Layout for the object gallery
        LinearLayout bgLayout = new LinearLayout(getContext());
        bgLayout.setLayoutParams(params);
        bgLayout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams paramsObject = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        //int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, mActivity.getResources().getDisplayMetrics());
        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, mActivity.getResources().getDisplayMetrics());


        int id = Integer.parseInt(nId);
        String type = ObjectsType.IMAGE.toString();
        String name = "";
        String description = "";
        String image = imageLink;
        String url = imageLink;

        Log.i(TAG, "createSuggestionObjects id: " + id + ", type: " + type + ", name: " + name + ", description: " + description + ", image: " + image + ", url: " + url);

        bgLayout.addView(getObjectType(id, type, name, description, image, url, paramsObject, size, status, 0));

        linearMain.addView(bgLayout);

        viewHolder.mImageGallery.removeAllViews();
        viewHolder.mImageGallery.addView(linearMain);


        RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) viewHolder.mTextDate.getLayoutParams();
        p.addRule(RelativeLayout.BELOW, R.id.image_gallery);

        viewHolder.mTextDate.setLayoutParams(p);


    }

}

