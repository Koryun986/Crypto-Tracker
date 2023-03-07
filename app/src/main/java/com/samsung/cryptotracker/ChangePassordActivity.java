package com.samsung.cryptotracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassordActivity extends AppCompatActivity {
    private Boolean isClosedEyePassword = false;
    private Boolean isClosedEyeConfirmPassword = false;

    private FirebaseAuth auth;
    private FirebaseUser user;

    EditText password;
    EditText confirmPassword;
    ImageView eyeIconPassword;
    ImageView eyeIconConfirmPassword;
    TextView changePasswordBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_passord);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        password = findViewById(R.id.change_password);
        confirmPassword = findViewById(R.id.confirm_password);
        eyeIconPassword = findViewById(R.id.password_eye1);
        eyeIconConfirmPassword = findViewById(R.id.password_eye2);
        changePasswordBtn = findViewById(R.id.change_password_btn);

        eyeIconPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isClosedEyePassword){
                    eyeIconPassword.setImageResource(R.drawable.ic_eye_open);
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());;
                } else {
                    eyeIconPassword.setImageResource(R.drawable.ic_eye_closed);
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                isClosedEyePassword = !isClosedEyePassword;
            }
        });

        eyeIconConfirmPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isClosedEyeConfirmPassword){
                    eyeIconConfirmPassword.setImageResource(R.drawable.ic_eye_open);
                    confirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());;
                } else {
                    eyeIconConfirmPassword.setImageResource(R.drawable.ic_eye_closed);
                    confirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                isClosedEyeConfirmPassword = !isClosedEyeConfirmPassword;
            }
        });

        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String passwordText = String.valueOf(password.getText());
                String confirmPasswordText = String.valueOf(confirmPassword.getText());

                if (TextUtils.isEmpty(passwordText) || TextUtils.isEmpty(confirmPasswordText)){
                    Toast.makeText(getApplicationContext(), "Please enter password", Toast.LENGTH_SHORT).show();
                }else if (!passwordText.equals(confirmPasswordText)){
                    Toast.makeText(getApplicationContext(), "Confirm Password is wrong", Toast.LENGTH_SHORT).show();
                }else {
                    user.updatePassword(passwordText)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Password Changed!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(ChangePassordActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                }
            }
        });
    }
}