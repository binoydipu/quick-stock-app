package com.binoydipu.quickstock.views.sales;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class AddNewSaleActivity extends AppCompatActivity {

    private ImageView ivToolbarBack;
    private AutoCompleteTextView tvSelectItems;
    private TextInputEditText etSaleExpireDate;
    private TextView tvCancleItem, tvSaveItem;
    private long saleDateInMillis;
    private ProgressBar progressBar;
    private FirebaseCloudStorage cloudStorage;

    private String[] items = {"Apple", "Orange", "Banana", "Watch"};
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_sale);

        Toolbar toolbar = findViewById(R.id.toolbar);
        ivToolbarBack = findViewById(R.id.toolbar_back_btn);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        tvSelectItems = findViewById(R.id.auto_complete_tv);
        arrayAdapter = new ArrayAdapter<>(this, R.layout.list_item, items);
        tvSelectItems.setAdapter(arrayAdapter);
        tvCancleItem = findViewById(R.id.cancle_item_tv);
        etSaleExpireDate = findViewById(R.id.sale_date_et);
        tvSaveItem = findViewById(R.id.save_item_tv);
        progressBar = findViewById(R.id.progress_circular);
        cloudStorage = FirebaseCloudStorage.getInstance();
        saleDateInMillis = -1;

        ivToolbarBack.setOnClickListener(v -> onBackPressed());
        tvCancleItem.setOnClickListener(v -> onBackPressed());

        tvSelectItems.setOnItemClickListener((parent, view, position, id) -> {
            String item = parent.getItemAtPosition(position).toString();
            Toast.makeText(this, "Item: " + item, Toast.LENGTH_SHORT).show();
        });

        ivToolbarBack.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Discard Sale")
                .setMessage("Are you sure you want to discard this sale?")
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
            Log.e("AddNewSaleActivity", "String to Double Conversion Failed");
        }
        return parsedValue;
    }
    private int parseIntegerValue(String value) {
        int parsedValue = -1;
        try {
            parsedValue = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            Log.e("AddNewSaleActivity", "String to Int Conversion Failed");
        }
        return parsedValue;
    }
}