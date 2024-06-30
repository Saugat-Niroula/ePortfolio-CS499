package com.example.myapplicationsaugatniroula;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private Context appContext;
    private FirebaseFirestore firestoreDb;
    private DataMigration dataMigration;

    @Before
    public void setUp() {
        // Context of the app under test
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        firestoreDb = FirebaseFirestore.getInstance();
        dataMigration = new DataMigration(appContext);
    }

    @Test
    public void useAppContext() {
        // Verify the package name
        assertEquals("com.example.myapplicationsaugatniroula", appContext.getPackageName());
    }

    @Test
    public void testFirestoreIntegration() {
        // Create a sample data entry
        Map<String, Object> sampleData = new HashMap<>();
        sampleData.put("itemName", "TestItem");
        sampleData.put("quantity", 10);

        // Add data to Firestore and verify it was added correctly
        firestoreDb.collection("testItems")
                .add(sampleData)
                .addOnSuccessListener(documentReference -> {
                    String documentId = documentReference.getId();
                    firestoreDb.collection("testItems")
                            .document(documentId)
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {
                                assertTrue(documentSnapshot.exists());
                                assertEquals("TestItem", documentSnapshot.getString("itemName"));
                                assertEquals(10, documentSnapshot.getLong("quantity").intValue());
                            });
                })
                .addOnFailureListener(e -> fail("Failed to add document to Firestore"));
    }

    @Test
    public void testEncryption() throws Exception {
        // Sample data to encrypt
        String sampleData = "TestPassword";
        String secretKey = "my_super_secret_key";

        // Encrypt the data using DataMigration's encryption method
        String encryptedData = dataMigration.encrypt(sampleData, secretKey);

        // Decrypt the data using DataMigration's decryption method
        String decryptedData = dataMigration.decrypt(encryptedData, secretKey);

        // Verify the decrypted data matches the original data
        assertEquals(sampleData, decryptedData);
    }
}
