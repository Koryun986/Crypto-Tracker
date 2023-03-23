package com.samsung.cryptotracker;

 import static com.samsung.cryptotracker.Constants.getArrayListFromJSONArray;

 import android.content.BroadcastReceiver;
 import android.content.Intent;
 import android.content.IntentFilter;
 import android.net.ConnectivityManager;
 import android.os.Bundle;

 import android.os.Handler;
 import android.util.Log;
 import android.view.MenuItem;
import android.view.View;
 import android.view.WindowManager;
 import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import android.widget.Spinner;
import android.widget.TextView;

 import androidx.annotation.NonNull;
 import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
 import androidx.fragment.app.Fragment;
 import androidx.fragment.app.FragmentTransaction;


 import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
 import com.google.android.gms.tasks.OnCompleteListener;
 import com.google.android.gms.tasks.Task;
 import com.google.android.material.bottomnavigation.BottomNavigationView;
 import com.google.android.material.navigation.NavigationBarView;
 import com.google.firebase.auth.FirebaseAuth;
 import com.google.firebase.auth.FirebaseUser;
 import com.google.firebase.database.DataSnapshot;
 import com.google.firebase.database.DatabaseError;
 import com.google.firebase.database.DatabaseReference;
 import com.google.firebase.database.FirebaseDatabase;
 import com.google.firebase.database.ValueEventListener;
 import com.google.firebase.messaging.FirebaseMessaging;
 import com.google.firebase.messaging.FirebaseMessagingService;
 import com.samsung.cryptotracker.Adapter.ListViewAdapter;

import com.samsung.cryptotracker.Exchange.ExchangedCurrency;
 import com.samsung.cryptotracker.MVVM.CurrencyInfoViewModel;
 import com.samsung.cryptotracker.Notification.NotificationSend;


 import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static String NOTIFICATION_LOW_PRICE (String id, Double price) {
        return "The price of " + id +" is under " + price + "$";
    }
    private static String NOTIFICATION_HIGH_PRICE (String id, Double price) {
        return "The price of " + id +" is over " + price + "$";
    }

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private String FCM_REGISTRATION_TOKEN = null ;
    CurrencyInfoViewModel notificationModel;
    BroadcastReceiver broadcastReceiver = null;
    ListView listView;
    BottomNavigationView navigationView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notificationModel = new CurrencyInfoViewModel(getApplication());

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        listView = findViewById(R.id.list_view);
        navigationView = findViewById(R.id.bottom_navigation_bar);
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference(Constants.FIREBASE_USERS);

        broadcastReceiver = new InternetReceiver();
        internetstatus();

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {

                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        FCM_REGISTRATION_TOKEN = token;

                    }
                });

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot userSnap = snapshot.child(user.getUid());
                if (!userSnap.exists()) {
                    User userClass = new User(user.getEmail());
                    userSnap.getRef().setValue(userClass);
                }else {
                    DataSnapshot notificationsSnap = snapshot.child(user.getUid()).child(Constants.FIREBASE_NOTIFICATIONS);
                    if (notificationsSnap.exists()){
                        String coins = "";
                        for (DataSnapshot snap : notificationsSnap.getChildren()){
                            coins += "," + snap.getKey();
                        }
                        notificationModel.loadCurrencyData(coins);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        notificationModel.getData().observe(this, data -> {
            if (data != null) {
                for (JSONObject obj : data){
                    try {
                        String id = obj.getString(Constants.CURRENCY_ID);
                        Double price = obj.getDouble(Constants.CURRENCY_PRICE);
                        DatabaseReference notificationRef = ref.child(user.getUid()).child(Constants.FIREBASE_NOTIFICATIONS).child(id).getRef();
                        notificationRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Double messagePrice = snapshot.child(Constants.FIREBASE_NOTIFICATIONS_HIGH).getValue(Double.class);
                                if (price >= messagePrice){
                                    NotificationSend.pushNotification(MainActivity.this,
                                            FCM_REGISTRATION_TOKEN,
                                            "Crypto Tracker",
                                                NOTIFICATION_HIGH_PRICE(id, messagePrice)
                                            );


                                }else if (price <= messagePrice){
                                    NotificationSend.pushNotification(MainActivity.this,
                                            FCM_REGISTRATION_TOKEN,
                                            "Crypto Tracker",
                                            NOTIFICATION_LOW_PRICE(id, messagePrice)
                                    );

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Fragment marketFragment = new MarketFragment();
        Fragment portfolioFragment = new PortfolioFragment();
        Fragment profileFragment = new ProfileFragment();



//        Fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment, marketFragment)
                .commit();


//        Navigation View
        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                switch (item.getItemId()) {
                    case R.id.market:
                        ft.replace(R.id.fragment, marketFragment);
                        ft.commit();
                        break;
                    case R.id.portfolio:
                        ft.replace(R.id.fragment, portfolioFragment);
                        ft.commit();
                        break;
                    case R.id.profile:
                        ft.replace(R.id.fragment, profileFragment);
                        ft.commit();
                }
                return false;
            }
        });


    }

    public void internetstatus(){
        registerReceiver(broadcastReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }


}