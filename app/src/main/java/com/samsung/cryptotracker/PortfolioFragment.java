package com.samsung.cryptotracker;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.samsung.cryptotracker.Constants.getArrayListFromJSONArray;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    View view;
    LinearLayout addCoin;
    TextView portfolioMoney;
    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_portfolio, container, false);
        addCoin = view.findViewById(R.id.add_to_portfolio);
        portfolioMoney = view.findViewById(R.id.portfolio_money);
        listView = view.findViewById(R.id.list_view);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("users");

        addCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddCoinToPortfolioActivity.class);
                startActivity(intent);
            }
        });

        ref.child(user.getUid()).child("portfolio").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    portfolioMoney.setText("0");
                    String coins = "";
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        String coinId = snap.getKey();
                        coins += "," + coinId;
                    }
                    loadJsonFromUrl(Constants.CURRENCY_URL(coins));
                }else{
                    String arr[] = new String[0];
                    ListAdapter listAdapter = new ArrayAdapter<String>(getApplicationContext(),
                            android.R.layout.simple_list_item_1,
                            arr);

                    listView.setAdapter(listAdapter);
                    portfolioMoney.setText("0");
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
                                ref.child(user.getUid()).child("portfolio").child(id).child("count").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()){
                                            Double count = snapshot.getValue(Double.class);
                                            try {
                                                Double money = count * obj.getDouble(Constants.CURRENCY_PRICE);
                                                setPortfolioMoney(money);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }else {
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                            ListAdapter listAdapter = new PortfolioAdapter(getApplicationContext(), R.layout.portfolio_row, R.id.container, item);
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


    private void setPortfolioMoney (Double money) {
        Double currentMoney = Double.valueOf(portfolioMoney.getText().toString());
        Double finalMoney = money + currentMoney;
        DecimalFormat dec = new DecimalFormat("#0.00");
        portfolioMoney.setText(String.valueOf(dec.format(finalMoney)));
    }

}