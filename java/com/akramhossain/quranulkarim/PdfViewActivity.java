package com.akramhossain.quranulkarim;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.github.barteksc.pdfviewer.PDFView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import java.io.File;

public class PdfViewActivity extends AppCompatActivity {

    PDFView pdfView;
    private TextView pageInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_pdf_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        pageInfo = findViewById(R.id.pageInfo);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) ab.setDisplayHomeAsUpEnabled(true);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //
        pdfView = findViewById(R.id.pdfView);

        String filePath = getIntent().getStringExtra("pdf_path");

        String title = getIntent().getStringExtra("pdf_file_name");

        getSupportActionBar().setTitle(title);

        Log.d("PDF_FILE",filePath);

        if (filePath != null) {
            File file = new File(filePath);
            if (file.exists()) {
                pdfView.fromFile(file)
                        .enableSwipe(true)
                        .enableDoubletap(true)
                        .enableAnnotationRendering(true)
                        .nightMode(false) // set true for dark background
                        .onPageChange((page, pageCount) -> {
                            pageInfo.setText("Page " + (page + 1) + " of " + pageCount);
                        })
                        .load();
            } else {
                Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "Invalid file path", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    // Handle back button in toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close the activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}