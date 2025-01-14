package com.binoydipu.quickstock.views.inventory;

import static com.binoydipu.quickstock.constants.ConstantValues.LOW_STOCK_LIMIT;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.binoydipu.quickstock.R;
import com.binoydipu.quickstock.services.cloud.FirebaseCloudStorage;
import com.binoydipu.quickstock.services.cloud.ItemModel;
import com.binoydipu.quickstock.utilities.format.NumberFormater;
import com.binoydipu.quickstock.views.inventory.adapter.StockSummaryAdapter;

import java.util.ArrayList;
import java.util.Objects;

public class LowStockActivity extends AppCompatActivity {
    private ImageView ivToolbarBack;
    private TextView tvLowStockItems, tvStockValue;
    private ArrayList<ItemModel> itemModels, lowStockItems;
    private ProgressBar progressBar;
    private RecyclerView rvItemsList;

    private FirebaseCloudStorage cloudStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_low_stock);

        Toolbar toolbar = findViewById(R.id.toolbar);
        ivToolbarBack = findViewById(R.id.toolbar_back_btn);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        tvLowStockItems = findViewById(R.id.low_stock_items_tv);
        tvStockValue = findViewById(R.id.stock_value_tv);
        rvItemsList = findViewById(R.id.low_stock_recyclerview);
        progressBar = findViewById(R.id.progress_circular);
        itemModels = new ArrayList<>();
        lowStockItems = new ArrayList<>();
        cloudStorage = FirebaseCloudStorage.getInstance();

        progressBar.setVisibility(View.VISIBLE);
        itemModels = cloudStorage.getAllItems(this, isReceived -> {
            progressBar.setVisibility(View.GONE);
            if(isReceived) {
                displayLowStockSummary();
            }
        });

        ivToolbarBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    private void displayLowStockSummary() {
        int lowItems = 0;
        double lowStockValue = 0;
        lowStockItems.clear();
        for(ItemModel item : itemModels) {
            if(item.getStockQuantity() < LOW_STOCK_LIMIT) {
                if(item.getStockQuantity() > 0) lowStockValue += item.getStockQuantity() * item.getPurchasePrice();
                lowItems++;
                lowStockItems.add(item);
            }
        }
        String stockValueString = NumberFormater.formatPrice(lowStockValue);

        tvLowStockItems.setText(String.valueOf(lowItems));
        tvStockValue.setText(stockValueString);

        displayItemsList();
    }

    private void displayItemsList() {
        rvItemsList.setLayoutManager(new LinearLayoutManager(this));
        StockSummaryAdapter stockSummaryAdapter = new StockSummaryAdapter(this, lowStockItems);
        rvItemsList.setAdapter(stockSummaryAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        itemModels = cloudStorage.getAllItems(this, isReceived -> {
            progressBar.setVisibility(View.GONE);
            if(isReceived) {
                displayLowStockSummary();
            }
        });
    }
}