package com.binoydipu.quickstock;

import static com.binoydipu.quickstock.constants.ConstantValues.ON_LOGIN_PENDING_EMAIL_VERIFICATION;
import static com.binoydipu.quickstock.constants.ConstantValues.ON_LOGIN_SUCCESSFUL;
import static com.binoydipu.quickstock.constants.RegexPatterns.emailPattern;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.binoydipu.quickstock.services.auth.FirebaseAuthProvider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister, tvForgotPassword, tvResendVerifyEmail;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseAuthProvider authProvider;

    @Override
    protected void onStart() { // already logged in
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null && currentUser.isEmailVerified()) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.email_et);
        etPassword = findViewById(R.id.password_et);
        btnLogin = findViewById(R.id.login_btn);
        tvRegister = findViewById(R.id.register_text);
        tvForgotPassword = findViewById(R.id.forgot_pass_text);
        tvResendVerifyEmail = findViewById(R.id.resend_ver_email_text);
        progressBar = findViewById(R.id.progress_circular);
        mAuth = FirebaseAuth.getInstance();
        authProvider = FirebaseAuthProvider.getInstance();

        btnLogin.setOnClickListener(v -> {
            String email = Objects.requireNonNull(etEmail.getText()).toString().trim();
            String password = Objects.requireNonNull(etPassword.getText()).toString().trim();
            if (email.isEmpty()) {
                etEmail.setError("Required field!");
                etEmail.requestFocus();
            } else if(!emailPattern.matcher(email).matches()) {
                etEmail.setError("Invalid Email");
                etEmail.requestFocus();
            } else if (password.isEmpty()) {
                etPassword.setError("Required field!");
                etPassword.requestFocus();
            } else {
                progressBar.setVisibility(View.VISIBLE);
                authProvider.loginWithFirebase(this, email, password, loginStatus -> {
                    progressBar.setVisibility(View.GONE);
                    if(loginStatus.equals(ON_LOGIN_SUCCESSFUL)) {
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else if(loginStatus.equals(ON_LOGIN_PENDING_EMAIL_VERIFICATION)) {
                        tvResendVerifyEmail.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            clearSelections();
        });
        tvForgotPassword.setOnClickListener(v -> {
            Toast.makeText(this, "Under Work", Toast.LENGTH_SHORT).show();
            clearSelections();
        });
        tvResendVerifyEmail.setOnClickListener(v -> {
            mAuth = FirebaseAuth.getInstance();
            authProvider.sendEmailVerification(this);
        });
    }

    private void clearSelections() {
        etEmail.setText("");
        etEmail.clearFocus();
        etPassword.setText("");
        etPassword.clearFocus();
    }
}