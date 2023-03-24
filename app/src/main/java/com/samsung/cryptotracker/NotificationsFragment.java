package com.samsung.cryptotracker;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.samsung.cryptotracker.MVVM.CurrencyInfoViewModel;

import org.json.JSONException;

import java.text.DecimalFormat;


public class NotificationsFragment extends Fragment {

    private static final String TOAST_MESSAGE = "Please enter price";
    private static final String TOAST_MESSAGE_LOW_PRICE = "Please enter lower price then the current one";
    private static final String TOAST_MESSAGE_HIGH_PRICE = "Please enter higher price then the current one";

    private static final String param = "id";
    private static final String minus20 = "-20%";
    private static final String minus10 = "-10%";
    private static final String minus5 = "-5%";
    private static final String plus5 = "5%";
    private static final String plus10 = "10%";
    private static final String plus20 = "20%";
    private static final String formatPattern = "#0.0";

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
    LinearLayout highBtn;
    LinearLayout lowBtn;
    LinearLayout highPriceLayout;
    LinearLayout lowPriceLayout;
    TextView highPrice;
    TextView lowPrice;
    ImageView deleteHighPrice;
    ImageView deleteLowerPrice;
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
        highPriceLayout = view.findViewById(R.id.current_high_notification);
        lowPriceLayout = view.findViewById(R.id.current_low_notification);
        highPrice = view.findViewById(R.id.current_high_price);
        lowPrice = view.findViewById(R.id.current_low_price);
        highBtn = view.findViewById(R.id.high_btn);
        lowBtn = view.findViewById(R.id.low_btn);
        deleteHighPrice = view.findViewById(R.id.notification_delete_higher_price);
        deleteLowerPrice = view.findViewById(R.id.notification_delete_lower_price);
        minus20Btn = view.findViewById(R.id.notification_minus_20perc);
        minus10Btn = view.findViewById(R.id.notification_minus_10perc);
        minus5Btn = view.findViewById(R.id.notification_minus_5perc);
        plus5Btn = view.findViewById(R.id.notification_plus_5perc);
        plus10Btn = view.findViewById(R.id.notification_plus_10perc);
        plus20Btn = view.findViewById(R.id.notification_plus_20perc);

        deleteHighPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ref.child(user.getUid()).child(Constants.FIREBASE_NOTIFICATIONS).child(id).child(Constants.FIREBASE_NOTIFICATIONS_HIGH).removeValue();
            }
        });
        deleteLowerPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ref.child(user.getUid()).child(Constants.FIREBASE_NOTIFICATIONS).child(id).child(Constants.FIREBASE_NOTIFICATIONS_LOW).removeValue();
            }
        });

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot notificationSnap = snapshot.child(user.getUid()).child(Constants.FIREBASE_NOTIFICATIONS).child(id);
                if (notificationSnap.exists()) {
                    if (notificationSnap.child(Constants.FIREBASE_NOTIFICATIONS_HIGH).exists()){
                        highPriceLayout.setVisibility(View.VISIBLE);
                        highPrice.setText(notificationSnap.child(Constants.FIREBASE_NOTIFICATIONS_HIGH).getValue(Double.class).toString());
                    }else {
                        highPriceLayout.setVisibility(View.GONE);
                    }
                    if (notificationSnap.child(Constants.FIREBASE_NOTIFICATIONS_LOW).exists()){
                        lowPriceLayout.setVisibility(View.VISIBLE);
                        lowPrice.setText(notificationSnap.child(Constants.FIREBASE_NOTIFICATIONS_LOW).getValue(Double.class).toString());
                    }else {
                        lowPriceLayout.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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

        highBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(notificationPrice.getText().toString())) {
                    Double price = Double.valueOf(notificationPrice.getText().toString());
                    if (price <= currentPrice) {
                        Toast.makeText(getContext(), TOAST_MESSAGE_HIGH_PRICE, Toast.LENGTH_SHORT).show();
                    }else {
                        ref.child(user.getUid()).child(Constants.FIREBASE_NOTIFICATIONS).child(id).child(Constants.FIREBASE_NOTIFICATIONS_HIGH).setValue(price);
                        }
                }else {
                    Toast.makeText(getContext(), TOAST_MESSAGE, Toast.LENGTH_SHORT).show();
                }
            }
        });

        lowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(notificationPrice.getText().toString())) {
                    Double price = Double.valueOf(notificationPrice.getText().toString());
                    if (price >= currentPrice) {
                        Toast.makeText(getContext(), TOAST_MESSAGE_LOW_PRICE, Toast.LENGTH_SHORT).show();
                    }else {
                        ref.child(user.getUid()).child(Constants.FIREBASE_NOTIFICATIONS).child(id).child(Constants.FIREBASE_NOTIFICATIONS_LOW).setValue(price);
                    }
                } else {
                    Toast.makeText(getContext(), TOAST_MESSAGE, Toast.LENGTH_SHORT).show();
                }
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
        DecimalFormat dec = new DecimalFormat(formatPattern);
        notificationPrice.setText(dec.format(price));
    }
}