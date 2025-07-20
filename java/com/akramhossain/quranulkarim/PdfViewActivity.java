package com.akramhossain.quranulkarim;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.akramhossain.quranulkarim.util.Utils;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import java.io.File;
import androidx.appcompat.app.AlertDialog;

public class PdfViewActivity extends AppCompatActivity {

    PDFView pdfView;
    private TextView pageInfo;
    FloatingActionButton fabGotoPage;
    private int lastPage = 0;
    SharedPreferences mPrefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_pdf_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pageInfo = findViewById(R.id.pageInfo);
        pageInfo.setOnLongClickListener(v -> {
            resetLastPage();
            return true;
        });

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

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

        fabGotoPage = findViewById(R.id.fabGotoPage);
        fabGotoPage.setOnClickListener(v -> showGotoPageDialog());

        Log.d("PDF_FILE",filePath);

        if (filePath == null) {
            Toast.makeText(this, "PDF path is missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            Toast.makeText(this, "File not found:\n" + filePath, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        mPrefs = getApplicationContext().getSharedPreferences(Utils.PREF_NAME, MODE_PRIVATE);
        String key = "last_page_" + filePath.hashCode();
        int savedPage = mPrefs.getInt(key, -1);

        Log.d("RESUME_PDF_KEY", key);

        if (savedPage >= 0) {
            new AlertDialog.Builder(this)
                    .setTitle("Resume reading?")
                    .setMessage("Continue from page " + (savedPage + 1) + "?")
                    .setPositiveButton("Yes", (dialog, which) -> loadPdf(file, savedPage))
                    .setNegativeButton("No", (dialog, which) -> loadPdf(file, 0))
                    .show();
        } else {
            loadPdf(file, 0);
        }

        FloatingActionButton resetFab = findViewById(R.id.fabResetProgress);
        resetFab.setOnClickListener(v -> resetLastPage());
    }

    private void resetLastPage() {
        String filePath = getIntent().getStringExtra("pdf_path");
        SharedPreferences.Editor editor = getSharedPreferences(Utils.PREF_NAME, MODE_PRIVATE).edit();
        String key = "last_page_" + filePath.hashCode();

        Log.d("RESET_PDF_KEY", key);

        editor.remove(key);
        //editor.apply();
        boolean success = editor.commit();
        if (success) {
            Toast.makeText(this, "Reading progress reset", Toast.LENGTH_SHORT).show();
            pdfView.jumpTo(0);
        }else{
            Toast.makeText(this, "Failed to reset progress", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadPdf(File file, int startPage) {
        pdfView.fromFile(file)
                .defaultPage(startPage)
                .enableSwipe(true)
                .enableDoubletap(true)
                .enableAnnotationRendering(true)
                .onPageChange((page, pageCount) -> {
                    lastPage = page;
                    pageInfo.setText("Page " + (page + 1) + " of " + pageCount);
                })
                .load();
    }

    private void showGotoPageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Go to Page");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);
        builder.setPositiveButton("Go", (dialog, which) -> {
            String pageStr = input.getText().toString().trim();
            if (!pageStr.isEmpty()) {
                int pageNum = Integer.parseInt(pageStr);
                int totalPages = pdfView.getPageCount();
                if (pageNum >= 1 && pageNum <= totalPages) {
                    pdfView.jumpTo(pageNum - 1); // 0-based index
                } else {
                    Toast.makeText(this, "Page must be between 1 and " + totalPages, Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
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

    @Override
    protected void onPause() {
        super.onPause();
        saveLastPage();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveLastPage();
    }

    private void saveLastPage() {
        if (lastPage <= 3) {
            return;
        }
        String filePath = getIntent().getStringExtra("pdf_path");
        SharedPreferences.Editor editor = getSharedPreferences(Utils.PREF_NAME, MODE_PRIVATE).edit();
        String key = "last_page_" + filePath.hashCode();
        Log.d("LAST_SAVED_PDF_KEY", key);
        editor.putInt(key, lastPage);
        editor.apply();
    }
}