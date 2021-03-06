package com.akramhossain.quranulkarim;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akramhossain.quranulkarim.helper.DatabaseHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

public class ShareVerseActivity extends Activity {

    public static String ayah_index;
    public static String text_tashkeel;
    public static String content_en;
    public static String content_bn;
    public static String ayah_num;
    public static String surah_id;
    public static String ayah_key;
    public static String surah_name;
    DatabaseHelper dbhelper;
    Typeface font;

    TextView tv_surah_name,tv_ayah_arabic,tv_ayah_english,tv_ayah_bangla,tv_ayah_num;
    Button shareBtn,colorBtn;
    Bitmap bitmap;

    private static final int PERMISSION_REQUEST_CODE = 100;

    int[] androidColors;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            ayah_index = extras.getString("ayah_index");
            text_tashkeel = extras.getString("text_tashkeel");
            content_en = extras.getString("content_en");
            content_bn = extras.getString("content_bn");
            ayah_num = extras.getString("ayah_num");
            surah_id = extras.getString("surah_id");
            ayah_key = extras.getString("ayah_key");
        }

        font = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/Siyamrupali.ttf");

        dbhelper = new DatabaseHelper(getApplicationContext());

        String suraSql = "SELECT * FROM sura WHERE surah_id = "+surah_id;
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(suraSql,null);

        try {
            if (cursor.moveToFirst()) {
                surah_name = cursor.getString(5);
            }
        }catch (Exception e){
            Log.i("Share Verse", e.getMessage());
        }finally {
            if (cursor != null && !cursor.isClosed()){
                cursor.close();
            }
            db.close();
        }

        setTitle("Share Ayah");

        setContentView(R.layout.activity_share_verse);

        tv_surah_name = (TextView) findViewById(R.id.surah_name);
        tv_surah_name.setText("Surah "+surah_name);

        tv_ayah_arabic = (TextView) findViewById(R.id.ayah_arabic);
        tv_ayah_arabic.setText(text_tashkeel);

        tv_ayah_english = (TextView) findViewById(R.id.ayah_english);
        tv_ayah_english.setText(content_en);

        tv_ayah_bangla = (TextView) findViewById(R.id.ayah_bangla);
        tv_ayah_bangla.setTypeface(font);
        tv_ayah_bangla.setText(content_bn);

        tv_ayah_num = (TextView) findViewById(R.id.ayah_num);
        tv_ayah_num.setText(surah_name+" "+ayah_key);

        Log.d("arabic",text_tashkeel);
        Log.d("index",ayah_index);
        Log.d("BN",content_bn);
        Log.d("EN",content_en);
        Log.d("id",surah_id);
        Log.d("ayah",ayah_num);
        Log.d("key",ayah_key);
        Log.d("surah",surah_name);

        shareBtn = (Button) findViewById(R.id.shareBtn);

        shareBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                RelativeLayout layout = (RelativeLayout)findViewById(R.id.shareSection);
                layout.setDrawingCacheEnabled(true);
                //bitmap = Bitmap.createBitmap(layout.getDrawingCache());
                bitmap = loadBitmapFromView(layout, layout.getWidth(), layout.getHeight());

                layout.setDrawingCacheEnabled(false);

                if (Build.VERSION.SDK_INT >= 23){
                    if (checkPermission()) {
                        String mPath = Environment.getExternalStorageDirectory().toString() + "/" + "share-ayah.jpg";
                        OutputStream fout = null;
                        File imageFile = new File(mPath);
                        try {
                            fout = new FileOutputStream(imageFile);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout);
                            fout.flush();
                            fout.close();
                            Uri uri = FileProvider.getUriForFile(getApplicationContext(),"com.akramhossain.quranulkarim.provider", imageFile);
                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);
                            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                            shareIntent.setType("image/*");
                            startActivity(Intent.createChooser(shareIntent, "Share Ayah"));

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else {
                        requestPermission(); // Code for permission
                    }
                }else{
                    String mPath = Environment.getExternalStorageDirectory().toString() + "/" + "share-ayah.jpg";
                    OutputStream fout = null;
                    File imageFile = new File(mPath);
                    try {
                        fout = new FileOutputStream(imageFile);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout);
                        fout.flush();
                        fout.close();
                        Uri uri = FileProvider.getUriForFile(getApplicationContext(),"com.akramhossain.quranulkarim.provider", imageFile);
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        shareIntent.setType("image/*");
                        startActivity(Intent.createChooser(shareIntent, "Share Ayah"));

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        colorBtn = (Button) findViewById(R.id.colorBtn);

        androidColors = getResources().getIntArray(R.array.androidcolors);
        int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];

        colorBtn.setBackgroundColor(randomAndroidColor);

        colorBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                RelativeLayout layout = (RelativeLayout)findViewById(R.id.shareSection);

                int color = ((ColorDrawable)colorBtn.getBackground()).getColor();

                layout.setBackgroundColor(color);

                int nextAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
                //
                colorBtn.setBackgroundColor(nextAndroidColor);
            }
        });
    }

    public static Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width , height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(0, 0, width, height);
        //Get the view’s background
        Drawable bgDrawable =v.getBackground();
        if (bgDrawable!=null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(c);
        }
        else {
            //does not have background drawable, then draw white background on the canvas
            c.drawColor(Color.WHITE);
        }
        v.draw(c);
        return b;
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(ShareVerseActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(ShareVerseActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(ShareVerseActivity.this, "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(ShareVerseActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    RelativeLayout layout = (RelativeLayout)findViewById(R.id.shareSection);
                    layout.setDrawingCacheEnabled(true);
                    bitmap = loadBitmapFromView(layout, layout.getWidth(), layout.getHeight());
                    layout.setDrawingCacheEnabled(false);
                    String mPath = Environment.getExternalStorageDirectory().toString() + "/" + "share-ayah.jpg";
                    OutputStream fout = null;
                    File imageFile = new File(mPath);
                    try {
                        fout = new FileOutputStream(imageFile);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout);
                        fout.flush();
                        fout.close();
                        Uri uri = FileProvider.getUriForFile(getApplicationContext(),"com.akramhossain.quranulkarim.provider", imageFile);
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        shareIntent.setType("image/*");
                        startActivity(Intent.createChooser(shareIntent, "Share Ayah"));

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

            } else {
                Log.e("value", "Permission Denied, You cannot use local drive .");
            }
            break;
        }
    }
}
