package com.binoydipu.quickstock.views.inventory;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.binoydipu.quickstock.R;
import com.binoydipu.quickstock.services.cloud.FirebaseCloudStorage;
import com.binoydipu.quickstock.services.cloud.ItemModel;
import com.binoydipu.quickstock.utilities.dialogs.DialogHelper;
import com.binoydipu.quickstock.views.AboutActivity;
import com.binoydipu.quickstock.views.profile.NotificationActivity;
import com.binoydipu.quickstock.views.profile.ProfileActivity;
import com.binoydipu.quickstock.views.inventory.adapter.ItemListAdapter;
import com.binoydipu.quickstock.views.reports.InventoryReportActivity;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;

public class ManageInventoryActivity extends AppCompatActivity {

    private ExtendedFloatingActionButton fbAddNewItem;
    private EditText etSearchKeyword;
    private ImageView ivClearSearch, ivSearchStaff, ivToolbarBack;
    private LinearLayout stockSummary, lowStockSummary, onlineStore, inventoryReport;

    private ArrayList<ItemModel> itemModels;
    private ProgressBar progressBar;
    private RecyclerView rvItemsList;

    private FirebaseCloudStorage cloudStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_inventory);

        Toolbar toolbar = findViewById(R.id.toolbar);
        ivToolbarBack = findViewById(R.id.toolbar_back_btn);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        fbAddNewItem = findViewById(R.id.add_new_item_fb);
        progressBar = findViewById(R.id.progress_circular);
        rvItemsList = findViewById(R.id.inventory_recyclerview);
        stockSummary = findViewById(R.id.stock_summary_tv);
        lowStockSummary = findViewById(R.id.low_stock_summary_tv);
        inventoryReport = findViewById(R.id.inventory_report_ll);
        onlineStore = findViewById(R.id.online_store_tv);
        etSearchKeyword = findViewById(R.id.search_keywords_et);
        ivClearSearch = findViewById(R.id.clear_search_text_iv);
        ivSearchStaff = findViewById(R.id.search_staff_btn);
        itemModels = new ArrayList<>();
        cloudStorage = FirebaseCloudStorage.getInstance();

        progressBar.setVisibility(View.VISIBLE);
        itemModels = cloudStorage.getAllItems(this, isReceived -> {
            progressBar.setVisibility(View.GONE);
            if(isReceived) {
                displayItemsList();
            }
        });

        fbAddNewItem.setOnClickListener(v -> startActivity(new Intent(this, AddNewItemActivity.class)));
        stockSummary.setOnClickListener(v -> startActivity(new Intent(this, StockSummaryActivity.class)));
        lowStockSummary.setOnClickListener(v -> startActivity(new Intent(this, LowStockActivity.class)));
        inventoryReport.setOnClickListener(v -> startActivity(new Intent(this, InventoryReportActivity.class)));
        onlineStore.setOnClickListener(v -> Toast.makeText(this, "Not Available Yet", Toast.LENGTH_SHORT).show());

        ivSearchStaff.setOnClickListener(v -> {
            displayItemsList();
            etSearchKeyword.clearFocus();
            // to close keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etSearchKeyword.getWindowToken(), 0);
        });

        ivClearSearch.setOnClickListener(v -> etSearchKeyword.setText(""));
        ivToolbarBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    private void displayItemsList() {
        String searchText = etSearchKeyword.getText().toString().trim();
        rvItemsList.setLayoutManager(new LinearLayoutManager(this));
        ItemListAdapter itemListAdapter = new ItemListAdapter(this, itemModels);
        itemListAdapter.filterItems(searchText);
        rvItemsList.setAdapter(itemListAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        itemModels = cloudStorage.getAllItems(this, isReceived -> {
            progressBar.setVisibility(View.GONE);
            if(isReceived) {
                displayItemsList();
            }
        });
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