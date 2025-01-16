package com.binoydipu.quickstock.views;

import static com.binoydipu.quickstock.constants.ConstantValues.PREF_SWITCH;
import static com.binoydipu.quickstock.constants.ConstantValues.SWITCH_KEY;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.binoydipu.quickstock.R;
import com.binoydipu.quickstock.services.auth.FirebaseAuthProvider;
import com.binoydipu.quickstock.services.cloud.FirebaseCloudStorage;
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
    private TextView tvUserName, tvEmail, tvStaffId, tvMobileNo;
    private FirebaseAuthProvider authProvider;
    private FirebaseCloudStorage cloudStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        ivToolbarBack = findViewById(R.id.toolbar_back_btn);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        tvUserName = findViewById(R.id.user_name_tv);
        tvEmail = findViewById(R.id.user_email_tv);
        tvStaffId = findViewById(R.id.staff_id_tv);
        tvMobileNo = findViewById(R.id.mobile_no_tv);
        notificationSwitch = findViewById(R.id.notification_switch);
        btnEditProfile = findViewById(R.id.edit_profile_btn);
        logoutLayout = findViewById(R.id.logout_layout);
        sharedPreferences = getSharedPreferences(PREF_SWITCH, MODE_PRIVATE);
        authProvider = FirebaseAuthProvider.getInstance();
        cloudStorage = FirebaseCloudStorage.getInstance();

        // Check Previously Saved Preferences
        checkPreferences();
        setLayoutValues();

        btnEditProfile.setOnClickListener(v -> startActivity(new Intent(this, EditProfileActivity.class)));

        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(SWITCH_KEY, isChecked);
            editor.apply();
        });

        logoutLayout.setOnClickListener(v -> DialogHelper.logoutDialog(this));
        ivToolbarBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    private void setLayoutValues() {
        cloudStorage.getUserByUserId(authProvider.getCurrentUserId(), user -> {
            if(user != null) {
                tvUserName.setText(user.getUserName());
                tvEmail.setText(user.getUserEmail());
                tvStaffId.setText(user.getStaffId());
                tvMobileNo.setText(user.getMobileNo());
            } else {
                Toast.makeText(this, "Failed to retrieve user info", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkPreferences() {
        boolean isSwitchOn = sharedPreferences.getBoolean(SWITCH_KEY, false);
        notificationSwitch.setChecked(isSwitchOn);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setLayoutValues();
    }
}