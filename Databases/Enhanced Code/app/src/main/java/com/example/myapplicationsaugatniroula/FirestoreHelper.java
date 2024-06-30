package com.example.myapplicationsaugatniroula;

import android.content.Context;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.HashMap;
import java.util.Map;

/**
 * FirestoreHelper class using Singleton Pattern to interact with Firebase Firestore.
 */
public class FirestoreHelper {
    // Singleton instance
    private static FirestoreHelper instance;
    // Firestore database instance
    private final FirebaseFirestore db;

    // Private constructor to prevent direct instantiation
    private FirestoreHelper() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Static method to get the singleton instance.
     *
     * @return FirestoreHelper instance
     */
    public static synchronized FirestoreHelper getInstance() {
        if (instance == null) {
            instance = new FirestoreHelper();
        }
        return instance;
    }

    /**
     * Add or update inventory item in Firestore.
     *
     * @param itemNumber The item number
     * @param itemName   The item name
     * @param quantity   The item quantity
     * @return Task representing the asynchronous operation
     */
    public Task<Void> addOrUpdateInventoryItem(int itemNumber, String itemName, int quantity) {
        CollectionReference inventoryRef = db.collection(DatabaseContract.InventoryEntry.COLLECTION_NAME);
        Map<String, Object> item = new HashMap<>();
        item.put(DatabaseContract.InventoryEntry.FIELD_ITEM_NUMBER, itemNumber);
        item.put(DatabaseContract.InventoryEntry.FIELD_ITEM_NAME, itemName);
        item.put(DatabaseContract.InventoryEntry.FIELD_QUANTITY, quantity);
        return inventoryRef.document(String.valueOf(itemNumber)).set(item, SetOptions.merge());
    }

    /**
     * Delete inventory item from Firestore.
     *
     * @param itemNumber The item number
     * @return Task representing the asynchronous operation
     */
    public Task<Void> deleteInventoryItem(int itemNumber) {
        CollectionReference inventoryRef = db.collection(DatabaseContract.InventoryEntry.COLLECTION_NAME);
        return inventoryRef.document(String.valueOf(itemNumber)).delete();
    }

    /**
     * Query inventory items from Firestore.
     *
     * @return Task representing the asynchronous operation that will return QuerySnapshot
     */
    public Task<QuerySnapshot> getInventoryItems() {
        CollectionReference inventoryRef = db.collection(DatabaseContract.InventoryEntry.COLLECTION_NAME);
        return inventoryRef.get();
    }

    /**
     * Add or update user in Firestore.
     *
     * @param username The username
     * @param firstName The first name
     * @param lastName The last name
     * @param password The password (should be encrypted in production)
     * @return Task representing the asynchronous operation
     */
    public Task<Void> addOrUpdateUser(String username, String firstName, String lastName, String password) {
        CollectionReference usersRef = db.collection(DatabaseContract.UserEntry.COLLECTION_NAME);
        Map<String, Object> user = new HashMap<>();
        user.put(DatabaseContract.UserEntry.FIELD_FIRST_NAME, firstName);
        user.put(DatabaseContract.UserEntry.FIELD_LAST_NAME, lastName);
        user.put(DatabaseContract.UserEntry.FIELD_USERNAME, username);
        user.put(DatabaseContract.UserEntry.FIELD_PASSWORD, password); // Consider encrypting password
        return usersRef.document(username).set(user, SetOptions.merge());
    }

    /**
     * Delete user from Firestore.
     *
     * @param username The username
     * @return Task representing the asynchronous operation
     */
    public Task<Void> deleteUser(String username) {
        CollectionReference usersRef = db.collection(DatabaseContract.UserEntry.COLLECTION_NAME);
        return usersRef.document(username).delete();
    }

    /**
     * Query users from Firestore.
     *
     * @return Task representing the asynchronous operation that will return QuerySnapshot
     */
    public Task<QuerySnapshot> getUsers() {
        CollectionReference usersRef = db.collection(DatabaseContract.UserEntry.COLLECTION_NAME);
        return usersRef.get();
    }
}
