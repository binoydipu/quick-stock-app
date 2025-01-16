package com.binoydipu.quickstock.views.reports;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.binoydipu.quickstock.R;
import com.binoydipu.quickstock.services.cloud.FirebaseCloudStorage;
import com.binoydipu.quickstock.services.cloud.ItemModel;
import com.binoydipu.quickstock.utilities.format.NumberFormater;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class InventoryReportActivity extends AppCompatActivity {

    private ImageView ivToolbarBack;

    private Button btnGenerateReport;
    private TextView tvPdfStatus;
    private ArrayList<ItemModel> itemList;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_report);

        Toolbar toolbar = findViewById(R.id.toolbar);
        ivToolbarBack = findViewById(R.id.toolbar_back_btn);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        ivToolbarBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);


        btnGenerateReport = findViewById(R.id.generate_report_btn);
        tvPdfStatus = findViewById(R.id.status_tv);
        progressBar = findViewById(R.id.progress_circular);
        itemList = new ArrayList<>();

        btnGenerateReport.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            itemList = FirebaseCloudStorage.getInstance().getAllItems(this, isReceived -> {
                progressBar.setVisibility(View.GONE);
                if(isReceived) {
                    showReport();
                    generatePDF();
                }
            });
        });
    }

    private void showReport() {
        String reportText = generateReportString();
        Log.d("REPORT", reportText);
        tvPdfStatus.setText(reportText);
    }

    public String generateReportString() {
        StringBuilder report = new StringBuilder();
        report.append("QuickStock Inventory Report\n");
        report.append("Generated on: ").append(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date())).append("\n\n");

        report.append(String.format("%-15s %-10s %-12s %-12s %-12s %-12s %-12s\n",
                "Item Name", "Code", "Purchase", "Sale", "Quantity", "Stock Value", "Expiry Date"));
        report.append("-------------------------------------------------------------------------------------------\n");

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        for (ItemModel item : itemList) {
            String expiryDate = dateFormat.format(new Date(item.getExpireDateInMillis()));

            report.append(String.format(Locale.US,"%-15s %-10s %-12.2f %-12.2f %-12d %-12.2f %-12s",
                    item.getItemName(),
                    item.getItemCode(),
                    item.getPurchasePrice(),
                    item.getSalePrice(),
                    item.getStockQuantity(),
                    item.getStockValue(),
                    expiryDate)
            );
        }

        report.append("\n\nEnd of Report");
        return report.toString();
    }

    public void generatePDF() {
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create(); // A4 size
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        // for Body text
        Paint paint = new Paint();
        paint.setTextSize(12);
        paint.setColor(Color.BLACK);

        // For title
        Paint headerPaint = new Paint();
        headerPaint.setColor(Color.BLACK);
        headerPaint.setTextSize(24);
        headerPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        // For column title
        Paint columnTitlePaint = new Paint();
        columnTitlePaint.setColor(Color.BLACK);
        columnTitlePaint.setTextSize(12);
        columnTitlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        // Initial vertical position for the text
        float x = 50;
        float y = 50;

        // Title
        canvas.drawText("QuickStock Inventory Report", x, y, headerPaint);
        y += 30; // Move to next line

        // Date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        canvas.drawText("Generated on: " + dateFormat.format(new Date()), x, y, paint);
        y += 30; // Move to next line

        // Table Headers
        canvas.drawText(String.format("%-15s %-10s %-12s %-12s %-12s %-12s %-12s", "Item", "Code", "Purchase", "Sale", "Quantity", "Stock Value", "Expiry Date"), x, y, columnTitlePaint);
        y += 20; // Move to next line for content

        // Draw a line separator
        canvas.drawLine(x, y, 595 - 50, y, paint);
        y += 20; // Move to next line

        // Draw each item from the list
        SimpleDateFormat expiryDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        for (ItemModel item : itemList) {
            String expiryDate = expiryDateFormat.format(new Date(item.getExpireDateInMillis()));

            String line = String.format(Locale.US,"%-15s %-10s %-12.2f %-12.2f %-12d %-12.2f %-12s",
                    item.getItemName(),
                    item.getItemCode(),
                    item.getPurchasePrice(),
                    item.getSalePrice(),
                    item.getStockQuantity(),
                    item.getStockValue(),
                    expiryDate);

            canvas.drawText(line, x, y, paint);
            y += 20; // Move to next line for the next item

            // Check if we've reached the bottom of the page and need to add a new page
            if (y > 800) {
                pdfDocument.finishPage(page);
                page = pdfDocument.startPage(pageInfo); // Create a new page
                canvas = page.getCanvas();
                y = 50; // Reset the vertical position for the new page
            }
        }

        // Finish the page and document
        pdfDocument.finishPage(page);

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "Inventory Report " + NumberFormater.getTodayDateString() + ".pdf");
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);  // Save to Downloads folder

        Uri uri = getContentResolver().insert(MediaStore.Files.getContentUri("external"), contentValues);

        if (uri != null) {
            try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
                pdfDocument.writeTo(outputStream);
                pdfDocument.close();
                Toast.makeText(this, "PDF Created in Downloads Folder", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Unable to create PDF", Toast.LENGTH_SHORT).show();
                Log.e("InventoryReportActivity:error generating pdf- ", e.toString());
            }
        }
    }
}