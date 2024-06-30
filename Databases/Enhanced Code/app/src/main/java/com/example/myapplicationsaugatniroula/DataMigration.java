package com.example.myapplicationsaugatniroula;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class DataMigration {
    private SQLiteDatabase sqliteDb;
    private FirebaseFirestore firestoreDb;

    // Encryption key for AES encryption
    private static final String SECRET_KEY = "my_super_secret_key";

    public DataMigration(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context); // Assuming DatabaseHelper is used
        sqliteDb = dbHelper.getReadableDatabase();
        firestoreDb = FirebaseFirestore.getInstance();
    }

    // Method to migrate all data (items and users)
    public void migrateData() {
        migrateItems();
        migrateUsers();
    }

    // Method to migrate items data
    public void migrateItems() {
        String[] projection = {
                DatabaseContract.InventoryEntry.FIELD_ITEM_NUMBER,
                DatabaseContract.InventoryEntry.FIELD_ITEM_NAME,
                DatabaseContract.InventoryEntry.FIELD_QUANTITY
        };

        Cursor cursor = sqliteDb.query(
                DatabaseContract.InventoryEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            int itemNumber = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.InventoryEntry.FIELD_ITEM_NUMBER));
            String itemName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.InventoryEntry.FIELD_ITEM_NAME));
            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.InventoryEntry.FIELD_QUANTITY));

            Map<String, Object> item = new HashMap<>();
            item.put("itemNumber", itemNumber);
            item.put("itemName", itemName);
            item.put("quantity", quantity);

            firestoreDb.collection("items").add(item)
                    .addOnSuccessListener(documentReference -> Log.d("DataMigration", "DocumentSnapshot added with ID: " + documentReference.getId()))
                    .addOnFailureListener(e -> Log.w("DataMigration", "Error adding document", e));
        }

        cursor.close();
    }

    // Method to migrate users data with encryption
    public void migrateUsers() {
        String[] projection = {
                DatabaseContract.UserEntry.FIELD_FIRST_NAME,
                DatabaseContract.UserEntry.FIELD_LAST_NAME,
                DatabaseContract.UserEntry.FIELD_USERNAME,
                DatabaseContract.UserEntry.FIELD_PASSWORD
        };

        Cursor cursor = sqliteDb.query(
                DatabaseContract.UserEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            String firstName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.UserEntry.FIELD_FIRST_NAME));
            String lastName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.UserEntry.FIELD_LAST_NAME));
            String username = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.UserEntry.FIELD_USERNAME));
            String password = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.UserEntry.FIELD_PASSWORD));

            try {
                String encryptedPassword = encrypt(password, SECRET_KEY);

                Map<String, Object> user = new HashMap<>();
                user.put("firstName", firstName);
                user.put("lastName", lastName);
                user.put("username", username);
                user.put("password", encryptedPassword);

                firestoreDb.collection("users").add(user)
                        .addOnSuccessListener(documentReference -> Log.d("DataMigration", "DocumentSnapshot added with ID: " + documentReference.getId()))
                        .addOnFailureListener(e -> Log.w("DataMigration", "Error adding document", e));
            } catch (Exception e) {
                Log.e("DataMigration", "Error encrypting password", e);
            }
        }

        cursor.close();
    }

    // Method to encrypt a string using AES
    public String encrypt(String data, String secretKey) throws Exception {
        SecretKeySpec key = generateKey(secretKey);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedData = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return android.util.Base64.encodeToString(encryptedData, android.util.Base64.DEFAULT);
    }

    // Method to decrypt a string using AES
    public String decrypt(String encryptedData, String secretKey) throws Exception {
        SecretKeySpec key = generateKey(secretKey);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedValue = android.util.Base64.decode(encryptedData, android.util.Base64.DEFAULT);
        byte[] decryptedData = cipher.doFinal(decodedValue);
        return new String(decryptedData, StandardCharsets.UTF_8);
    }

    // Method to generate an AES key from the secret key
    private SecretKeySpec generateKey(String secretKey) throws NoSuchAlgorithmException {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = secretKey.getBytes(StandardCharsets.UTF_8);
        digest.update(bytes, 0, bytes.length);
        byte[] key = digest.digest();
        return new SecretKeySpec(key, "AES");
    }
}
