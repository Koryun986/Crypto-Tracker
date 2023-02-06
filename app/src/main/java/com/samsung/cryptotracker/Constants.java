package com.samsung.cryptotracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Constants {
    public static final String API_URL (String cur) {
        return "https://api.coingecko.com/api/v3/coins/markets?vs_currency="+cur+"&order=market_cap_desc&0";
    }
    public static final String CURRENCY_URL (String id) {
        return "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&ids="+id+"&order=market_cap_desc&sparkline=false";
    }
    public static final String SEARCH_CURRENCY = "https://api.coingecko.com/api/v3/search?query=";
    public static final String CURRENCY_CHART (String name, int days) {
        return "https://api.coingecko.com/api/v3/coins/"+name+"/market_chart?vs_currency=usd&days=" + days;
    }
    public static final String CURRENCY_ID = "id";
    public static final String CURRENCY_NAME = "name";
    public static final String CURRENCY_IMAGE = "image";
    public static final String CURRENCY_PRICE = "current_price";
    public static final String CURRENCY_MARKET_CUP_CHANGE = "market_cap_change_percentage_24h";
    public static ArrayList<JSONObject> getArrayListFromJSONArray(JSONArray jsonArray){
        ArrayList< JSONObject> aList = new ArrayList< JSONObject>();
        try {
            if(jsonArray!= null){
                for(int i = 0; i< jsonArray.length();i++){
                    aList.add(jsonArray.getJSONObject(i));
                }

            }

        }catch (JSONException js){
            js.printStackTrace();
        }
        return aList;
    }
}
