package com.example.myapplicationsaugatniroula;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.content.Intent;

public class DataDisplayActivity extends AppCompatActivity {
    // Member variables for database helper, input fields, and table layout
    private DatabaseHelper dbHelper;
    private EditText itemNameEditText;
    private EditText quantityEditText;
    private TableLayout dataTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_display_screen);

        // Initialize the database helper
        dbHelper = new DatabaseHelper(this);

        // Bind XML views to Java objects
        itemNameEditText = findViewById(R.id.itemNameEditText);
        quantityEditText = findViewById(R.id.quantityEditText);
        dataTable = findViewById(R.id.dataTable);

        // Set up the listener for the add data button
        Button addDataButton = findViewById(R.id.addDataButton);
        addDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve item details from input fields
                String itemName = itemNameEditText.getText().toString();
                int quantity = Integer.parseInt(quantityEditText.getText().toString());

                // Add the item to the database and update the table
                addItemToDatabase(itemName, quantity);
                updateTable();
                clearInputFields();
            }
        });

        // Navigate to permission request activity when the permission button is clicked
        Button permissionButton = findViewById(R.id.permissionButton);
        permissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DataDisplayActivity.this, PermissionRequestActivity.class);
                startActivity(intent);
            }
        });

        // Update the table with data on activity creation
        updateTable();
    }

    // Function to add an item to the database
    private void addItemToDatabase(String itemName, int quantity) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.DataEntry.COLUMN_ITEM_NAME, itemName);
        values.put(DatabaseContract.DataEntry.COLUMN_QUANTITY, quantity);
        db.insert(DatabaseContract.DataEntry.TABLE_NAME, null, values);
        db.close();
    }

    // Function to update the display table with the latest data
    private void updateTable() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                DatabaseContract.DataEntry._ID,
                DatabaseContract.DataEntry.COLUMN_ITEM_NAME,
                DatabaseContract.DataEntry.COLUMN_QUANTITY
        };

        Cursor cursor = db.query(
                DatabaseContract.DataEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

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
            // Extract item details from the cursor
            int itemId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.DataEntry._ID));
            String itemName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.DataEntry.COLUMN_ITEM_NAME));
            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.DataEntry.COLUMN_QUANTITY));

            // Create a new table row and populate it with item details
            TableRow row = new TableRow(this);
            TextView itemNameView = new TextView(this);
            itemNameView.setText(itemName);
            EditText quantityEditText = new EditText(this);
            quantityEditText.setText(String.valueOf(quantity));
            quantityEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

            // Button to handle the item update
            Button updateButton = new Button(this);
            updateButton.setText("Update");
            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateItem(itemId, Integer.parseInt(quantityEditText.getText().toString()));
                }
            });

            // Button to handle the item deletion
            Button deleteButton = new Button(this);
            deleteButton.setText("Delete");
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteItem(itemId);
                }
            });

            row.addView(itemNameView);
            row.addView(quantityEditText);
            row.addView(updateButton);
            row.addView(deleteButton);
            dataTable.addView(row);
        }

        cursor.close();
        db.close();
    }

    // Function to clear the input fields after an item has been added
    private void clearInputFields() {
        itemNameEditText.setText("");
        quantityEditText.setText("");
    }

    // Function to update a specific item's quantity in the database
    private void updateItem(int itemId, int newQuantity) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.DataEntry.COLUMN_QUANTITY, newQuantity);
        String selection = DatabaseContract.DataEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(itemId) };
        db.update(DatabaseContract.DataEntry.TABLE_NAME, values, selection, selectionArgs);
        db.close();

        updateTable();
    }

    // Function to delete a specific item from the database
    private void deleteItem(int itemId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            String selection = DatabaseContract.DataEntry._ID + " = ?";
            String[] selectionArgs = { String.valueOf(itemId) };
            db.delete(DatabaseContract.DataEntry.TABLE_NAME, selection, selectionArgs);
            updateTable();
        } catch (Exception e) {
            Toast.makeText(this, "Error deleting item: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            db.close();
        }
    }
}
