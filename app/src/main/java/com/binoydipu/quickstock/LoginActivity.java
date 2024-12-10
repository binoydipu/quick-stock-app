package com.binoydipu.quickstock;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;

    private Button btnLogin;

    private TextView tvRegister, tvForgotPassword;

    private static final Pattern emailPattern = Pattern.compile("^[a-z0-9]+@[a-z]+\\.[a-z.]{2,}$"); // abc@something.com
    private static final Pattern passwordPattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[^\\w\\s]).{6,32}$"); // aA@12345

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.email_et);
        etPassword = findViewById(R.id.password_et);
        btnLogin = findViewById(R.id.login_btn);
        tvRegister = findViewById(R.id.register_text);
        tvForgotPassword = findViewById(R.id.forgot_pass_text);

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
            } else if(!passwordPattern.matcher(password).matches()) {
                etPassword.setError("at least a lowercase, a uppercase, a digit and a special character required");
                etPassword.requestFocus();
            } else {
                // TODO: Login with email password
                Toast.makeText(this, "Hello " + email + ", " + password, Toast.LENGTH_SHORT).show();
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
    }
    private void clearSelections() {
        etEmail.setText("");
        etEmail.clearFocus();
        etPassword.setText("");
        etPassword.clearFocus();
    }
}