package com.akramhossain.quranulkarim;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akramhossain.quranulkarim.helper.DatabaseHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BackupRestoreActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String TAG = BackupRestoreActivity.class.getSimpleName();
    String bookmarkStr="";
    String quickLinkStr="";
    String reportStr="";
    String lastPositionStr="";
    String wordAnserStr="";
    int PICKFILE_REQUEST_CODE=1;
    SharedPreferences mPrefs;

    TextView lastBckpTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_restore);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String lastBackupTimeInPm = mPrefs.getString("LAST_BACKUP_TIME", "");
        lastBckpTime = (TextView) findViewById(R.id.lastBckpTime);
        lastBckpTime.setText("Last backup: "+lastBackupTimeInPm);
        if(!lastBackupTimeInPm.equals("")){
            lastBckpTime.setVisibility(View.VISIBLE);
        }else{
            lastBckpTime.setVisibility(View.GONE);
        }
        Button backup = (Button) findViewById(R.id.button);
        backup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                String sql = "select * from bookmark";
                Cursor cursor = db.rawQuery(sql, null);
                try {
                    if (cursor.moveToFirst()) {
                        do {
                            bookmarkStr+= "insert into bookmark(ayah_id) values("+cursor.getString(cursor.getColumnIndexOrThrow("ayah_id"))+");";
                        }while (cursor.moveToNext());
                    }
                }catch (Exception e){
                    Log.i(TAG, e.getMessage());
                }
                finally {
                    if (cursor != null && !cursor.isClosed()){
                        cursor.close();
                    }
                    db.close();
                }
                //
                SQLiteDatabase db1 = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                String sql1 = "select * from quick_link";
                Cursor cursor1 = db1.rawQuery(sql1, null);
                try {
                    if (cursor1.moveToFirst()) {
                        do {
                            quickLinkStr+= "insert into quick_link(sura_id) values("+cursor1.getString(cursor1.getColumnIndexOrThrow("sura_id"))+");";
                        }while (cursor1.moveToNext());
                    }
                }catch (Exception e){
                    Log.i(TAG, e.getMessage());
                }
                finally {
                    if (cursor1 != null && !cursor1.isClosed()){
                        cursor1.close();
                    }
                    db1.close();
                }
                //
                SQLiteDatabase db2 = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                String sql2 = "select * from reports";
                Cursor cursor2 = db2.rawQuery(sql2, null);
                try {
                    if (cursor2.moveToFirst()) {
                        do {
                            reportStr+= "insert into reports(date,perform_tahajjud,perform_fajr,morning_adhkar,quran_recitation,study_hadith,salat_ud_doha,dhuhr_prayer,asr_prayer,maghrib_prayer,isha_prayer,charity,literature,surah_mulk_recitation,recitation_last_2_surah_baqarah,ayatul_kursi,recitation_first_last_10_surah_kahf,tasbih,smoking,alcohol,haram_things,backbiting,slandering) " +
                                    "values('"+cursor2.getString(cursor2.getColumnIndexOrThrow("date"))+"',"+cursor2.getString(cursor2.getColumnIndexOrThrow("perform_tahajjud"))+","+cursor2.getString(cursor2.getColumnIndexOrThrow("perform_fajr"))+","+cursor2.getString(cursor2.getColumnIndexOrThrow("morning_adhkar"))+","+cursor2.getString(cursor2.getColumnIndexOrThrow("quran_recitation"))+","+cursor2.getString(cursor2.getColumnIndexOrThrow("study_hadith"))+","+cursor2.getString(cursor2.getColumnIndexOrThrow("salat_ud_doha"))+","+cursor2.getString(cursor2.getColumnIndexOrThrow("dhuhr_prayer"))+","+cursor2.getString(cursor2.getColumnIndexOrThrow("asr_prayer"))+","+cursor2.getString(cursor2.getColumnIndexOrThrow("maghrib_prayer"))+","+cursor2.getString(cursor2.getColumnIndexOrThrow("isha_prayer"))+","+cursor2.getString(cursor2.getColumnIndexOrThrow("charity"))+","+cursor2.getString(cursor2.getColumnIndexOrThrow("literature"))+","+cursor2.getString(cursor2.getColumnIndexOrThrow("surah_mulk_recitation"))+","+cursor2.getString(cursor2.getColumnIndexOrThrow("recitation_last_2_surah_baqarah"))+","+cursor2.getString(cursor2.getColumnIndexOrThrow("ayatul_kursi"))+","+cursor2.getString(cursor2.getColumnIndexOrThrow("recitation_first_last_10_surah_kahf"))+","+cursor2.getString(cursor2.getColumnIndexOrThrow("tasbih"))+","+cursor2.getString(cursor2.getColumnIndexOrThrow("smoking"))+","+cursor2.getString(cursor2.getColumnIndexOrThrow("alcohol"))+","+cursor2.getString(cursor2.getColumnIndexOrThrow("haram_things"))+","+cursor2.getString(cursor2.getColumnIndexOrThrow("backbiting"))+","+cursor2.getString(cursor2.getColumnIndexOrThrow("slandering"))+");";
                        }while (cursor2.moveToNext());
                    }
                }catch (Exception e){
                    Log.i(TAG, e.getMessage());
                }
                finally {
                    if (cursor2 != null && !cursor2.isClosed()){
                        cursor2.close();
                    }
                    db2.close();
                }
                //
                SQLiteDatabase db3 = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                String sql3 = "select * from last_position";
                Cursor cursor3 = db3.rawQuery(sql3, null);
                try {
                    if (cursor3.moveToFirst()) {
                        do {
                            lastPositionStr+= "insert into last_position(sura_id,position) values("+cursor3.getString(cursor3.getColumnIndexOrThrow("sura_id"))+","+cursor3.getString(cursor3.getColumnIndexOrThrow("position"))+");";
                        }while (cursor3.moveToNext());
                    }
                }catch (Exception e){
                    Log.i(TAG, e.getMessage());
                }
                finally {
                    if (cursor3 != null && !cursor3.isClosed()){
                        cursor3.close();
                    }
                    db3.close();
                }
                //
                SQLiteDatabase db4 = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                String sql4 = "select * from word_answers";
                Cursor cursor4 = db4.rawQuery(sql4, null);
                try {
                    if (cursor4.moveToFirst()) {
                        do {
                            wordAnserStr+= "insert into word_answers(word_id,datetime,is_right_answer) values("+cursor4.getString(cursor4.getColumnIndexOrThrow("word_id"))+",'"+cursor4.getString(cursor4.getColumnIndexOrThrow("datetime"))+"',"+cursor4.getString(cursor4.getColumnIndexOrThrow("is_right_answer"))+");";
                        }while (cursor4.moveToNext());
                    }
                }catch (Exception e){
                    Log.i(TAG, e.getMessage());
                }
                finally {
                    if (cursor4 != null && !cursor4.isClosed()){
                        cursor4.close();
                    }
                    db4.close();
                }
                /*Log.i(TAG, bookmarkStr);
                Log.i(TAG, quickLinkStr);
                Log.i(TAG, reportStr);
                Log.i(TAG, lastPositionStr);*/
                try {
                    File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    long backup_num = timestamp.getTime();
                    File myFile = new File(path, "quran_tafsir_english_bangla_"+String.valueOf(backup_num)+".backup");
                    FileOutputStream fOut = new FileOutputStream(myFile,true);
                    OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                    myOutWriter.append(bookmarkStr);
                    myOutWriter.append(quickLinkStr);
                    myOutWriter.append(reportStr);
                    myOutWriter.append(lastPositionStr);
                    myOutWriter.append(wordAnserStr);
                    myOutWriter.close();
                    fOut.close();
                    Toast.makeText(getApplicationContext(),"Backup saved to "+path,Toast.LENGTH_LONG).show();

                    DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm a");
                    Date date = new Date();
                    SharedPreferences.Editor editor = mPrefs.edit();
                    editor.putString("LAST_BACKUP_TIME", dateFormat.format(date));
                    lastBckpTime.setText("Last backup: "+dateFormat.format(date));
                    if(!lastBackupTimeInPm.equals("")){
                        lastBckpTime.setVisibility(View.VISIBLE);
                    }else{
                        lastBckpTime.setVisibility(View.GONE);
                    }
                    editor.apply();
                }
                catch (java.io.IOException e) {
                    //do something if an IOException occurs.
                    Toast.makeText(getApplicationContext(),"ERROR - Could't not backup data",Toast.LENGTH_LONG).show();
                }
            }
        });
        Button restore = (Button) findViewById(R.id.button1);
        restore.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
// Ask specifically for something that can be opened:
                chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
                chooseFile.setType("application/*");
                startActivityForResult(
                        Intent.createChooser(chooseFile, "Choose a file"),
                        PICKFILE_REQUEST_CODE
                );
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICKFILE_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                Uri uri = data.getData();
                String fileContent = readTextFile(uri);
                SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                String sql = "delete from quick_link;delete from last_position;delete from bookmark;delete from reports;delete from word_answers;"+fileContent;
                //Log.i(TAG, fileContent);
                db.beginTransaction();
                try {
                    String[] arrOfSql = sql.split(";");
                    for (String sq : arrOfSql){
                        //Log.i(TAG, sq);
                        db.execSQL(sq+";");
                    }
                    db.setTransactionSuccessful();
                    Toast.makeText(getApplicationContext(),"Data successfully restored",Toast.LENGTH_LONG).show();
                }catch (Exception e) {
                    Log.i("Restore Backup", e.getMessage());
                } finally {
                    db.endTransaction();
                    db.close();
                }
            }
        }
    }

    private String readTextFile(Uri uri)
    {
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try
        {
            reader = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(uri)));
            String line = "";
            while ((line = reader.readLine()) != null)
            {
                builder.append(line);
            }
            reader.close();
        }
        catch (IOException e) {e.printStackTrace();}
        return builder.toString();
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return true;
        }
        int result = ContextCompat.checkSelfPermission(BackupRestoreActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(BackupRestoreActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(BackupRestoreActivity.this, "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(BackupRestoreActivity.this, permissions(), PERMISSION_REQUEST_CODE);
        }
    }

    public static String[] storage_permissions = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String[] storage_permissions_33 = {
            android.Manifest.permission.READ_MEDIA_IMAGES,
            android.Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_VIDEO
    };

    public static String[] permissions() {
        String[] p;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            p = storage_permissions_33;
        } else {
            p = storage_permissions;
        }
        return p;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

}