package com.example.myapplicationsaugatniroula;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Activity to handle user login and account creation.
 */
public class LoginActivity extends AppCompatActivity {

    // Declaring UI elements
    private EditText usernameEditText;
    private EditText passwordEditText;

    // Initialize Firebase Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

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
                isValidCredentials(username, password);
            }
        });

        // Setting up the create account button and its click listener
        Button createAccountButton = findViewById(R.id.createAccountButton);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check username availability
                String newUsername = usernameEditText.getText().toString();
                isUsernameAvailable(newUsername);
            }
        });
    }

    /**
     * Checks whether the provided username and password combination is valid.
     *
     * @param username - username provided by the user.
     * @param password - password provided by the user.
     */
    private void isValidCredentials(String username, String password) {
        db.collection("users")
                .whereEqualTo("username", username)
                .whereEqualTo("password", password)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            Intent intent = new Intent(LoginActivity.this, DataDisplayActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            showToast("Invalid credentials. Please try again.");
                        }
                    }
                });
    }

    /**
     * Checks if the given username is available in the database.
     *
     * @param username - username to check.
     */
    private void isUsernameAvailable(String username) {
        db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult().isEmpty()) {
                            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                            startActivity(intent);
                        } else {
                            showToast("Username already exists. Please choose another username or login using valid credentials.");
                        }
                    }
                });
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
