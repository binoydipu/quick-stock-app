package com.binoydipu.quickstock;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.binoydipu.quickstock.services.auth.FirebaseAuthProvider;

public class HomeActivity extends AppCompatActivity {

    private Button btnLogout;

    FirebaseAuthProvider authProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
//        toolbar.setNavigationOnClickListener(v -> {
//            startActivity(new Intent(UpdateProductActivity.this, AdminPanelActivity.class));
//            finish();
//        });

        btnLogout = findViewById(R.id.logout_btn);
        authProvider = FirebaseAuthProvider.getInstance();

        if(!authProvider.isUserLoggedIn()) {
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Welcome back", Toast.LENGTH_SHORT).show();
        }

        btnLogout.setOnClickListener(v -> {
            if(authProvider.logOut()) {
                Toast.makeText(this, "Successfully Logged Out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Could Not Logout User", Toast.LENGTH_SHORT).show();
            }
        });
    }
}