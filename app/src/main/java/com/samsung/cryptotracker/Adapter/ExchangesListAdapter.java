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

public class ExchangesListAdapter extends ArrayAdapter<JSONObject> {

    private String EXCHANGES_LOGO = "image";
    private String EXCHANGES_NAME = "name";




    int listLayout;
    ArrayList<JSONObject> obj;
    Context context;

    public ExchangesListAdapter(Context context, int listLayout, int field, ArrayList<JSONObject> obj) {
        super(context, listLayout, field, obj);
        this.context = context;
        this.listLayout = listLayout;
        this.obj = obj;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listViewItem = inflater.inflate(listLayout, null, false);
        ImageView logo = listViewItem.findViewById(R.id.exchanges_logo);
        TextView name =  listViewItem.findViewById(R.id.exchanges_name);

        try {
            Picasso.get().load(obj.get(position).getString(EXCHANGES_LOGO)).into(logo);
            name.setText(obj.get(position).getString(EXCHANGES_NAME));
            return listViewItem;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listViewItem;
    }
}
