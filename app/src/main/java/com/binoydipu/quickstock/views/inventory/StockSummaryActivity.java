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

public class StockSummaryActivity extends AppCompatActivity {

    private ImageView ivToolbarBack;
    private TextView tvNoOfItems, tvLowStockItems, tvStockValue;
    private ArrayList<ItemModel> itemModels;
    private ProgressBar progressBar;
    private RecyclerView rvItemsList;

    private FirebaseCloudStorage cloudStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_summary);

        Toolbar toolbar = findViewById(R.id.toolbar);
        ivToolbarBack = findViewById(R.id.toolbar_back_btn);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        tvNoOfItems = findViewById(R.id.no_of_items_tv);
        tvLowStockItems = findViewById(R.id.low_stock_items_tv);
        tvStockValue = findViewById(R.id.stock_value_tv);
        rvItemsList = findViewById(R.id.stock_summary_recyclerview);
        progressBar = findViewById(R.id.progress_circular);
        itemModels = new ArrayList<>();
        cloudStorage = FirebaseCloudStorage.getInstance();

        progressBar.setVisibility(View.VISIBLE);
        itemModels = cloudStorage.getAllItems(this, isReceived -> {
            progressBar.setVisibility(View.GONE);
            if(isReceived) {
                displayStockSummary();
            }
        });

        ivToolbarBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    private void displayStockSummary() {
        int lowItems = 0;
        double stockValue = 0;
        for(ItemModel item : itemModels) {
            stockValue += item.getStockQuantity() * item.getPurchasePrice();
            if(item.getStockQuantity() < LOW_STOCK_LIMIT) lowItems++;
        }
        String stockValueString = NumberFormater.formatPrice(stockValue);

        tvNoOfItems.setText(String.valueOf(itemModels.size()));
        tvLowStockItems.setText(String.valueOf(lowItems));
        tvStockValue.setText(stockValueString);

        displayItemsList();
    }

    private void displayItemsList() {
        rvItemsList.setLayoutManager(new LinearLayoutManager(this));
        StockSummaryAdapter stockSummaryAdapter = new StockSummaryAdapter(this, itemModels);
        rvItemsList.setAdapter(stockSummaryAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        itemModels = cloudStorage.getAllItems(this, isReceived -> {
            progressBar.setVisibility(View.GONE);
            if(isReceived) {
                displayStockSummary();
            }
        });
    }
}