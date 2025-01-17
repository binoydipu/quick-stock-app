package com.binoydipu.quickstock.views.reports;

import static com.binoydipu.quickstock.constants.ConstantValues.LOW_STOCK_LIMIT;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.binoydipu.quickstock.R;
import com.binoydipu.quickstock.services.auth.FirebaseAuthProvider;
import com.binoydipu.quickstock.services.cloud.FirebaseCloudStorage;
import com.binoydipu.quickstock.services.cloud.ItemModel;
import com.binoydipu.quickstock.utilities.dialogs.DialogHelper;
import com.binoydipu.quickstock.utilities.format.NumberFormater;
import com.binoydipu.quickstock.views.AboutActivity;
import com.binoydipu.quickstock.views.profile.NotificationActivity;
import com.binoydipu.quickstock.views.profile.ProfileActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private Uri pdfUri;

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
                    pdfUri = generatePDF();
                }
            });
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.inventory_report_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.share_menu) {
            if(pdfUri != null) {
                sharePDF(pdfUri);
            } else {
                Toast.makeText(this, "Generate Report First", Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    private void showReport() {
        String reportText = generateReportString();
        Log.d("REPORT", reportText);
        tvPdfStatus.setText(reportText);
    }

    public void sharePDF(Uri uri) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("application/pdf");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);  // Grant permission to read the file

        startActivity(Intent.createChooser(shareIntent, "Share PDF using"));
    }

    public String generateReportString() {
        StringBuilder report = new StringBuilder();
        report.append("QuickStock Inventory Report\n");
        report.append("Generated on: ").append(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date())).append("\n\n");

        report.append(String.format("%-15s %-10s %-12s %-12s %-12s %-12s %-12s\n",
                "Item Name", "Code", "Purchase", "Sale", "Quantity", "Stock Value", "Expiry Date"));
        report.append("-------------------------------------------------------------------------------------------\n\n");

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        int lowItems = 0;
        double stockValue = 0;
        double lowStockValue = 0;

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
            report.append("\n\n");

            stockValue += item.getStockValue();
            if(item.getStockQuantity() < LOW_STOCK_LIMIT) {
                lowStockValue += item.getStockValue();
                lowItems++;
            }
        }

        report.append("\n");
        report.append(String.format("%-20s %-20s %-20s %-20s\n",
                "No. Items", "Value", "No. Low Items", "Low Stock Value"));
        report.append("-------------------------------------------------------------------------------------------\n");
        report.append(String.format(Locale.US,"%-25d %-25.2f %-25d %-25.2f\n",
                itemList.size(),
                stockValue,
                lowItems,
                lowStockValue));

        String userEmail = FirebaseAuthProvider.getInstance().getCurrentUserEmail();
        String footerText = "\n\nReport Generated by: " + userEmail;
        report.append(footerText);

        report.append("\n\nEnd of Report");
        return report.toString();
    }

    public Uri generatePDF() {
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

        // for logo
        Bitmap logoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.quick_stock);
        Bitmap quickStockLogo = Bitmap.createScaledBitmap(logoBitmap, 30, 30, false);
        canvas.drawBitmap(quickStockLogo, 520, 25, null); // upper-right corner

        // Initial position for the text
        float x = 50;
        float y = 50;

        // Title
        canvas.drawText("QuickStock Inventory Report", x, y, headerPaint);
        y += 30; // Move to next line

        // Date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        canvas.drawText("Generated on: " + dateFormat.format(new Date()), x, y, paint);
        y += 40; // Move to next line

        // Table Headers
        canvas.drawText(String.format("%-15s %-10s %-12s %-12s %-12s %-12s %-12s", "Item", "Code", "Purchase", "Sale", "Quantity", "Stock Value", "Expiry Date"), x, y, columnTitlePaint);
        y += 20; // Move to next line for content

        // Draw a line separator
        canvas.drawLine(x, y, 595 - 50, y, paint);
        y += 20; // Move to next line

        SimpleDateFormat expiryDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        int lowItems = 0;
        double stockValue = 0;
        double lowStockValue = 0;

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

            stockValue += item.getStockValue();
            if(item.getStockQuantity() < LOW_STOCK_LIMIT) {
                lowStockValue += item.getStockValue();
                lowItems++;
            }
        }

        y += 20;
        // Check if we've reached the bottom of the page and need to add a new page
        if (y > 730) {
            pdfDocument.finishPage(page);
            page = pdfDocument.startPage(pageInfo); // Create a new page
            canvas = page.getCanvas();
            y = 50; // Reset the vertical position for the new page
        }

        canvas.drawText(String.format("%-20s %-20s %-20s %-20s\n",
                "No. Items", "Value", "No. Low Items", "L.S. Value"), x, y, columnTitlePaint);

        y += 20; // Move to next line for the next item

        // Draw a line separator
        canvas.drawLine(x, y, 595 - 50, y, paint);
        y += 20; // Move to next line

        canvas.drawText(String.format(Locale.US,"%-25d %-25.2f %-25d %-25.2f\n",
                itemList.size(),
                stockValue,
                lowItems,
                lowStockValue), x, y, paint);


        // For footer
        y = 820;
        Paint footerPaint = new Paint();
        footerPaint.setColor(Color.BLACK);
        footerPaint.setTextSize(12);
        footerPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));

        String userEmail = FirebaseAuthProvider.getInstance().getCurrentUserEmail();
        String footerText = "Report Generated by: " + userEmail;
        canvas.drawText(footerText, x, y, footerPaint);

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
        return uri;
    }
}