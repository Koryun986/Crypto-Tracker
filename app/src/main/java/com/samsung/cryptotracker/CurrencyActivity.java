package com.samsung.cryptotracker;

import static com.samsung.cryptotracker.Constants.getArrayListFromJSONArray;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.samsung.cryptotracker.MVVM.CurrencyInfoViewModel;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class CurrencyActivity extends AppCompatActivity {

    private static final String argId = "id";

    CurrencyInfoViewModel currencyInfoViewModel;

    ImageView icon;
    TextView name;
    String id;
    ArrayList<JSONObject> data;
    TextView backButton;
    BottomNavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency);
        currencyInfoViewModel = new CurrencyInfoViewModel(getApplication());

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            id = extra.getString(argId);
        }
        Fragment chartFragment = new CurrencyChartFragment();
        Fragment marketsPriceFragment = new MarketsPriceFragment();
        Fragment currencyNotifications = new NotificationsFragment();

        Bundle args = new Bundle();
        args.putString("id", id);
        chartFragment.setArguments(args);
        marketsPriceFragment.setArguments(args);
        currencyNotifications.setArguments(args);

        currencyInfoViewModel.getData().observe(this, data -> {
            if(data != null) {
                try {
                    JSONObject obj = data.get(0);
                    Picasso.get().load(obj.getString(Constants.CURRENCY_IMAGE)).into(icon);
                    name.setText(obj.getString(Constants.CURRENCY_NAME));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {

            }
        });
        currencyInfoViewModel.loadCurrencyData(id);
        navigationView = findViewById(R.id.bottom_navigation_bar);
        icon = findViewById(R.id.coin_icon);
        name = findViewById(R.id.coin_name);
        backButton = findViewById(R.id.back_button);


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
                    case R.id.currency_notifications:
                        ft.replace(R.id.fragment, currencyNotifications);
                        ft.commit();
                        break;
                }
                return false;
            }
        });
    }

}
