package com.binoydipu.quickstock.views;

import static com.binoydipu.quickstock.constants.ConstantValues.PREF_SWITCH;
import static com.binoydipu.quickstock.constants.ConstantValues.SWITCH_KEY;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.binoydipu.quickstock.R;
import com.binoydipu.quickstock.utilities.dialogs.DialogHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    private MaterialSwitch notificationSwitch;
    private MaterialButton btnEditProfile;
    private SharedPreferences sharedPreferences;
    private ImageView ivToolbarBack;
    private ConstraintLayout logoutLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        ivToolbarBack = findViewById(R.id.toolbar_back_btn);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        notificationSwitch = findViewById(R.id.notification_switch);
        btnEditProfile = findViewById(R.id.edit_profile_btn);
        logoutLayout = findViewById(R.id.logout_layout);
        sharedPreferences = getSharedPreferences(PREF_SWITCH, MODE_PRIVATE);

        // Check Previously Saved Preferences
        checkPreferences();

        btnEditProfile.setOnClickListener(v -> {
            Toast.makeText(this, "Not Available Yet", Toast.LENGTH_SHORT).show();
        });

        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(SWITCH_KEY, isChecked);
            editor.apply();
        });

        logoutLayout.setOnClickListener(v -> DialogHelper.logoutDialog(this));
        ivToolbarBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    private void checkPreferences() {
        boolean isSwitchOn = sharedPreferences.getBoolean(SWITCH_KEY, false);
        notificationSwitch.setChecked(isSwitchOn);
    }
}