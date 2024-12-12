package com.binoydipu.quickstock.views;

import static com.binoydipu.quickstock.constants.ConstantValues.ADMIN_EMAIL;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.binoydipu.quickstock.R;
import com.binoydipu.quickstock.services.auth.FirebaseAuthProvider;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    private Button btnLogout;
    private CardView cvStaffData;

    private FirebaseAuthProvider authProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnLogout = findViewById(R.id.logout_btn);
        cvStaffData = findViewById(R.id.view_staff_data_cv);
        authProvider = FirebaseAuthProvider.getInstance();

        checkIfLoggedIn();
        adjustLayout();
        
        cvStaffData.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, StaffListActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            if(authProvider.logOut()) {
                Toast.makeText(this, "Successfully Logged out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Could Not Logout User", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkIfLoggedIn() {
        if(!authProvider.isUserLoggedIn()) {
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            Log.d("HomeActivity", "Current user: " + authProvider.getCurrentUserEmail());
        }
    }

    // Some options are unavailable to staff, so filter those
    private void adjustLayout() {
        if(!authProvider.getCurrentUserEmail().equals(ADMIN_EMAIL)) {
            cvStaffData.setCardBackgroundColor(getResources().getColor(R.color.gray, getTheme()));
            cvStaffData.setEnabled(false);
        }
    }
}