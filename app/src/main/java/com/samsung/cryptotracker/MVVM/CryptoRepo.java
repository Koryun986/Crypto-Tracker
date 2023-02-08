package com.samsung.cryptotracker.MVVM;

import static com.samsung.cryptotracker.Constants.getArrayListFromJSONArray;

import android.content.Context;
import android.view.View;
import android.widget.ListAdapter;

import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.samsung.cryptotracker.Constants;
import com.samsung.cryptotracker.ListViewAdapter;
import com.samsung.cryptotracker.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CryptoRepo {

    public MutableLiveData<List<CryptoModel>> requestCrypto(Context context) {
        final MutableLiveData<List<CryptoModel>> mutableLiveData = new MutableLiveData<>();
        final String url = Constants.API_URL("usd");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            ArrayList<JSONObject> item = getArrayListFromJSONArray(jsonArray);

                            List<CryptoModel> aList = new ArrayList<>();
                            if (jsonArray != null) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    CryptoModel cryptoModel = new CryptoModel();
                                    cryptoModel.setCryptoInfo(jsonArray.getJSONObject(i));
                                    aList.add(cryptoModel);
                                }
                            }
                            mutableLiveData.setValue(aList);
                        } catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        return mutableLiveData;
    }
}