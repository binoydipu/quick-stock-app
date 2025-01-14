package com.binoydipu.quickstock.views.reports;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.binoydipu.quickstock.R;
import com.binoydipu.quickstock.utilities.dialogs.DialogHelper;
import com.binoydipu.quickstock.views.AboutActivity;
import com.binoydipu.quickstock.views.NotificationActivity;
import com.binoydipu.quickstock.views.ProfileActivity;

import java.util.Objects;

public class GenerateReportsActivity extends AppCompatActivity {

    private ImageView ivToolbarBack;
    private CardView cvSalesReport, cvInventoryReport, cvProfitAndLoss, cvDayBook, cvBalanceSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_reports);

        Toolbar toolbar = findViewById(R.id.toolbar);
        ivToolbarBack = findViewById(R.id.toolbar_back_btn);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        ivToolbarBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        cvSalesReport = findViewById(R.id.sales_report_cv);
        cvInventoryReport = findViewById(R.id.inventory_report_cv);
        cvProfitAndLoss = findViewById(R.id.profit_loss_cv);
        cvDayBook = findViewById(R.id.day_book_cv);
        cvBalanceSheet = findViewById(R.id.balance_sheet_cv);

        cvSalesReport.setOnClickListener(v -> startActivity(new Intent(this, SalesReportActivity.class)));
        cvInventoryReport.setOnClickListener(v -> startActivity(new Intent(this, InventoryReportActivity.class)));
        cvProfitAndLoss.setOnClickListener(v -> startActivity(new Intent(this, ProfitLossReportActivity.class)));
        cvDayBook.setOnClickListener(v -> Toast.makeText(this, "Not Yet Available", Toast.LENGTH_SHORT).show());
        cvBalanceSheet.setOnClickListener(v -> Toast.makeText(this, "Upgrade to Premium", Toast.LENGTH_SHORT).show());
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
}