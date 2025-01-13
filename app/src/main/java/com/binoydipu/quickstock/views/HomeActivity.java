package com.binoydipu.quickstock.views;

import static com.binoydipu.quickstock.constants.ConstantValues.ADMIN_EMAIL;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.binoydipu.quickstock.R;
import com.binoydipu.quickstock.services.auth.FirebaseAuthProvider;
import com.binoydipu.quickstock.utilities.dialogs.DialogHelper;
import com.binoydipu.quickstock.views.inventory.ManageInventoryActivity;
import com.binoydipu.quickstock.views.staff.StaffListActivity;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    private CardView cvStaffData, cvManageInventory, cvTrackSales, cvGenerateReports;

    private FirebaseAuthProvider authProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        cvStaffData = findViewById(R.id.view_staff_data_cv);
        cvManageInventory = findViewById(R.id.manage_inventory_cb);
        cvTrackSales = findViewById(R.id.track_sales_cv);
        cvGenerateReports = findViewById(R.id.generate_report_cv);
        authProvider = FirebaseAuthProvider.getInstance();

        checkIfLoggedIn();
        adjustLayout();
        
        cvStaffData.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, StaffListActivity.class);
            startActivity(intent);
        });
        cvManageInventory.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ManageInventoryActivity.class);
            startActivity(intent);
        });
        cvTrackSales.setOnClickListener(v -> {
            Toast.makeText(this, "Not Available Yet", Toast.LENGTH_SHORT).show();
        });
        cvGenerateReports.setOnClickListener(v -> {
            Toast.makeText(this, "Not Available Yet", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit App")
                .setMessage("Are you sure you want to exit?")
                .setIcon(R.drawable.quick_stock)
                .setPositiveButton("Yes", (dialog, which) -> {
                    super.onBackPressed();
                    finishAffinity();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.notification_menu) {
            startActivity(new Intent(this, NotificationActivity.class));
        } else if(id == R.id.profile_menu) {
            startActivity(new Intent(this, ProfileActivity.class));
        } else if(id == R.id.logout_menu) {
            DialogHelper.logoutDialog(this);
        } else if(id == R.id.about_menu) {
            startActivity(new Intent(this, AboutActivity.class));
        }
        return true;
    }

    /*
    private void logoutUser() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setIcon(R.drawable.quick_stock)
                .setPositiveButton("Yes", (dialog, which) -> {
                    if(authProvider.logOut()) {
                        Toast.makeText(this, "Logout Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Could Not Logout User", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }*/
}