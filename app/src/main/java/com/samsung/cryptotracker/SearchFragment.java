package com.samsung.cryptotracker;

import static com.samsung.cryptotracker.Constants.getArrayListFromJSONArray;

import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.samsung.cryptotracker.Adapter.ListViewAdapter;
import com.samsung.cryptotracker.DB.DBManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchFragment extends Fragment {


    public SearchFragment() {
    }

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    View view;
    SearchView searchView;
    ListView listView;
    ProgressBar progressBar;
    private DBManager dbManager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);
        searchView = view.findViewById(R.id.search_view);
        listView = view.findViewById(R.id.list_view);
        dbManager = new DBManager(getContext());
        progressBar = view.findViewById(R.id.progressBar);

        searchView.onActionViewExpanded();
        searchView.setIconified(false);
        searchView.clearFocus();



        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String name) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String name) {
                if(name.length() == 0){
                    listView.setEmptyView(view.findViewById(R.id.empty_list_view));
                }else{
                    loadJsonFromUrl(Constants.SEARCH_CURRENCY, name);
                }

                return true;
            }
        });
        return view;
    }

    private void loadJsonFromUrl(String url, String name) {
        final ProgressBar progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + name,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            ArrayList<JSONObject> item = getArrayListFromJSONArray(jsonObject.getJSONArray("coins"));
                            ListAdapter listAdapter = new ListViewAdapter(getContext(), R.layout.row, R.id.container, item);
                            listView.setAdapter(listAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                error.printStackTrace();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
        dbManager.closeDb();
    }
}