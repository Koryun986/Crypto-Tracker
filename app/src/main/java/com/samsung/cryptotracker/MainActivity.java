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
 import com.google.firebase.database.DataSnapshot;
 import com.google.firebase.database.DatabaseError;
 import com.google.firebase.database.DatabaseReference;
 import com.google.firebase.database.FirebaseDatabase;
 import com.google.firebase.database.ValueEventListener;
 import com.samsung.cryptotracker.Adapter.ListViewAdapter;

import com.samsung.cryptotracker.Exchange.ExchangedCurrency;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    ListView listView;
    Toolbar toolbar;
    BottomNavigationView navigationView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        listView = findViewById(R.id.list_view);
        navigationView = findViewById(R.id.bottom_navigation_bar);
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();;
        ref = database.getReference("users");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.child(user.getUid()).exists()) {
                    User userClass = new User(user.getEmail());
                    snapshot.child(user.getUid()).getRef().setValue(userClass);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        Fragment marketFragment = new MarketFragment();


        Fragment profileFragment = new ProfileFragment();



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

                    case R.id.profile:
                        ft.replace(R.id.fragment, profileFragment);
                        ft.commit();
                }
                return false;
            }
        });


    }



}