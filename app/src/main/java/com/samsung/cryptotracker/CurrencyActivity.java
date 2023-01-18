package com.samsung.cryptotracker;

import static com.samsung.cryptotracker.Constants.getArrayListFromJSONArray;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CurrencyActivity extends AppCompatActivity {
    ImageView icon;
    TextView name;
    TextView price;
    String id;
    ArrayList<JSONObject> data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency);
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            id = extra.getString("id");
        }
        icon = findViewById(R.id.coin_icon);
        name = findViewById(R.id.coin_name);
        price = findViewById(R.id.coin_price);
        Loader loader = new Loader();
        loader.start();
    }

    class Loader extends Thread {
        @Override
        public void run() {
            super.run();
            String url = Constants.CURRENCY_URL(id);
            loadCurrencyData(url);
        }
    }



    private void loadCurrencyData (String url) {
        ProgressBar progressBar = findViewById(R.id.currency_activity_progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            data = getArrayListFromJSONArray(jsonArray);
                            Picasso.get().load(data.get(0).getString(Constants.CURRENCY_IMAGE)).into(icon);
                            name.setText(data.get(0).getString(Constants.CURRENCY_NAME));
                            price.setText(data.get(0).getString(Constants.CURRENCY_PRICE) + "$");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}