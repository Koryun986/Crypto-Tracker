package com.samsung.cryptotracker;

import static com.samsung.cryptotracker.Constants.getArrayListFromJSONArray;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.samsung.cryptotracker.Adapter.CurrencyMarketPriceAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MarketsPriceFragment extends Fragment {

    private static final String ARG_PARAM = "id";

    private String id;

    public MarketsPriceFragment() {
        // Required empty public constructor
    }


    public static MarketsPriceFragment newInstance(String param) {
        MarketsPriceFragment fragment = new MarketsPriceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, param);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getString(ARG_PARAM);
        }
    }

    View view;
    ProgressBar progressBar;
    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_markets_price, container, false);
        progressBar = view.findViewById(R.id.progress_bar);
        listView = view. findViewById(R.id.list_view);

        Loader loader = new Loader();
        loader.start();

        return view;
    }

    class Loader extends Thread {
        @Override
        public void run() {
            super.run();
            loadMarketsData();

        }
    }

    private void loadMarketsData () {
        progressBar.setVisibility(View.VISIBLE);
        String url = Constants.CURRENCY_PRICE_MARKETS(id);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            ArrayList<JSONObject> item = getArrayListFromJSONArray(jsonObject.getJSONArray("tickers"));
                            progressBar.setVisibility(View.GONE);
                            CurrencyMarketPriceAdapter listAdapter = new CurrencyMarketPriceAdapter(getContext(), R.layout.currency_markets_row, R.id.container, item);
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
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);

    }
}