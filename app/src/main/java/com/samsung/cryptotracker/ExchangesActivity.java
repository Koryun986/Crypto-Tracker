package com.samsung.cryptotracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.samsung.cryptotracker.Adapter.ExchangesListAdapter;
import com.samsung.cryptotracker.MVVM.ExchangesViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ExchangesActivity extends AppCompatActivity {

    private final String exchangesId = "id";
    private final String exchangesName = "name";

    ExchangesViewModel exchangesViewModel;

    ListView listView;
    ImageView backBtn;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchagnes);

        exchangesViewModel = new ExchangesViewModel(getApplication());

        listView = findViewById(R.id.list_view);
        backBtn = findViewById(R.id.back_button);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ExchangesActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                List<JSONObject> list = exchangesViewModel.getData().getValue();
                Intent intent = new Intent(ExchangesActivity.this, ExchangesMarketActivity.class);
                try {
                    intent.putExtra(exchangesId, list.get(i).getString(exchangesName));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
            }
        });

        exchangesViewModel.isDataLoaded().observe(this, isLoaded -> {
            if(isLoaded) {
                swipeRefreshLayout.setRefreshing(true);
            }else {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        exchangesViewModel.getData().observe(this, data -> {
            if (data != null) {
                ExchangesListAdapter listAdapter = new ExchangesListAdapter(this, R.layout.exchanges_row, R.id.container, (ArrayList<JSONObject>) data);
                listView.setAdapter(listAdapter);
            }
        });

        exchangesViewModel.loadData();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                exchangesViewModel.loadData();
            }
        });

    }
}