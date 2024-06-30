package com.example.myapplicationsaugatniroula;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Activity to display and manage inventory data.
 */
public class DataDisplayActivity extends AppCompatActivity {
    private DataController dataController;
    private EditText itemNameEditText;
    private EditText quantityEditText;
    private TableLayout dataTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_display_screen);

        // Initialize DataController instance
        dataController = new DataController(this);

        // Bind XML views to Java objects
        itemNameEditText = findViewById(R.id.itemNameEditText);
        quantityEditText = findViewById(R.id.quantityEditText);
        dataTable = findViewById(R.id.dataTable);

        // Set up the listener for the add data button
        Button addDataButton = findViewById(R.id.addDataButton);
        addDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemName = itemNameEditText.getText().toString();
                int quantity = Integer.parseInt(quantityEditText.getText().toString());
                dataController.addItem(itemName, quantity);
                updateTable();
                clearInputFields();
            }
        });

        // Set up the listener for the generate PDF button
        Button generatePdfButton = findViewById(R.id.generatePdfButton);
        generatePdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataController.generatePDFReport(DataDisplayActivity.this, new DataController.PDFGenerationCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(DataDisplayActivity.this, "PDF Report Generated", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure() {
                        Toast.makeText(DataDisplayActivity.this, "Failed to Generate PDF Report", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Set up the listener for the scan barcode button
        Button scanBarcodeButton = findViewById(R.id.scanBarcodeButton);
        scanBarcodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DataDisplayActivity.this, BarcodeScannerActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        // Update the table with data on activity creation
        updateTable();
    }

    /**
     * Function to update the display table with the latest data from Firestore.
     */
    private void updateTable() {
        dataController.getAllItems(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    dataTable.removeAllViews();

                    // Add table headers first
                    TableRow header = new TableRow(DataDisplayActivity.this);
                    TextView headerName = new TextView(DataDisplayActivity.this);
                    headerName.setText("Item Name");
                    TextView headerQty = new TextView(DataDisplayActivity.this);
                    headerQty.setText("Quantity");
                    TextView headerAction = new TextView(DataDisplayActivity.this);
                    headerAction.setText("Actions");
                    header.addView(headerName);
                    header.addView(headerQty);
                    header.addView(headerAction);
                    dataTable.addView(header);

                    // Iterate through Firestore data and populate the table
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String itemId = document.getId();
                        String itemName = document.getString(DatabaseContract.InventoryEntry.FIELD_ITEM_NAME);
                        int quantity = document.getLong(DatabaseContract.InventoryEntry.FIELD_QUANTITY).intValue();

                        TableRow row = new TableRow(DataDisplayActivity.this);
                        TextView itemNameView = new TextView(DataDisplayActivity.this);
                        itemNameView.setText(itemName);
                        EditText quantityEditText = new EditText(DataDisplayActivity.this);
                        quantityEditText.setText(String.valueOf(quantity));
                        quantityEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

                        Button updateButton = new Button(DataDisplayActivity.this);
                        updateButton.setText("Update");
                        updateButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dataController.updateItem(itemId, Integer.parseInt(quantityEditText.getText().toString()));
                                updateTable();
                            }
                        });

                        Button deleteButton = new Button(DataDisplayActivity.this);
                        deleteButton.setText("Delete");
                        deleteButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dataController.deleteItem(itemId);
                                updateTable();
                            }
                        });

                        row.addView(itemNameView);
                        row.addView(quantityEditText);
                        row.addView(updateButton);
                        row.addView(deleteButton);
                        dataTable.addView(row);
                    }
                } else {
                    Toast.makeText(DataDisplayActivity.this, "Error getting data.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Function to clear the input fields after an item has been added.
     */
    private void clearInputFields() {
        itemNameEditText.setText("");
        quantityEditText.setText("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String barcode = data.getStringExtra("BARCODE");
                // Handle barcode data
                Toast.makeText(this, "Scanned: " + barcode, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
