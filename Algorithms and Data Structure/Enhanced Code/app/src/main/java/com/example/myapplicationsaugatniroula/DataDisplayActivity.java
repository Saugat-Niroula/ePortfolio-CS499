package com.example.myapplicationsaugatniroula;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
                dataController.generatePDFReport(DataDisplayActivity.this);
                Toast.makeText(DataDisplayActivity.this, "PDF Report Generated", Toast.LENGTH_SHORT).show();
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

    // Function to update the display table with the latest data
    private void updateTable() {
        Cursor cursor = dataController.getAllItems();
        dataTable.removeAllViews();

        // Add table headers first
        TableRow header = new TableRow(this);
        TextView headerName = new TextView(this);
        headerName.setText("Item Name");
        TextView headerQty = new TextView(this);
        headerQty.setText("Quantity");
        TextView headerAction = new TextView(this);
        headerAction.setText("Actions");
        header.addView(headerName);
        header.addView(headerQty);
        header.addView(headerAction);
        dataTable.addView(header);

        // Iterate through cursor data and populate the table
        while (cursor.moveToNext()) {
            int itemId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.DataEntry._ID));
            String itemName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.DataEntry.COLUMN_ITEM_NAME));
            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.DataEntry.COLUMN_QUANTITY));
            TableRow row = new TableRow(this);
            TextView itemNameView = new TextView(this);
            itemNameView.setText(itemName);
            EditText quantityEditText = new EditText(this);
            quantityEditText.setText(String.valueOf(quantity));
            quantityEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
            Button updateButton = new Button(this);
            updateButton.setText("Update");
            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dataController.updateItem(itemId, Integer.parseInt(quantityEditText.getText().toString()));
                    updateTable();
                }
            });
            Button deleteButton = new Button(this);
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
        cursor.close();
    }

    // Function to clear the input fields after an item has been added
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
