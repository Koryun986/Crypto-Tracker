package com.samsung.cryptotracker.MVVM;

import static com.samsung.cryptotracker.Constants.getArrayListFromJSONArray;

import android.app.Application;
import android.os.AsyncTask;

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
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.samsung.cryptotracker.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChartViewModel extends AndroidViewModel {
    private MutableLiveData<List<Entry>> mutableLiveData;
    private MutableLiveData<Boolean> isLoaded;

    public ChartViewModel(@NonNull Application application) {
        super(application);
        isLoaded = new MutableLiveData<>();
        mutableLiveData = new MutableLiveData<>();
    }

    public LiveData<List<Entry>> getData(){
        return  mutableLiveData;
    }
    public LiveData<Boolean> isChartLoaded(){
        return  isLoaded;
    }

    public void loadChartData(String id, int days){
        isLoaded.setValue(true);
        AsyncTask.execute(() -> {
            String url = Constants.CURRENCY_CHART(id, days);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                isLoaded.setValue(false);
                                JSONObject jsonObject = new JSONObject(response);
                                ArrayList<Entry> arr = getArr(jsonObject.getJSONArray("prices"));
                                mutableLiveData.postValue(arr);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    isLoaded.setValue(false);
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getApplication());
            requestQueue.add(stringRequest);
        });
    }

    private static ArrayList<Entry> getArr(JSONArray jsonArray){
        ArrayList<Entry> aList = new ArrayList<>();
        try {
            if(jsonArray!= null){
                for(int i = 0; i< jsonArray.length();i++){

                    float date = Float.valueOf(jsonArray.getJSONArray(i).get(0).toString());
                    float value = Float.valueOf(jsonArray.getJSONArray(i).get(1).toString());

                    aList.add(new Entry(date,value));
                }

            }

        }catch (JSONException js){
            js.printStackTrace();
        }
        return aList;
    }
}
