package com.binoydipu.quickstock.utilities.browser;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.browser.customtabs.CustomTabsIntent;

public class OpenLinkHelper {
    public OpenLinkHelper() {}

    public static void openLinkInCustomTab(Context context, String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();

        try {
            customTabsIntent.launchUrl(context, Uri.parse(url));
        } catch (Exception e) {
            // If Chrome is not available, open the link in the default browser
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(intent);
        }
    }
}
