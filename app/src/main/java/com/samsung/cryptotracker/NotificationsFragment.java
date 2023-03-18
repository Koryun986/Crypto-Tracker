package com.samsung.cryptotracker;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.samsung.cryptotracker.MVVM.CurrencyInfoViewModel;

import org.json.JSONException;

import java.text.DecimalFormat;


public class NotificationsFragment extends Fragment {

    private static final String param = "id";
    private static final String firebaseNotifications = "notifications";
    private static final String minus20 = "-20%";
    private static final String minus10 = "-10%";
    private static final String minus5 = "-5%";
    private static final String plus5 = "5%";
    private static final String plus10 = "10%";
    private static final String plus20 = "20%";
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference ref;

    private String idParam;


    public NotificationsFragment() {
        // Required empty public constructor
    }

    public static NotificationsFragment newInstance(String param1) {
        NotificationsFragment fragment = new NotificationsFragment();
        Bundle args = new Bundle();
        args.putString(param, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idParam = getArguments().getString(param);
        }
    }

    View view;
    String id;
    Double currentPrice;
    EditText notificationPrice;
    TextView minus20Btn;
    TextView minus10Btn;
    TextView minus5Btn;
    TextView plus5Btn;
    TextView plus10Btn;
    TextView plus20Btn;
    TextView saveBtn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notifications, container, false);

        id = getArguments().getString(param);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference(Constants.FIREBASE_USERS);

        notificationPrice = view.findViewById(R.id.notification_price);
        saveBtn = view.findViewById(R.id.save_btn);
        minus20Btn = view.findViewById(R.id.notification_minus_20perc);
        minus10Btn = view.findViewById(R.id.notification_minus_10perc);
        minus5Btn = view.findViewById(R.id.notification_minus_5perc);
        plus5Btn = view.findViewById(R.id.notification_plus_5perc);
        plus10Btn = view.findViewById(R.id.notification_plus_10perc);
        plus20Btn = view.findViewById(R.id.notification_plus_20perc);

        CurrencyInfoViewModel currencyInfoViewModel = new CurrencyInfoViewModel(getActivity().getApplication());
        currencyInfoViewModel.getData().observe(getActivity(), data -> {
            try {
                if (data != null) {
                    currentPrice = data.get(0).getDouble(Constants.CURRENCY_PRICE);
                    notificationPrice.setText(currentPrice + "");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        currencyInfoViewModel.loadCurrencyData(id);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Double price = Double.valueOf(notificationPrice.getText().toString());
                ref.child(user.getUid()).child(firebaseNotifications).child(id).setValue(price);
            }
        });

        minus20Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPercent(minus20);
            }
        });
        
        minus10Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPercent(minus10);
            }
        });
        
        minus5Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPercent(minus5);
            }
        });

        plus5Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPercent(plus5);
            }
        });
        
        plus10Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPercent(plus10);
            }
        });
        
        plus20Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPercent(plus20);
            }
        });
        return view;
    }

    private void setPercent (String percent) {
        Double price;
        minus20Btn.setTextColor(getResources().getColor(R.color.white));
        minus10Btn.setTextColor(getResources().getColor(R.color.white));
        minus5Btn.setTextColor(getResources().getColor(R.color.white));
        plus5Btn.setTextColor(getResources().getColor(R.color.white));
        plus10Btn.setTextColor(getResources().getColor(R.color.white));
        plus20Btn.setTextColor(getResources().getColor(R.color.white));
        switch (percent) {
            case minus20:
                minus20Btn.setTextColor(getResources().getColor(R.color.red));
                price = currentPrice * 0.8;
                break; 
            case minus10:
                minus10Btn.setTextColor(getResources().getColor(R.color.red));
                price = currentPrice * 0.9;
                break;
            case minus5:
                minus5Btn.setTextColor(getResources().getColor(R.color.red));
                price = currentPrice * 0.95;
                break;
            case plus5:
                plus5Btn.setTextColor(getResources().getColor(R.color.green));
                price = currentPrice * 1.05;
                break;
            case plus10:
                plus10Btn.setTextColor(getResources().getColor(R.color.green));
                price = currentPrice * 1.1;
                break;
            case plus20:
                plus20Btn.setTextColor(getResources().getColor(R.color.green));     
                price = currentPrice * 1.2;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + percent);
        }
        DecimalFormat dec = new DecimalFormat("#0.0");
        notificationPrice.setText(dec.format(price));
    }
}