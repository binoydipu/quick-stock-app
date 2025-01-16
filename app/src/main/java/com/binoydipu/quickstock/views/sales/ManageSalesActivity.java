package com.binoydipu.quickstock.views.sales;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.binoydipu.quickstock.R;
import com.binoydipu.quickstock.services.cloud.FirebaseCloudStorage;
import com.binoydipu.quickstock.utilities.dialogs.DialogHelper;
import com.binoydipu.quickstock.views.AboutActivity;
import com.binoydipu.quickstock.views.NotificationActivity;
import com.binoydipu.quickstock.views.ProfileActivity;
import com.binoydipu.quickstock.views.reports.ProfitLossReportActivity;
import com.binoydipu.quickstock.views.reports.SalesReportActivity;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;

public class ManageSalesActivity extends AppCompatActivity {

    private ExtendedFloatingActionButton fbAddNewSale;
    private EditText etSearchKeyword;
    private ImageView ivClearSearch, ivSearchStaff, ivToolbarBack;
    private LinearLayout salesReport, profitAndLoss, onlineStore;

    private ProgressBar progressBar;
    private RecyclerView rvSalesList;

    private FirebaseCloudStorage cloudStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_sales);

        Toolbar toolbar = findViewById(R.id.toolbar);
        ivToolbarBack = findViewById(R.id.toolbar_back_btn);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        fbAddNewSale = findViewById(R.id.add_new_sale_fb);
        progressBar = findViewById(R.id.progress_circular);
        rvSalesList = findViewById(R.id.sales_recyclerview);
        salesReport = findViewById(R.id.sales_report_ll);
        profitAndLoss = findViewById(R.id.profit_loss_ll);
        onlineStore = findViewById(R.id.online_store_ll);
        etSearchKeyword = findViewById(R.id.search_keywords_et);
        ivClearSearch = findViewById(R.id.clear_search_text_iv);
        ivSearchStaff = findViewById(R.id.search_staff_btn);
        cloudStorage = FirebaseCloudStorage.getInstance();

        onlineStore.setOnClickListener(v -> Toast.makeText(this, "Not Available Yet", Toast.LENGTH_SHORT).show());

        ivSearchStaff.setOnClickListener(v -> {
            displaySalesList();
            etSearchKeyword.clearFocus();
            // to close keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etSearchKeyword.getWindowToken(), 0);
        });

        fbAddNewSale.setOnClickListener(v -> startActivity(new Intent(this, AddNewSaleActivity.class)));
        salesReport.setOnClickListener(v -> startActivity(new Intent(this, SalesReportActivity.class)));
        profitAndLoss.setOnClickListener(v -> startActivity(new Intent(this, ProfitLossReportActivity.class)));
        ivClearSearch.setOnClickListener(v -> etSearchKeyword.setText(""));
        ivToolbarBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    private void displaySalesList() {
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