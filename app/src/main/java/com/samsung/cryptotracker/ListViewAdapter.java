package com.samsung.cryptotracker;


import android.content.Context;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.samsung.cryptotracker.DB.DBManager;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ListViewAdapter extends ArrayAdapter<JSONObject> {
    int listLayout;
    ArrayList<JSONObject> obj;
    Context context;
    private DBManager dbManager;

    public ListViewAdapter(Context context, int listLayout, int field, ArrayList<JSONObject> obj) {
        super(context, listLayout, field, obj);
        this.context = context;
        this.listLayout = listLayout;
        this.obj = obj;
        dbManager = new DBManager(context);
        dbManager.openDB();
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
        if (!obj.get(position).has("large")) {
            try {
                String name = obj.get(position).getString(Constants.CURRENCY_ID);
                id.setText(name);
                double change = obj.get(position).getDouble(Constants.CURRENCY_MARKET_CUP_CHANGE);
                Picasso.get().load(obj.get(position).getString(Constants.CURRENCY_IMAGE)).into(icon);
                coinName.setText(obj.get(position).getString(Constants.CURRENCY_NAME));
                coinPrice.setText(obj.get(position).getDouble(Constants.CURRENCY_PRICE) + "$");
                coinChange.setText(change + "%");
                coinChange.setTextColor(change >= 0 ? ContextCompat.getColor(context, R.color.green) : ContextCompat.getColor(context, R.color.red));
                List<String> list = dbManager.getFromDb();
                if (list.contains(name) && !list.isEmpty()) {
                    favCoin.setBackground(ActivityCompat.getDrawable(context, R.drawable.ic_baseline_favorite_24));
                } else {
                    favCoin.setBackground(ActivityCompat.getDrawable(context, R.drawable.ic_baseline_favorite_border_24));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            favCoin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = null;
                    try {
                        name = obj.get(position).getString(Constants.CURRENCY_ID);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    List<String> list = dbManager.getFromDb();
                    if (list.contains(name) && !list.isEmpty()) {
                        dbManager.deleteFromDB(name);
                        favCoin.setBackground(ActivityCompat.getDrawable(context, R.drawable.ic_baseline_favorite_border_24));
                    } else {
                        dbManager.insertToDB(name);
                        favCoin.setBackground(ActivityCompat.getDrawable(context, R.drawable.ic_baseline_favorite_24));
                    }

                }
            });

            return listViewItem;
        }else{try {
            String name = obj.get(position).getString(Constants.CURRENCY_ID);
            id.setText(name);
            Picasso.get().load(obj.get(position).getString("large")).into(icon);
            coinName.setText(obj.get(position).getString("name"));
            List<String> list = dbManager.getFromDb();
            if (list.contains(name) && !list.isEmpty()) {
                favCoin.setBackground(ActivityCompat.getDrawable(context, R.drawable.ic_baseline_favorite_24));
            } else {
                favCoin.setBackground(ActivityCompat.getDrawable(context, R.drawable.ic_baseline_favorite_border_24));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
            favCoin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = null;
                    try {
                        name = obj.get(position).getString(Constants.CURRENCY_ID);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    List<String> list = dbManager.getFromDb();
                    if (list.contains(name) && !list.isEmpty()) {
                        dbManager.deleteFromDB(name);
                        favCoin.setBackground(ActivityCompat.getDrawable(context, R.drawable.ic_baseline_favorite_border_24));
                    } else {
                        dbManager.insertToDB(name);
                        favCoin.setBackground(ActivityCompat.getDrawable(context, R.drawable.ic_baseline_favorite_24));
                    }

                }
            });

            return listViewItem;
        }
    }
}
