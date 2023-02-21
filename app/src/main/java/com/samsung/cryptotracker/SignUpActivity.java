package com.samsung.cryptotracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {
    private Boolean isClosedEye = false;
    private FirebaseAuth mAuth;
    ImageView eye;
    EditText emailEditText;
    EditText passwordEditText;
    TextView toLogin;
    TextView signUpBtn;
    ProgressBar progressBar;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        eye = findViewById(R.id.password_eye);
        emailEditText = findViewById(R.id.sign_up_emial_edit_text);
        passwordEditText = findViewById(R.id.sign_up_password_edit_text);
        toLogin = findViewById(R.id.to_login);
        signUpBtn = findViewById(R.id.sign_up_btn);
        progressBar = findViewById(R.id.progress_bar);

        eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isClosedEye){
                    eye.setImageResource(R.drawable.ic_eye_open);
                    passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());;
                } else {
                    eye.setImageResource(R.drawable.ic_eye_closed);
                    passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                isClosedEye = !isClosedEye;
            }
        });

        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password;
                email = String.valueOf(emailEditText.getText());
                password = String.valueOf(passwordEditText.getText());

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(SignUpActivity.this, "Please enter email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(SignUpActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {

                                } else {
                                    Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
        });
    }
}