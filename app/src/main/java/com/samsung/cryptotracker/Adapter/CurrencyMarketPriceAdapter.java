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

public class CurrencyMarketPriceAdapter extends ArrayAdapter<JSONObject> {

    int listLayout;
    ArrayList<JSONObject> obj;
    Context context;

    public CurrencyMarketPriceAdapter(Context context, int listLayout, int field, ArrayList<JSONObject> obj) {
        super(context, listLayout, field, obj);
        this.context = context;
        this.listLayout = listLayout;
        this.obj = obj;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listViewItem = inflater.inflate(listLayout, null, false);
        TextView market =  listViewItem.findViewById(R.id.market_name);
        TextView price = listViewItem.findViewById(R.id.market_price);

        try {
            String target = obj.get(position).getString("target");
            String marketName = obj.get(position).getJSONObject("market").getString("name");
            Double marketPrice = obj.get(position).getJSONObject("converted_last").getDouble("usd");
            market.setText(marketName);
            price.setText(marketPrice + " " +target);

            return listViewItem;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listViewItem;
    }
}
