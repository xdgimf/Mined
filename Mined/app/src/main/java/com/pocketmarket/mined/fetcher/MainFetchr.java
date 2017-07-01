package com.pocketmarket.mined.fetcher;

import android.util.Log;

import com.pocketmarket.mined.utility.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public class MainFetchr {
    private static final String TAG = MainFetchr.class.getSimpleName();
    public static final String METHOD_TYPE_GET = "GET";
    public static final String METHOD_TYPE_POST = "POST";
    public static final String METHOD_TYPE_PUT = "PUT";
    public static final String METHOD_TYPE_DELETE = "DELETE";

    private static final String CONTENT_TYPE = "content-type";
    private static final String CONTENT_URL_ENCODED = "application/x-www-form-urlencoded";
    private static final String CONTENT_VALUE = "application/vnd.api+json";
    private static final String CONTENT_IMAGE = "multipart/form-data;boundary=";
    private static final String CONTENT_DISPOSITION = "Content-Disposition:";
    private static final String CONTENT_DISPOSITION_VALUE = "form-data";
    private static final String LINE_END = "\r\n";
    private static final String _HYPENS = "--";
    private static final String _BOUNDARY = "*****";
    private static final String AUTHORIZATION = "Authorization";

    // variables used for image upload
    private int bytesRead, bytesAvailable, bufferSize;

    /**
     * Network connection timeout, in milliseconds.
     */
    //private static final int NET_CONNECT_TIMEOUT_MILLIS = 5000;
//    private static final int NET_CONNECT_TIMEOUT_MILLIS = 50000;
    private static final int NET_CONNECT_TIMEOUT_MILLIS = 500000;


    /**
     * Url for httprequest no accesstoken
     *
     * @param urlSpec
     * @param requestMethodType
     * @param params
     * @return
     * @throws IOException
     */
    public final String getUrl(String urlSpec, String requestMethodType, HashMap<String, String> params) throws IOException {

        URL url = new URL(urlSpec);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        String response = "";

        try {

            // list of connection properties
            conn.setRequestProperty(CONTENT_TYPE, CONTENT_URL_ENCODED);
            conn.setReadTimeout(NET_CONNECT_TIMEOUT_MILLIS);
            conn.setConnectTimeout(NET_CONNECT_TIMEOUT_MILLIS);
            conn.setRequestMethod(requestMethodType);
            conn.setDoInput(true);
            conn.setDoOutput(params != null);

            if (params != null) {
                // add the out paramater for the post
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));

                writer.write(Utils.getPostDataString(params));

                writer.flush();
                writer.close();
                os.close();

            } else {
                conn.connect();
            }


            // read the response
            Log.i(TAG, "Response Code: " + conn.getResponseCode() + ", HTTP_OK: " + HttpURLConnection.HTTP_OK);
            int responseCode = conn.getResponseCode();


            String line;
            switch (responseCode) {
                case HttpURLConnection.HTTP_OK:
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                    break;

                default:
                    response = Integer.toString(responseCode);
                    break;
            }

            Log.i(TAG, "Response value: " + response);

            return response;
        } catch (SocketTimeoutException e) {
            Log.i(TAG, "Connection failed!!!");
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Failed to fetch items", e);
            return null;
        } finally {
            conn.disconnect();
        }
    }

    /**
     * Url for httprequest with accessToken
     *
     * @param urlSpec
     * @param requestMethodType
     * @param params
     * @param accessToken
     * @return
     * @throws IOException
     */
    public final String getUrl(String urlSpec, String requestMethodType,
                               HashMap<String, String> params, String accessToken) throws IOException {

        URL url = new URL(urlSpec);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        String response = "";

        String bearer = "Bearer " + accessToken;

        try {

            // list of connection properties
            conn.setReadTimeout(NET_CONNECT_TIMEOUT_MILLIS);
            conn.setConnectTimeout(NET_CONNECT_TIMEOUT_MILLIS);
            conn.setRequestMethod(requestMethodType);
            conn.setDoInput(true);
            conn.setDoOutput(params != null);
            conn.setRequestProperty(AUTHORIZATION, bearer);

            if (params != null) {

                // add the out paramater for the post
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));

                //writer.write(params.toString());
                writer.write(Utils.getPostDataString(params));

                writer.flush();
                writer.close();
                os.close();

            } else {
                conn.connect();
            }

            // read the response
            Log.i(TAG, "Response Code: " + conn.getResponseCode() + ", HTTP_CREATED: " + HttpURLConnection.HTTP_CREATED);
            int responseCode = conn.getResponseCode();


            String line;
            switch (responseCode) {
                case HttpURLConnection.HTTP_CREATED:
                case HttpURLConnection.HTTP_OK:
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                    break;

                default:
                    response = Integer.toString(responseCode);
                    break;
            }

            Log.i(TAG, "Response value: " + response);

            return response;
        } catch (SocketTimeoutException e) {
            Log.i(TAG, "Connection failed!!!");
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Failed to fetch items", e);
            return null;
        } finally {
            conn.disconnect();
        }
    }

    /**
     * Url for httprequest with accessToken
     *
     * @param urlSpec
     * @param requestMethodType
     * @param params
     * @return
     * @throws IOException
     */
    public final String getUrl(String urlSpec, String requestMethodType,
                               Map<String, ?> params) throws IOException {

        URL url = new URL(urlSpec);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        String response = "";

        try {

            // list of connection properties
            conn.setReadTimeout(NET_CONNECT_TIMEOUT_MILLIS);
            conn.setConnectTimeout(NET_CONNECT_TIMEOUT_MILLIS);
            conn.setRequestMethod(requestMethodType);
            conn.setDoInput(true);
            conn.setDoOutput(params != null);

            if (params != null) {

                // add the out paramater for the post
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));

                writer.write(params.toString());

                writer.flush();
                writer.close();
                os.close();

            } else {
                conn.connect();
            }

            // read the response
            Log.i(TAG, "Response Code: " + conn.getResponseCode() + ", HTTP_CREATED: " + HttpURLConnection.HTTP_CREATED);
            int responseCode = conn.getResponseCode();


            String line;
            switch (responseCode) {
                case HttpURLConnection.HTTP_CREATED:
                case HttpURLConnection.HTTP_OK:
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                    break;

                default:
                    response = Integer.toString(responseCode);
                    break;
            }

            Log.i(TAG, "Response value: " + response);

            return response;
        } catch (SocketTimeoutException e) {
            Log.i(TAG, "Connection failed!!!");
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Failed to fetch items", e);
            return null;
        } finally {
            conn.disconnect();
        }
    }


    /**
     * Url for image upload
     *
     * @param urlSpec
     * @param requestMethodType
     * @param file
     * @param accessToken
     * @param image
     * @return
     * @throws IOException
     */
    public final String getUrl(String urlSpec, String requestMethodType,
                               File file,
                               String accessToken,
                               String image) throws IOException {

        DataOutputStream dos = null;
        String fileName = file.getAbsolutePath();
        int maxBufferSize = 1 * 1024;
        byte[] buffer;

        FileInputStream fileInputStream = new FileInputStream(fileName);

        URL url = new URL(urlSpec);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        String bearer = "Bearer " + accessToken;

        String response = "";

        try {
            // list of connection properties
            conn.setReadTimeout(NET_CONNECT_TIMEOUT_MILLIS);
            conn.setConnectTimeout(NET_CONNECT_TIMEOUT_MILLIS);
            conn.setRequestMethod(requestMethodType);
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setChunkedStreamingMode(1024);
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + _BOUNDARY);
            conn.setRequestProperty(AUTHORIZATION, bearer);

            dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(_HYPENS + _BOUNDARY + LINE_END);

            dos.writeBytes("Content-Disposition: form-data; name=\"" + image + "\"; filename=\"" + file.getName() + "\"\r\nContent-Type: false\r\n");

            dos.writeBytes(LINE_END);

            // create a buffer of  maximum size
            bytesAvailable = fileInputStream.available();

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            Log.i(TAG, "original bytesRead: " + bytesRead + ", bytesAvailable: " + bytesAvailable);

            while (bytesRead > 0) {
                try {
                    dos.write(buffer, 0, bufferSize);
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                    Log.i(TAG, "Upload out of memory......");

                }

                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                Log.i(TAG, "bytesRead: " + bytesRead + ", bytesAvailable: " + bytesAvailable + ", bufferSize: " + bufferSize);

            }

            dos.writeBytes(LINE_END);

            dos.writeBytes(_HYPENS + _BOUNDARY + _HYPENS);

            // Responses from the server (code and message)
            int responseCode = conn.getResponseCode();
            String responseMessage = conn.getResponseMessage();

            Log.i(TAG, "HTTP Response is : "
                    + responseMessage + ": " + responseCode);

            String line;
            switch (responseCode) {
                case HttpURLConnection.HTTP_CREATED:
                case HttpURLConnection.HTTP_OK:
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                    break;

                default:
                    response = Integer.toString(responseCode);
                    break;
            }

            Log.i(TAG, "Response value: " + response);

            //close the streams //
            fileInputStream.close();
            dos.flush();
            dos.close();

            return response;

        } catch (SocketTimeoutException e) {
            Log.i(TAG, "Connection failed!!!");
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Failed to fetch items", e);
            return null;
        } finally {
            conn.disconnect();
        }
    }

}

