package com.binoydipu.quickstock.views.inventory;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.binoydipu.quickstock.R;
import com.binoydipu.quickstock.services.cloud.FirebaseCloudStorage;
import com.binoydipu.quickstock.utilities.format.NumberFormater;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Objects;

public class EditItemActivity extends AppCompatActivity {

    private ImageView ivToolbarBack;
    private TextInputEditText etItemName, etItemCode, etPurchasePrice,
            etSalePrice, etStockQuantity, etStockValue, etStockExpireDate;
    private TextView tvCancleItem, tvSaveItem;
    private String itemName, itemCode;
    private double purchasePrice, salePrice, stockValue;
    private int stockQuantity;
    long expireDateInMillis;
    private ProgressBar progressBar;
    private FirebaseCloudStorage cloudStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        Toolbar toolbar = findViewById(R.id.toolbar);
        ivToolbarBack = findViewById(R.id.toolbar_back_btn);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        itemName = intent.getStringExtra("itemName");
        itemCode = intent.getStringExtra("itemCode");
        purchasePrice = intent.getDoubleExtra("purchasePrice", 0);
        salePrice = intent.getDoubleExtra("salePrice", 0);
        stockQuantity = intent.getIntExtra("stockQuantity", 0);
        expireDateInMillis = intent.getLongExtra("expireDateInMillis", 0);
        stockValue = intent.getDoubleExtra("stockValue", 0);

        etItemName = findViewById(R.id.item_name_et);
        etItemCode = findViewById(R.id.item_code_et);
        etPurchasePrice = findViewById(R.id.purchase_price_et);
        etSalePrice = findViewById(R.id.sale_price_et);
        etStockQuantity = findViewById(R.id.stock_quantity_et);
        etStockValue = findViewById(R.id.stock_value_et);
        etStockExpireDate = findViewById(R.id.stock_expire_date_et);
        tvCancleItem = findViewById(R.id.cancle_item_tv);
        tvSaveItem = findViewById(R.id.save_item_tv);
        progressBar = findViewById(R.id.progress_circular);
        cloudStorage = FirebaseCloudStorage.getInstance();

        setPreviousValues(itemName, itemCode, purchasePrice, salePrice, stockQuantity, expireDateInMillis);

        etStockExpireDate.setOnClickListener(view -> {
            // Get the current date as default
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view1, selectedYear, selectedMonth, selectedDay) -> {
                Calendar selectedExpireDate = Calendar.getInstance();
                selectedExpireDate.set(selectedYear, selectedMonth, selectedDay);
                expireDateInMillis = selectedExpireDate.getTimeInMillis();

                String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                etStockExpireDate.setText(selectedDate);
            }, year, month, day);

            datePickerDialog.show();
        });

        tvSaveItem.setOnClickListener(v -> {
            itemName = Objects.requireNonNull(etItemName.getText()).toString().trim();
            itemCode = Objects.requireNonNull(etItemCode.getText()).toString().trim();
            String purchasePriceString = Objects.requireNonNull(etPurchasePrice.getText()).toString().trim();
            String salePriceString = Objects.requireNonNull(etSalePrice.getText()).toString().trim();
            String stockQuantityString = Objects.requireNonNull(etStockQuantity.getText()).toString().trim();
            String stockValueString = Objects.requireNonNull(etStockValue.getText()).toString().trim();
            purchasePrice = parseDoubleValue(purchasePriceString);
            salePrice = parseDoubleValue(salePriceString);
            stockQuantity = parseIntegerValue(stockQuantityString);
            stockValue = parseDoubleValue(stockValueString);

            if (itemName.isEmpty()) {
                etItemName.setError("Required field!");
                etItemName.requestFocus();
            } else if (itemCode.isEmpty()) {
                etItemCode.setError("Required field!");
                etItemCode.requestFocus();
            } else if (purchasePriceString.isEmpty()) {
                etPurchasePrice.setError("Required field!");
                etPurchasePrice.requestFocus();
            } else if (purchasePrice == -1.0) {
                etPurchasePrice.setError("Invalid Price!");
                etPurchasePrice.requestFocus();
            } else if (salePriceString.isEmpty()) {
                etSalePrice.setError("Required field!");
                etSalePrice.requestFocus();
            } else if (salePrice == -1.0) {
                etSalePrice.setError("Invalid Price!");
                etSalePrice.requestFocus();
            } else if (stockQuantityString.isEmpty()) {
                etStockQuantity.setError("Required field!");
                etStockQuantity.requestFocus();
            } else if (stockQuantity == -1) {
                etStockQuantity.setError("Invalid Price!");
                etStockQuantity.requestFocus();
            } else if (stockValueString.isEmpty()) {
                etStockValue.setError("Required field!");
                etStockValue.requestFocus();
            } else if (stockValue == -1.0) {
                etStockValue.setError("Invalid Value!");
                etStockValue.requestFocus();
            } else if (expireDateInMillis == -1) {
                etStockExpireDate.setError("Required field!");
                etStockExpireDate.requestFocus();
            } else {
                progressBar.setVisibility(View.VISIBLE);
                cloudStorage.updateItem(itemName, itemCode, purchasePrice, salePrice, stockQuantity, expireDateInMillis, stockValue, isItemUpdated -> {
                    progressBar.setVisibility(View.GONE);
                    if(isItemUpdated) {
                        Toast.makeText(this, "Item Updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "An Error Occurred", Toast.LENGTH_SHORT).show();
                    }
                    getOnBackPressedDispatcher().onBackPressed();
                });
            }
        });

        ivToolbarBack.setOnClickListener(v -> onBackPressed());
        tvCancleItem.setOnClickListener(v -> onBackPressed());
    }

    private void setPreviousValues(String itemName, String itemCode, double purchasePrice, double salePrice, int stockQuantity, long expireDateInMillis) {
        String purchasePriceString = String.valueOf(purchasePrice);
        String salePriceString = String.valueOf(salePrice);
        String stockQuantityString = String.valueOf(stockQuantity);
        String stockValueString = String.valueOf(stockValue);
        String expireDateString = NumberFormater.convertMillisToDate(expireDateInMillis);

        etItemName.setText(itemName);
        etItemCode.setText(itemCode);
        etSalePrice.setText(salePriceString);
        etPurchasePrice.setText(purchasePriceString);
        etStockQuantity.setText(stockQuantityString);
        etStockValue.setText(stockValueString);
        etStockExpireDate.setText(expireDateString);
    }

    private double parseDoubleValue(String value) {
        double parsedValue = -1.0;
        try {
            parsedValue = Double.parseDouble(value);
        } catch (NumberFormatException e) {
            Log.e("EditItemActivity", "String to Double Conversion Failed");
        }
        return parsedValue;
    }
    private int parseIntegerValue(String value) {
        int parsedValue = -1;
        try {
            parsedValue = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            Log.e("EditItemActivity", "String to Int Conversion Failed");
        }
        return parsedValue;
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Discard Change?")
                .setMessage("Are you sure you want to discard the changes?")
                .setIcon(R.drawable.quick_stock)
                .setPositiveButton("Yes", (dialog, which) -> {
                    super.onBackPressed();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }
}