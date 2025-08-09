package com.akramhossain.quranulkarim.adapter;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akramhossain.quranulkarim.MainActivity;
import com.akramhossain.quranulkarim.PdfViewActivity;
import com.akramhossain.quranulkarim.R;
import com.akramhossain.quranulkarim.app.AppController;
import com.akramhossain.quranulkarim.model.TafsirPdfList;
import com.akramhossain.quranulkarim.util.Utils;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class PdfBookViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context c;
    ArrayList<TafsirPdfList> pdfBookList;
    private Activity activity;
    Typeface font;
    SharedPreferences mPrefs;
    String screen;
    private long downloadId;
    private BroadcastReceiver onDownloadComplete;

    public PdfBookViewAdapter(Context c, ArrayList<TafsirPdfList> pdfBookList, Activity activity, String screen) {
        this.c = c;
        this.pdfBookList = pdfBookList;
        this.activity = activity;
        this.screen = screen;
        mPrefs = c.getSharedPreferences(Utils.PREF_NAME, 0);
        font = Typeface.createFromAsset(c.getAssets(), "fonts/Siyamrupali.ttf");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.pdf_book_list, parent, false);
        RecyclerViewHolder rvHolder = new RecyclerViewHolder(v);
        rvHolder.name_bangla.setTypeface(font);
        return rvHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RecyclerViewHolder rvHolder= (RecyclerViewHolder) holder;
        TafsirPdfList tb = pdfBookList.get(position);
        rvHolder.name_bangla.setText(tb.getName_bangla());
        rvHolder.name_english.setText(tb.getName_english());
        rvHolder.file_name.setText(tb.getFile_name());
        rvHolder.url.setText(tb.getUrl());
        Picasso.get().load(tb.getThumb()).into(rvHolder.thumb);
        //
        File localFile = new File(c.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), tb.getFile_name());
        if (localFile.exists()) {
            rvHolder.actionIcon.setImageResource(android.R.drawable.ic_menu_view); // Open icon
        } else {
            rvHolder.actionIcon.setImageResource(android.R.drawable.stat_sys_download); // Download icon
        }
        holder.itemView.setOnClickListener(v -> {
            if (localFile.exists()) {
                openPdf(localFile, tb.getName_english());
            } else {
                downloadAndOpen(tb.getUrl(), tb.getFile_name(), tb.getName_english());
            }
        });
    }

    @Override
    public int getItemCount() {
        return pdfBookList.size();
    }

    private void openPdf(File file, String filename) {
        //Log.d("PDF_FILE",file.getAbsolutePath());
        Intent intent = new Intent(c, PdfViewActivity.class);
        intent.putExtra("pdf_path", file.getAbsolutePath());
        intent.putExtra("pdf_file_name", filename);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        c.startActivity(intent);
    }

    private void downloadAndOpen(String url, String fileName, String title) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle("Downloading " + fileName);
        request.setDescription("Please wait...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(c, Environment.DIRECTORY_DOWNLOADS, fileName);

        DownloadManager manager = (DownloadManager) c.getSystemService(Context.DOWNLOAD_SERVICE);
        downloadId = manager.enqueue(request);
        Toast.makeText(c, "Downloading started...", Toast.LENGTH_SHORT).show();
        // Optional: Listen for completion and auto-open
        checkDownloadStatus(downloadId, fileName, title);
    }

    private void checkDownloadStatus(final long downloadId, final String fileName, final String title) {
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(downloadId);
                DownloadManager manager = (DownloadManager) c.getSystemService(Context.DOWNLOAD_SERVICE);
                Cursor cursor = manager.query(query);
                if (cursor != null && cursor.moveToFirst()) {
                    int statusIndex = cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS);
                    int status = cursor.getInt(statusIndex);
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        pushData(title);
                        File file = new File(c.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);
                        if (file.exists()) {
                            openPdf(file, title);
                        }
                    } else {
                        // Keep checking every second until complete
                        handler.postDelayed(this, 1000);
                    }
                    cursor.close();
                }
            }
        }, 1000);
    }

    private void pushData(String bookName){
        Uri uri = new Uri.Builder()
                .scheme("http")
                .authority("quran.codxplore.com")
                .appendPath("api")
                .appendPath("v1")
                .appendPath("download-count.php")
                .appendQueryParameter("book_name", bookName)
                .build();
        String Url = uri.toString();;
        Log.d("DOWNLOAD PUSH URL", Url);
        //
        String tag_string_req = "download_count_api";
        StringRequest strReq = new StringRequest(Request.Method.GET,Url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("DOWNLOAD_COUNT", "Api Response: " + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("DOWNLOAD_COUNT", "Api Error: " + error);
            }
        }) {};
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView name_bangla;
        TextView name_english;
        TextView file_name;
        TextView url;
        ImageView thumb;
        ImageView actionIcon;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            name_bangla = (TextView) itemView.findViewById(R.id.name_bangla);
            name_english = (TextView) itemView.findViewById(R.id.name_english);
            file_name = (TextView) itemView.findViewById(R.id.file_name);
            url = (TextView) itemView.findViewById(R.id.url);
            thumb = (ImageView) itemView.findViewById(R.id.thumb);
            actionIcon = itemView.findViewById(R.id.actionIcon);
        }
    }
}
