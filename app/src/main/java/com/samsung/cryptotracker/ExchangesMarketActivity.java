package com.samsung.cryptotracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.samsung.cryptotracker.Adapter.ExchangesMarketListAdapter;
import com.samsung.cryptotracker.MVVM.ExchangesMarketViewModel;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ExchangesMarketActivity extends AppCompatActivity {

    ExchangesMarketViewModel exchangesMarketViewModel;

    private String id;
    private String RESPONSE_MARKET = "market";
    private String RESPONSE_MARKET_NAME = "name";
    private String RESPONSE_MARKET_LOGO = "logo";
    private String TRADE_URL = "trade_url";

    ImageView backBtn;
    ImageView exchangesLogo;
    TextView exchangesName;
    SwipeRefreshLayout swipeRefreshLayout;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchanges_market);

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            id = extra.getString("id");
        }

        exchangesMarketViewModel = new ExchangesMarketViewModel(getApplication());

        backBtn = findViewById(R.id.back_button);
        exchangesLogo = findViewById(R.id.exchanges_market_logo);
        exchangesName = findViewById(R.id.exchanges_market_name);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        listView = findViewById(R.id.list_view);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ExchangesMarketActivity.this, ExchangesActivity.class);
                startActivity(intent);
                finish();
            }
        });

        exchangesMarketViewModel.getData().observe(this, data -> {
            swipeRefreshLayout.setRefreshing(true);
            if (data != null) {
                List<JSONObject> list = data;
                try {
                    Picasso.get().load(list.get(0).getJSONObject(RESPONSE_MARKET).getString(RESPONSE_MARKET_LOGO)).into(exchangesLogo);
                    exchangesName.setText(list.get(0).getJSONObject(RESPONSE_MARKET).getString(RESPONSE_MARKET_NAME) );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ExchangesMarketListAdapter listAdapter = new ExchangesMarketListAdapter(this, R.layout.exchanges_market_row, R.id.container, (ArrayList<JSONObject>) data);
                listView.setAdapter(listAdapter);
            }else {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(this).
                                setMessage("Oops Page Note Found").
                                setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                builder.create().show();
            }
            swipeRefreshLayout.setRefreshing(false);
        });

        exchangesMarketViewModel.loadData(id);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                exchangesMarketViewModel.loadData(id);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                List<JSONObject> list = exchangesMarketViewModel.getData().getValue();
                try {
                    String url = list.get(i).getString(TRADE_URL);
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