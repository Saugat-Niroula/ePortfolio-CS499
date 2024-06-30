package com.example.myapplicationsaugatniroula;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.HashMap;
import java.util.Map;

public class BarcodeScannerActivity extends AppCompatActivity {

    // Declare Firebase Firestore instance
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barcode_scanner_activity); // Set the layout
        db = FirebaseFirestore.getInstance(); // Initialize Firestore
        new IntentIntegrator(this).initiateScan(); // Start the barcode scanner
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                String barcode = result.getContents();
                saveBarcodeToFirestore(barcode); // Save barcode to Firestore
                Intent intent = new Intent();
                intent.putExtra("BARCODE", barcode);
                setResult(RESULT_OK, intent);
            } else {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
        finish();
    }

    /**
     * Save the scanned barcode to Firebase Firestore.
     *
     * @param barcode The scanned barcode.
     */
    private void saveBarcodeToFirestore(String barcode) {
        // Create a map to store the barcode
        Map<String, Object> barcodeData = new HashMap<>();
        barcodeData.put("barcode", barcode);

        // Save the barcode data to Firestore
        db.collection("barcodes")
                .add(barcodeData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(BarcodeScannerActivity.this, "Barcode saved", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(BarcodeScannerActivity.this, "Error saving barcode", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
