package com.binoydipu.quickstock.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.binoydipu.quickstock.R;
import com.binoydipu.quickstock.helper.adapter.StaffListAdapter;
import com.binoydipu.quickstock.services.auth.AuthUser;
import com.binoydipu.quickstock.services.cloud.FirebaseCloudStorage;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Objects;

public class StaffListActivity extends AppCompatActivity {

    private EditText etSearchKeyword;
    private ImageView ivClearSearch, ivSearchStaff;
    private ProgressBar progressBar;
    private RecyclerView rvStaffLists;

    private ArrayList<AuthUser> staffList;
    private FirebaseCloudStorage cloudStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed() );

        etSearchKeyword = findViewById(R.id.search_keywords_et);
        ivClearSearch = findViewById(R.id.clear_search_text_iv);
        ivSearchStaff = findViewById(R.id.search_staff_btn);
        progressBar = findViewById(R.id.progress_circular);
        rvStaffLists = findViewById(R.id.staff_list_recyclerview);
        staffList = new ArrayList<>();
        cloudStorage = FirebaseCloudStorage.getInstance();

        progressBar.setVisibility(View.VISIBLE);
        staffList = cloudStorage.getStaffData(this, isReceived -> {
            progressBar.setVisibility(View.GONE);
            if(isReceived) {
                displayStaffList();
            }
        });

        ivSearchStaff.setOnClickListener(v -> displayStaffList());
        ivClearSearch.setOnClickListener(v -> etSearchKeyword.setText(""));
    }

    private void displayStaffList() {
        String searchText = etSearchKeyword.getText().toString().trim();
        rvStaffLists.setLayoutManager(new LinearLayoutManager(this));
        StaffListAdapter staffListAdapter = new StaffListAdapter(this, staffList);
        staffListAdapter.filterProducts(searchText);
        rvStaffLists.setAdapter(staffListAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        staffList = cloudStorage.getStaffData(this, isReceived -> {
            progressBar.setVisibility(View.GONE);
            if(isReceived) {
                displayStaffList();
            }
        });
    }
}