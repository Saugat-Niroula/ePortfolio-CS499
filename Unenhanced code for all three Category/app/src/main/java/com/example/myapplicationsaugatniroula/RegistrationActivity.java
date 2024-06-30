package com.example.myapplicationsaugatniroula;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegistrationActivity extends AppCompatActivity {

    // Define UI elements and helper class.
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText regUsernameEditText;
    private EditText regPasswordEditText;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_screen);

        // Initialize the database helper.
        dbHelper = new DatabaseHelper(this);

        // Connect UI elements to their corresponding views in the layout.
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        regUsernameEditText = findViewById(R.id.regUsernameEditText);
        regPasswordEditText = findViewById(R.id.regPasswordEditText);

        // Setup event listener for the registration button.
        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Fetch entered values from the UI elements.
                String firstName = firstNameEditText.getText().toString();
                String lastName = lastNameEditText.getText().toString();
                String username = regUsernameEditText.getText().toString();
                String password = regPasswordEditText.getText().toString();

                // Check for empty fields.
                if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty()) {
                    showToast("Please fill all fields.");
                } else {
                    // Check username availability and proceed with registration.
                    if (isUsernameAvailable(username)) {
                        insertUser(firstName, lastName, username, password);
                        showToast("Registration completed. Please return to the login screen.");
                    } else {
                        showToast("Username already exists. Please create another username.");
                    }
                }
            }
        });

        // Setup event listener for the "Return to Login" button.
        Button returnToLoginButton = findViewById(R.id.returnToLoginButton);
        returnToLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // End the activity and return to the previous one (Login screen).
                finish();
            }
        });
    }

    /**
     * Check if a username is available in the database.
     *
     * @param username The username to check.
     * @return true if username is available, false otherwise.
     */
    private boolean isUsernameAvailable(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {DatabaseContract.UserEntry._ID};
        String selection = DatabaseContract.UserEntry.COLUMN_USERNAME + "=?";
        String[] selectionArgs = {username};

        // Query the database for the provided username.
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

        // Return true if the username is not present in the database.
        return count == 0;
    }

    /**
     * Insert a new user into the database.
     *
     * @param firstName The first name of the user.
     * @param lastName  The last name of the user.
     * @param username  The username of the user.
     * @param password  The password of the user.
     */
    private void insertUser(String firstName, String lastName, String username, String password) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.UserEntry.COLUMN_FIRST_NAME, firstName);
        values.put(DatabaseContract.UserEntry.COLUMN_LAST_NAME, lastName);
        values.put(DatabaseContract.UserEntry.COLUMN_USERNAME, username);
        values.put(DatabaseContract.UserEntry.COLUMN_PASSWORD, password);

        // Insert the new user record into the database.
        db.insert(DatabaseContract.UserEntry.TABLE_NAME, null, values);
        db.close();
    }

    /**
     * Display a short message to the user.
     *
     * @param message The message to display.
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
