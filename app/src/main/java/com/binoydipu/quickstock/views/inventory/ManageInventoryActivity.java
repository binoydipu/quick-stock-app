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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.binoydipu.quickstock.R;
import com.binoydipu.quickstock.services.auth.FirebaseAuthProvider;
import com.binoydipu.quickstock.services.cloud.FirebaseCloudStorage;
import com.binoydipu.quickstock.services.cloud.ItemModel;
import com.binoydipu.quickstock.views.HomeActivity;
import com.binoydipu.quickstock.views.LoginActivity;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;

public class ManageInventoryActivity extends AppCompatActivity {

    private ExtendedFloatingActionButton fbAddNewItem;
    private EditText etSearchKeyword;
    private ImageView ivClearSearch, ivSearchStaff, ivToolbarBack;
    private LinearLayout stockSummary, lowStockSummary, onlineStore;

    private ArrayList<ItemModel> itemModels;
    private ProgressBar progressBar;
    private RecyclerView rvItemsList;

    private FirebaseCloudStorage cloudStorage;
    private FirebaseAuthProvider authProvider;

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
        onlineStore = findViewById(R.id.online_store_tv);
        etSearchKeyword = findViewById(R.id.search_keywords_et);
        ivClearSearch = findViewById(R.id.clear_search_text_iv);
        ivSearchStaff = findViewById(R.id.search_staff_btn);
        itemModels = new ArrayList<>();
        cloudStorage = FirebaseCloudStorage.getInstance();
        authProvider = FirebaseAuthProvider.getInstance();

        progressBar.setVisibility(View.VISIBLE);
        itemModels = cloudStorage.getAllItems(this, isReceived -> {
            progressBar.setVisibility(View.GONE);
            if(isReceived) {
                displayItemsList();
            }
        });

        fbAddNewItem.setOnClickListener(v -> {
            startActivity(new Intent(this, AddNewItemActivity.class));
        });

        stockSummary.setOnClickListener(v -> {

        });

        lowStockSummary.setOnClickListener(v -> {

        });

        onlineStore.setOnClickListener(v -> {
            Toast.makeText(this, "Not Available Yet", Toast.LENGTH_SHORT).show();
        });

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
        rvItemsList.setLayoutManager(new LinearLayoutManager(this));
        ItemListAdapter itemListAdapter = new ItemListAdapter(this, itemModels);
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
            Toast.makeText(this, "Notification", Toast.LENGTH_SHORT).show();
        } else if(id == R.id.profile_menu) {
            Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
        } else if(id == R.id.logout_menu) {
            logoutUser();
        }
        return true;
    }

    private void logoutUser() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setIcon(R.drawable.quick_stock)
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (authProvider.logOut()) {
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
    }
}