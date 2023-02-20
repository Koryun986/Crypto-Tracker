package com.samsung.cryptotracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class LoginActivity extends AppCompatActivity {

    ImageView eye;
    EditText email;
    EditText password;
    private Boolean isClosedEye = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        eye = findViewById(R.id.password_eye);
        email = findViewById(R.id.login_emial_edit_text);
        password = findViewById(R.id.login_password_edit_text);

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
    }
}