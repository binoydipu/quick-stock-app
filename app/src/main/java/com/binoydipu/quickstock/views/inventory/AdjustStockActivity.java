package com.binoydipu.quickstock.views.inventory;

import static java.lang.Math.max;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.binoydipu.quickstock.services.cloud.ItemModel;
import com.binoydipu.quickstock.utilities.format.NumberFormater;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Objects;

public class AdjustStockActivity extends AppCompatActivity {

    private ImageView ivToolbarBack;
    private TextInputEditText etAdjustmentDate, etQuantity, etPrice, etComments;
    private String sQuantity, sPrice, sComments, sAdjustmentDate, itemName;
    private double price;
    private int quantity;
    private long adjustmentDateInMillis;
    private MaterialButton btnReduce, btnAdd;
    private FirebaseCloudStorage cloudStorage;
    private ProgressBar progressBar;
    private ItemModel itemModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjust_stock);

        Toolbar toolbar = findViewById(R.id.toolbar);
        ivToolbarBack = findViewById(R.id.toolbar_back_btn);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        etAdjustmentDate = findViewById(R.id.adjustment_date_et);
        etQuantity = findViewById(R.id.quantity_et);
        etPrice = findViewById(R.id.price_et);
        etComments = findViewById(R.id.comment_et);
        btnReduce = findViewById(R.id.reduce_stock_btn);
        btnAdd = findViewById(R.id.add_stock_btn);
        progressBar = findViewById(R.id.progress_circular);
        cloudStorage = FirebaseCloudStorage.getInstance();

        adjustmentDateInMillis = NumberFormater.getTodayInMillis();
        sAdjustmentDate = NumberFormater.convertMillisToDate(adjustmentDateInMillis);
        etAdjustmentDate.setText(sAdjustmentDate);

        Intent intent = getIntent();
        itemName = intent.getStringExtra("itemName");

        etAdjustmentDate.setOnClickListener(view -> {
            // Get the current date as default
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view1, selectedYear, selectedMonth, selectedDay) -> {
                Calendar selectedExpireDate = Calendar.getInstance();
                selectedExpireDate.set(selectedYear, selectedMonth, selectedDay);
                adjustmentDateInMillis = selectedExpireDate.getTimeInMillis();

                String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                etAdjustmentDate.setText(selectedDate);
            }, year, month, day);

            datePickerDialog.show();
        });

        btnReduce.setOnClickListener(v -> {
            if(checkInputFields()) {
                progressBar.setVisibility(View.VISIBLE);
                cloudStorage.getItemByName(itemName, item -> {
                    progressBar.setVisibility(View.GONE);
                    if(item != null) {
                        itemModel = item;
                        int stockQuantity = itemModel.getStockQuantity() - quantity;
                        double stockValue = max(0.0, itemModel.getStockValue() - (quantity * itemModel.getPurchasePrice()));
                        itemModel.setStockQuantity(stockQuantity);
                        itemModel.setStockValue(stockValue);

                        progressBar.setVisibility(View.VISIBLE);
                        cloudStorage.updateItem(itemName, itemModel.getItemCode(), itemModel.getPurchasePrice(),
                                itemModel.getSalePrice(), itemModel.getStockQuantity(), itemModel.getExpireDateInMillis(),
                                itemModel.getStockValue(), isItemUpdated -> {
                            progressBar.setVisibility(View.GONE);
                            if(isItemUpdated) {
                                Toast.makeText(this, "Item Updated", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "An Error Occurred", Toast.LENGTH_SHORT).show();
                            }
                            getOnBackPressedDispatcher().onBackPressed();
                        });
                    } else {
                        Toast.makeText(this, "Failed to retrieve item", Toast.LENGTH_SHORT).show();
                        getOnBackPressedDispatcher().onBackPressed();
                    }
                });
            }
        });
        btnAdd.setOnClickListener(v -> {
            if(checkInputFields()) {
                progressBar.setVisibility(View.VISIBLE);
                cloudStorage.getItemByName(itemName, item -> {
                    progressBar.setVisibility(View.GONE);
                    if(item != null) {
                        itemModel = item;
                        int stockQuantity = itemModel.getStockQuantity() + quantity;
                        double stockValue = max(0.0, itemModel.getStockValue() + (quantity * price));
                        itemModel.setStockQuantity(stockQuantity);
                        itemModel.setStockValue(stockValue);

                        progressBar.setVisibility(View.VISIBLE);
                        cloudStorage.updateItem(itemName, itemModel.getItemCode(), itemModel.getPurchasePrice(),
                                itemModel.getSalePrice(), itemModel.getStockQuantity(), itemModel.getExpireDateInMillis(),
                                itemModel.getStockValue(), isItemUpdated -> {
                            progressBar.setVisibility(View.GONE);
                            if(isItemUpdated) {
                                Toast.makeText(this, "Item Updated", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "An Error Occurred", Toast.LENGTH_SHORT).show();
                            }
                            getOnBackPressedDispatcher().onBackPressed();
                        });
                    } else {
                        Toast.makeText(this, "Failed to retrieve item", Toast.LENGTH_SHORT).show();
                        getOnBackPressedDispatcher().onBackPressed();
                    }
                });
            }
        });
        ivToolbarBack.setOnClickListener(v -> onBackPressed());
    }

    boolean checkInputFields() {
        sQuantity = Objects.requireNonNull(etQuantity.getText()).toString().trim();
        sPrice = Objects.requireNonNull(etPrice.getText()).toString().trim();
        sComments = Objects.requireNonNull(etComments.getText()).toString().trim();
        sAdjustmentDate = Objects.requireNonNull(etAdjustmentDate.getText()).toString().trim();
        price = parseDoubleValue(sPrice);
        quantity = parseIntegerValue(sQuantity);

        if (sAdjustmentDate.isEmpty()) {
            etAdjustmentDate.setError("Required field!");
            etAdjustmentDate.requestFocus();
        } else if (sQuantity.isEmpty()) {
            etQuantity.setError("Required field!");
            etQuantity.requestFocus();
        } else if (quantity == -1) {
            etQuantity.setError("Invalid Price!");
            etQuantity.requestFocus();
        } else if(sPrice.isEmpty()) {
            etPrice.setError("Required field!");
            etPrice.requestFocus();
        } else if (price == -1.0) {
            etPrice.setError("Invalid Price!");
            etPrice.requestFocus();
        } else {
            return true;
        }
        return false;
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
}