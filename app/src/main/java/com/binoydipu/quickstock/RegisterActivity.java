package com.binoydipu.quickstock;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etName, etID, etEmail, etPassword, etConfirmPassword, etMobile;
    private Button btnRegister;
    private TextView tvLogin;
    private RadioButton adminRadioButton, staffRadioButton;

    private static final Pattern namePattern = Pattern.compile("^[a-zA-Z .-]+$"); // letters, space, dot, dash
    private static final Pattern idPattern = Pattern.compile("^[0-9]{5}$"); // 5 digits number
    private static final Pattern emailPattern = Pattern.compile("^[a-z0-9]+@[a-z]+\\.[a-z.]{2,}$"); // abc@something.com
    private static final Pattern passwordPattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[^\\w\\s]).{6,32}$"); // aA@12345
    private static final Pattern phonePattern = Pattern.compile("^(\\+88)?01[2-9][0-9]{8}$"); // (+88) 01 7 12345678

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.name_et);
        etID = findViewById(R.id.id_et);
        etEmail = findViewById(R.id.email_et);
        etPassword = findViewById(R.id.password_et);
        etConfirmPassword = findViewById(R.id.confirm_password_et);
        etMobile = findViewById(R.id.mobile_et);
        btnRegister = findViewById(R.id.register_btn);
        tvLogin = findViewById(R.id.login_text);
        adminRadioButton = findViewById(R.id.as_admin_rb);
        staffRadioButton = findViewById(R.id.as_staff_rb);

        btnRegister.setOnClickListener(v -> {
            String name = Objects.requireNonNull(etName.getText()).toString().trim();
            String id = Objects.requireNonNull(etID.getText()).toString().trim();
            String email = Objects.requireNonNull(etEmail.getText()).toString().trim();
            String password = Objects.requireNonNull(etPassword.getText()).toString().trim();
            String confirmPassword = Objects.requireNonNull(etConfirmPassword.getText()).toString().trim();
            String mobile = Objects.requireNonNull(etMobile.getText()).toString().trim();
            if (name.isEmpty()) {
                etName.setError("Required field!");
                etName.requestFocus();
            } else if(!namePattern.matcher(name).matches()) {
                etName.setError("Invalid name. Use letters, space, - and . only");
                etName.requestFocus();
            } else if (id.isEmpty()) {
                etID.setError("Required field!");
                etID.requestFocus();
            } else if(!idPattern.matcher(id).matches()) {
                etID.setError("Invalid ID");
                etID.requestFocus();
            } else if (email.isEmpty()) {
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
            } else if (confirmPassword.isEmpty()) {
                etConfirmPassword.setError("Required field!");
                etConfirmPassword.requestFocus();
            } else if(!confirmPassword.equals(password)) {
                etConfirmPassword.setError("Passwords does not match");
                etConfirmPassword.requestFocus();
            } else if (mobile.isEmpty()) {
                etMobile.setError("Required field!");
                etMobile.requestFocus();
            } else if(!phonePattern.matcher(mobile).matches()) {
                etMobile.setError("Invalid Mobile Number");
                etMobile.requestFocus();
            } else if(!adminRadioButton.isChecked() && !staffRadioButton.isChecked()) {
                Toast.makeText(this, "Select Your Position", Toast.LENGTH_SHORT).show();
            } else {
                String position = adminRadioButton.isChecked() ? adminRadioButton.getText().toString() : staffRadioButton.getText().toString();
                Toast.makeText(this, "Hello: " + name + " " + position, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                clearSelections();
            }
        });
        tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            clearSelections();
        });
    }

    private void clearSelections() {
        etName.setText("");
        etID.setText("");
        etEmail.setText("");
        etPassword.setText("");
        etConfirmPassword.setText("");
        etMobile.setText("");
        adminRadioButton.setChecked(false);
        staffRadioButton.setChecked(false);
        etName.clearFocus();
        etEmail.clearFocus();
        etID.clearFocus();
        etPassword.clearFocus();
        etConfirmPassword.clearFocus();
        etMobile.clearFocus();
    }
}