package com.samsung.cryptotracker.Notification;

import android.app.DownloadManager;
import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NotificationSend {
    private static final String BASE_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String SERVER_KEY = "key=AAAA_NUbbwI:APA91bFwgUmaCSmGzpIJ5pjeHXGjFxr4PiPK5t6edCDGnChYrWOSNvI96T1J_2_CCJ4f1Ugch1422-oyV1WoYssSR8-pODP2qxPKPerSm6EoO-aZzcQ8jzKW0BLRoF_pqJP9Sb5lAbpn";
    private static final String NOTIFICATION_TOKEN_TO = "to";
    private static final String NOTIFICATION_TITLE = "title";
    private static final String NOTIFICATION_BODY = "body";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_TYPE_APPLICATION = "application/json";
    private static final String AUTH = "Authorization";


    public static void pushNotification(Context context, String token, String title, String message){
        RequestQueue queue = Volley.newRequestQueue(context);

        try{
            JSONObject json = new JSONObject();
            json.put(NOTIFICATION_TOKEN_TO, token);
            JSONObject notification = new JSONObject();
            notification.put(NOTIFICATION_TITLE, title);
            notification.put(NOTIFICATION_BODY, message);
            json.put("notification", notification);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BASE_URL, json, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i("korr", response.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("korr", "error");
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put(CONTENT_TYPE, CONTENT_TYPE_APPLICATION);
                    params.put(AUTH, SERVER_KEY);
                    return params;
                }
            };
            queue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
