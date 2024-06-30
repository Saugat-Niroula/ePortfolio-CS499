package com.example.myapplicationsaugatniroula;

import android.provider.BaseColumns;

/**
 * A contract class that defines the constants related to the database.
 * It helps ensure that the database schema remains consistent
 * and provides a central place for the schema definitions.
 */
public final class DatabaseContract {

    // Private constructor to prevent instantiation of the contract class
    private DatabaseContract() {}

    /**
     * Inner class that defines the constants for the Data table.
     * Implements BaseColumns for _ID column, which is recommended for Android tables.
     */
    public static class DataEntry implements BaseColumns {
        // Table name for data
        public static final String TABLE_NAME = "data";
        // Columns for the data table
        public static final String COLUMN_ITEM_NUMBER = "item_number";
        public static final String COLUMN_ITEM_NAME = "item_name";
        public static final String COLUMN_QUANTITY = "quantity";
    }

    /**
     * Inner class that defines the constants for the Users table.
     * Implements BaseColumns for _ID column, which is recommended for Android tables.
     */
    public static class UserEntry implements BaseColumns {
        // Table name for users
        public static final String TABLE_NAME = "users";
        // Columns for the users table
        public static final String COLUMN_FIRST_NAME = "first_name";
        public static final String COLUMN_LAST_NAME = "last_name";
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_PASSWORD = "password";
    }
}
