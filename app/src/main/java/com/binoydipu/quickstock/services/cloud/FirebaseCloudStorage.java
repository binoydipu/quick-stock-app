package com.binoydipu.quickstock.services.cloud;

import android.util.Log;

import com.binoydipu.quickstock.services.auth.AuthUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

// Singleton Class
public class FirebaseCloudStorage {

    private static final String TAG = "FirebaseCloudStorage";
    private static final String USER_COLLECTION = "users";
    private static FirebaseCloudStorage instance;
    private final FirebaseFirestore firestore;

    // Private constructor for singleton
    private FirebaseCloudStorage() {
        firestore = FirebaseFirestore.getInstance();
    }

    public static FirebaseCloudStorage getInstance() {
        if (instance == null) {
            instance = new FirebaseCloudStorage();
        }
        return instance;
    }

    public void storeUserInfoInFirestore(String userId, String name, String id, String email, String mobile, boolean emailVerified, OnUserInfoStoredListener listener) {
        DocumentReference db = firestore.collection(USER_COLLECTION).document(userId);
        AuthUser userInfo = new AuthUser(userId, name, id, email, mobile, emailVerified);

        db.set(userInfo)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "storedUserInFirestore:success");
                    listener.onUserInfoStored(true); // Notify success
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Failed to store user info: " + e);
                    listener.onUserInfoStored(false); // Notify failure
                });
    }

    public interface OnUserInfoStoredListener {
        void onUserInfoStored(boolean isInfoStored);
    }
}