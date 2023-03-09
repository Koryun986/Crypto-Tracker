package com.samsung.cryptotracker;

import static com.samsung.cryptotracker.Constants.getArrayListFromJSONArray;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.samsung.cryptotracker.Adapter.CurrencyMarketPriceAdapter;
import com.samsung.cryptotracker.Adapter.ListViewAdapter;
import com.samsung.cryptotracker.Chart.ChartMarker;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class CurrencyActivity extends AppCompatActivity {
    ImageView icon;
    TextView name;
    TextView price;
    String id;
    ArrayList<JSONObject> data;
    LineChart lineChart;
    TextView button1Day;
    TextView button7Day;
    TextView button1Month;
    TextView button3Month;
    TextView backButton;
    BottomNavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency);
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            id = extra.getString("id");
        }
        Fragment chartFragment = new CurrencyChartFragment();
        Fragment marketsPriceFragment = new MarketsPriceFragment();

        Bundle args = new Bundle();
        args.putString("id", id);
        chartFragment.setArguments(args);
        marketsPriceFragment.setArguments(args);


        navigationView = findViewById(R.id.bottom_navigation_bar);
        icon = findViewById(R.id.coin_icon);
        name = findViewById(R.id.coin_name);
        backButton = findViewById(R.id.back_button);

        Loader loader = new Loader();
        loader.start();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment, chartFragment)
                .commit();
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                switch (item.getItemId()) {
                    case R.id.chart:
                        ft.replace(R.id.fragment, chartFragment);
                        ft.commit();
                        break;
                    case R.id.market_price:
                        ft.replace(R.id.fragment, marketsPriceFragment);
                        ft.commit();
                        break;

                }
                return false;
            }
        });
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
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                data = getArrayListFromJSONArray(jsonArray);
                                Picasso.get().load(data.get(0).getString(Constants.CURRENCY_IMAGE)).into(icon);
                                name.setText(data.get(0).getString(Constants.CURRENCY_NAME));
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
