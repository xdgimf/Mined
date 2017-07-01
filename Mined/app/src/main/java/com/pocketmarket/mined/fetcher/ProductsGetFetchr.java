package com.pocketmarket.mined.fetcher;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pocketmarket.mined.dto.ProductsDTO;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public class ProductsGetFetchr extends MainFetchr {

    private static final String TAG = "ProductsGetFetchr";

    public ArrayList<ProductsDTO> fetchItems(String sUrl) throws IOException {

        String response = null;

        // request of http post
        response = getUrl(sUrl, METHOD_TYPE_GET, null);

        if (response == null)
            return null;


        // parse the string result from the parser
        JsonElement jsonElement = new JsonParser().parse(response);


        // get the json object response of the string
        JsonArray productsArray = jsonElement.getAsJsonArray();

        ArrayList<ProductsDTO> productsList = new ArrayList<ProductsDTO>();

        for (int i = 0; i < productsArray.size(); i++){
            JsonObject jsonObject = productsArray.get(i).getAsJsonObject();
            ProductsDTO productsItems = new ProductsDTO();

            String id = jsonObject.get("id").getAsString();
            productsItems.setId(Integer.parseInt(id));

            String name = jsonObject.get("name").getAsString();
            productsItems.setName(name);

            String description = jsonObject.get("description").getAsString();
            productsItems.setDescription(description);

            String photo = jsonObject.get("photo").getAsString();
            productsItems.setPhoto(photo);

            String serverdate = jsonObject.get("serverdate").getAsString();
            Log.d(TAG, "serverdate: " + serverdate);

            productsItems.setServerdate(serverdate);

            String amount = jsonObject.get("amount").getAsString();
            productsItems.setAmount(amount);

            String type = jsonObject.get("type").getAsString();
            productsItems.setType(Integer.parseInt(type));

            productsList.add(productsItems);

        }



        return productsList;

    }
}

