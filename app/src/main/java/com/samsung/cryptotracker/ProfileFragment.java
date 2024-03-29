package com.samsung.cryptotracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ProfileFragment extends Fragment {

    private static final String ALERT_MESSAGE = "Are you sure you want delete your account?";
    private static final String ALERT_TOAST_MESSAGE = "Deleted User Successfully!";
    private static final String ALERT_CANCEL = "Cancel";
    private static final String ALERT_DELETE_ACCOUNT = "Delete Account";

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    View view;
    TextView logOutBtn;
    TextView profileEmail;
    ConstraintLayout toFavorites;
    ConstraintLayout toExchanges;
    ConstraintLayout changePassword;
    TextView profileDelete;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();;
        ref = database.getReference(Constants.FIREBASE_USERS);

        view = inflater.inflate(R.layout.fragment_profile, container, false);
        logOutBtn = view.findViewById(R.id.log_out);
        profileEmail = view.findViewById(R.id.profile_email);
        toFavorites = view.findViewById(R.id.profile_to_favorites);
        toExchanges = view.findViewById(R.id.profile_exchanges);
        profileDelete = view.findViewById(R.id.profile_delete);
        changePassword = view.findViewById(R.id.profile_change_password);

        profileEmail.setText(user.getEmail());

        toFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FavoritesActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        toExchanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ExchangesActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ChangePassordActivity.class);
                startActivity(intent);
            }
        });

        profileDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getContext())
                        .setMessage(ALERT_MESSAGE)
                        .setCancelable(false)
                        .setPositiveButton(ALERT_DELETE_ACCOUNT, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(getActivity(), ALERT_TOAST_MESSAGE, Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                                        startActivity(intent);
                                        getActivity().finish();
                                    }
                                });
                            }
                        })
                        .setNegativeButton(ALERT_CANCEL, null)
                        .show();
            }
        });

        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        return view;
    }
}