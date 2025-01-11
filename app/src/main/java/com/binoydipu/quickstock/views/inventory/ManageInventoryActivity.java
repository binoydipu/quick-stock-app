package com.binoydipu.quickstock.views.inventory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.binoydipu.quickstock.R;
import com.binoydipu.quickstock.helper.adapter.ItemListAdapter;
import com.binoydipu.quickstock.helper.adapter.StaffListAdapter;
import com.binoydipu.quickstock.services.cloud.FirebaseCloudStorage;
import com.binoydipu.quickstock.services.cloud.ItemModel;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;

public class ManageInventoryActivity extends AppCompatActivity {

    private ExtendedFloatingActionButton fbAddNewItem;
    private ArrayList<ItemModel> itemModels;
    private FirebaseCloudStorage cloudStorage;
    private ProgressBar progressBar;
    private RecyclerView rvItemsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_inventory);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed() );

        fbAddNewItem = findViewById(R.id.add_new_item_fb);
        progressBar = findViewById(R.id.progress_circular);
        rvItemsList = findViewById(R.id.inventory_recyclerview);
        itemModels = new ArrayList<>();
        cloudStorage = FirebaseCloudStorage.getInstance();

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
}