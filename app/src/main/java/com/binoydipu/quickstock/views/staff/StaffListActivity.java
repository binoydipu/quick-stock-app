package com.binoydipu.quickstock.views.staff;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.binoydipu.quickstock.R;
import com.binoydipu.quickstock.services.auth.AuthUser;
import com.binoydipu.quickstock.services.cloud.FirebaseCloudStorage;
import com.binoydipu.quickstock.views.staff.adapter.StaffListAdapter;

import java.util.ArrayList;
import java.util.Objects;

public class StaffListActivity extends AppCompatActivity {

    private EditText etSearchKeyword;
    private ImageView ivClearSearch, ivSearchStaff, ivToolbarBack;
    private ProgressBar progressBar;
    private RecyclerView rvStaffLists;

    private ArrayList<AuthUser> staffList;
    private FirebaseCloudStorage cloudStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        ivToolbarBack = findViewById(R.id.toolbar_back_btn);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        etSearchKeyword = findViewById(R.id.search_keywords_et);
        ivClearSearch = findViewById(R.id.clear_search_text_iv);
        ivSearchStaff = findViewById(R.id.search_staff_btn);
        progressBar = findViewById(R.id.progress_circular);
        rvStaffLists = findViewById(R.id.staff_list_recyclerview);
        staffList = new ArrayList<>();
        cloudStorage = FirebaseCloudStorage.getInstance();

        progressBar.setVisibility(View.VISIBLE);
        staffList = cloudStorage.getAllUsers(this, isReceived -> {
            progressBar.setVisibility(View.GONE);
            if(isReceived) {
                displayStaffList();
            }
        });

        ivToolbarBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        ivSearchStaff.setOnClickListener(v -> {
            displayStaffList();
            etSearchKeyword.clearFocus();
            // to close keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etSearchKeyword.getWindowToken(), 0);
        });
        ivClearSearch.setOnClickListener(v -> etSearchKeyword.setText(""));
    }

    private void displayStaffList() {
        String searchText = etSearchKeyword.getText().toString().trim();
        rvStaffLists.setLayoutManager(new LinearLayoutManager(this));
        StaffListAdapter staffListAdapter = new StaffListAdapter(this, staffList);
        staffListAdapter.filterStaffs(searchText);
        rvStaffLists.setAdapter(staffListAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        staffList = cloudStorage.getAllUsers(this, isReceived -> {
            progressBar.setVisibility(View.GONE);
            if(isReceived) {
                displayStaffList();
            }
        });
    }
}