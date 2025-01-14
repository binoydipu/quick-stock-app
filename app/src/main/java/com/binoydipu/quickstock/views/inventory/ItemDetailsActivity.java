package com.binoydipu.quickstock.views.inventory;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.binoydipu.quickstock.R;
import com.binoydipu.quickstock.services.cloud.FirebaseCloudStorage;
import com.binoydipu.quickstock.services.cloud.ItemModel;
import com.binoydipu.quickstock.utilities.dialogs.DialogHelper;
import com.binoydipu.quickstock.utilities.format.NumberFormater;
import com.binoydipu.quickstock.views.AboutActivity;
import com.binoydipu.quickstock.views.NotificationActivity;
import com.binoydipu.quickstock.views.ProfileActivity;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.Objects;

public class ItemDetailsActivity extends AppCompatActivity {

    private TextView tvItemName, tvSalePrice, tvPurchasePrice, tvStockSize, tvStockValue, tvItemCode, tvExpireDate;
    private String itemName;
    private ProgressBar progressBar;
    private RecyclerView rvItemTransactions;
    private ImageView ivToolbarBack;
    private ExtendedFloatingActionButton fbAdjustStock;
    private ItemModel itemModel;
    private FirebaseCloudStorage cloudStorage;

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
        rvItemTransactions = findViewById(R.id.stock_transactions_recyclerview);
        itemModel = new ItemModel();
        cloudStorage = FirebaseCloudStorage.getInstance();

        Intent intent = getIntent();
        itemName = intent.getStringExtra("itemName");
        displayItemInfo();
        
        fbAdjustStock.setOnClickListener(v -> {
            itemName = itemModel.getItemName();
            Intent intent2 = new Intent(this, AdjustStockActivity.class);
            intent2.putExtra("itemName", itemName);
            startActivity(intent2);
        });
        ivToolbarBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    private void displayItemInfo() {
        progressBar.setVisibility(View.VISIBLE);
        cloudStorage.getItemByName(itemName, item -> {
            progressBar.setVisibility(View.GONE);
            if(item != null) {
                itemModel = item;
                String purchasePriceString = NumberFormater.formatPrice(itemModel.getPurchasePrice());
                String salePriceString = NumberFormater.formatPrice(itemModel.getSalePrice());
                String stockQuantityString = String.valueOf(itemModel.getStockQuantity());
                String expireDateString = NumberFormater.convertMillisToDate(itemModel.getExpireDateInMillis());
                String stockValueString = NumberFormater.formatPrice(itemModel.getStockValue());

                tvItemName.setText(itemName);
                tvItemCode.setText(itemModel.getItemCode());
                tvSalePrice.setText(salePriceString);
                tvPurchasePrice.setText(purchasePriceString);
                tvStockSize.setText(stockQuantityString);
                tvStockValue.setText(stockValueString);
                tvExpireDate.setText(expireDateString);

                if(itemModel.getStockQuantity() < 0) {
                    tvStockSize.setTextColor(getResources().getColor(R.color.red, getTheme()));
                } else {
                    tvStockSize.setTextColor(getResources().getColor(R.color.green, getTheme()));
                }
            } else {
                Toast.makeText(this, "Failed to retrieve item", Toast.LENGTH_SHORT).show();
                getOnBackPressedDispatcher().onBackPressed();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayItemInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_detail_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.edit_item_menu) {
            itemName = itemModel.getItemName();
            Intent intent = new Intent(this, EditItemActivity.class);
            intent.putExtra("itemName", itemName);
            intent.putExtra("itemCode", itemModel.getItemCode());
            intent.putExtra("purchasePrice", itemModel.getPurchasePrice());
            intent.putExtra("salePrice", itemModel.getSalePrice());
            intent.putExtra("stockQuantity", itemModel.getStockQuantity());
            intent.putExtra("expireDateInMillis", itemModel.getExpireDateInMillis());
            intent.putExtra("stockValue", itemModel.getStockValue());
            startActivity(intent);
        } else if(id == R.id.delete_item_menu) {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Item?")
                    .setMessage("Are you sure you want to delete the item?")
                    .setIcon(R.drawable.quick_stock)
                    .setPositiveButton("Yes", (dialog, which) -> {
                        cloudStorage.deleteItemByName(itemName, isItemDeleted -> {
                            if(isItemDeleted) {
                                Toast.makeText(this, "Item Deleted", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "An Error Occurred", Toast.LENGTH_SHORT).show();
                            }
                            getOnBackPressedDispatcher().onBackPressed();
                        });
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        }
        return true;
    }
}