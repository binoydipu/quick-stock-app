package com.binoydipu.quickstock.views;

import static com.binoydipu.quickstock.constants.ConstantValues.ADMIN_EMAIL;
import static com.binoydipu.quickstock.constants.RegexPatterns.emailPattern;
import static com.binoydipu.quickstock.constants.RegexPatterns.idPattern;
import static com.binoydipu.quickstock.constants.RegexPatterns.namePattern;
import static com.binoydipu.quickstock.constants.RegexPatterns.passwordPattern;
import static com.binoydipu.quickstock.constants.RegexPatterns.phonePattern;
import static com.binoydipu.quickstock.services.auth.AuthProviderConstants.ON_USER_CREATION_FAILURE;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.binoydipu.quickstock.R;
import com.binoydipu.quickstock.services.auth.FirebaseAuthProvider;
import com.binoydipu.quickstock.services.cloud.FirebaseCloudStorage;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etName, etID, etEmail, etPassword, etConfirmPassword, etMobile;
    private Button btnRegister;
    private TextView tvLogin;
    private ProgressBar progressBar;

    private FirebaseAuthProvider authProvider;
    private FirebaseCloudStorage cloudStorage;

    @Override
    protected void onStart() {
        super.onStart();
        // In case of user already logged in
        if(authProvider.isUserLoggedIn()) { // user != null
            if(authProvider.getCurrentUserEmail().equals(ADMIN_EMAIL) || authProvider.isUserEmailVerified()) { // user email is verified
                Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

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
        progressBar = findViewById(R.id.progress_circular);
        authProvider = FirebaseAuthProvider.getInstance();
        cloudStorage = FirebaseCloudStorage.getInstance();

        tvLogin.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

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
                etPassword.setError("at least a lowercase, a uppercase, a digit, a special character and length[6, 32] required");
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
            } else {
                progressBar.setVisibility(View.VISIBLE);

                authProvider.createUser(this, email, password, userStatus -> {
                    progressBar.setVisibility(View.GONE);

                    if (!userStatus.equals(ON_USER_CREATION_FAILURE)) { // on success - returns uId

                        cloudStorage.storeUserInfo(userStatus, name, id, email, mobile, authProvider.isUserEmailVerified(), isInfoStored -> {
                            getOnBackPressedDispatcher().onBackPressed();
                            authProvider.logOut();
//                            finish();
                        });
                    }
                });
            }
        });
    }
}