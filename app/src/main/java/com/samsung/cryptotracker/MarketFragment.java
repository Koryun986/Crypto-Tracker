package com.samsung.cryptotracker;

import static com.samsung.cryptotracker.Constants.getArrayListFromJSONArray;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
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
import com.samsung.cryptotracker.Adapter.ListViewAdapter;
import com.samsung.cryptotracker.Exchange.ExchangedCurrency;
import com.samsung.cryptotracker.MVVM.MarketInfoViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;


public class MarketFragment extends Fragment {

    private static final String ALERT_MESSAGE = "Oops Page Note Found";
    private static final String ALERT_CANCEL = "Cancel";
    private static final String ARG_ID = "id";


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
    LinearLayout navigation;
    SwipeRefreshLayout swipeRefreshLayout;
    Spinner spinner;
    Toolbar toolbar;
    SearchView searchView;
    String[] spinnerValues = {"USD","EUR","RUB"};
    ProgressBar progressBar;
    MarketInfoViewModel marketInfoViewModel;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_market, container, false);

        toolbar = view.findViewById(R.id.tool_bar);
        spinner = view.findViewById(R.id.spinner);
        searchView = view.findViewById(R.id.search_bar);
        listView = view.findViewById(R.id.list_view);
        progressBar = view.findViewById(R.id.progressBar);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        navigation = view.findViewById(R.id.list_view_navigation);
        ExchangedCurrency exchangedCurrency = new ExchangedCurrency();

        marketInfoViewModel = new MarketInfoViewModel(getActivity().getApplication()) ;

        marketInfoViewModel.isMarketLoaded().observe(getActivity(), isLoaded -> {
            if (isLoaded) {
                swipeRefreshLayout.setRefreshing(true);
            }else {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        marketInfoViewModel.getData().observe(getActivity(), data-> {
            if (data != null) {
                ListAdapter listAdapter = new ListViewAdapter(getActivity(), R.layout.row, R.id.container, (ArrayList<JSONObject>) data);
                listView.setAdapter(listAdapter);
            }else {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getActivity()).
                                setMessage(ALERT_MESSAGE).
                                setPositiveButton(ALERT_CANCEL, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                builder.create().show();
            }

        });

        marketInfoViewModel.loadData();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                marketInfoViewModel.loadData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });



//      Tool Bar
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.favorites:
                        Intent intent = new Intent(getContext(), FavoritesActivity.class);
                        startActivity(intent);
                            break;
                    default:
                        break;
                }
                return false;
            }
        });
        searchView.clearFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String name) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String name) {
                if(name.length() == 0 ){
                    marketInfoViewModel.loadData();
                }else{
//                    searchViewModel.loadSearchData(name);
                    loadJsonFromUrlSearchView(Constants.SEARCH_CURRENCY, name);

                }

                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView id = view.findViewById(R.id.coin_id);
                Intent intent = new Intent(getContext(), CurrencyActivity.class);
                intent.putExtra(ARG_ID, id.getText());
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
                marketInfoViewModel.loadData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        return view;
    }



    private void loadJsonFromUrlSearchView(String url, String name) {
        final ProgressBar progressBar = view.findViewById(R.id.progressBar);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + name,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            ArrayList<JSONObject> item = getArrayListFromJSONArray(jsonObject.getJSONArray("coins"));
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
    }
}