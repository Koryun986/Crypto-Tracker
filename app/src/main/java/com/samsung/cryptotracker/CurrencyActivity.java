package com.samsung.cryptotracker;

import static com.samsung.cryptotracker.Constants.getArrayListFromJSONArray;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DecimalStyle;
import java.util.ArrayList;

public class CurrencyActivity extends AppCompatActivity {
    ImageView icon;
    TextView name;
    TextView price;
    String id;
    ArrayList<JSONObject> data;
    LineChart lineChart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency);
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            id = extra.getString("id");
        }
        icon = findViewById(R.id.coin_icon);
        name = findViewById(R.id.coin_name);
        price = findViewById(R.id.coin_price);
        lineChart = findViewById(R.id.line_chart);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(false);
        lineChartStyle();
        Loader loader = new Loader();
        loader.start();
    }

    class Loader extends Thread {
        @Override
        public void run() {
            super.run();
            String url = Constants.CURRENCY_URL(id);
            loadCurrencyData(url);
            loadChartData(Constants.CURRENCY_CHART(id, 30));
        }
    }

    private void lineChartStyle () {
        XAxis xAxis = lineChart.getXAxis();
        YAxis leftAxis = lineChart.getAxisLeft();
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setAxisMinimum(0);
        xAxis.setGranularityEnabled(true);
        rightAxis.setGridColor(ContextCompat.getColor(getApplicationContext(),R.color.gray));
        rightAxis.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.gray));
        leftAxis.setEnabled(false);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setEnabled(false);
        xAxis.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.gray));
        lineChart.setDescription(null);
        lineChart.animateX(3000);
        IMarker marker = new ChartMarker(getApplicationContext(), R.layout.custom_marker_view_layuot);
        lineChart.setMarker(marker);

    }

    private void lineDataSetStyle (LineDataSet set) {
        set.setColor(Color.rgb(217,95,20));
        set.setFillAlpha(110);
        set.setDrawCircles(false);
        set.setDrawCircleHole(false);
        set.setDrawValues(false);
        set.setDrawFilled(true);
        set.setLineWidth(1);
        set.setDrawHighlightIndicators(false);
        set.setFillColor(Color.rgb(217,95,20));
        set.setValueTextColor(R.color.white);
        set.isHighlightEnabled();
        set.setDrawHighlightIndicators(false);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setFillDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.line_chart_gradient));

    }

    private void loadChartData (String url) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            ArrayList<Entry> arr = getArr(jsonObject.getJSONArray("prices"));
                            LineDataSet set = new LineDataSet(arr,"Data");
                            ArrayList<ILineDataSet> dataSet = new ArrayList<>();
                            lineDataSetStyle(set);
                            dataSet.add(set);
                            LineData lineData = new LineData(dataSet);
                            lineChart.setData(lineData);
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
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }



    private void loadCurrencyData (String url) {
        ProgressBar progressBar = findViewById(R.id.currency_activity_progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            data = getArrayListFromJSONArray(jsonArray);
                            Picasso.get().load(data.get(0).getString(Constants.CURRENCY_IMAGE)).into(icon);
                            name.setText(data.get(0).getString(Constants.CURRENCY_NAME));
                            price.setText(data.get(0).getString(Constants.CURRENCY_PRICE) + "$");
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
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public static ArrayList<Entry> getArr(JSONArray jsonArray){
        ArrayList<Entry> aList = new ArrayList<>();
        try {
            if(jsonArray!= null){
                for(int i = 0; i< jsonArray.length();i++){

                    float date = Float.valueOf(jsonArray.getJSONArray(i).get(0).toString());
                    float value = Float.valueOf(jsonArray.getJSONArray(i).get(1).toString());

                    aList.add(new Entry(date,value));
                }

            }

        }catch (JSONException js){
            js.printStackTrace();
        }
        return aList;
    }


}