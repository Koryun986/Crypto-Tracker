package com.samsung.cryptotracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ExchangesMarketActivity extends AppCompatActivity {

    ImageView backBtn;
    ImageView exchangesLogo;
    TextView exchangesName;
    SwipeRefreshLayout swipeRefreshLayout;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchanges_market);

        backBtn = findViewById(R.id.back_button);
        exchangesLogo = findViewById(R.id.exchanges_market_logo);
        exchangesName = findViewById(R.id.exchanges_market_name);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        listView = findViewById(R.id.list_view);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ExchangesMarketActivity.this, ExchagnesActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}