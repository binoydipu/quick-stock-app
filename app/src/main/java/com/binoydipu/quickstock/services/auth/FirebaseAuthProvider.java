package com.binoydipu.quickstock.services.auth;

import static com.binoydipu.quickstock.constants.ConstantValues.ON_LOGIN_FAILURE;
import static com.binoydipu.quickstock.constants.ConstantValues.ON_LOGIN_PENDING_EMAIL_VERIFICATION;
import static com.binoydipu.quickstock.constants.ConstantValues.ON_LOGIN_SUCCESSFUL;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

// Singleton Class
public class FirebaseAuthProvider {

    private static final String TAG = "FirebaseAuthProvider";
    private static FirebaseAuthProvider instance;
    private final FirebaseAuth mAuth;
    private final FirebaseFirestore firestore;

    private FirebaseAuthProvider() {
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    public static FirebaseAuthProvider getInstance() {
        if (instance == null) {
            instance = new FirebaseAuthProvider();
        }
        return instance;
    }

    public AuthUser getCurrentUser() {
        FirebaseUser user = mAuth.getCurrentUser();
        if(user == null) {
            return null;
        }
        AuthUser userInfo = new AuthUser();
        firestore.collection("users").whereEqualTo("userId", user.getUid()).get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot snapshot = task.getResult().getDocuments().get(1);
                        String email = Objects.requireNonNull(snapshot.get("userEmail")).toString();
                    }
                })
                .addOnFailureListener(e -> {

                });
        // TODO: Complete the Get Current User Method
        return userInfo;
    }

    public void createUser(Context context, String email, String password, OnUserCreationListener listener) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        assert user != null;
                        sendEmailVerification(context);
                        Toast.makeText(context, "Account Created!", Toast.LENGTH_SHORT).show();
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

    public void loginWithFirebase(Context context, String email, String password, OnLoginEventListener listener) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        Log.d(TAG, "loginUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        if(user != null && user.isEmailVerified()) {
                            Toast.makeText(context, "Login Success!", Toast.LENGTH_SHORT).show();
                            listener.onLoginSuccess(ON_LOGIN_SUCCESSFUL); // successful
                        } else {
                            Toast.makeText(context, "Please Verify Your Email!", Toast.LENGTH_SHORT).show();
                            listener.onLoginSuccess(ON_LOGIN_PENDING_EMAIL_VERIFICATION); // pending email verification
                        }
                    }
                    else {
                        Log.w(TAG, " "+ task.getException());
                        listener.onLoginSuccess(ON_LOGIN_FAILURE);
                        if(task.getException() instanceof FirebaseAuthInvalidUserException) {
                            Toast.makeText(context, "User Does Not Exists!", Toast.LENGTH_SHORT).show();
                        } else if(task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(context, "Invalid Credentials!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Login Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void sendEmailVerification(Context context) {
        FirebaseUser user = mAuth.getCurrentUser();
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

    public interface OnLoginEventListener {
        void onLoginSuccess(String loginStatus);
    }
}
