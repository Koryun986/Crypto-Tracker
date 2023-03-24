package com.samsung.cryptotracker;

import static com.samsung.cryptotracker.Constants.getArrayListFromJSONArray;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.samsung.cryptotracker.Adapter.ListViewAdapter;
import com.samsung.cryptotracker.Exchange.ExchangedCurrency;
import com.samsung.cryptotracker.MVVM.CurrencyInfoViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FavoritesActivity extends AppCompatActivity {
    private static final String idArg = "id";


    private FirebaseDatabase database;
    private DatabaseReference ref;
    private FirebaseAuth auth;
    private FirebaseUser user;

    CurrencyInfoViewModel favoritesViewModel;

    SwipeRefreshLayout swipeRefreshLayout;
    ListView listView;
    ImageView toMarket;
    Spinner spinner;
    String[] spinnerValues = {"USD","EUR","RUB"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        favoritesViewModel = new CurrencyInfoViewModel(getApplication());

        listView = findViewById(R.id.list_view);
        spinner = findViewById(R.id.spinner);
        toMarket = findViewById(R.id.to_market);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        database = FirebaseDatabase.getInstance();
        ref = database.getReference(Constants.FIREBASE_USERS);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        favoritesViewModel.isInfoLoaded().observe(this, isLoaded -> {
            if (isLoaded) {
                swipeRefreshLayout.setRefreshing(true);
            }else {
                swipeRefreshLayout.setRefreshing(false);
            }
        });


        favoritesViewModel.getData().observe(this, data -> {
            if (data != null) {
                ListAdapter listAdapter = new ListViewAdapter(getApplication(), R.layout.row, R.id.container, (ArrayList<JSONObject>) data);
                listView.setAdapter(listAdapter);
            }else {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(this).
                                setMessage("Oops Page Note Found").
                                setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                });
                builder.create().show();
            }

        });


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFavorites();
                swipeRefreshLayout.setRefreshing(false);
            }
        });



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView id = view.findViewById(R.id.coin_id);
                Intent intent = new Intent(getApplicationContext(), CurrencyActivity.class);
                intent.putExtra(idArg, id.getText());
                startActivity(intent);
            }
        });

        //        Spinner Exchanged Currency
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, spinnerValues);
        adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String value = adapterView.getItemAtPosition(i).toString().toLowerCase(Locale.ROOT);
                ExchangedCurrency.exchangedCurrency = value;
                getFavorites();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        toMarket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FavoritesActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }



    private void getFavorites() {
        List<String> list = new ArrayList<>();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot favorites = snapshot.child(user.getUid()).child(Constants.FIREBASE_FAVORITES);
                for (DataSnapshot favCoin: favorites.getChildren()) {
                    String coin = favCoin.getValue(String.class);
                    list.add(coin);

                }
                if (!list.isEmpty()){
                    String coins = "";
                    for (int i = 0; i < list.size(); i++) {
                        coins += "," + list.get(i);
                    }
                    favoritesViewModel.loadCurrencyData(coins);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}