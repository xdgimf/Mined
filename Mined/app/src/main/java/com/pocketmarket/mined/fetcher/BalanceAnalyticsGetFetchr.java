package com.pocketmarket.mined.fetcher;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pocketmarket.mined.dto.BalanceAnalyticsDTO;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */

public class BalanceAnalyticsGetFetchr extends MainFetchr {

    private static final String TAG = "BalanceAnalyticsGetFetchr";

    public ArrayList<BalanceAnalyticsDTO> fetchItems(String sUrl){

        String response = null;

        // request of http post
        try {
            response = getUrl(sUrl, METHOD_TYPE_GET, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response == null)
            return null;

        // parse the string result from the parser
        JsonElement jsonElement = new JsonParser().parse(response);


        // get the json object response of the string
        JsonArray productsArray = jsonElement.getAsJsonArray();

        ArrayList<BalanceAnalyticsDTO> balanceAnalyticsList = new ArrayList<BalanceAnalyticsDTO>();

        for (int i = 0; i < productsArray.size(); i++) {
            JsonObject jsonObject = productsArray.get(i).getAsJsonObject();
            BalanceAnalyticsDTO balanceAnalytics = new BalanceAnalyticsDTO();

            String id = jsonObject.get("id").getAsString();
            balanceAnalytics.setId(Integer.parseInt(id));

            BigDecimal credit = jsonObject.get("credit").getAsBigDecimal();
            balanceAnalytics.setCredit(credit);

            BigDecimal debit = jsonObject.get("debit").getAsBigDecimal();
            balanceAnalytics.setDebit(debit);

            balanceAnalyticsList.add(balanceAnalytics);

        }

        return balanceAnalyticsList;

    }

}
