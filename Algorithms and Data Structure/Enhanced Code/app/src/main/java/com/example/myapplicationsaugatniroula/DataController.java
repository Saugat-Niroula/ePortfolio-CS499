package com.example.myapplicationsaugatniroula;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

// Controller class for handling data operations
public class DataController {
    private DatabaseHelper dbHelper;

    // Constructor to initialize DatabaseHelper instance
    public DataController(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    // Method to add an item to the database
    public void addItem(String itemName, int quantity) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.DataEntry.COLUMN_ITEM_NAME, itemName);
        values.put(DatabaseContract.DataEntry.COLUMN_QUANTITY, quantity);
        db.insert(DatabaseContract.DataEntry.TABLE_NAME, null, values);
        db.close();
    }

    // Method to update an item's quantity in the database
    public void updateItem(int itemId, int newQuantity) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.DataEntry.COLUMN_QUANTITY, newQuantity);
        String selection = DatabaseContract.DataEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(itemId)};
        db.update(DatabaseContract.DataEntry.TABLE_NAME, values, selection, selectionArgs);
        db.close();
    }

    // Method to delete an item from the database
    public void deleteItem(int itemId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = DatabaseContract.DataEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(itemId)};
        db.delete(DatabaseContract.DataEntry.TABLE_NAME, selection, selectionArgs);
        db.close();
    }

    // Method to get all items from the database
    public Cursor getAllItems() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                DatabaseContract.DataEntry._ID,
                DatabaseContract.DataEntry.COLUMN_ITEM_NAME,
                DatabaseContract.DataEntry.COLUMN_QUANTITY
        };
        return db.query(
                DatabaseContract.DataEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );
    }

    // Method to generate a PDF report of the inventory
    public void generatePDFReport(Context context) {
        Cursor cursor = getAllItems();
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        int yPos = 25;
        Paint paint = new Paint();
        while (cursor.moveToNext()) {
            String itemName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.DataEntry.COLUMN_ITEM_NAME));
            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.DataEntry.COLUMN_QUANTITY));
            page.getCanvas().drawText(itemName + " - " + quantity, 10, yPos, paint);
            yPos += 25;
        }
        document.finishPage(page);
        cursor.close();
        try {
            File pdfFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "InventoryReport.pdf");
            document.writeTo(new FileOutputStream(pdfFile));
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to perform an indexed search on item name
    public Cursor searchItems(String itemName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                DatabaseContract.DataEntry._ID,
                DatabaseContract.DataEntry.COLUMN_ITEM_NAME,
                DatabaseContract.DataEntry.COLUMN_QUANTITY
        };
        String selection = DatabaseContract.DataEntry.COLUMN_ITEM_NAME + " LIKE ?";
        String[] selectionArgs = {"%" + itemName + "%"};
        return db.query(
                DatabaseContract.DataEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
    }

    // Method to sort items by name or quantity
    public Cursor sortItems(String sortBy) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String orderBy = sortBy.equals("name") ? DatabaseContract.DataEntry.COLUMN_ITEM_NAME : DatabaseContract.DataEntry.COLUMN_QUANTITY;
        String[] projection = {
                DatabaseContract.DataEntry._ID,
                DatabaseContract.DataEntry.COLUMN_ITEM_NAME,
                DatabaseContract.DataEntry.COLUMN_QUANTITY
        };
        return db.query(
                DatabaseContract.DataEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                orderBy
        );
    }

    // Method to filter items by quantity
    public Cursor filterItemsByQuantity(int quantity) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                DatabaseContract.DataEntry._ID,
                DatabaseContract.DataEntry.COLUMN_ITEM_NAME,
                DatabaseContract.DataEntry.COLUMN_QUANTITY
        };
        String selection = DatabaseContract.DataEntry.COLUMN_QUANTITY + " >= ?";
        String[] selectionArgs = {String.valueOf(quantity)};
        return db.query(
                DatabaseContract.DataEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
    }
}
