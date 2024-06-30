package com.example.myapplicationsaugatniroula;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller class for handling data operations with Firestore.
 */
public class DataController {
    private CollectionReference inventoryRef;

    // Constructor to initialize Firestore instance
    public DataController(Context context) {
        inventoryRef = FirebaseFirestore.getInstance().collection(DatabaseContract.InventoryEntry.COLLECTION_NAME);
    }

    /**
     * Callback interface for PDF generation.
     */
    public interface PDFGenerationCallback {
        void onSuccess();
        void onFailure();
    }

    /**
     * Method to add an item to Firestore.
     *
     * @param itemName The name of the item
     * @param quantity The quantity of the item
     */
    public void addItem(String itemName, int quantity) {
        Map<String, Object> item = new HashMap<>();
        item.put(DatabaseContract.InventoryEntry.FIELD_ITEM_NAME, itemName);
        item.put(DatabaseContract.InventoryEntry.FIELD_QUANTITY, quantity);
        inventoryRef.add(item);
    }

    /**
     * Method to update an item's quantity in Firestore.
     *
     * @param itemId The ID of the item
     * @param newQuantity The new quantity of the item
     */
    public void updateItem(String itemId, int newQuantity) {
        inventoryRef.document(itemId).update(DatabaseContract.InventoryEntry.FIELD_QUANTITY, newQuantity);
    }

    /**
     * Method to delete an item from Firestore.
     *
     * @param itemId The ID of the item
     */
    public void deleteItem(String itemId) {
        inventoryRef.document(itemId).delete();
    }

    /**
     * Method to get all items from Firestore.
     *
     * @param onCompleteListener Listener to handle completion of the Firestore query
     */
    public void getAllItems(OnCompleteListener<QuerySnapshot> onCompleteListener) {
        inventoryRef.get().addOnCompleteListener(onCompleteListener);
    }

    /**
     * Method to generate a PDF report of the inventory.
     *
     * @param context The context to use for file operations
     * @param callback The callback to handle PDF generation result
     */
    public void generatePDFReport(Context context, PDFGenerationCallback callback) {
        getAllItems(task -> {
            if (task.isSuccessful()) {
                PdfDocument document = new PdfDocument();
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
                PdfDocument.Page page = document.startPage(pageInfo);
                int yPos = 25;
                Paint paint = new Paint();
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    String itemName = documentSnapshot.getString(DatabaseContract.InventoryEntry.FIELD_ITEM_NAME);
                    int quantity = documentSnapshot.getLong(DatabaseContract.InventoryEntry.FIELD_QUANTITY).intValue();
                    page.getCanvas().drawText(itemName + " - " + quantity, 10, yPos, paint);
                    yPos += 25;
                }
                document.finishPage(page);
                try {
                    File pdfFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "InventoryReport.pdf");
                    document.writeTo(new FileOutputStream(pdfFile));
                    document.close();
                    callback.onSuccess();
                } catch (IOException e) {
                    e.printStackTrace();
                    callback.onFailure();
                }
            } else {
                callback.onFailure();
            }
        });
    }

    /**
     * Method to search items by name in Firestore.
     *
     * @param itemName The name of the item to search for
     * @param onCompleteListener Listener to handle completion of the Firestore query
     */
    public void searchItems(String itemName, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        inventoryRef.whereEqualTo(DatabaseContract.InventoryEntry.FIELD_ITEM_NAME, itemName).get().addOnCompleteListener(onCompleteListener);
    }

    /**
     * Method to sort items by a specified field in Firestore.
     *
     * @param sortBy The field to sort by (e.g., "name" or "quantity")
     * @param onCompleteListener Listener to handle completion of the Firestore query
     */
    public void sortItems(String sortBy, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        Query query = sortBy.equals("name") ?
                inventoryRef.orderBy(DatabaseContract.InventoryEntry.FIELD_ITEM_NAME) :
                inventoryRef.orderBy(DatabaseContract.InventoryEntry.FIELD_QUANTITY);
        query.get().addOnCompleteListener(onCompleteListener);
    }

    /**
     * Method to filter items by quantity in Firestore.
     *
     * @param quantity The minimum quantity to filter by
     * @param onCompleteListener Listener to handle completion of the Firestore query
     */
    public void filterItemsByQuantity(int quantity, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        inventoryRef.whereGreaterThanOrEqualTo(DatabaseContract.InventoryEntry.FIELD_QUANTITY, quantity).get().addOnCompleteListener(onCompleteListener);
    }
}
