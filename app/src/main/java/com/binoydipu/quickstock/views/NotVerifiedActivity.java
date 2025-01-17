package com.binoydipu.quickstock.views;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.binoydipu.quickstock.R;
import com.binoydipu.quickstock.services.auth.FirebaseAuthProvider;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class NotVerifiedActivity extends AppCompatActivity {

    private ImageView ivToolbarBack;
    private MaterialButton btnCancleRegistration;
    private TextInputLayout tilPassword;
    private TextInputEditText etPassword;
    private ProgressBar progressBar;
    private FirebaseAuthProvider authProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_verified);

        Toolbar toolbar = findViewById(R.id.toolbar);
        ivToolbarBack = findViewById(R.id.toolbar_back_btn);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        authProvider = FirebaseAuthProvider.getInstance();

        ivToolbarBack.setOnClickListener(v -> {
            authProvider.logOut();
            getOnBackPressedDispatcher().onBackPressed();
        });

        btnCancleRegistration = findViewById(R.id.cancle_reg_btn);
        tilPassword = findViewById(R.id.password_textInputLayout);
        etPassword = findViewById(R.id.password_et);
        progressBar = findViewById(R.id.progress_circular);

        btnCancleRegistration.setOnClickListener(v -> {
            tilPassword.setVisibility(View.VISIBLE);
            String password = Objects.requireNonNull(etPassword.getText()).toString().trim();
            
            if (password.isEmpty()) {
                etPassword.setError("Required field!");
                etPassword.requestFocus();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Cancel Registration")
                        .setMessage("Are you sure you want to cancel the registration? Your account will be deleted.")
                        .setIcon(R.drawable.quick_stock)
                        .setPositiveButton("Yes", (dialog, which) -> {
                            progressBar.setVisibility(View.VISIBLE);
                            authProvider.deleteUser(this, authProvider.getCurrentUserId(), password, isUserDeleted -> {
                                progressBar.setVisibility(View.GONE);
                                if(isUserDeleted) {
                                    Toast.makeText(this, "Registration Canceled", Toast.LENGTH_SHORT).show();
                                    getOnBackPressedDispatcher().onBackPressed();
                                }
                            });
                        })
                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        });
    }
}