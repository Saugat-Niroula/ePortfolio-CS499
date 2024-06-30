package com.example.myapplicationsaugatniroula;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// DatabaseHelper class using Singleton Pattern
public class DatabaseHelper extends SQLiteOpenHelper {
    // Database version and name
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "YourAppDatabase.db";
    // Singleton instance
    private static DatabaseHelper instance;

    // SQL statement to create the data entry table
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DatabaseContract.DataEntry.TABLE_NAME + " (" +
                    DatabaseContract.DataEntry._ID + " INTEGER PRIMARY KEY," +
                    DatabaseContract.DataEntry.COLUMN_ITEM_NUMBER + " INTEGER," +
                    DatabaseContract.DataEntry.COLUMN_ITEM_NAME + " TEXT," +
                    DatabaseContract.DataEntry.COLUMN_QUANTITY + " INTEGER)";

    // SQL statement to create the user entry table
    private static final String SQL_CREATE_USER_ENTRIES =
            "CREATE TABLE " + DatabaseContract.UserEntry.TABLE_NAME + " (" +
                    DatabaseContract.UserEntry._ID + " INTEGER PRIMARY KEY," +
                    DatabaseContract.UserEntry.COLUMN_FIRST_NAME + " TEXT," +
                    DatabaseContract.UserEntry.COLUMN_LAST_NAME + " TEXT," +
                    DatabaseContract.UserEntry.COLUMN_USERNAME + " TEXT," +
                    DatabaseContract.UserEntry.COLUMN_PASSWORD + " TEXT)";

    // SQL statement to delete the data entry table
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DatabaseContract.DataEntry.TABLE_NAME;

    // SQL statement to delete the user entry table
    private static final String SQL_DELETE_USER_ENTRIES =
            "DROP TABLE IF EXISTS " + DatabaseContract.UserEntry.TABLE_NAME;

    // Private constructor to prevent direct instantiation
    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Static method to get the singleton instance
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    // Called when the database is created for the first time
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);         // Create data entry table
        db.execSQL(SQL_CREATE_USER_ENTRIES);    // Create user entry table
    }

    // Called when the database needs to be upgraded
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Delete old tables
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(SQL_DELETE_USER_ENTRIES);
        // Recreate the tables
        onCreate(db);
    }
}
