package com.samsung.cryptotracker;

import static com.samsung.cryptotracker.Constants.getArrayListFromJSONArray;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.samsung.cryptotracker.Adapter.CurrencyMarketPriceAdapter;
import com.samsung.cryptotracker.Adapter.ListViewAdapter;
import com.samsung.cryptotracker.Chart.ChartMarker;
import com.samsung.cryptotracker.MVVM.ChartViewModel;
import com.samsung.cryptotracker.MVVM.CurrencyInfoViewModel;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class CurrencyChartFragment extends Fragment {


    private static final String ARG_PARAM = "id";
    private static final String ALERT_MESSAGE = "Oops Page Note Found";
    private static final String ALERT_CANCEL = "Cancel";
    private static final String CHART_LABEL = "Data";

    private String mParam1;

    public CurrencyChartFragment() {
        // Required empty public constructor
    }


    public static CurrencyChartFragment newInstance(String param) {
        CurrencyChartFragment fragment = new CurrencyChartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM);
        }
    }

    private int defaultChartDays = 30;

    View view;

    CurrencyInfoViewModel currencyInfoViewModel;
    ChartViewModel chartViewModel;

    TextView price;
    String id;
    ArrayList<JSONObject> data;
    LineChart lineChart;
    TextView button1Day;
    TextView button7Day;
    TextView button1Month;
    TextView button3Month;

    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_currency_chart, container, false);

        currencyInfoViewModel = new CurrencyInfoViewModel(getActivity().getApplication());
        chartViewModel = new ChartViewModel(getActivity().getApplication());

        id = getArguments().getString(ARG_PARAM);
        progressBar = view.findViewById(R.id.currency_activity_progress_bar);
        button1Day = view.findViewById(R.id.chart_1d);
        button7Day = view.findViewById(R.id.chart_7d);
        button1Month = view.findViewById(R.id.chart_1m);
        button3Month = view.findViewById(R.id.chart_3m);
        price = view.findViewById(R.id.coin_price);
        lineChart = view.findViewById(R.id.line_chart);


        currencyInfoViewModel.isInfoLoaded().observe(getActivity(), isLoaded -> {
            if (isLoaded) {
                progressBar.setVisibility(View.VISIBLE);
            }else {
                progressBar.setVisibility(View.GONE);
            }
        });



        currencyInfoViewModel.getData().observe(getActivity(), data -> {
            if (data != null) {
                try {
                    price.setText(data.get(0).getString(Constants.CURRENCY_PRICE) + "$");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getContext()).
                                setMessage(ALERT_MESSAGE).
                                setPositiveButton(ALERT_CANCEL, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        getActivity().finish();
                                    }
                                });
                builder.create().show();
            }
        });

        currencyInfoViewModel.loadCurrencyData(id);

        chartViewModel.isChartLoaded().observe(getActivity(), isLoaded -> {
            if (isLoaded){
                progressBar.setVisibility(View.VISIBLE);
            }else {
                progressBar.setVisibility(View.GONE);
            }
        });

        chartViewModel.getData().observe(getActivity(), data-> {
            LineDataSet set = new LineDataSet(data,CHART_LABEL);
            ArrayList<ILineDataSet> dataSet = new ArrayList<>();
            lineDataSetStyle(set);
            dataSet.add(set);
            LineData lineData = new LineData(dataSet);
            lineChart.setData(lineData);
            lineChartStyle();
        });

        chartViewModel.loadChartData(id, defaultChartDays);

        button1Day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chartDayButtonClickListener(view, 1);
            }
        });
        button7Day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chartDayButtonClickListener(view, 7);
            }
        });
        button1Month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chartDayButtonClickListener(view, 30);
            }
        });
        button3Month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chartDayButtonClickListener(view, 90);
            }
        });


        return view;

    }



    private void lineChartStyle () {
        XAxis xAxis = lineChart.getXAxis();
        YAxis leftAxis = lineChart.getAxisLeft();
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setAxisMinimum(0);
        xAxis.setGranularityEnabled(true);
        rightAxis.setGridColor(ContextCompat.getColor(getContext(),R.color.gray));
        rightAxis.setTextColor(ContextCompat.getColor(getContext(),R.color.gray));
        leftAxis.setEnabled(false);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new TimeValueFormatter());
        xAxis.setTextColor(ContextCompat.getColor(getContext(),R.color.gray));
        lineChart.setDescription(null);
        lineChart.animateX(2000);
        IMarker marker = new ChartMarker(getContext(), R.layout.custom_marker_view_layuot);
        lineChart.setMarker(marker);
        lineChart.setFocusable(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(false);
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
        set.setFillDrawable(ContextCompat.getDrawable(getContext(),R.drawable.line_chart_gradient));

    }


    public void chartDayButtonClickListener(View view, int days) {

        button1Day.setTextColor(ContextCompat.getColor(getContext(),R.color.blue_price));
        button7Day.setTextColor(ContextCompat.getColor(getContext(),R.color.blue_price));
        button1Month.setTextColor(ContextCompat.getColor(getContext(),R.color.blue_price));
        button3Month.setTextColor(ContextCompat.getColor(getContext(),R.color.blue_price));

        switch(days) {
            case 1:
                button1Day.setTextColor(ContextCompat.getColor(getContext(),R.color.orange));
                break;
            case 7:
                button7Day.setTextColor(ContextCompat.getColor(getContext(),R.color.orange));
                break;
            case 30:
                button1Month.setTextColor(ContextCompat.getColor(getContext(),R.color.orange));
                break;
            case 90:
                button3Month.setTextColor(ContextCompat.getColor(getContext(),R.color.orange));
                break;
        }

        chartViewModel.loadChartData(id, days);
    }


    private class TimeValueFormatter extends ValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
            String formatedDate = sdf.format(new java.util.Date((long) value));
            return formatedDate.substring(5,10);
        }
    }



}