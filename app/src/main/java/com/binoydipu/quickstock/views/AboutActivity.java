package com.binoydipu.quickstock.views;

import static com.binoydipu.quickstock.constants.ConstantValues.DEV_EMAIL;
import static com.binoydipu.quickstock.constants.ConstantValues.DEV_FACEBOOK;
import static com.binoydipu.quickstock.constants.ConstantValues.DEV_GITHUB;
import static com.binoydipu.quickstock.constants.ConstantValues.DEV_LINKEDIN;
import static com.binoydipu.quickstock.constants.ConstantValues.DEV_YOUTUBE;
import static com.binoydipu.quickstock.constants.ConstantValues.PROJECT_GITHUB;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.binoydipu.quickstock.R;
import com.binoydipu.quickstock.utilities.browser.OpenLinkHelper;

import java.util.Objects;

public class AboutActivity extends AppCompatActivity {

    private ImageView ivToolbarBack, ivFacebook, ivLinkedin, ivYoutube, ivEmail, ivGithub;
    private TextView tvProjectGithubLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar toolbar = findViewById(R.id.toolbar);
        ivToolbarBack = findViewById(R.id.toolbar_back_btn);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        ivFacebook = findViewById(R.id.dev_facebook);
        ivLinkedin = findViewById(R.id.dev_linkedin);
        ivYoutube = findViewById(R.id.dev_youtube);
        ivEmail = findViewById(R.id.dev_email);
        ivGithub = findViewById(R.id.dev_github);
        tvProjectGithubLink = findViewById(R.id.project_github_link);

        ivFacebook.setOnClickListener(v -> OpenLinkHelper.openLinkInCustomTab(this, DEV_FACEBOOK));
        ivLinkedin.setOnClickListener(v -> OpenLinkHelper.openLinkInCustomTab(this, DEV_LINKEDIN));
        ivYoutube.setOnClickListener(v -> OpenLinkHelper.openLinkInCustomTab(this, DEV_YOUTUBE));
        ivEmail.setOnClickListener(v -> OpenLinkHelper.openLinkInCustomTab(this, DEV_EMAIL));
        ivGithub.setOnClickListener(v -> OpenLinkHelper.openLinkInCustomTab(this, DEV_GITHUB));
        tvProjectGithubLink.setOnClickListener(v -> OpenLinkHelper.openLinkInCustomTab(this, PROJECT_GITHUB));

        ivToolbarBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }
}