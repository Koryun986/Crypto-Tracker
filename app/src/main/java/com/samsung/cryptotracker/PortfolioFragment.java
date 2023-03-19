package com.samsung.cryptotracker;

//import static com.facebook.FacebookSdk.getApplicationContext;
import static com.samsung.cryptotracker.Constants.getArrayListFromJSONArray;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.samsung.cryptotracker.Adapter.ListViewAdapter;
import com.samsung.cryptotracker.Adapter.PortfolioAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class PortfolioFragment extends Fragment {


    public PortfolioFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private final String portfolioFirebase = "portfolio";
    private final String priceFirebase = "price";
    private final String countFirebase = "count";
    private final String FORMAT_PATTERN = "#0.00";

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    View view;
    LinearLayout addCoin;
    TextView portfolioMoney;
    ListView listView;
    TextView portfolioProfit;
    TextView dollarChar;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_portfolio, container, false);
        addCoin = view.findViewById(R.id.add_to_portfolio);
        portfolioMoney = view.findViewById(R.id.portfolio_money);
        listView = view.findViewById(R.id.list_view);
        portfolioProfit = view.findViewById(R.id.portfolio_profit);
        dollarChar = view.findViewById(R.id.dollar_char);
        progressBar = view.findViewById(R.id.progress_bar);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference(Constants.FIREBASE_USERS);

        addCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddCoinToPortfolioActivity.class);
                startActivity(intent);
            }
        });

        ref.child(user.getUid()).child(portfolioFirebase).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String defaultMoney = "0";
                if (snapshot.exists()) {
                    progressBar.setVisibility(View.VISIBLE);
                    dollarChar.setVisibility(View.VISIBLE);
                    portfolioProfit.setText(defaultMoney);
                    portfolioMoney.setText(defaultMoney);
                    String coins = "";
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        String coinId = snap.getKey();
                        coins += "," + coinId;
                    }
                    loadJsonFromUrl(Constants.CURRENCY_URL(coins));
                }else{
                    dollarChar.setVisibility(View.GONE);
                    portfolioProfit.setText("");
                    String arr[] = new String[0];
                    ListAdapter listAdapter = new ArrayAdapter<String>(getContext(),
                            android.R.layout.simple_list_item_1,
                            arr);

                    listView.setAdapter(listAdapter);
                    portfolioMoney.setText(defaultMoney);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    private void loadJsonFromUrl(String url) {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            ArrayList<JSONObject> item = getArrayListFromJSONArray(jsonArray);
                            for (JSONObject obj : item){
                                String id = obj.getString(Constants.CURRENCY_ID);
                                Double price = obj.getDouble(Constants.CURRENCY_PRICE);
                                ref.child(user.getUid()).child(portfolioFirebase).child(id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()){
                                            Double boughtPrice = snapshot.child(priceFirebase).getValue(Double.class);
                                            Double count = snapshot.child(countFirebase).getValue(Double.class);
                                            Double money = count * price;
                                            Double profit =  money - count*boughtPrice;
                                            setPortfolioMoney(money, profit);
                                        }else {
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                            ListAdapter listAdapter = new PortfolioAdapter(getContext(), R.layout.portfolio_row, R.id.container, item);
                            listView.setAdapter(listAdapter);
                            progressBar.setVisibility(View.GONE);

                        } catch (JSONException e) {
                            progressBar.setVisibility(View.GONE);
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


    private void setPortfolioMoney (Double money, Double profit) {
        Double currentMoney = Double.valueOf(portfolioMoney.getText().toString());
        Double finalMoney = money + currentMoney;
        DecimalFormat dec = new DecimalFormat(FORMAT_PATTERN);
        Double currentProfit = Double.valueOf((portfolioProfit.getText().toString()));
        Double finalPorfit = currentProfit + profit;
        portfolioMoney.setText(String.valueOf(dec.format(finalMoney)));
        portfolioProfit.setText(String.valueOf(dec.format(finalPorfit)));
        if (finalPorfit >= 0) {
            portfolioProfit.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
            dollarChar.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
        }else {
            portfolioProfit.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
            dollarChar.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
        }
    }

}