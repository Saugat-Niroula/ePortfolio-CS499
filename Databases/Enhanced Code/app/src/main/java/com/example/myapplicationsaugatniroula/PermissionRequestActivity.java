package com.example.myapplicationsaugatniroula;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Activity to handle SMS notification permissions.
 */
public class PermissionRequestActivity extends AppCompatActivity {

    // Define UI elements as member variables for easy access
    private Button setupNotificationButton;
    private Button givePermissionButton;
    private Button dontGivePermissionButton;
    private EditText phoneNumberEditText;
    private Button saveButton;
    private TextView notificationTextView;

    // Firestore instance
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.permission_request_screen);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements by referencing their IDs
        setupNotificationButton = findViewById(R.id.setupNotificationButton);
        givePermissionButton = findViewById(R.id.givePermissionButton);
        dontGivePermissionButton = findViewById(R.id.dontGivePermissionButton);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        saveButton = findViewById(R.id.saveButton);
        notificationTextView = findViewById(R.id.notificationTextView);

        // Set up the listener for the setup notification button
        setupNotificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the setup button and show the permission buttons
                setupNotificationButton.setVisibility(View.GONE);
                givePermissionButton.setVisibility(View.VISIBLE);
                dontGivePermissionButton.setVisibility(View.VISIBLE);
            }
        });

        // Set up the listener for the "yes" button
        givePermissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the permission buttons and show the phone number input and save button
                phoneNumberEditText.setVisibility(View.VISIBLE);
                saveButton.setVisibility(View.VISIBLE);
                dontGivePermissionButton.setVisibility(View.GONE);
                givePermissionButton.setVisibility(View.GONE);
            }
        });

        // Set up the listener for the save button after entering phone number
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save phone number to Firestore
                savePhoneNumberToFirestore(phoneNumberEditText.getText().toString());

                // Display the enabled notification message
                notificationTextView.setText("SMS notification is now enabled.");
                notificationTextView.setVisibility(View.VISIBLE);
                phoneNumberEditText.setVisibility(View.GONE);
                saveButton.setVisibility(View.GONE);
            }
        });

        // Set up the listener for the "no" button
        dontGivePermissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Display the disabled notification message
                notificationTextView.setText("SMS notifications are disabled.");
                notificationTextView.setVisibility(View.VISIBLE);
                givePermissionButton.setVisibility(View.GONE);
                dontGivePermissionButton.setVisibility(View.GONE);
            }
        });

        // Set up the listener to return to the previous screen
        Button returnToDataDisplayButton = findViewById(R.id.returnToDataDisplayButton);
        returnToDataDisplayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();  // Finish the current activity and return to the previous one
            }
        });
    }

    /**
     * Method to save the phone number to Firestore.
     *
     * @param phoneNumber The phone number to save.
     */
    private void savePhoneNumberToFirestore(String phoneNumber) {
        db.collection("users").document("user_permissions")
                .set(new UserPermission(phoneNumber))
                .addOnSuccessListener(aVoid -> {
                    // Handle successful save
                    notificationTextView.setText("Phone number saved successfully.");
                })
                .addOnFailureListener(e -> {
                    // Handle failed save
                    notificationTextView.setText("Failed to save phone number.");
                });
    }

    /**
     * Class representing user permission data.
     */
    private static class UserPermission {
        private String phoneNumber;

        public UserPermission(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }
    }
}
