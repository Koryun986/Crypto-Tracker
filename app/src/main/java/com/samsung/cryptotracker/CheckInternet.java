package com.samsung.cryptotracker;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckInternet {

    private static final String statusConnected = "connected";
    private static final String statusDisonnected = "disconnected";

    public static String getNetworkInfo(Context context){
        String status = null;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo!=null){
            status = statusConnected;
            return status;
        }
        else {
            status = statusDisonnected;
            return status;
        }
    }
}