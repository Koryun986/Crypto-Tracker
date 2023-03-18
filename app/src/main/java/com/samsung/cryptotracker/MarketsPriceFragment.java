package com.samsung.cryptotracker;

import static com.samsung.cryptotracker.Constants.getArrayListFromJSONArray;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.samsung.cryptotracker.MVVM.MarketsPriceViewModel;

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

    MarketsPriceViewModel marketsPriceViewModel;

    View view;
    ProgressBar progressBar;
    SwipeRefreshLayout swipeRefreshLayout;
    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_markets_price, container, false);
        progressBar = view.findViewById(R.id.progress_bar);
        listView = view. findViewById(R.id.list_view);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        marketsPriceViewModel = new MarketsPriceViewModel(getActivity().getApplication());

        marketsPriceViewModel.isInfoLoaded().observe(getActivity(), isLoaded -> {
            if (isLoaded) {
                swipeRefreshLayout.setRefreshing(true);
            }else {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        marketsPriceViewModel.getData().observe(getActivity(), data -> {
            if (data != null) {
                CurrencyMarketPriceAdapter listAdapter = new CurrencyMarketPriceAdapter(getContext(), R.layout.currency_markets_row, R.id.container, (ArrayList<JSONObject>) data);
                listView.setAdapter(listAdapter);
            }
        });

        marketsPriceViewModel.loadData(id);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                marketsPriceViewModel.loadData(id);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return view;
    }


}