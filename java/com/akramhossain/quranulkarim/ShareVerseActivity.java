package com.akramhossain.quranulkarim;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akramhossain.quranulkarim.helper.DatabaseHelper;
import com.akramhossain.quranulkarim.util.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Random;

public class ShareVerseActivity extends AppCompatActivity {

    public static String ayah_index;
    public static String text_tashkeel;
    public static String content_en;
    public static String content_bn;
    public static String ayah_num;
    public static String surah_id;
    public static String ayah_key;
    public static String surah_name;
    public static String ayah_trans;
    public static String text_tajweed;

    DatabaseHelper dbhelper;
    Typeface font, fontUthmani, fontAlmajeed, fontAlQalam, fontNooreHidayat, fontSaleem, fontTahaNaskh, fontKitab;

    TextView tv_surah_name,tv_ayah_arabic,tv_ayah_english,tv_ayah_bangla,tv_ayah_num, tv_trans;
    Button shareBtn,colorBtn, imgBtn, dowlBtn;
    Bitmap bitmap;
    int randomNumber;

    private static final int PERMISSION_REQUEST_CODE = 100;

    int[] androidColors;
    String[] androidStringColors;

    SharedPreferences mPrefs;

    String fontFamily = "fontUthmani";
    String fontSize = "30px";
    String bodyBgColor = "#303030";
    String bodyTxtColor = "#ffffff";
    WebView wv_text_tajweed;

    String appTheme = "";

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
            ayah_trans = extras.getString("trans");
            text_tajweed = extras.getString("text_tajweed");
        }

        mPrefs = getApplicationContext().getSharedPreferences(Utils.PREF_NAME, 0);

        font = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/Siyamrupali.ttf");
        fontUthmani = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/KFGQPC_Uthmanic_Script_HAFS_Regular.ttf");
        fontAlmajeed = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/AlMajeedQuranicFont_shiped.ttf");
        fontAlQalam = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/AlQalamQuran.ttf");
        fontNooreHidayat = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/noorehidayat.ttf");
        fontSaleem = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/PDMS_Saleem_QuranFont.ttf");

        fontTahaNaskh = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/KFGQPC_Uthman_Taha_Naskh_Regular.ttf");
        fontKitab = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/kitab.ttf");

        //dbhelper = new DatabaseHelper(getApplicationContext());
        dbhelper = DatabaseHelper.getInstance(getApplicationContext());

        String suraSql = "SELECT * FROM sura WHERE surah_id = "+surah_id;
        SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
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

        tv_trans = (TextView) findViewById(R.id.trans);
        tv_trans.setTypeface(font);
        tv_trans.setText(ayah_trans);

        wv_text_tajweed = (WebView) findViewById(R.id.text_tajweed);

        String mp_arabicFontFamily = mPrefs.getString("arabicFontFamily", "Noore Huda");
        String mp_arFz = mPrefs.getString("arFontSize", "30");
        String mp_enFz = mPrefs.getString("enFontSize", "15");
        String mp_bnFz = mPrefs.getString("bnFontSize", "15");
        if(mp_arabicFontFamily.equals("Al Majeed Quranic Font")){
            tv_ayah_arabic.setTypeface(fontAlmajeed);
            fontFamily = "fontAlmajeed";
        }
        if(mp_arabicFontFamily.equals("Al Qalam Quran")){
            tv_ayah_arabic.setTypeface(fontAlQalam);
            fontFamily = "fontAlQalam";
        }
        if(mp_arabicFontFamily.equals("Noore Huda")){
            tv_ayah_arabic.setTypeface(fontUthmani);
            fontFamily = "fontUthmani";
        }
        if(mp_arabicFontFamily.equals("Noore Hidayat")){
            tv_ayah_arabic.setTypeface(fontNooreHidayat);
            fontFamily = "fontNooreHidayat";
        }
        if(mp_arabicFontFamily.equals("Saleem Quran")){
            tv_ayah_arabic.setTypeface(fontSaleem);
            fontFamily = "fontSaleem";
        }
        if(mp_arabicFontFamily.equals("KFGQPC Uthman Taha Naskh")){
            tv_ayah_arabic.setTypeface(fontTahaNaskh);
            fontFamily = "fontTahaNaskh";
        }
        if(mp_arabicFontFamily.equals("Arabic Regular")){
            tv_ayah_arabic.setTypeface(fontKitab);
            fontFamily = "fontKitab";
        }
        if(!mp_arFz.equals("")){
            tv_ayah_arabic.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_arFz));
            fontSize = mp_arFz+"px";
        }
        if(!mp_enFz.equals("")){
            tv_ayah_english.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_enFz));
        }
        if(!mp_bnFz.equals("")){
            tv_ayah_bangla.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_bnFz));
            tv_trans.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_bnFz));
        }
        appTheme = mPrefs.getString("APP_NIGHT_MODE", "-1");
        if (appTheme.equals("1")) {
            bodyBgColor = "#303030";
            bodyTxtColor = "#ffffff";
        }else if (appTheme.equals("0")) {
            bodyBgColor = "#FAFAFA";
            bodyTxtColor = "#000000";
        }else {
            bodyBgColor = "#303030";
            bodyTxtColor = "#ffffff";
        }
        String style = Utils.tajweedCss(fontFamily,fontSize,"#1b3022","#ffffff", appTheme);
        String html = "<html><head>"+style+"</head><body>"+text_tajweed+"</body></html>";
        wv_text_tajweed.loadDataWithBaseURL(null,html, "text/html; charset=utf-8", "UTF-8",null);
        String mushaf = mPrefs.getString("mushaf", "IndoPak");
        if(mushaf.equals("Tajweed")) {
            wv_text_tajweed.setVisibility(View.VISIBLE);
            tv_ayah_arabic.setVisibility(View.GONE);
        }else{
            wv_text_tajweed.setVisibility(View.GONE);
            tv_ayah_arabic.setVisibility(View.VISIBLE);
        }

        /*Log.d("arabic",text_tashkeel);
        Log.d("index",ayah_index);
        Log.d("BN",content_bn);
        Log.d("EN",content_en);
        Log.d("id",surah_id);
        Log.d("ayah",ayah_num);
        Log.d("key",ayah_key);
        Log.d("surah",surah_name);*/

        String show_bn_pron = mPrefs.getString("show_bn_pron", "2");
        if (show_bn_pron.equals("-1")) {
            tv_trans.setVisibility(View.GONE);
        }
        String show_en_trans = mPrefs.getString("show_en_trans", "2");
        if (show_en_trans.equals("-1")) {
            tv_ayah_english.setVisibility(View.GONE);
        }
        String show_bn_trans = mPrefs.getString("show_bn_trans", "2");
        if (show_bn_trans.equals("-1")) {
            tv_ayah_bangla.setVisibility(View.GONE);
        }

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
                        String mPath = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DCIM) + "/" + "share-ayah.jpg";
                        //ContextWrapper cw = new ContextWrapper(getApplicationContext());
                        //File directory = cw.getDir(Environment.DIRECTORY_DCIM, Context.MODE_PRIVATE);
                        //File file = new File(Environment.DIRECTORY_DCIM, "share-ayah" + ".jpg");
                        //String mPath = file.toString();
                        //
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
                            shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            shareIntent.setFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                            shareIntent.setType("image/*");
                            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                            //
                            Intent chooser = Intent.createChooser(shareIntent, "Share Ayah");
                            List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY);
                            for (ResolveInfo resolveInfo : resInfoList) {
                                String packageName = resolveInfo.activityInfo.packageName;
                                getApplicationContext().grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            }
                            startActivity(chooser);

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
                        //
                        Intent chooser = Intent.createChooser(shareIntent, "Share Ayah");
                        List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY);
                        for (ResolveInfo resolveInfo : resInfoList) {
                            String packageName = resolveInfo.activityInfo.packageName;
                            getApplicationContext().grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        }
                        startActivity(chooser);

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
                //
                String hexColor = Integer.toHexString(color).substring(2);
                Log.d("color",hexColor);
                String bgColor = "#"+hexColor;
                String style = Utils.tajweedCss(fontFamily,fontSize,bgColor,"#ffffff", appTheme);
                String html = "<html><head>"+style+"</head><body>"+text_tajweed+"</body></html>";
                wv_text_tajweed.loadDataWithBaseURL(null,html, "text/html; charset=utf-8", "UTF-8",null);
            }
        });

        imgBtn =  (Button) findViewById(R.id.imgBtn);
        androidStringColors = getResources().getStringArray(R.array.stringcolors);
        int arrayLength = androidStringColors.length;

        Random random = new Random();
        int randomNumber = random.nextInt(arrayLength);
        int randomNumber1 = random.nextInt(arrayLength);
        //Log.d("Color",androidStringColors[randomNumber]);
        int[] colors = {Color.parseColor(androidStringColors[randomNumber]),Color.parseColor(androidStringColors[randomNumber1])};
        GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,colors);
        gd.setCornerRadius(0f);
        imgBtn.setBackground(gd);

        imgBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int arrayLength = androidStringColors.length;
                Random random = new Random();
                int randomNumber = random.nextInt(arrayLength);
                int randomNumber1 = random.nextInt(arrayLength);
                //
                int[] colors = {Color.parseColor(androidStringColors[randomNumber]),Color.parseColor(androidStringColors[randomNumber1])};
                GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,colors);
                gd.setCornerRadius(0f);
                //
                RelativeLayout layout = (RelativeLayout)findViewById(R.id.shareSection);
                layout.setBackground(gd);
                imgBtn.setBackground(gd);
                //
                //
                /*int gdColor1 = Color.parseColor(androidStringColors[randomNumber]);
                int gdColor2 = Color.parseColor(androidStringColors[randomNumber1]);
                String hexColor1 = Integer.toHexString(gdColor2).substring(2);
                Log.d("gd top color",hexColor1);
                String bgColor = "#"+hexColor1;
                String style = Utils.tajweedCss(fontFamily,fontSize,"#FAFAFA","#000000",appTheme);
                String html = "<html><head>"+style+"</head><body>"+text_tajweed+"</body></html>";
                wv_text_tajweed.loadDataWithBaseURL(null,html, "text/html; charset=utf-8", "UTF-8",null);*/
            }
        });

        dowlBtn = (Button) findViewById(R.id.dowlBtn);
        dowlBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                RelativeLayout layout = (RelativeLayout)findViewById(R.id.shareSection);
                layout.setDrawingCacheEnabled(true);
                //bitmap = Bitmap.createBitmap(layout.getDrawingCache());
                bitmap = loadBitmapFromView(layout, layout.getWidth(), layout.getHeight());

                layout.setDrawingCacheEnabled(false);

                if (Build.VERSION.SDK_INT >= 23){
                    if (checkPermission()) {
                        String mPath = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DCIM) + "/" + "share-ayah.jpg";
                        OutputStream fout = null;
                        File imageFile = new File(mPath);
                        try {
                            fout = new FileOutputStream(imageFile);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout);
                            fout.flush();
                            fout.close();

                            String fileName = "surah_"+surah_id+"_ayah_"+ayah_num+"_"+ayah_index+".jpg";

                            MediaStore.Images.Media.insertImage(getContentResolver(),imageFile.getAbsolutePath(),fileName,imageFile.getName());

                            Toast.makeText(getApplicationContext(), "Image saved!.", Toast.LENGTH_LONG).show();

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else {
                        requestPermission(); // Code for permission
                    }
                }
            }
        });

    }

    public static Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width , height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(0, 0, width, height);
        //Get the view’s background
        Drawable bgDrawable = v.getBackground();

        if (bgDrawable!=null) {
            //has background drawable, then draw it on the canvas
            if(bgDrawable instanceof GradientDrawable){
                GradientDrawable gradientDrawable = (GradientDrawable) bgDrawable;
                gradientDrawable.setBounds(0, 0, c.getWidth(), c.getHeight());
                gradientDrawable.draw(c);
            }else {
                bgDrawable.draw(c);
            }
        }
        else {
            //does not have background drawable, then draw white background on the canvas
            c.drawColor(Color.WHITE);
        }
        v.draw(c);
        return b;
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return true;
        }
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
            ActivityCompat.requestPermissions(ShareVerseActivity.this, permissions(), PERMISSION_REQUEST_CODE);
        }
    }

    public static String[] storage_permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String[] storage_permissions_33 = {
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_AUDIO,
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
                    RelativeLayout layout = (RelativeLayout) findViewById(R.id.shareSection);
                    layout.setDrawingCacheEnabled(true);
                    bitmap = loadBitmapFromView(layout, layout.getWidth(), layout.getHeight());
                    layout.setDrawingCacheEnabled(false);
                    //String mPath = Environment.getExternalStorageDirectory().toString() + "/" + "share-ayah.jpg";
                    String mPath = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DCIM) + "/" + "share-ayah.jpg";
                    OutputStream fout = null;
                    File imageFile = new File(mPath);
                    try {
                        fout = new FileOutputStream(imageFile);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fout);
                        fout.flush();
                        fout.close();
                        Uri uri = FileProvider.getUriForFile(getApplicationContext(), "com.akramhossain.quranulkarim.provider", imageFile);
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        shareIntent.setType("image/*");
                        shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        //
                        Intent chooser = Intent.createChooser(shareIntent, "Share Ayah");
                        List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY);
                        for (ResolveInfo resolveInfo : resInfoList) {
                            String packageName = resolveInfo.activityInfo.packageName;
                            getApplicationContext().grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        }
                        startActivity(chooser);

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
