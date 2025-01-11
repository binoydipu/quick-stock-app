package com.binoydipu.quickstock.views.inventory;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.binoydipu.quickstock.R;
import com.binoydipu.quickstock.services.auth.FirebaseAuthProvider;
import com.binoydipu.quickstock.services.cloud.FirebaseCloudStorage;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Objects;

public class AddNewItemActivity extends AppCompatActivity {

    private TextInputEditText etItemName, etItemCode, etPurchasePrice,
            etSalePrice, etStockQuantity, etStockExpireDate;
    private TextView tvCancleItem, tvSaveItem;
    long expireDateInMillis;
    private ProgressBar progressBar;
    private FirebaseCloudStorage cloudStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_item);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        etItemName = findViewById(R.id.item_name_et);
        etItemCode = findViewById(R.id.item_code_et);
        etPurchasePrice = findViewById(R.id.purchase_price_et);
        etSalePrice = findViewById(R.id.sale_price_et);
        etStockQuantity = findViewById(R.id.stock_quantity_et);
        etStockExpireDate = findViewById(R.id.stock_expire_date_et);
        tvCancleItem = findViewById(R.id.cancle_item_tv);
        tvSaveItem = findViewById(R.id.save_item_tv);
        progressBar = findViewById(R.id.progress_circular);
        cloudStorage = FirebaseCloudStorage.getInstance();
        expireDateInMillis = -1;

        tvCancleItem.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Discard Item")
                    .setMessage("Are you sure you want to discard this item?")
                    .setIcon(R.drawable.quick_stock)
                    .setPositiveButton("Yes", (dialog, which) -> getOnBackPressedDispatcher().onBackPressed())
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });

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
            String itemName = Objects.requireNonNull(etItemName.getText()).toString().trim();
            String itemCode = Objects.requireNonNull(etItemCode.getText()).toString().trim();
            String purchasePrice = Objects.requireNonNull(etPurchasePrice.getText()).toString().trim();
            String salePrice = Objects.requireNonNull(etSalePrice.getText()).toString().trim();
            String stockQuantity = Objects.requireNonNull(etStockQuantity.getText()).toString().trim();
            double dblPurchasePrice = parseDoubleValue(purchasePrice);
            double dblSalePrice = parseDoubleValue(salePrice);
            int intStockQuantity = parseIntegerValue(stockQuantity);

            if (itemName.isEmpty()) {
                etItemName.setError("Required field!");
                etItemName.requestFocus();
            } else if (itemCode.isEmpty()) {
                etItemCode.setError("Required field!");
                etItemCode.requestFocus();
            } else if (purchasePrice.isEmpty()) {
                etPurchasePrice.setError("Required field!");
                etPurchasePrice.requestFocus();
            } else if (dblPurchasePrice == -1.0) {
                etPurchasePrice.setError("Invalid Price!");
                etPurchasePrice.requestFocus();
            } else if (salePrice.isEmpty()) {
                etSalePrice.setError("Required field!");
                etSalePrice.requestFocus();
            } else if (dblSalePrice == -1.0) {
                etSalePrice.setError("Invalid Price!");
                etSalePrice.requestFocus();
            } else if (stockQuantity.isEmpty()) {
                etStockQuantity.setError("Required field!");
                etStockQuantity.requestFocus();
            } else if (intStockQuantity == -1) {
                etStockQuantity.setError("Invalid Price!");
                etStockQuantity.requestFocus();
            } else if (expireDateInMillis == -1) {
                etStockExpireDate.setError("Required field!");
                etStockExpireDate.requestFocus();
            } else {
                progressBar.setVisibility(View.VISIBLE);
                cloudStorage.addNewItem(itemName, itemCode, dblPurchasePrice, dblSalePrice, intStockQuantity, expireDateInMillis, isItemAdded -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    if(isItemAdded) {
                        Toast.makeText(this, "Item Added", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "An Error Occurred", Toast.LENGTH_SHORT).show();
                    }
                    getOnBackPressedDispatcher().onBackPressed();
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Discard Item")
                .setMessage("Are you sure you want to discard this item?")
                .setIcon(R.drawable.quick_stock)
                .setPositiveButton("Yes", (dialog, which) -> {
                    super.onBackPressed();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private double parseDoubleValue(String value) {
        double parsedValue = -1.0;
        try {
            parsedValue = Double.parseDouble(value);
        } catch (NumberFormatException e) {
            Log.e("AddNewItemActivity", "String to Double Conversion Failed");
        }
        return parsedValue;
    }
    private int parseIntegerValue(String value) {
        int parsedValue = -1;
        try {
            parsedValue = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            Log.e("AddNewItemActivity", "String to Int Conversion Failed");
        }
        return parsedValue;
    }
}