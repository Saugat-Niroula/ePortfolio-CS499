package com.example.myapplicationsaugatniroula;

/**
 * A contract class that defines the constants related to Firestore collections and documents.
 * It helps ensure that the Firestore schema remains consistent
 * and provides a central place for the schema definitions.
 */
public final class DatabaseContract {
    // Private constructor to prevent instantiation of the contract class
    private DatabaseContract() {}

    /**
     * Constants for the Inventory collection in Firestore.
     */
    public static class InventoryEntry {
        // Collection name for inventory data
        public static final String COLLECTION_NAME = "inventory";
        // Document fields for the inventory collection
        public static final String FIELD_ITEM_NUMBER = "item_number";
        public static final String FIELD_ITEM_NAME = "item_name";
        public static final String FIELD_QUANTITY = "quantity";
        // Table name for SQLite
        public static final String TABLE_NAME = "inventory";
    }

    /**
     * Constants for the Users collection in Firestore.
     */
    public static class UserEntry {
        // Collection name for user data
        public static final String COLLECTION_NAME = "users";
        // Document fields for the users collection
        public static final String FIELD_FIRST_NAME = "first_name";
        public static final String FIELD_LAST_NAME = "last_name";
        public static final String FIELD_USERNAME = "username";
        public static final String FIELD_PASSWORD = "password";
        // Table name for SQLite
        public static final String TABLE_NAME = "users";
    }

    /**
     * Constants for the Data collection in Firestore (if needed).
     */
    public static class DataEntry {
        // Collection name for data
        public static final String COLLECTION_NAME = "data";
        // Document fields for the data collection
        public static final String COLUMN_ITEM_NAME = "item_name";
        public static final String COLUMN_QUANTITY = "quantity";
        // Table name for SQLite
        public static final String TABLE_NAME = "data";
    }
}
