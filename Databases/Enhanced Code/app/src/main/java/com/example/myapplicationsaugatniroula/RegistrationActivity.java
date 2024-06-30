package com.example.myapplicationsaugatniroula;

import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Activity for user registration.
 */
public class RegistrationActivity extends AppCompatActivity {

    // Define UI elements and Firestore instance.
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText regUsernameEditText;
    private EditText regPasswordEditText;
    private FirebaseFirestore db;

    // Encryption key for AES encryption
    private static final String SECRET_KEY = "my_super_secret_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_screen);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

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
                        try {
                            String encryptedPassword = encrypt(password, SECRET_KEY);
                            insertUser(firstName, lastName, username, encryptedPassword);
                            showToast("Registration completed. Please return to the login screen.");
                        } catch (Exception e) {
                            showToast("Error encrypting password.");
                        }
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
     * Check if a username is available in the Firestore database.
     *
     * @param username The username to check.
     * @return true if username is available, false otherwise.
     */
    private boolean isUsernameAvailable(String username) {
        // Firestore query logic to check username availability.
        // This is a placeholder. Implement Firestore query logic here.
        // Returning true for illustration.
        return true;
    }

    /**
     * Insert a new user into Firestore.
     *
     * @param firstName The first name of the user.
     * @param lastName  The last name of the user.
     * @param username  The username of the user.
     * @param password  The encrypted password of the user.
     */
    private void insertUser(String firstName, String lastName, String username, String password) {
        User user = new User(firstName, lastName, username, password);
        db.collection("users")
                .add(user)
                .addOnSuccessListener(documentReference -> showToast("User registered successfully."))
                .addOnFailureListener(e -> showToast("Registration failed."));
    }

    /**
     * Display a short message to the user.
     *
     * @param message The message to display.
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Encrypt a string using AES.
     *
     * @param data      The string to encrypt.
     * @param secretKey The secret key for encryption.
     * @return The encrypted string.
     * @throws Exception If encryption fails.
     */
    private String encrypt(String data, String secretKey) throws Exception {
        SecretKeySpec key = generateKey(secretKey);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedData = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeToString(encryptedData, Base64.DEFAULT);
    }

    /**
     * Generate an AES key from the secret key.
     *
     * @param secretKey The secret key.
     * @return The AES key.
     * @throws NoSuchAlgorithmException If key generation fails.
     */
    private SecretKeySpec generateKey(String secretKey) throws NoSuchAlgorithmException {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = secretKey.getBytes(StandardCharsets.UTF_8);
        digest.update(bytes, 0, bytes.length);
        byte[] key = digest.digest();
        return new SecretKeySpec(key, "AES");
    }

    /**
     * Class representing a user.
     */
    private static class User {
        private String firstName;
        private String lastName;
        private String username;
        private String password;

        public User(String firstName, String lastName, String username, String password) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.username = username;
            this.password = password;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
