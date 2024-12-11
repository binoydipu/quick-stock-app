package com.binoydipu.quickstock.services.auth;

import static com.binoydipu.quickstock.RegisterActivity.TAG;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.binoydipu.quickstock.RegisterActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseAuthProvider { // Singleton Class
    private static FirebaseAuthProvider instance;

    private FirebaseAuthProvider() {}

    public static FirebaseAuthProvider getInstance() {
        if (instance == null) {
            instance = new FirebaseAuthProvider();
        }
        return instance;
    }

    public void createUser(Context context, String email, String password, OnUserCreationListener listener) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        assert user != null;
                        sendEmailVerification(context, user);
                        listener.onUserCreated(true); // Notify success
                    } else {
                        Log.w(TAG, " "+ task.getException());
                        if (task.getException() instanceof FirebaseAuthUserCollisionException){
                            Toast.makeText(context, "User Already Exists!!", Toast.LENGTH_SHORT).show();
                        } else{
                            Toast.makeText(context, "Authentication Failed!", Toast.LENGTH_SHORT).show();
                        }
                        listener.onUserCreated(false); // Notify failure
                    }
                });

    }

    public void sendEmailVerification(Context context, FirebaseUser user) {
        if (user != null && !user.isEmailVerified()) {
            user.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Verification Email Sent.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Error Sending Verification Email.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public interface OnUserCreationListener {
        void onUserCreated(boolean isSuccessful);
    }

    public interface OnUserInfoStoredListener {
        void onUserInfoStored(boolean isInfoStored);
    }
}
