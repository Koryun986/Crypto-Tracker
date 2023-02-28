package com.samsung.cryptotracker;

import static com.samsung.cryptotracker.Constants.getArrayListFromJSONArray;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.samsung.cryptotracker.Adapter.ListViewAdapter;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class AddCoinToPortfolioActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private String name = null;
    ListView listView;
    SearchView searchView;
    ImageView backBtn;
    LinearLayout currencyLayout;
    EditText currencyCount;
    EditText currencyBoughtPrice;
    TextView addToPortfolio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_coin_to_portfolio);
        listView = findViewById(R.id.list_view);
        searchView = findViewById(R.id.search_bar);
        backBtn = findViewById(R.id.back_button);
        currencyLayout = findViewById(R.id.currency_layout);
        currencyCount = findViewById(R.id.curency_count);
        currencyBoughtPrice = findViewById(R.id.currency_bought_price);
        addToPortfolio = findViewById(R.id.add_to_portfolio);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();;
        ref = database.getReference("users");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView id = view.findViewById(R.id.coin_id);
                ImageView viewIcon = view.findViewById(R.id.coin_icon);
                TextView viewCoinName = view.findViewById(R.id.coin_name);
                currencyLayout.setVisibility(View.VISIBLE);
                name = id.getText().toString();
                isEditTextEmpty();
                ImageView icon = findViewById(R.id.curency_icon);
                TextView name = findViewById(R.id.curency_name);
                icon.setImageDrawable(viewIcon.getDrawable());
                name.setText(viewCoinName.getText());
                String arr[] = new String[0];
                ListAdapter listAdapter = new ArrayAdapter<String>(AddCoinToPortfolioActivity.this,
                        android.R.layout.simple_list_item_1,
                        arr);

                listView.setAdapter(listAdapter);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
                    String arr[] = new String[0];
                    ListAdapter listAdapter = new ArrayAdapter<String>(AddCoinToPortfolioActivity.this,
                            android.R.layout.simple_list_item_1,
                            arr);

                    listView.setAdapter(listAdapter);
                }else{
                    loadJsonFromUrlSearchView(Constants.SEARCH_CURRENCY, name);
                }

                return true;
            }
        });

        currencyBoughtPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isEditTextEmpty();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        currencyCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isEditTextEmpty();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void isEditTextEmpty(){
        if (!TextUtils.isEmpty(currencyCount.getText()) && !TextUtils.isEmpty(currencyBoughtPrice.getText()) &&!TextUtils.isEmpty(name) ){
            addToPortfolio.setBackgroundResource(R.drawable.login_register_btn_bg);
            addToPortfolio.setEnabled(true);

            addToPortfolio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Double count = Double.valueOf(String.valueOf(currencyCount.getText()));
                    Double price = Double.valueOf(String.valueOf(currencyBoughtPrice.getText()));
                    Portfolio portfolio = new Portfolio(count, price);
                    ref.child(user.getUid()).child("portfolio").child(name).setValue(portfolio);
                    Intent intent = new Intent(AddCoinToPortfolioActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }else{
            addToPortfolio.setEnabled(false);
            addToPortfolio.setBackgroundResource(R.drawable.btn_bg_disable);

        }
    }

    private void loadJsonFromUrlSearchView(String url, String name) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + name,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            ArrayList<JSONObject> item = getArrayListFromJSONArray(jsonObject.getJSONArray("coins"));
                            ListAdapter listAdapter = new ListViewAdapter(getApplicationContext(), R.layout.row, R.id.container, item);
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
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}