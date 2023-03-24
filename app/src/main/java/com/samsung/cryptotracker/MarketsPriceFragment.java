package com.samsung.cryptotracker;

import static com.samsung.cryptotracker.Constants.getArrayListFromJSONArray;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.samsung.cryptotracker.Adapter.CurrencyMarketPriceAdapter;
import com.samsung.cryptotracker.MVVM.MarketsPriceViewModel;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MarketsPriceFragment extends Fragment {

    private static final String ARG_PARAM = "id";
    private final String TRADE_URL = "trade_url";
    private final String LIST_TARGET = "target";
    private final String LIST_PRICE = "last";
    private final String LIST_MARKET = "market";
    private final String LIST_MARKET_NAME = "name";
    private final String LIST_MARKET_LOGO = "logo";
    private final String TOAST_MESSAGE = "Sorry this exchange is not avaliable";
    private final String All_EXCHANGES = "all exchanges";
    private final String MAX_MIN = "max and min";

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
    TextView allExchanges;
    TextView maxMin;
    Spinner spinner;
    LinearLayout maxMinLayout;
    LinearLayout maxPriceLayout;
    LinearLayout minPriceLayout;
    ImageView marketLogoMaxPrice;
    ImageView marketLogoMinPrice;
    TextView marketNameMaxPrice;
    TextView marketNameMinPrice;
    TextView marketMaxPrice;
    TextView marketMinPrice;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_markets_price, container, false);
        progressBar = view.findViewById(R.id.progress_bar);
        listView = view. findViewById(R.id.list_view);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        allExchanges = view.findViewById(R.id.all_exchanges);
        maxMin = view.findViewById(R.id.max_min);
        spinner = view.findViewById(R.id.spinner);
        maxMinLayout = view.findViewById(R.id.max_min_layout);
        marketLogoMaxPrice = view.findViewById(R.id.market_logo_max);
        marketLogoMinPrice = view.findViewById(R.id.market_logo_min);
        marketNameMaxPrice = view.findViewById(R.id.market_name_max);
        marketNameMinPrice = view.findViewById(R.id.market_name_min);
        marketMaxPrice = view.findViewById(R.id.market_price_max);
        marketMinPrice = view.findViewById(R.id.market_price_min);
        maxPriceLayout = view.findViewById(R.id.max_price_layout);
        minPriceLayout = view.findViewById(R.id.min_price_layout);

        marketsPriceViewModel = new MarketsPriceViewModel(getActivity().getApplication());

        marketsPriceViewModel.isInfoLoaded().observe(getActivity(), isLoaded -> {
            if (isLoaded) {
                swipeRefreshLayout.setRefreshing(true);
            }else {
                swipeRefreshLayout.setRefreshing(false);
            }
        });



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                List<JSONObject> list = marketsPriceViewModel.getData().getValue();
                JSONObject obj = list.get(i);
                try {
                    String url = obj.getString(TRADE_URL);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                } catch (JSONException e) {
                    Toast.makeText(getContext(), TOAST_MESSAGE, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        });

        marketsPriceViewModel.getData().observe(getActivity(), data -> {
            if (data != null) {
                List<String> spinnerList = new ArrayList<>();
                for (JSONObject listItem : data) {
                    try {
                        String target = listItem.getString(LIST_TARGET);
                        if (!spinnerList.contains(target)){
                            spinnerList.add(target);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, spinnerList);
                spinner.setAdapter(dataAdapter);
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

        allExchanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleListChange(All_EXCHANGES);
            }
        });

        maxMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleListChange(MAX_MIN);
            }
        });

        return view;
    }

    private void handleListChange (String type) {
        allExchanges.setBackground(null);
        allExchanges.setTextColor(getResources().getColor(R.color.blue_price));
        maxMin.setTextColor(getResources().getColor(R.color.blue_price));
        maxMin.setBackground(null);
        switch (type) {
            case All_EXCHANGES:
                allExchanges.setBackground(getResources().getDrawable(R.color.gray_logo));
                allExchanges.setTextColor(getResources().getColor(R.color.blue_bg));
                swipeRefreshLayout.setVisibility(View.VISIBLE);
                maxMinLayout.setVisibility(View.GONE);
                    break;
            case MAX_MIN:
                maxMin.setBackground(getResources().getDrawable(R.color.gray_logo));
                maxMin.setTextColor(getResources().getColor(R.color.blue_bg));
                swipeRefreshLayout.setVisibility(View.GONE);
                maxMinLayout.setVisibility(View.VISIBLE);
                spinnerItemChange();
                break;
        }

    }

    private void spinnerItemChange () {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                List<JSONObject> list = marketsPriceViewModel.getData().getValue();
                String currentPriceTarget = (String) adapterView.getSelectedItem();
                JSONObject maxObj = null;
                JSONObject minObj = null;
                for (JSONObject listItem : list ){
                    try {
                        String target = listItem.getString(LIST_TARGET);
                        if (target.equals(currentPriceTarget)){
                            if (maxObj == null && minObj == null) {
                                maxObj = listItem;
                                minObj = listItem;
                                continue;
                            }else {
                                Double maxPrice = maxObj.getDouble(LIST_PRICE);
                                Double minPrice = minObj.getDouble(LIST_PRICE);
                                Double currentPrice = listItem.getDouble(LIST_PRICE);
                                if (currentPrice >= maxPrice) {
                                    maxObj = listItem;
                                }else if (currentPrice < minPrice) {
                                    minObj = listItem;
                                }
                            }
                        }
                    } catch (JSONException e) {

                    }
                }

                try {
                    setPrices(maxObj, minObj, currentPriceTarget);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setPrices(JSONObject maxObj, JSONObject minObj, String target) throws JSONException {
        String maxMarketName = maxObj.getJSONObject(LIST_MARKET).getString(LIST_MARKET_NAME);
        String minMarketName = minObj.getJSONObject(LIST_MARKET).getString(LIST_MARKET_NAME);
        Double maxObjPrice = maxObj.getDouble(LIST_PRICE);
        Double minObjPrice = minObj.getDouble(LIST_PRICE);
        marketNameMaxPrice.setText(maxMarketName);
        marketNameMinPrice.setText(minMarketName);
        Picasso.get().load(maxObj.getJSONObject(LIST_MARKET).getString(LIST_MARKET_LOGO)).into(marketLogoMaxPrice);
        Picasso.get().load(minObj.getJSONObject(LIST_MARKET).getString(LIST_MARKET_LOGO)).into(marketLogoMinPrice);
        marketMaxPrice.setText(maxObjPrice + target);
        marketMinPrice.setText(minObjPrice + target);

        maxPriceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String url = maxObj.getString(TRADE_URL);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        minPriceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String url = minObj.getString(TRADE_URL);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}