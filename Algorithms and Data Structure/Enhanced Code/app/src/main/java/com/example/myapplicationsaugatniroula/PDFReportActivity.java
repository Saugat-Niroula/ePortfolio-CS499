package com.example.myapplicationsaugatniroula;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PDFReportActivity extends AppCompatActivity {
    private DataController dataController;
    private TextView pdfReportStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pdf_report_activity);

        pdfReportStatus = findViewById(R.id.pdfReportStatus);
        Button backButton = findViewById(R.id.backButton);
        dataController = new DataController(this);

        // Generate PDF Report
        generatePDFReport();

        // Back button listener
        backButton.setOnClickListener(v -> finish());
    }

    private void generatePDFReport() {
        dataController.generatePDFReport(this);
        pdfReportStatus.setText("PDF Report Generated Successfully");
    }
}
