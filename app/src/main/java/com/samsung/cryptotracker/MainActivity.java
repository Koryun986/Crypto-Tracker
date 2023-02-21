package com.samsung.cryptotracker;

 import static com.samsung.cryptotracker.Constants.getArrayListFromJSONArray;

import android.content.Intent;
import android.os.Bundle;

 import android.os.Handler;
 import android.util.Log;
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
 import androidx.fragment.app.Fragment;
 import androidx.fragment.app.FragmentTransaction;


 import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
 import com.google.android.material.bottomnavigation.BottomNavigationView;
 import com.google.android.material.navigation.NavigationBarView;
 import com.google.firebase.auth.FirebaseAuth;
 import com.google.firebase.auth.FirebaseUser;
 import com.samsung.cryptotracker.Adapter.ListViewAdapter;
import com.samsung.cryptotracker.DB.DBManager;
import com.samsung.cryptotracker.Exchange.ExchangedCurrency;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser user;
    ListView listView;
    Toolbar toolbar;
    BottomNavigationView navigationView;
    TextView logOut;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        listView = findViewById(R.id.list_view);
        toolbar = findViewById(R.id.toolBar);
        navigationView = findViewById(R.id.bottom_navigation_bar);
        logOut = findViewById(R.id.log_out);

        Fragment marketFragment = new MarketFragment();
        Fragment favoritesFragment = new FavoritesFragment();
        Fragment searchFragment = new SearchFragment();

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });


//        Fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment, marketFragment)
                .commit();


//        Navigation View
        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
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
                        ft.replace(R.id.fragment, searchFragment);
                        ft.commit();
                        break;

                }
                return false;
            }
        });


    }



}