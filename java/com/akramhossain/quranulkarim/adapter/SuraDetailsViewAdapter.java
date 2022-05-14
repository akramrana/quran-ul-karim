package com.akramhossain.quranulkarim.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.akramhossain.quranulkarim.ConnectionDetector;
import com.akramhossain.quranulkarim.R;
import com.akramhossain.quranulkarim.ShareVerseActivity;
import com.akramhossain.quranulkarim.TafsirActivity;
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
import java.net.URL;
import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by akram on 3/31/2019.
 */

public class SuraDetailsViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context c;
    ArrayList<Ayah> ayahs;
    Typeface font, fontUthmani, fontAlmajeed, fontAlQalam, fontNooreHidayat, fontSaleem;
    MediaPlayer mp;
    ProgressDialog pd;
    DatabaseHelper dbhelper;
    //SQLiteDatabase db;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private Activity activity;
    SharedPreferences mPrefs;

    public SuraDetailsViewAdapter(Context c, ArrayList<Ayah> ayahs, Activity activity) {
        this.c = c;
        this.ayahs = ayahs;
        //
        font = Typeface.createFromAsset(c.getAssets(),"fonts/Siyamrupali.ttf");
        fontUthmani = Typeface.createFromAsset(c.getAssets(),"fonts/KFGQPC_Uthmanic_Script_HAFS_Regular.ttf");
        fontAlmajeed = Typeface.createFromAsset(c.getAssets(),"fonts/AlMajeedQuranicFont_shiped.ttf");
        fontAlQalam = Typeface.createFromAsset(c.getAssets(),"fonts/AlQalamQuran.ttf");
        fontNooreHidayat = Typeface.createFromAsset(c.getAssets(),"fonts/noorehidayat.ttf");
        fontSaleem = Typeface.createFromAsset(c.getAssets(),"fonts/PDMS_Saleem_QuranFont.ttf");
        //
        dbhelper = new DatabaseHelper(c);
        this.activity = activity;
        //db = dbhelper.getWritableDatabase();

        cd = new ConnectionDetector(c);
        isInternetPresent = cd.isConnectingToInternet();
        mPrefs = PreferenceManager.getDefaultSharedPreferences(activity);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(c).inflate(R.layout.sura_ayah_list,parent,false);
        RecyclerViewHolder rvHolder = new RecyclerViewHolder(v);
        rvHolder.content_bn.setTypeface(font);
        return rvHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RecyclerViewHolder rvHolder= (RecyclerViewHolder) holder;
        final Ayah ayah = ayahs.get(position);
        rvHolder.ayah_index.setText(ayah.getAyah_index());
        rvHolder.text_tashkeel.setText(ayah.getText_tashkeel());
        rvHolder.content_en.setText(ayah.getContent_en());
        rvHolder.content_bn.setText(ayah.getContent_bn());
        rvHolder.ayah_num.setText(ayah.getAyah_num());
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
                String activityName = activity.getClass().getSimpleName();
                if(activityName.equals("SuraDetailsActivity")){
                    TextView play_audio = (TextView) activity.findViewById(R.id.play_audio);
                    play_audio.setVisibility(View.VISIBLE);
                    TextView pause_audio = (TextView) activity.findViewById(R.id.pause_audio);
                    pause_audio.setVisibility(View.GONE);
                    TextView resume_audio = (TextView) activity.findViewById(R.id.resume_audio);
                    resume_audio.setVisibility(View.GONE);
                    TextView stop_audio = (TextView) activity.findViewById(R.id.stop_audio);
                    stop_audio.setVisibility(View.GONE);
                }
                if (isInternetPresent) {
                    if (checkPermission()) {
                        new AsyncTask<Void, Void, Void>() {
                            protected void onPreExecute() {
                                pd = new ProgressDialog(c);
                                pd.setTitle("Processing...");
                                pd.setMessage("Please wait.");
                                pd.setCancelable(true);
                                pd.setIndeterminate(true);
                                pd.show();
                            }

                            protected Void doInBackground(Void... params) {
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
                                if (pd != null && pd.isShowing()) {
                                    pd.dismiss();
                                }
                            }
                        }.execute();
                    }else{
                        requestPermission(); // Code for permission
                    }
                }else{
                    AlertDialog.Builder alert = new AlertDialog.Builder(c);
                    alert.setTitle(R.string.text_warning);
                    alert.setMessage(R.string.text_enable_internet);
                    alert.setPositiveButton(R.string.text_ok,null);
                    alert.show();
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

        rvHolder.tafsirs.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try {
                    Intent in = new Intent(c, TafsirActivity.class);
                    in.putExtra("ayah_index", ayah.getAyah_index());
                    in.putExtra("text_tashkeel", ayah.getText_tashkeel());
                    in.putExtra("content_en", ayah.getContent_en());
                    in.putExtra("content_bn", ayah.getContent_bn());
                    in.putExtra("ayah_num", ayah.getAyah_num());
                    in.putExtra("surah_id", ayah.getSurah_id());
                    in.putExtra("ayah_key", ayah.getAyah_key());
                    c.startActivity(in);
                }catch (Exception e) {
                    Log.e("Tafsirs", e.getMessage());
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
        TextView tafsirs;
        Button playBtn;
        Button bookmarkBtn;
        Button wordMeaningButton;
        TextView ayah_num;
        Button shareButton;
        Button copyButton;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            ayah_index = (TextView) itemView.findViewById(R.id.ayah_index);
            ayah_num = (TextView) itemView.findViewById(R.id.ayah_num);
            text_tashkeel = (TextView) itemView.findViewById(R.id.text_tashkeel);
            content_en = (TextView) itemView.findViewById(R.id.content_en);
            content_bn = (TextView) itemView.findViewById(R.id.content_bn);
            content_bn.setTypeface(font);
            sajdah = (TextView) itemView.findViewById(R.id.sajdah);
            playBtn = (Button) itemView.findViewById(R.id.playBtn);
            bookmarkBtn = (Button) itemView.findViewById(R.id.bookmarkBtn);
            wordMeaningButton = (Button) itemView.findViewById(R.id.wordMeaningButton);
            shareButton = (Button) itemView.findViewById(R.id.shareButton);
            copyButton = (Button) itemView.findViewById(R.id.copyButton);
            tafsirs = (TextView) itemView.findViewById(R.id.tafsirs);
            //
            String mp_arabicFontFamily = mPrefs.getString("arabicFontFamily", "Arabic Regular");
            String mp_arFz = mPrefs.getString("arFontSize", "30");
            String mp_enFz = mPrefs.getString("enFontSize", "15");
            String mp_bnFz = mPrefs.getString("bnFontSize", "15");
            /*Log.i("arabicFontFamily", mp_arabicFontFamily);
            Log.i("arFontSize", mp_arFz);
            Log.i("enFontSize", mp_enFz);
            Log.i("bnFontSize", mp_bnFz);*/
            if(mp_arabicFontFamily.equals("Al Majeed Quranic Font")){
                text_tashkeel.setTypeface(fontAlmajeed);
            }
            if(mp_arabicFontFamily.equals("Al Qalam Quran")){
                text_tashkeel.setTypeface(fontAlQalam);
            }
            if(mp_arabicFontFamily.equals("Uthmanic Script")){
                text_tashkeel.setTypeface(fontUthmani);
            }
            if(mp_arabicFontFamily.equals("Noore Hidayat")){
                text_tashkeel.setTypeface(fontNooreHidayat);
            }
            if(mp_arabicFontFamily.equals("Saleem Quran")){
                text_tashkeel.setTypeface(fontSaleem);
            }
            if(!mp_arFz.equals("")){
                text_tashkeel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_arFz));
            }
            if(!mp_enFz.equals("")){
                content_en.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_enFz));
            }
            if(!mp_bnFz.equals("")){
                content_bn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_bnFz));
            }
        }
    }
}
