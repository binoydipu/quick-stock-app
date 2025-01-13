package com.binoydipu.quickstock.utilities.dialogs;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.binoydipu.quickstock.R;
import com.binoydipu.quickstock.services.auth.FirebaseAuthProvider;
import com.binoydipu.quickstock.views.LoginActivity;

public class DialogHelper {
    public DialogHelper() {}

    public static void logoutDialog(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setIcon(R.drawable.quick_stock)
                .setPositiveButton("Yes", (dialog, which) -> {
                    if(FirebaseAuthProvider.getInstance().logOut()) {
                        Toast.makeText(context, "Logout Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, LoginActivity.class);
                        context.startActivity(intent);
                        if (context instanceof Activity) {
                            ((Activity) context).finish();
                        }
                    } else {
                        Toast.makeText(context, "Could Not Logout User", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
