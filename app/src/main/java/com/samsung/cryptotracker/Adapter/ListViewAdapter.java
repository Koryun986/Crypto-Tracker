package com.samsung.cryptotracker.Adapter;


import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.samsung.cryptotracker.Constants;

import com.samsung.cryptotracker.Exchange.ExchangedCurrency;
import com.samsung.cryptotracker.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListViewAdapter extends ArrayAdapter<JSONObject> {
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private final String listViewForSearch = "large";
    private final String favoritesFirebase = "favorites";
    int listLayout;
    ArrayList<JSONObject> obj;
    Context context;

    public ListViewAdapter(Context context, int listLayout, int field, ArrayList<JSONObject> obj) {
        super(context, listLayout, field, obj);
        this.context = context;
        this.listLayout = listLayout;
        this.obj = obj;
        database = FirebaseDatabase.getInstance();
        ref = database.getReference(Constants.FIREBASE_USERS);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listViewItem = inflater.inflate(listLayout, null, false);
        TextView id = listViewItem.findViewById(R.id.coin_id);
        ImageView icon = listViewItem.findViewById(R.id.coin_icon);
        TextView coinName = listViewItem.findViewById(R.id.coin_name);
        TextView coinPrice = listViewItem.findViewById(R.id.coin_price);
        TextView coinChange = listViewItem.findViewById(R.id.coin_change);
        TextView favCoin = listViewItem.findViewById(R.id.coin_favorite);

        String exchangedCurrency = ExchangedCurrency.exchangedCurrency;
        String exchangedCurrencySymbol = "";
        switch(exchangedCurrency) {
            case "usd":
                exchangedCurrencySymbol = "$";
                break;
            case "rub":
                exchangedCurrencySymbol = "₽";
                break;
            case "eur":
                exchangedCurrencySymbol = "€";
                break;
            default:
        }

        if (!obj.get(position).has(listViewForSearch)) {
            try {
                String name = obj.get(position).getString(Constants.CURRENCY_ID);
                id.setText(name);
                double change = obj.get(position).getDouble(Constants.CURRENCY_MARKET_CUP_CHANGE);
                Picasso.get().load(obj.get(position).getString(Constants.CURRENCY_IMAGE)).into(icon);
                coinName.setText(obj.get(position).getString(Constants.CURRENCY_NAME));
                coinPrice.setText(obj.get(position).getDouble(Constants.CURRENCY_PRICE) + exchangedCurrencySymbol);
                coinChange.setText(change + "%");
                coinChange.setTextColor(change >= 0 ? ContextCompat.getColor(context, R.color.green) : ContextCompat.getColor(context, R.color.red));
                favCoin.setBackground(ActivityCompat.getDrawable(context, R.drawable.ic_baseline_favorite_border_24));

                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        DataSnapshot favorites = snapshot.child(user.getUid()).child(favoritesFirebase);
                        for (DataSnapshot favCoinSnap: favorites.getChildren()){
                            String coin = favCoinSnap.getValue(String.class);
                            if (coin.equals(name)){
                                favCoin.setBackground(ActivityCompat.getDrawable(context, R.drawable.ic_baseline_favorite_24));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
            favCoin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = null;
                    try {
                        name = obj.get(position).getString(Constants.CURRENCY_ID);
                        String coinName = name;
                        List<String> coinsList = new ArrayList<>();
                        favCoin.setBackground(ActivityCompat.getDrawable(context, R.drawable.ic_baseline_favorite_24));
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                DataSnapshot favorites = snapshot.child(user.getUid()).child(favoritesFirebase);
                                boolean ifExist = false;
                                for (DataSnapshot favCoinSnap: favorites.getChildren()){
                                    String coin = favCoinSnap.getValue(String.class);
                                    if (coin.equals(coinName)){
                                        ifExist = !ifExist;
                                        favCoin.setBackground(ActivityCompat.getDrawable(context, R.drawable.ic_baseline_favorite_border_24));
                                    }else {
                                        coinsList.add(coin);
                                    }
                                }
                                if (!ifExist){
                                    coinsList.add(coinName);
                                }
                                ref.child(user.getUid()).child(favoritesFirebase).setValue(coinsList);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            return listViewItem;
        }else{try {
            String name = obj.get(position).getString(Constants.CURRENCY_ID);
            id.setText(name);
            Picasso.get().load(obj.get(position).getString(listViewForSearch)).into(icon);
            coinName.setText(obj.get(position).getString(Constants.CURRENCY_NAME));
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    DataSnapshot favorites = snapshot.child(user.getUid()).child(favoritesFirebase);
                    for (DataSnapshot favCoinSnap: favorites.getChildren()){
                        String coin = favCoinSnap.getValue(String.class);
                        if (coin.equals(name)){
                            favCoin.setBackground(ActivityCompat.getDrawable(context, R.drawable.ic_baseline_favorite_24));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
            favCoin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = null;
                    try {
                        name = obj.get(position).getString(Constants.CURRENCY_ID);
                         Boolean ifExist = setFavorite(name);
                         if (ifExist) {
                             favCoin.setBackground(ActivityCompat.getDrawable(context, R.drawable.ic_baseline_favorite_border_24));
                         }else {
                             favCoin.setBackground(ActivityCompat.getDrawable(context, R.drawable.ic_baseline_favorite_24));
                         }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            return listViewItem;
        }
    }

    private Boolean setFavorite(String name){
        final boolean[] ifExist = {false};
        String coinName = name;
        List<String> coinsList = new ArrayList<>();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot favorites = snapshot.child(user.getUid()).child(favoritesFirebase);
                for (DataSnapshot favCoin: favorites.getChildren()){
                    String coin = favCoin.getValue(String.class);
                    if (coin.equals(coinName)){
                        ifExist[0] = !ifExist[0];
                    }else {
                        coinsList.add(coin);
                    }
                }
                if (!ifExist[0]){
                    coinsList.add(coinName);
                }
                ref.child(user.getUid()).child(favoritesFirebase).setValue(coinsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return ifExist[0];
    }
}
