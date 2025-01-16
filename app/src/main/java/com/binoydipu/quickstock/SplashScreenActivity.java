package com.binoydipu.quickstock;

import static com.binoydipu.quickstock.constants.ConstantValues.ADMIN_EMAIL;
import static com.binoydipu.quickstock.constants.ConstantValues.SPLASH_SCREEN_TIMEOUT;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.binoydipu.quickstock.services.auth.FirebaseAuthProvider;
import com.binoydipu.quickstock.services.cloud.FirebaseCloudStorage;
import com.binoydipu.quickstock.views.HomeActivity;
import com.binoydipu.quickstock.views.LoginActivity;
import com.binoydipu.quickstock.views.NotVerifiedActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();
        // In case of user already logged in
        FirebaseAuthProvider authProvider = FirebaseAuthProvider.getInstance();
        if(authProvider.isUserLoggedIn()) { // user != null
            if(authProvider.getCurrentUserEmail().equals(ADMIN_EMAIL) || authProvider.isUserEmailVerified()) { // user email is verified
                if(authProvider.getCurrentUserEmail().equals(ADMIN_EMAIL)) {
                    Intent intent = new Intent(this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    FirebaseCloudStorage cloudStorage = FirebaseCloudStorage.getInstance();
                    cloudStorage.getUserByUserId(authProvider.getCurrentUserId(), user -> {
                        if(user != null && user.isStaffVerified()) {
                            Intent intent = new Intent(this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            startActivity(new Intent(this, NotVerifiedActivity.class));
                        }
                    });
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(() -> {
            FirebaseAuthProvider authProvider = FirebaseAuthProvider.getInstance();
            if(!authProvider.isUserLoggedIn()) {
                Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_SCREEN_TIMEOUT);
    }
}