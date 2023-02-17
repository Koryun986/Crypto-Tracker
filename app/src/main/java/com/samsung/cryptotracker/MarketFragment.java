package com.samsung.cryptotracker;

import static com.samsung.cryptotracker.Constants.getArrayListFromJSONArray;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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


public class MarketFragment extends Fragment {


    public MarketFragment() {
    }


    public static MarketFragment newInstance(String param1, String param2) {
        MarketFragment fragment = new MarketFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    View view;
    ListView listView;
    DBManager dbManager;
    Spinner spinner;
    String[] spinnerValues = {"USD","EUR","RUB"};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_market, container, false);
        dbManager = new DBManager(getContext());
        spinner = view.findViewById(R.id.spinner);
        listView = view.findViewById(R.id.list_view);
        ExchangedCurrency exchangedCurrency = new ExchangedCurrency();




        Loader loader = new Loader();
        loader.start();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView id = view.findViewById(R.id.coin_id);
                Intent intent = new Intent(getContext(), CurrencyActivity.class);
                intent.putExtra("id", id.getText());
                startActivity(intent);
            }
        });

//        Spinner Exchanged Currency
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, spinnerValues);
        adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String value = adapterView.getItemAtPosition(i).toString().toLowerCase(Locale.ROOT);
                ExchangedCurrency.exchangedCurrency = value;
                loadJsonFromUrl();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        return view;
    }

    class Loader extends Thread{

        @Override
        public void run() {
            super.run();
            loadJsonFromUrl();
        }
    }


    private void loadJsonFromUrl() {
        new android.os.Handler(Looper.getMainLooper()).postDelayed(
                new Runnable() {
                    public void run() {
                        final ProgressBar progressBar = view.findViewById(R.id.progressBar);
                        String url = Constants.API_URL();
                        progressBar.setVisibility(View.VISIBLE);
                        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        progressBar.setVisibility(View.GONE);
                                        try {
                                            JSONArray jsonArray = new JSONArray(response);
                                            ArrayList<JSONObject> item = getArrayListFromJSONArray(jsonArray);
                                            ListAdapter listAdapter = new ListViewAdapter(getContext(), R.layout.row, R.id.container, item);
                                            listView.setAdapter(listAdapter);

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressBar.setVisibility(View.GONE);
                                error.printStackTrace();
                            }
                        });
                        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                        requestQueue.add(stringRequest);
                        dbManager.closeDb();
                    }
                },
                1000);

    }

//    private void loadJsonFromUrl(String url, String name) {
//        final ProgressBar progressBar = view.findViewById(R.id.progressBar);
//        progressBar.setVisibility(View.VISIBLE);
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + name,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        progressBar.setVisibility(View.GONE);
//                        try {
//                            JSONObject jsonObject = new JSONObject(response);
//                            ArrayList<JSONObject> item = getArrayListFromJSONArray(jsonObject.getJSONArray("coins"));
//                            ListAdapter listAdapter = new ListViewAdapter(getContext(), R.layout.row, R.id.container, item);
//                            listView.setAdapter(listAdapter);
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                progressBar.setVisibility(View.GONE);
//                error.printStackTrace();
//            }
//        });
//        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
//        requestQueue.add(stringRequest);
//        dbManager.closeDb();
//    }
}