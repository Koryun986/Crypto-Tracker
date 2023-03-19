package com.samsung.cryptotracker;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class InternetReceiver extends BroadcastReceiver {
    private static final String statusConnected = "connected";
    private static final String statusDisonnected = "disconnected";
    private static final String ALERT_MESSAGE = "No Connection";
    private static final String ALERT_BTN = "OK";

    @Override
    public void onReceive(Context context, Intent intent) {
        String status = CheckInternet.getNetworkInfo(context);
        if (status.equals(statusConnected)){
        }
        else if (status.equals(statusDisonnected)) {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(context).
                            setMessage(ALERT_MESSAGE).
                            setPositiveButton(ALERT_BTN, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
            builder.create().show();

        }
    }
}