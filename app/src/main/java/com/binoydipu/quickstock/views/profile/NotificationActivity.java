package com.binoydipu.quickstock.views.profile;

import static com.binoydipu.quickstock.constants.ConstantValues.PREF_SWITCH;
import static com.binoydipu.quickstock.constants.ConstantValues.SWITCH_KEY;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.binoydipu.quickstock.R;
import com.google.android.material.materialswitch.MaterialSwitch;

import java.util.Objects;

public class NotificationActivity extends AppCompatActivity {

    private ImageView ivToolbarBack;
    private MaterialSwitch notificationSwitch;
    private ProgressBar progressBar;
    private RecyclerView rvNotification;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        Toolbar toolbar = findViewById(R.id.toolbar);
        ivToolbarBack = findViewById(R.id.toolbar_back_btn);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        notificationSwitch = findViewById(R.id.notification_switch);
        progressBar = findViewById(R.id.progress_circular);
        rvNotification = findViewById(R.id.notification_recyclerview);
        sharedPreferences = getSharedPreferences(PREF_SWITCH, MODE_PRIVATE);

        // Check Previously Saved Preferences
        checkPreferences();

        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(SWITCH_KEY, isChecked);
            editor.apply();
        });

        ivToolbarBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    private void checkPreferences() {
        boolean isSwitchOn = sharedPreferences.getBoolean(SWITCH_KEY, false);
        notificationSwitch.setChecked(isSwitchOn);
    }
}