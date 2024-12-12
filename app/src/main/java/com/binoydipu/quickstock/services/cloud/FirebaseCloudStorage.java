package com.binoydipu.quickstock.services.cloud;

import static com.binoydipu.quickstock.services.cloud.CloudStorageConstants.FIELD_STAFF_ID;
import static com.binoydipu.quickstock.services.cloud.CloudStorageConstants.FIELD_UID;
import static com.binoydipu.quickstock.services.cloud.CloudStorageConstants.FIELD_USER_EMAIL;
import static com.binoydipu.quickstock.services.cloud.CloudStorageConstants.FIELD_USER_EMAIL_VERIFIED;
import static com.binoydipu.quickstock.services.cloud.CloudStorageConstants.FIELD_USER_MOBILE;
import static com.binoydipu.quickstock.services.cloud.CloudStorageConstants.FIELD_USER_NAME;
import static com.binoydipu.quickstock.services.cloud.CloudStorageConstants.USER_COLLECTION;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.binoydipu.quickstock.services.auth.AuthUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

// Singleton Class
public class FirebaseCloudStorage {

    private static final String TAG = "FirebaseCloudStorage";
    private static FirebaseCloudStorage instance;
    private final FirebaseFirestore firestore;

    private FirebaseCloudStorage() {
        FirebaseFirestore.setLoggingEnabled(true);
        firestore = FirebaseFirestore.getInstance();
    }

    public static FirebaseCloudStorage getInstance() {
        if (instance == null) {
            instance = new FirebaseCloudStorage();
        }
        return instance;
    }

    public void storeUserInfo(String userId, String name, String id, String email, String mobile, boolean emailVerified, OnUserInfoStoredListener listener) {
        DocumentReference db = firestore.collection(USER_COLLECTION).document(userId);
        AuthUser userInfo = new AuthUser(userId, name, id, email, mobile, emailVerified);

        db.set(userInfo)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "storeUserInfo:success");
                    listener.onUserInfoStored(true); // Notify success
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "storeUserInfo:failure- " + e);
                    listener.onUserInfoStored(false); // Notify failure
                });
    }

    public ArrayList<AuthUser> getStaffData(Context context, OnStaffDataReceivedListener listener) {
        ArrayList<AuthUser> staffList = new ArrayList<>();

        firestore.collection(USER_COLLECTION)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        Log.d(TAG, "getStaffData:success");
                        for(DocumentSnapshot doc : task.getResult()) {
                            AuthUser user = new AuthUser(
                                    doc.getString(FIELD_UID),
                                    doc.getString(FIELD_USER_NAME),
                                    doc.getString(FIELD_STAFF_ID),
                                    doc.getString(FIELD_USER_EMAIL),
                                    doc.getString(FIELD_USER_MOBILE),
                                    Boolean.TRUE.equals(doc.getBoolean(FIELD_USER_EMAIL_VERIFIED))
                            );
                            staffList.add(user);
                        }
                        listener.onStaffDataReceived(true);
                    } else {
                        Log.w(TAG, "getStaffData:failure- " + task.getException());
                        Toast.makeText(context, "Failed to Retrieve Data", Toast.LENGTH_SHORT).show();
                        listener.onStaffDataReceived(false);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "getStaffData:failure- " + e);
                    Toast.makeText(context, "Failed to Retrieve Data", Toast.LENGTH_SHORT).show();
                    listener.onStaffDataReceived(false);
                });

        return staffList;
    }

    public interface OnUserInfoStoredListener {
        void onUserInfoStored(boolean isInfoStored);
    }

    public interface OnStaffDataReceivedListener {
        void onStaffDataReceived(boolean isReceived);
    }
}