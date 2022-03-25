package com.akramhossain.quranulkarim.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.AsyncTask;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.akramhossain.quranulkarim.ConnectionDetector;
import com.akramhossain.quranulkarim.R;
import com.akramhossain.quranulkarim.ShareVerseActivity;
import com.akramhossain.quranulkarim.WordMeaningActivity;
import com.akramhossain.quranulkarim.helper.AudioPlay;
import com.akramhossain.quranulkarim.helper.DatabaseHelper;
import com.akramhossain.quranulkarim.model.Ayah;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class JuzHizbRubViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context c;
    ArrayList<Ayah> ayahs;
    Typeface font;
    MediaPlayer mp;
    ProgressDialog pd;
    DatabaseHelper dbhelper;
    private Activity activity;
    private static final int PERMISSION_REQUEST_CODE = 100;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;

    public JuzHizbRubViewAdapter(Context c, ArrayList<Ayah> ayahs, Activity activity) {
        this.c = c;
        this.ayahs = ayahs;
        font = Typeface.createFromAsset(c.getAssets(),"fonts/Siyamrupali.ttf");
        dbhelper = new DatabaseHelper(c);
        this.activity = activity;
        cd = new ConnectionDetector(c);
        isInternetPresent = cd.isConnectingToInternet();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(c).inflate(R.layout.juz_hiz_rub_list,parent,false);
        RecyclerViewHolder rvHolder = new RecyclerViewHolder(v);
        rvHolder.content_bn.setTypeface(font);
        return rvHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final RecyclerViewHolder rvHolder= (RecyclerViewHolder) holder;
        final Ayah ayah = ayahs.get(position);
        rvHolder.ayah_index.setText(ayah.getAyah_index());
        rvHolder.text_tashkeel.setText(ayah.getText_tashkeel());
        rvHolder.content_en.setText(ayah.getContent_en());
        rvHolder.content_bn.setText(ayah.getContent_bn());
        rvHolder.ayah_num.setText(ayah.getAyah_key());
        String sajdahText = "";
        if(ayah.getSajdah().equals("0")){
            sajdahText = "No";
        }else{
            sajdahText = "Yes";
        }
        rvHolder.sajdah.setText("Sajdah: "+sajdahText);

        rvHolder.playBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (isInternetPresent) {
                    if (checkPermission()) {
                        new AsyncTask<Void, Void, Void>() {
                            protected void onPreExecute() {
                                pd = new ProgressDialog(c);
                                pd.setTitle("Processing...");
                                pd.setMessage("Please wait.");
                                pd.setCancelable(false);
                                pd.setIndeterminate(true);
                                pd.show();
                            }

                            protected Void doInBackground(Void... params) {
                                /*AudioPlay.stopAudio();
                                AudioPlay.playAudio(c, ayah.getAudio_url());
                                return null;*/
                                try {
                                    URL url = new URL(ayah.getAudio_url());
                                    String fileName = url.getFile().replaceAll("/", "_").toLowerCase();
                                    Log.d("File Name:", fileName);
                                    //
                                    String mPath = c.getExternalFilesDir(Environment.DIRECTORY_MUSIC) + "/";
                                    String fullPath = mPath + fileName;
                                    Log.d("File Path:", mPath);
                                    Log.d("Full File Path:", fullPath);
                                    File file = new File(fullPath);
                                    if (file.exists()) {
                                        Log.d("File Path:", "Exist!");
                                        AudioPlay.stopAudio();
                                        AudioPlay.playAudio(c, fullPath);
                                    } else {
                                        Log.d("File Path:", "Not Exist Downloading!");
                                        downloadFile(ayah.getAudio_url(), fileName, mPath);
                                        AudioPlay.stopAudio();
                                        AudioPlay.playAudio(c, fullPath);
                                    }
                                    //
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }

                            protected void onPostExecute(Void result) {
                                if (pd!=null && pd.isShowing()) {
                                    pd.dismiss();
                                }
                            }
                        }.execute();
                    }else{
                        requestPermission(); // Code for permission
                    }
                }
            }
        });

        rvHolder.wordMeaningButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try {
                    Intent in = new Intent(c, WordMeaningActivity.class);
                    in.putExtra("ayah_index", ayah.getAyah_index());
                    in.putExtra("text_tashkeel", ayah.getText_tashkeel());
                    in.putExtra("content_en", ayah.getContent_en());
                    in.putExtra("content_bn", ayah.getContent_bn());
                    c.startActivity(in);
                }catch (Exception e) {
                    Log.e("Favorite", e.getMessage());
                }
            }
        });

        rvHolder.bookmarkBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try {
                    SQLiteDatabase db = dbhelper.getWritableDatabase();
                    Button bookmark = view.findViewById(R.id.bookmarkBtn);
                    String sql = "SELECT * FROM bookmark WHERE ayah_id = "+ayah.getAyah_index();
                    //Log.i("BOOKMARK_SQL", sql);
                    Cursor cursor = db.rawQuery(sql,null);
                    try {
                        if (cursor.moveToFirst()) {
                            db.execSQL("DELETE FROM bookmark WHERE ayah_id = " + ayah.getAyah_index());
                            Toast.makeText(c, "Deleted from bookmark.", Toast.LENGTH_LONG).show();
                            bookmark.setCompoundDrawablesWithIntrinsicBounds(0, android.R.drawable.btn_star, 0, 0);
                        } else {
                            ContentValues values = new ContentValues();
                            values.put("ayah_id", ayah.getAyah_index());
                            dbhelper.getWritableDatabase().insertOrThrow("bookmark", "", values);
                            Toast.makeText(c, "Added to bookmark.", Toast.LENGTH_LONG).show();
                            bookmark.setCompoundDrawablesWithIntrinsicBounds(0, android.R.drawable.btn_star_big_on, 0, 0);
                        }
                    }
                    catch (Exception e){
                        Log.i("Bookmark Button", e.getMessage());
                    }
                    finally {
                        if (cursor != null && !cursor.isClosed()){
                            cursor.close();
                        }
                        db.close();
                    }

                }catch (Exception e) {
                    Log.e("Favorite", e.getMessage());
                }
            }
        });

        SQLiteDatabase db = dbhelper.getWritableDatabase();
        String checksql = "SELECT * FROM bookmark WHERE ayah_id = "+ayah.getAyah_index();
        //Log.i("CHECK_BOOKMARK_SQL", checksql);
        Cursor cursor = db.rawQuery(checksql,null);

        try {
            if (cursor.moveToFirst()) {
                rvHolder.bookmarkBtn.setCompoundDrawablesWithIntrinsicBounds(0, android.R.drawable.btn_star_big_on, 0, 0);
            } else {
                rvHolder.bookmarkBtn.setCompoundDrawablesWithIntrinsicBounds(0, android.R.drawable.btn_star, 0, 0);
            }
        }catch (Exception e){
            Log.i("Bookmark Check", e.getMessage());
        }finally {
            if (cursor != null && !cursor.isClosed()){
                cursor.close();
            }
            db.close();
        }

        rvHolder.shareButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try {
                    Intent in = new Intent(c, ShareVerseActivity.class);
                    in.putExtra("ayah_index", ayah.getAyah_index());
                    in.putExtra("text_tashkeel", ayah.getText_tashkeel());
                    in.putExtra("content_en", ayah.getContent_en());
                    in.putExtra("content_bn", ayah.getContent_bn());
                    in.putExtra("ayah_num", ayah.getAyah_num());
                    in.putExtra("surah_id", ayah.getSurah_id());
                    in.putExtra("ayah_key", ayah.getAyah_key());
                    c.startActivity(in);
                }catch (Exception e) {
                    Log.e("Share", e.getMessage());
                }
            }
        });
        rvHolder.surah_name.setText("Sura "+ayah.getName_simple()+", Ayah "+ayah.getAyah_num());

        rvHolder.copyButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try {
                    String fullAyat = ayah.getText_tashkeel()+"\n\n"+ayah.getContent_en()+"\n\n"+ayah.getContent_bn()+"\n\nSura "+ayah.getName_simple()+", Ayah "+ayah.getAyah_num();
                    String label = ayah.getName_simple()+", Ayah "+ayah.getAyah_num();
                    Log.d(label,fullAyat);
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) c.getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText(label,fullAyat);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(c, "Ayah Copied.", Toast.LENGTH_LONG).show();
                }catch (Exception e) {
                    Log.e("Copied", e.getMessage());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return ayahs.size();
    }

    static void downloadFile(String dwnload_file_path, String fileName, String pathToSave) {
        int downloadedSize = 0;
        int totalSize = 0;
        try {
            URL url = new URL(dwnload_file_path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            //urlConnection.setDoOutput(true);
            // connect
            urlConnection.connect();
            File myDir;
            myDir = new File(pathToSave);
            myDir.mkdirs();
            // create a new file, to save the downloaded file
            String mFileName = fileName;
            File file = new File(myDir, mFileName);
            FileOutputStream fileOutput = new FileOutputStream(file);
            // Stream used for reading the data from the internet
            InputStream inputStream = urlConnection.getInputStream();
            // this is the total size of the file which we are downloading
            totalSize = urlConnection.getContentLength();
            byte[] buffer = new byte[1024];
            int bufferLength = 0;

            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fileOutput.write(buffer, 0, bufferLength);
                downloadedSize += bufferLength;
            }
            // close the output stream when complete //
            fileOutput.close();

        } catch (final MalformedURLException e) {
            // showError("Error : MalformedURLException " + e);
            e.printStackTrace();
        } catch (final IOException e) {
            // showError("Error : IOException " + e);
            e.printStackTrace();
        } catch (final Exception e) {
            // showError("Error : Please check your internet connection " + e);
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(c, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(c, "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView ayah_index;
        TextView text_tashkeel;
        TextView content_en;
        TextView content_bn;
        TextView sajdah;
        Button playBtn;
        Button wordMeaningButton;
        Button removeBookmarkButton;
        Button shareButton;
        TextView ayah_num;
        TextView surah_name;
        Button bookmarkBtn;
        Button copyButton;

        public RecyclerViewHolder(View itemView) {
            super(itemView);

            ayah_index = (TextView) itemView.findViewById(R.id.ayah_index);
            text_tashkeel = (TextView) itemView.findViewById(R.id.text_tashkeel);
            content_en = (TextView) itemView.findViewById(R.id.content_en);
            content_bn = (TextView) itemView.findViewById(R.id.content_bn);
            content_bn.setTypeface(font);
            sajdah = (TextView) itemView.findViewById(R.id.sajdah);
            playBtn = (Button) itemView.findViewById(R.id.playBtn);
            wordMeaningButton = (Button) itemView.findViewById(R.id.wordMeaningButton);
            removeBookmarkButton = (Button) itemView.findViewById(R.id.removeBookmarkButton);
            shareButton = (Button) itemView.findViewById(R.id.shareButton);
            ayah_num = (TextView) itemView.findViewById(R.id.ayah_num);
            bookmarkBtn = (Button) itemView.findViewById(R.id.bookmarkBtn);
            surah_name = (TextView) itemView.findViewById(R.id.surah_name);
            copyButton = (Button) itemView.findViewById(R.id.copyButton);
        }
    }
}
