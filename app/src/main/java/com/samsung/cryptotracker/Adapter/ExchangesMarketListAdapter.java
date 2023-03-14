package com.samsung.cryptotracker.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.samsung.cryptotracker.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ExchangesMarketListAdapter extends ArrayAdapter<JSONObject> {

    private String CURRENCY_NAME = "base";
    private String TARGET = "target";
    private String PRICE = "last";


    int listLayout;
    ArrayList<JSONObject> obj;
    Context context;

    public ExchangesMarketListAdapter(Context context, int listLayout, int field, ArrayList<JSONObject> obj) {
        super(context, listLayout, field, obj);
        this.context = context;
        this.listLayout = listLayout;
        this.obj = obj;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listViewItem = inflater.inflate(listLayout, null, false);
        TextView name =  listViewItem.findViewById(R.id.market_currency_name);
        TextView price =  listViewItem.findViewById(R.id.market_currency_price);

        try {
            name.setText(obj.get(position).getString(CURRENCY_NAME));
            price.setText(obj.get(position).getString(PRICE)+ " " + obj.get(position).get(TARGET));
            return listViewItem;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listViewItem;
    }
}
