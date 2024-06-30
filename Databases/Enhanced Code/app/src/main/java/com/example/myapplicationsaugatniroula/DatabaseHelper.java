package com.example.myapplicationsaugatniroula;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "local_database.db";
    private static final int DATABASE_VERSION = 1;

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the Inventory table
        String CREATE_INVENTORY_TABLE = "CREATE TABLE " + DatabaseContract.InventoryEntry.TABLE_NAME + " (" +
                DatabaseContract.InventoryEntry.FIELD_ITEM_NUMBER + " INTEGER PRIMARY KEY," +
                DatabaseContract.InventoryEntry.FIELD_ITEM_NAME + " TEXT," +
                DatabaseContract.InventoryEntry.FIELD_QUANTITY + " INTEGER)";
        db.execSQL(CREATE_INVENTORY_TABLE);

        // Create the Users table
        String CREATE_USERS_TABLE = "CREATE TABLE " + DatabaseContract.UserEntry.TABLE_NAME + " (" +
                DatabaseContract.UserEntry.FIELD_USERNAME + " TEXT PRIMARY KEY," +
                DatabaseContract.UserEntry.FIELD_FIRST_NAME + " TEXT," +
                DatabaseContract.UserEntry.FIELD_LAST_NAME + " TEXT," +
                DatabaseContract.UserEntry.FIELD_PASSWORD + " TEXT)";
        db.execSQL(CREATE_USERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.InventoryEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.UserEntry.TABLE_NAME);
        // Create tables again
        onCreate(db);
    }
}
