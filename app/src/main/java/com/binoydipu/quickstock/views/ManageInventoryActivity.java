package com.binoydipu.quickstock.views;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.binoydipu.quickstock.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.Objects;

public class ManageInventoryActivity extends AppCompatActivity {

    private ExtendedFloatingActionButton fbAddNewItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_inventory);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed() );

        fbAddNewItem = findViewById(R.id.add_new_item_fb);

        fbAddNewItem.setOnClickListener(v -> {
            startActivity(new Intent(this, AddNewItemActivity.class));
        });
    }
}