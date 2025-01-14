package com.binoydipu.quickstock.services.cloud;

import static com.binoydipu.quickstock.services.cloud.CloudStorageConstants.ITEM_COLLECTION;
import static com.binoydipu.quickstock.services.cloud.CloudStorageConstants.ON_ITEM_ADDED;
import static com.binoydipu.quickstock.services.cloud.CloudStorageConstants.ON_ITEM_CHECK_FAILURE;
import static com.binoydipu.quickstock.services.cloud.CloudStorageConstants.ON_ITEM_EXISTS;
import static com.binoydipu.quickstock.services.cloud.CloudStorageConstants.USER_COLLECTION;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.binoydipu.quickstock.services.auth.AuthUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

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
                           int stockQuantity, long expireDateInMillis, double stockValue, OnNewItemAddedListener listener) {
        DocumentReference db = firestore.collection(ITEM_COLLECTION).document(itemName);
        db.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    Log.w(TAG, "addNewItem: Item already exists");
                    listener.onItemAdded(ON_ITEM_EXISTS); // Notify failure due to duplicate item
                } else {
                    // item doesn't exist
                    ItemModel itemModel = new ItemModel(itemName, itemCode, purchasePrice, salePrice, stockQuantity, expireDateInMillis, stockValue);
                    db.set(itemModel)
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "addNewItem:success");
                                listener.onItemAdded(ON_ITEM_ADDED); // Notify success
                            })
                            .addOnFailureListener(e -> {
                                Log.w(TAG, "addNewItem:failure - " + e);
                                listener.onItemAdded(ON_ITEM_CHECK_FAILURE); // Notify failure
                            });
                }
            } else {
                Log.e(TAG, "addNewItem: Error checking document existence", task.getException());
                listener.onItemAdded(ON_ITEM_CHECK_FAILURE); // Notify failure
            }
        });
    }

    public void updateItem(String itemName, String itemCode, double purchasePrice, double salePrice,
                           int stockQuantity, long expireDateInMillis, double stockValue, OnNewItemUpdatedListener listener) {
        DocumentReference db = firestore.collection(ITEM_COLLECTION).document(itemName);
        ItemModel itemModel = new ItemModel(itemName, itemCode, purchasePrice, salePrice, stockQuantity, expireDateInMillis, stockValue);

        db.set(itemModel)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "updateItem:success");
                    listener.onItemUpdated(true); // Notify success
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "updateItem:failure- " + e);
                    listener.onItemUpdated(false); // Notify failure
                });
    }

    public void deleteItemByName(String itemName, OnItemDeletedListener listener) {
        DocumentReference db = firestore.collection(ITEM_COLLECTION).document(itemName);

        db.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    db.delete()
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "deleteItemByName:success");
                                listener.onItemDeleted(true); // Notify success
                            })
                            .addOnFailureListener(e -> {
                                Log.w(TAG, "deleteItemByName:failed", e);
                                listener.onItemDeleted(false); // Notify failure
                            });
                } else {
                    Log.w(TAG, "deleteItemByName: Item not found");
                    listener.onItemDeleted(false); // Item doesn't exist
                }
            } else {
                Log.e(TAG, "deleteItemByName: Error checking item existence", task.getException());
                listener.onItemDeleted(false); // Error checking existence
            }
        });
    }

    public void getItemByName(String itemName, OnItemRetrievedListener listener) {
        DocumentReference db = firestore.collection(ITEM_COLLECTION).document(itemName);

        db.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    ItemModel item = document.toObject(ItemModel.class);
                    Log.d(TAG, "getItemByName:success");
                    listener.onItemRetrieved(item); // Return the item through the listener
                } else {
                    Log.w(TAG, "getItemByName:not found");
                    listener.onItemRetrieved(null);
                }
            } else {
                Log.e(TAG, "getItemByName:error", task.getException());
                listener.onItemRetrieved(null);
            }
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
                        listener.onItemDataReceived(true);
                    } else {
                        Log.w(TAG, "getAllItems:failure- " + task.getException());
                        Toast.makeText(context, "Failed to Retrieve Data", Toast.LENGTH_SHORT).show();
                        listener.onItemDataReceived(false);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "getAllItems:failure- " + e);
                    Toast.makeText(context, "Failed to Retrieve Data", Toast.LENGTH_SHORT).show();
                    listener.onItemDataReceived(false);
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
        void onItemAdded(String itemStatus);
    }

    public interface OnNewItemUpdatedListener {
        void onItemUpdated(boolean isItemUpdated);
    }

    public interface OnItemRetrievedListener {
        void onItemRetrieved(ItemModel itemModel);
    }

    public interface OnItemDeletedListener {
        void onItemDeleted(boolean isItemDeleted);
    }

    public interface OnItemsReceivedListener {
        void onItemDataReceived(boolean isReceived);
    }
}