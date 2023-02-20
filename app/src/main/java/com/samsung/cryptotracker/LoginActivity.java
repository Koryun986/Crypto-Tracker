package com.samsung.cryptotracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    ImageView eye;
    EditText email;
    EditText password;
    TextView toSignUp;
    private Boolean isClosedEye = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        eye = findViewById(R.id.password_eye);
        email = findViewById(R.id.login_emial_edit_text);
        password = findViewById(R.id.login_password_edit_text);
        toSignUp = findViewById(R.id.create_account);

        eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isClosedEye){
                    eye.setImageResource(R.drawable.ic_eye_open);
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());;
                } else {
                    eye.setImageResource(R.drawable.ic_eye_closed);
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                isClosedEye = !isClosedEye;
            }
        });

        toSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}