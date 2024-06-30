package com.example.myapplicationsaugatniroula;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Activity to handle PDF report generation.
 */
public class PDFReportActivity extends AppCompatActivity {
    private DataController dataController;
    private TextView pdfReportStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pdf_report_activity);

        // Initialize UI elements
        pdfReportStatus = findViewById(R.id.pdfReportStatus);
        Button backButton = findViewById(R.id.backButton);

        // Initialize DataController instance with Firestore
        dataController = new DataController(this);

        // Generate PDF Report
        generatePDFReport();

        // Back button listener
        backButton.setOnClickListener(v -> finish());
    }

    /**
     * Method to generate a PDF report.
     */
    private void generatePDFReport() {
        dataController.generatePDFReport(this, new DataController.PDFGenerationCallback() {
            @Override
            public void onSuccess() {
                pdfReportStatus.setText("PDF Report Generated Successfully");
            }

            @Override
            public void onFailure() {
                pdfReportStatus.setText("Failed to Generate PDF Report");
            }
        });
    }
}
