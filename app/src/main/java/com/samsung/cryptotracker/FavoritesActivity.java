package com.samsung.cryptotracker;

import static com.samsung.cryptotracker.Constants.getArrayListFromJSONArray;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.samsung.cryptotracker.Adapter.ListViewAdapter;
import com.samsung.cryptotracker.DB.DBManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {
    private DBManager dbManager;
    ListView listView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        listView = findViewById(R.id.list_view);
        toolbar = findViewById(R.id.toolBar);

        toolbar.getChildAt(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Loader loader = new Loader();
        loader.start();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView id = view.findViewById(R.id.coin_id);
                Intent intent = new Intent(getApplicationContext(), CurrencyActivity.class);
                intent.putExtra("id", id.getText());
                startActivity(intent);
            }
        });
    }

    class Loader extends Thread{
        @Override
        public void run() {
            super.run();
            dbManager = new DBManager(getApplicationContext());
            dbManager.openDB();
            List<String> list = dbManager.getFromDb();
            String coins = "";
            for (int i = 0; i < list.size(); i++) {
                coins += "," + list.get(i);
            }
            loadJsonFromUrl(Constants.CURRENCY_URL(coins));
        }
    }

    private void loadJsonFromUrl(String url) {
        final ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            ArrayList<JSONObject> item = getArrayListFromJSONArray(jsonArray);
                            ListAdapter listAdapter = new ListViewAdapter(getApplicationContext(), R.layout.row, R.id.container, item);
                            listView.setAdapter(listAdapter);

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
        dbManager.closeDb();
    }
}