package com.binoydipu.quickstock.views.inventory;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.binoydipu.quickstock.R;
import com.binoydipu.quickstock.utilities.format.NumberFormater;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.Objects;

public class ItemDetailsActivity extends AppCompatActivity {

    private TextView tvItemName, tvSalePrice, tvPurchasePrice, tvStockSize, tvStockValue, tvItemCode, tvExpireDate;
    private ProgressBar progressBar;
    private RecyclerView rvItemDetails;
    private ImageView ivToolbarBack;
    private ExtendedFloatingActionButton fbAdjustStock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        ivToolbarBack = findViewById(R.id.toolbar_back_btn);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        tvItemName = findViewById(R.id.item_name_tv);
        tvSalePrice = findViewById(R.id.sale_price_tv);
        tvPurchasePrice = findViewById(R.id.purchase_price_tv);
        tvStockSize = findViewById(R.id.stock_size_tv);
        tvStockValue = findViewById(R.id.stock_value_tv);
        tvItemCode = findViewById(R.id.item_code_tv);
        tvExpireDate = findViewById(R.id.expire_date_tv);
        fbAdjustStock = findViewById(R.id.adjust_stock_fb);
        progressBar = findViewById(R.id.progress_circular);
        rvItemDetails = findViewById(R.id.stock_transactions_recyclerview);

        Intent intent = getIntent();
        String itemName = intent.getStringExtra("itemName");
        String itemCode = intent.getStringExtra("itemCode");
        double purchasePrice = intent.getDoubleExtra("purchasePrice", 0);
        double salePrice = intent.getDoubleExtra("salePrice", 0);
        int stockQuantity = intent.getIntExtra("stockQuantity", 0);
        long expireDateInMillis = intent.getLongExtra("expireDateInMillis", 0);

        displayItemInfo(itemName, itemCode, purchasePrice, salePrice, stockQuantity, expireDateInMillis);
        
        fbAdjustStock.setOnClickListener(v -> {
            Toast.makeText(this, "Not Available Yet", Toast.LENGTH_SHORT).show();
        });
        ivToolbarBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    private void displayItemInfo(String itemName, String itemCode, double purchasePrice, double salePrice,
                                 int stockQuantity, long expireDateInMillis) {
        String purchasePriceString = NumberFormater.formatPrice(purchasePrice);
        String salePriceString = NumberFormater.formatPrice(salePrice);
        String stockQuantityString = String.valueOf(stockQuantity);
        String expireDateString = NumberFormater.convertMillisToDate(expireDateInMillis);
        double stockValue = stockQuantity >= 0 ? purchasePrice * stockQuantity : 0.0;
        String stockValueString = NumberFormater.formatPrice(stockValue);

        tvItemName.setText(itemName);
        tvItemCode.setText(itemCode);
        tvSalePrice.setText(salePriceString);
        tvPurchasePrice.setText(purchasePriceString);
        tvStockSize.setText(stockQuantityString);
        tvStockValue.setText(stockValueString);
        tvExpireDate.setText(expireDateString);

        if(stockQuantity < 0) {
            tvStockSize.setBackgroundColor(getResources().getColor(R.color.red, getTheme()));
        }
    }
}