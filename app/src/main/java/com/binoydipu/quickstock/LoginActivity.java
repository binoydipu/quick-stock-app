package com.binoydipu.quickstock;

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

    public static final String TAG = "LoginActivity";

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
                loginWithFirebase(email, password);
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
            sendEmailVerification(mAuth.getCurrentUser());
        });
    }

    private void loginWithFirebase(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
                    if(task.isSuccessful()) {
                        Log.d(TAG, "loginUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        if(user != null && user.isEmailVerified()) {
                            Toast.makeText(getApplicationContext(), "Login Success!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Please Verify Your Email!", Toast.LENGTH_SHORT).show();
                            tvResendVerifyEmail.setVisibility(View.VISIBLE);
                        }
                    }
                    else {
                        Log.w(TAG, " "+ task.getException());
                        if(task.getException() instanceof FirebaseAuthInvalidUserException) {
                            Toast.makeText(getApplicationContext(), "User Does Not Exists!", Toast.LENGTH_SHORT).show();
                        } else if(task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(getApplicationContext(), "Invalid Credentials!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Login Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void sendEmailVerification(FirebaseUser user) {
        if (user != null && !user.isEmailVerified()) {
            user.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        mAuth.signOut();
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Verification Email Sent.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Error Sending Verification Email.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void clearSelections() {
        etEmail.setText("");
        etEmail.clearFocus();
        etPassword.setText("");
        etPassword.clearFocus();
    }
}