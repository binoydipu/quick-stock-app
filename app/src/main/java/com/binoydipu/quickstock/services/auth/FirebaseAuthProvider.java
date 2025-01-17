package com.binoydipu.quickstock.services.auth;

import static com.binoydipu.quickstock.constants.ConstantValues.ADMIN_EMAIL;
import static com.binoydipu.quickstock.services.auth.AuthProviderConstants.ON_LOGIN_FAILURE;
import static com.binoydipu.quickstock.services.auth.AuthProviderConstants.ON_LOGIN_PENDING_EMAIL_VERIFICATION;
import static com.binoydipu.quickstock.services.auth.AuthProviderConstants.ON_LOGIN_SUCCESSFUL;
import static com.binoydipu.quickstock.services.auth.AuthProviderConstants.ON_USER_CREATION_FAILURE;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.binoydipu.quickstock.services.cloud.FirebaseCloudStorage;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseAuthProvider {

    private static final String TAG = "FirebaseAuthProvider";
    private static FirebaseAuthProvider instance;
    private final FirebaseAuth mAuth;

    private FirebaseAuthProvider() {
        mAuth = FirebaseAuth.getInstance();
    }

    public static FirebaseAuthProvider getInstance() {
        if (instance == null) {
            instance = new FirebaseAuthProvider();
        }
        return instance;
    }

    public String getCurrentUserEmail() {
        assert mAuth.getCurrentUser() != null;
        return mAuth.getCurrentUser().getEmail();
    }

    public String getCurrentUserId() {
        assert mAuth.getCurrentUser() != null;
        return mAuth.getCurrentUser().getUid();
    }

    public boolean isUserLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }

    public boolean isUserEmailVerified() {
        assert mAuth.getCurrentUser() != null;
        return mAuth.getCurrentUser().isEmailVerified();
    }

    public void isUserAuthenticated(String password, OnUserAuthenticationListener listener) {
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        user.reauthenticate(
                EmailAuthProvider.getCredential(user.getEmail(), password))
                .addOnCompleteListener(task -> {
                    listener.onUserAuthenticated(task.isSuccessful());
                });
    }

    public boolean logOut() {
        if(mAuth.getCurrentUser() == null) {
            Log.w(TAG, "logOut:userNotFound");
            return false;
        }
        mAuth.signOut();
        Log.d(TAG, "logOut:success");
        return true;
    }

    public void createUser(Context context, String email, String password, OnUserCreationListener listener) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        Log.d(TAG, "createUser:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        assert user != null;
                        sendEmailVerification(context);
                        Toast.makeText(context, "Account Created!", Toast.LENGTH_SHORT).show();
                        listener.onUserCreated(user.getUid()); // Notify success with user ID
                    } else {
                        Log.w(TAG, " "+ task.getException());
                        if (task.getException() instanceof FirebaseAuthUserCollisionException){
                            Toast.makeText(context, "User Already Exists!!", Toast.LENGTH_SHORT).show();
                        } else{
                            Toast.makeText(context, "Authentication Failed!", Toast.LENGTH_SHORT).show();
                        }
                        listener.onUserCreated(ON_USER_CREATION_FAILURE); // Notify failure
                    }
                });

    }

    public void deleteUser(Context context, String userId, String password, OnUserDeletedListener listener) {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null && user.getUid().equals(userId)) {
            // Re-authenticate the user before deleting
            user.reauthenticate(EmailAuthProvider.getCredential(user.getEmail(), password))
                    .addOnCompleteListener(reauthTask -> {
                        if (reauthTask.isSuccessful()) {
                            Log.d(TAG, "Re-authentication successful");
                            FirebaseCloudStorage.getInstance().deleteUserByUserId(userId, isDeleted -> {
                                if (isDeleted) {
                                    Log.d(TAG, "User data deleted from Firestore");

                                    user.delete().addOnCompleteListener(deleteTask -> {
                                        if (deleteTask.isSuccessful()) {
                                            Log.d(TAG, "User successfully deleted from Firebase Auth");
                                            listener.onUserDeleted(true);
                                        } else {
                                            Log.w(TAG, "Failed to delete user from Firebase Auth", deleteTask.getException());
                                            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                                            listener.onUserDeleted(false);
                                        }
                                    });

                                } else {
                                    Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                                    Log.w(TAG, "Failed to delete user data from Firestore");
                                    listener.onUserDeleted(false);
                                }
                            });
                        } else {
                            Log.w(TAG, "Re-authentication failed", reauthTask.getException());
                            Toast.makeText(context, "Re-authentication required before deleting account", Toast.LENGTH_SHORT).show();
                            listener.onUserDeleted(false);
                        }
                    });
        } else {
            Log.w(TAG, "User not authenticated or ID mismatch");
            listener.onUserDeleted(false);
        }
    }


    public void logIn(Context context, String email, String password, OnLoginEventListener listener) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        Log.d(TAG, "logIn:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        if(user != null && (user.isEmailVerified() || email.equals(ADMIN_EMAIL))) {
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
                            Log.d(TAG, "sendEmailVerification:success");
                            Toast.makeText(context, "Verification Email Sent", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.w(TAG, "sendEmailVerification:failure- "+ task.getException());
                            Toast.makeText(context, "Error Sending Verification Email. Try again Later", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void sendPasswordResetEmail(Context context, String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "sendPasswordResetEmail:success");
                        Toast.makeText(context, "Password Reset Email Sent", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.w(TAG, "sendPasswordResetEmail:failure- "+ task.getException());
                        Toast.makeText(context, "Error Sending Email.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Log.w(TAG, e.toString()));
    }

    public interface OnUserCreationListener {
        void onUserCreated(String userStatus);
    }

    public interface OnLoginEventListener {
        void onLoginSuccess(String loginStatus);
    }

    public interface OnUserAuthenticationListener {
        void onUserAuthenticated(boolean isAuthenticated);
    }

    public interface OnUserDeletedListener {
        void onUserDeleted(boolean isUserDeleted);
    }
}
