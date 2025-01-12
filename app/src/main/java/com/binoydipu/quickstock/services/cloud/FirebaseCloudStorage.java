package com.binoydipu.quickstock.services.cloud;

import static com.binoydipu.quickstock.services.cloud.CloudStorageConstants.ITEM_COLLECTION;
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

    public void storeUserInfo(String userId, String name, String id, String email, String mobile,
                              boolean emailVerified, OnUserInfoStoredListener listener) {
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

    public ArrayList<AuthUser> getAllUsers(Context context, OnStaffDataReceivedListener listener) {
        ArrayList<AuthUser> staffList = new ArrayList<>();

        firestore.collection(USER_COLLECTION)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        Log.d(TAG, "getStaffData:success");
                        for(DocumentSnapshot doc : task.getResult()) {
                            AuthUser user = doc.toObject(AuthUser.class);
                            if(user != null) {
                                staffList.add(user);
                            }
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

    public void addNewItem(String itemName, String itemCode, double purchasePrice, double salePrice,
                           int stockQuantity, long expireDateInMillis, OnNewItemAddedListener listener) {
        DocumentReference db = firestore.collection(ITEM_COLLECTION).document(itemName);
        ItemModel itemModel = new ItemModel(itemName, itemCode, purchasePrice, salePrice, stockQuantity, expireDateInMillis);

        db.set(itemModel)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "addNewItem:success");
                    listener.isItemAdded(true); // Notify success
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "addNewItem:failure- " + e);
                    listener.isItemAdded(false); // Notify failure
                });
    }

    public ArrayList<ItemModel> getAllItems(Context context, OnItemsReceivedListener listener) {
        ArrayList<ItemModel> itemModels = new ArrayList<>();

        firestore.collection(ITEM_COLLECTION)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        Log.d(TAG, "getAllItems:success");
                        for(DocumentSnapshot doc : task.getResult()) {
                            ItemModel item = doc.toObject(ItemModel.class);
                            if(item != null) {
                                itemModels.add(item);
                            }
                        }
                        listener.onStaffDataReceived(true);
                    } else {
                        Log.w(TAG, "getAllItems:failure- " + task.getException());
                        Toast.makeText(context, "Failed to Retrieve Data", Toast.LENGTH_SHORT).show();
                        listener.onStaffDataReceived(false);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "getAllItems:failure- " + e);
                    Toast.makeText(context, "Failed to Retrieve Data", Toast.LENGTH_SHORT).show();
                    listener.onStaffDataReceived(false);
                });

        return itemModels;
    }

    public interface OnUserInfoStoredListener {
        void onUserInfoStored(boolean isInfoStored);
    }

    public interface OnStaffDataReceivedListener {
        void onStaffDataReceived(boolean isReceived);
    }

    public interface OnNewItemAddedListener {
        void isItemAdded(boolean isInfoStored);
    }

    public interface OnItemsReceivedListener {
        void onStaffDataReceived(boolean isReceived);
    }
}