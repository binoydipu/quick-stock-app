package com.binoydipu.quickstock.views;

import static com.binoydipu.quickstock.constants.ConstantValues.ADMIN_EMAIL;
import static com.binoydipu.quickstock.constants.RegexPatterns.emailPattern;
import static com.binoydipu.quickstock.services.auth.AuthProviderConstants.ON_LOGIN_PENDING_EMAIL_VERIFICATION;
import static com.binoydipu.quickstock.services.auth.AuthProviderConstants.ON_LOGIN_SUCCESSFUL;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.binoydipu.quickstock.R;
import com.binoydipu.quickstock.services.auth.FirebaseAuthProvider;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister, tvForgotPassword, tvResendVerifyEmail;
    private ProgressBar progressBar;

    private FirebaseAuthProvider authProvider;

    @Override
    protected void onStart() {
        super.onStart();
        // In case of user already logged in
        if(authProvider.isUserLoggedIn()) { // user != null
            if(authProvider.getCurrentUserEmail().equals(ADMIN_EMAIL) || authProvider.isUserEmailVerified()) { // user email is verified
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
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
        authProvider = FirebaseAuthProvider.getInstance();

        btnLogin.setOnClickListener(v -> {
            String email = Objects.requireNonNull(etEmail.getText()).toString().trim();
            String password = Objects.requireNonNull(etPassword.getText()).toString().trim();

            if(checkIfAdmin(email, password)) return;

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

                authProvider.logIn(this, email, password, loginStatus -> {
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
            String email = Objects.requireNonNull(etEmail.getText()).toString().trim();
            if (email.isEmpty()) {
                etEmail.setError("Required field!");
                etEmail.requestFocus();
            } else if(!emailPattern.matcher(email).matches()) {
                etEmail.setError("Invalid Email");
                etEmail.requestFocus();
            } else {
                authProvider.sendPasswordResetEmail(this, email);
            }
        });

        tvResendVerifyEmail.setOnClickListener(v -> authProvider.sendEmailVerification(this));
    }

    private boolean checkIfAdmin(@NonNull String email, String password) {
        if (email.isEmpty()) {
            etEmail.setError("Required field!");
            etEmail.requestFocus();
        } else if (password.isEmpty()) {
            etPassword.setError("Required field!");
            etPassword.requestFocus();
        } else if(email.equals(ADMIN_EMAIL)) {
            authProvider.logIn(this, email, password, loginStatus -> {
                if(loginStatus.equals(ON_LOGIN_SUCCESSFUL)) {
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            return true;
        }
        return false;
    }

    private void clearSelections() {
        etEmail.setText("");
        etEmail.clearFocus();
        etPassword.setText("");
        etPassword.clearFocus();
    }
}