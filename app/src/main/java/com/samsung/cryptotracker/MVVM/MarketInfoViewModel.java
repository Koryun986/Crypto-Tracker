package com.samsung.cryptotracker.MVVM;

import static com.samsung.cryptotracker.Constants.getArrayListFromJSONArray;

import android.app.Application;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ListAdapter;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.samsung.cryptotracker.Adapter.ListViewAdapter;
import com.samsung.cryptotracker.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MarketInfoViewModel extends AndroidViewModel {
    private MutableLiveData<List<JSONObject>> mutableLiveData;
    private MutableLiveData<Boolean> isLoaded;

    public MarketInfoViewModel(@NonNull Application application) {
        super(application);
        isLoaded = new MutableLiveData<>();
        mutableLiveData = new MutableLiveData<>();
    }

    public LiveData<List<JSONObject>> getData(){
        return  mutableLiveData;
    }
    public LiveData<Boolean> isMarketLoaded() { return isLoaded; }

    public void loadData(){
        isLoaded.setValue(true);
        AsyncTask.execute(() -> {
                String url = Constants.API_URL();
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    isLoaded.setValue(false);
                                    JSONArray jsonArray = new JSONArray(response);
                                    ArrayList<JSONObject> item = getArrayListFromJSONArray(jsonArray);
                                    mutableLiveData.postValue(item);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        mutableLiveData.setValue(null);
                        isLoaded.setValue(false);
                    }
                });
                RequestQueue requestQueue = Volley.newRequestQueue(getApplication());
                requestQueue.add(stringRequest);
        });
    }
}
