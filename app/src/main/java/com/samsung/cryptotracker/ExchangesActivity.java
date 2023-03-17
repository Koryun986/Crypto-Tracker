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
                    intent.putExtra("id", list.get(i).getString("name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
            }
        });

        exchangesViewModel.getData().observe(this, data -> {
            swipeRefreshLayout.setRefreshing(true);
            if (data != null) {
                ExchangesListAdapter listAdapter = new ExchangesListAdapter(this, R.layout.exchanges_row, R.id.container, (ArrayList<JSONObject>) data);
                listView.setAdapter(listAdapter);
            }
            swipeRefreshLayout.setRefreshing(false);
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