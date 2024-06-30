package com.example.myapplicationsaugatniroula;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    // Declaring UI elements and database helper
    private EditText usernameEditText;
    private EditText passwordEditText;
    // Initialize database helper
    private DatabaseHelper dbHelper = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        // Linking UI elements with their respective IDs from XML layout
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        // Setting up the login button and its click listener
        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve the input from user
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                // Check if the credentials are valid and navigate accordingly
                if (isValidCredentials(username, password)) {
                    Intent intent = new Intent(LoginActivity.this, DataDisplayActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    showToast("Invalid credentials. Please try again.");
                }
            }
        });

        // Setting up the create account button and its click listener
        Button createAccountButton = findViewById(R.id.createAccountButton);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check username availability
                String newUsername = usernameEditText.getText().toString();
                if (isUsernameAvailable(newUsername)) {
                    Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                    startActivity(intent);
                } else {
                    showToast("Username already exists. Please choose another username or login using valid credentials.");
                }
            }
        });
    }

    /**
     * Checks whether the provided username and password combination is valid.
     *
     * @param username - username provided by the user.
     * @param password - password provided by the user.
     * @return - true if valid, false otherwise.
     */
    private boolean isValidCredentials(String username, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {DatabaseContract.UserEntry._ID};
        String selection = DatabaseContract.UserEntry.COLUMN_USERNAME + "=? AND " +
                DatabaseContract.UserEntry.COLUMN_PASSWORD + "=?";
        String[] selectionArgs = {username, password};
        Cursor cursor = db.query(
                DatabaseContract.UserEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count > 0;
    }

    /**
     * Checks if the given username is available in the database.
     *
     * @param username - username to check.
     * @return - true if available, false otherwise.
     */
    private boolean isUsernameAvailable(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {DatabaseContract.UserEntry._ID};
        String selection = DatabaseContract.UserEntry.COLUMN_USERNAME + "=?";
        String[] selectionArgs = {username};
        Cursor cursor = db.query(
                DatabaseContract.UserEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count == 0;
    }

    /**
     * Utility function to display a toast message.
     *
     * @param message - the message to be displayed.
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
