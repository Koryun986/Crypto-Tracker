package com.samsung.cryptotracker;

 import static com.samsung.cryptotracker.Constants.getArrayListFromJSONArray;

import android.content.Intent;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
 import android.view.WindowManager;
 import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import android.widget.Spinner;
import android.widget.TextView;

 import androidx.annotation.NonNull;
 import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
 import androidx.fragment.app.FragmentTransaction;


 import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
 import com.google.android.material.bottomnavigation.BottomNavigationView;
 import com.google.android.material.navigation.NavigationBarView;
 import com.samsung.cryptotracker.Adapter.ListViewAdapter;
import com.samsung.cryptotracker.DB.DBManager;
import com.samsung.cryptotracker.Exchange.ExchangedCurrency;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    Toolbar toolbar;
    BottomNavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.list_view);
        toolbar = findViewById(R.id.toolBar);
        navigationView = findViewById(R.id.bottom_navigation_bar);
        MarketFragment marketFragment = new MarketFragment();
        FavoritesFragment favoritesFragment = new FavoritesFragment();

//        Fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment, marketFragment)
                .commit();


//        Navigation View
        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                switch (item.getItemId()) {
                    case R.id.market:
                        ft.replace(R.id.fragment, marketFragment);
                        ft.commit();
                        break;
                    case R.id.favorites:
                        ft.replace(R.id.fragment, favoritesFragment);
                        ft.commit();
                        break;
                    case R.id.search:
                        break;

                }
                return false;
            }
        });


    }



}