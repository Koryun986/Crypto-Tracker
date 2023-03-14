package com.samsung.cryptotracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.samsung.cryptotracker.Adapter.ExchangesListAdapter;
import com.samsung.cryptotracker.MVVM.ExchangesViewModel;

import org.json.JSONObject;

import java.util.ArrayList;

public class ExchagnesActivity extends AppCompatActivity {

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
                Intent intent = new Intent(ExchagnesActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
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