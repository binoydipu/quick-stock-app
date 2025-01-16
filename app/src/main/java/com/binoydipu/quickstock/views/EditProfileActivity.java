package com.binoydipu.quickstock.views;


import static com.binoydipu.quickstock.constants.RegexPatterns.namePattern;
import static com.binoydipu.quickstock.constants.RegexPatterns.passwordPattern;
import static com.binoydipu.quickstock.constants.RegexPatterns.phonePattern;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.binoydipu.quickstock.R;
import com.binoydipu.quickstock.services.auth.FirebaseAuthProvider;
import com.binoydipu.quickstock.services.cloud.FirebaseCloudStorage;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity {

    private TextInputEditText etName, etID, etEmail, etPassword, etMobile;
    private Button btnUpdateProfile;
    private ProgressBar progressBar;

    private FirebaseAuthProvider authProvider;
    private FirebaseCloudStorage cloudStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        etName = findViewById(R.id.name_et);
        etID = findViewById(R.id.id_et);
        etEmail = findViewById(R.id.email_et);
        etPassword = findViewById(R.id.password_et);
        etMobile = findViewById(R.id.mobile_et);
        btnUpdateProfile = findViewById(R.id.register_btn);
        progressBar = findViewById(R.id.progress_circular);
        authProvider = FirebaseAuthProvider.getInstance();
        cloudStorage = FirebaseCloudStorage.getInstance();

        setLayoutValues();

        btnUpdateProfile.setOnClickListener(v -> {
            String name = Objects.requireNonNull(etName.getText()).toString().trim();
            String id = Objects.requireNonNull(etID.getText()).toString().trim();
            String email = Objects.requireNonNull(etEmail.getText()).toString().trim();
            String password = Objects.requireNonNull(etPassword.getText()).toString().trim();
            String mobile = Objects.requireNonNull(etMobile.getText()).toString().trim();
            if (name.isEmpty()) {
                etName.setError("Required field!");
                etName.requestFocus();
            } else if(!namePattern.matcher(name).matches()) {
                etName.setError("Invalid name. Use letters, space, - and . only");
                etName.requestFocus();
            } else if (password.isEmpty()) {
                etPassword.setError("Required field!");
                etPassword.requestFocus();
            } else if(!passwordPattern.matcher(password).matches()) {
                etPassword.setError("at least a lowercase, a uppercase, a digit, a special character and length[6, 32] required");
                etPassword.requestFocus();
            } else if (mobile.isEmpty()) {
                etMobile.setError("Required field!");
                etMobile.requestFocus();
            } else if(!phonePattern.matcher(mobile).matches()) {
                etMobile.setError("Invalid Mobile Number");
                etMobile.requestFocus();
            } else {
                progressBar.setVisibility(View.VISIBLE);
                authProvider.isUserAuthenticated(password, isAuthenticated -> { // check if password is correct
                    if(isAuthenticated) {
                        cloudStorage.getUserByUserId(authProvider.getCurrentUserId(), user -> {
                            cloudStorage.updateUser(user.getUserId(), name, id, email, mobile,
                                    user.isEmailVerified(), user.isStaffVerified(), isUserUpdated -> {
                                        progressBar.setVisibility(View.GONE);
                                        if(isUserUpdated) {
                                            Toast.makeText(this, "Info Updated", Toast.LENGTH_SHORT).show();
                                            getOnBackPressedDispatcher().onBackPressed();
                                        } else {
                                            Toast.makeText(this, "An Error Occurred", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        });
                    } else {
                        progressBar.setVisibility(View.GONE);
                        etPassword.setError("Incorrect Password!");
                        etPassword.requestFocus();
                    }
                });
            }
        });
    }

    private void setLayoutValues() {
        cloudStorage.getUserByUserId(authProvider.getCurrentUserId(), user -> {
            if(user != null) {
                etName.setText(user.getUserName());
                etMobile.setText(user.getMobileNo());
                etID.setText(user.getStaffId());
                etEmail.setText(user.getUserEmail());
            } else {
                Toast.makeText(this, "Failed to retrieve user info", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Discard Change?")
                .setMessage("Are you sure you want to discard the changes?")
                .setIcon(R.drawable.quick_stock)
                .setPositiveButton("Yes", (dialog, which) -> {
                    super.onBackPressed();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }
}