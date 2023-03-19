package com.samsung.cryptotracker.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PortfolioAdapter extends ArrayAdapter<JSONObject> {
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private final String portfolioFirebase = "portfolio";
    private final String portfolioCountFirebase = "count";
    private final String portfolioPriceFirebase = "price";
    private final String percentSymbol = "%";
    private final String formatPattern = "#0.00000";
    int listLayout;
    ArrayList<JSONObject> obj;
    Context context;

    public PortfolioAdapter(Context context, int listLayout, int field, ArrayList<JSONObject> obj) {
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
        TextView count = listViewItem.findViewById(R.id.coin_count);
        ImageView icon = listViewItem.findViewById(R.id.coin_icon);
        TextView coinName = listViewItem.findViewById(R.id.coin_name);
        TextView coinPrice = listViewItem.findViewById(R.id.coin_price);
        TextView coinBoughtPrice = listViewItem.findViewById(R.id.coin_bought_price);
        TextView coinChange = listViewItem.findViewById(R.id.coin_change);
        ImageView deleteCoin = listViewItem.findViewById(R.id.coin_delete);


        String exchangedCurrency = ExchangedCurrency.exchangedCurrency;
        String exchangedCurrencySymbol = "";
        switch (exchangedCurrency) {
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


        try {
            String name = obj.get(position).getString(Constants.CURRENCY_ID);
            id.setText(name);
            Double currentPrice  = obj.get(position).getDouble(Constants.CURRENCY_PRICE);
            Picasso.get().load(obj.get(position).getString(Constants.CURRENCY_IMAGE)).into(icon);
            coinName.setText(obj.get(position).getString(Constants.CURRENCY_NAME));
            coinPrice.setText(currentPrice + exchangedCurrencySymbol);
            String finalExchangedCurrencySymbol = exchangedCurrencySymbol;
            ref.child(user.getUid()).child(portfolioFirebase).child(String.valueOf(id.getText())).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child(portfolioCountFirebase).exists() && snapshot.child(portfolioPriceFirebase).exists()){
                        count.setText(snapshot.child(portfolioCountFirebase).getValue(Double.class).toString() + "x");
                        Double boughtPrice = snapshot.child(portfolioPriceFirebase).getValue(Double.class);
                        coinBoughtPrice.setText(boughtPrice.toString() + finalExchangedCurrencySymbol);
                        DecimalFormat dec = new DecimalFormat(formatPattern);
                        Double change = (currentPrice - boughtPrice) / boughtPrice;
                        coinChange.setText(dec.format(change) + percentSymbol);
                        coinChange.setTextColor(change >= 0 ? ContextCompat.getColor(context, R.color.green) : ContextCompat.getColor(context, R.color.red));
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
                deleteCoin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ref.child(user.getUid()).child(portfolioFirebase).child(String.valueOf(id.getText())).removeValue();
                    }
                });
            return listViewItem;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listViewItem;
    }
}
