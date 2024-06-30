package com.example.myapplicationsaugatniroula;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.test.core.app.ApplicationProvider;

import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;

public class ExampleUnitTest {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private DataMigration dataMigration;
    private FirebaseFirestore mockFirestore;

    @Before
    public void setUp() throws Exception {
        Context context = ApplicationProvider.getApplicationContext();
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        mockFirestore = Mockito.mock(FirebaseFirestore.class);
        dataMigration = new DataMigration(context);

        // Access the private field firestoreDb in DataMigration using reflection
        Field field = DataMigration.class.getDeclaredField("firestoreDb");
        field.setAccessible(true);
        field.set(dataMigration, mockFirestore);
    }

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testAddItem() {
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.DataEntry.COLUMN_ITEM_NAME, "TestItem");
        values.put(DatabaseContract.DataEntry.COLUMN_QUANTITY, 10);

        long newRowId = db.insert(DatabaseContract.DataEntry.TABLE_NAME, null, values);
        assertTrue(newRowId != -1);
    }

    @Test
    public void testRetrieveItems() {
        testAddItem();  // Add a test item first

        Cursor cursor = db.query(
                DatabaseContract.DataEntry.TABLE_NAME,
                null, null, null, null, null, null
        );

        assertNotNull(cursor);
        assertTrue(cursor.getCount() > 0);
        cursor.close();
    }

    @Test
    public void testMigrateItemsToFirestore() {
        // Add a test item
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.DataEntry.COLUMN_ITEM_NAME, "TestItem");
        values.put(DatabaseContract.DataEntry.COLUMN_QUANTITY, 10);
        db.insert(DatabaseContract.DataEntry.TABLE_NAME, null, values);

        // Mock Firestore behavior
        doAnswer(invocation -> {
            Map<String, Object> item = invocation.getArgument(0);
            assertEquals("TestItem", item.get("itemName"));
            assertEquals(10, item.get("quantity"));
            return null;
        }).when(mockFirestore).collection("items").add(anyMap());

        // Perform migration
        dataMigration.migrateItems();
    }

    @Test
    public void testMigrateUsersToFirestore() {
        // Add a test user
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.UserEntry.FIELD_FIRST_NAME, "John");
        values.put(DatabaseContract.UserEntry.FIELD_LAST_NAME, "Doe");
        values.put(DatabaseContract.UserEntry.FIELD_USERNAME, "johndoe");
        values.put(DatabaseContract.UserEntry.FIELD_PASSWORD, "password123");
        db.insert(DatabaseContract.UserEntry.TABLE_NAME, null, values);

        // Mock Firestore behavior
        doAnswer(invocation -> {
            Map<String, Object> user = invocation.getArgument(0);
            assertEquals("John", user.get("firstName"));
            assertEquals("Doe", user.get("lastName"));
            assertEquals("johndoe", user.get("username"));
            // Note: Password should be encrypted in real tests
            return null;
        }).when(mockFirestore).collection("users").add(anyMap());

        // Perform migration
        dataMigration.migrateUsers();
    }
}
