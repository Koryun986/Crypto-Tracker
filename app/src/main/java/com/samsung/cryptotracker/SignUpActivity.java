package com.samsung.cryptotracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

public class SignUpActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 1000;
    private final static String PUBLIC_PROFILE = "public_profile";
    private final static String AUTH_FAIL = "Authentication failed.";
    private Boolean isClosedEye = false;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;

    ImageView eye;
    EditText emailEditText;
    EditText passwordEditText;
    TextView toLogin;
    TextView signUpBtn;
    ProgressBar progressBar;
    LinearLayout googleSignUp;
    LinearLayout facebookSignUp;


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
        mCallbackManager = CallbackManager.Factory.create();
        mAuth = FirebaseAuth.getInstance();

        eye = findViewById(R.id.password_eye);
        emailEditText = findViewById(R.id.sign_up_emial_edit_text);
        passwordEditText = findViewById(R.id.sign_up_password_edit_text);
        toLogin = findViewById(R.id.to_login);
        signUpBtn = findViewById(R.id.sign_up_btn);
        progressBar = findViewById(R.id.progress_bar);
        googleSignUp = findViewById(R.id.google_signup);
        facebookSignUp = findViewById(R.id.facebook_signup);

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
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (password.length() <= 6) {
                    Toast.makeText(SignUpActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(SignUpActivity.this, "User registered successfully. Please verify your email",
                                                        Toast.LENGTH_SHORT).show();
                                                if (mAuth.getCurrentUser().isEmailVerified()) {
                                                    authSuccess();
                                                }else {
                                                    Toast.makeText(SignUpActivity.this, "Please verify your email",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                    });

                                } else {
                                    Toast.makeText(SignUpActivity.this, AUTH_FAIL,
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
        });

        createRequest();

        googleSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });

        facebookSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(SignUpActivity.this,Arrays.asList(PUBLIC_PROFILE));
                LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException error) {

                    }
                });
            }
        });
    }
    private void createRequest() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signUp() {
        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = accountTask.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            authSuccess();
                        }else {
                            String message = "Sorry something went wrong";
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();;
                        }
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            authSuccess();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext(), AUTH_FAIL,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void authSuccess() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}