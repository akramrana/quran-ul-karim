package com.akramhossain.quranulkarim.adapter;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
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
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
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
import com.akramhossain.quranulkarim.task.BackgroundTask;
import com.akramhossain.quranulkarim.util.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


public class SearchTermViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context c;
    ArrayList<Ayah> ayahs;
    Typeface font, fontUthmani, fontAlmajeed, fontAlQalam, fontNooreHidayat, fontSaleem, fontTahaNaskh, fontKitab;
    MediaPlayer mp;
    ProgressDialog pd;
    DatabaseHelper dbhelper;
    private Activity activity;
    private static final int PERMISSION_REQUEST_CODE = 100;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    //SQLiteDatabase db;
    SharedPreferences mPrefs;

    public SearchTermViewAdapter(Context c, ArrayList<Ayah> ayahs, Activity activity) {
        this.c = c;
        this.ayahs = ayahs;
        font = Typeface.createFromAsset(c.getAssets(), "fonts/Siyamrupali.ttf");
        fontUthmani = Typeface.createFromAsset(c.getAssets(), "fonts/KFGQPC_Uthmanic_Script_HAFS_Regular.ttf");
        fontAlmajeed = Typeface.createFromAsset(c.getAssets(), "fonts/AlMajeedQuranicFont_shiped.ttf");
        fontAlQalam = Typeface.createFromAsset(c.getAssets(), "fonts/AlQalamQuran.ttf");
        fontNooreHidayat = Typeface.createFromAsset(c.getAssets(), "fonts/noorehidayat.ttf");
        fontSaleem = Typeface.createFromAsset(c.getAssets(), "fonts/PDMS_Saleem_QuranFont.ttf");
        fontTahaNaskh = Typeface.createFromAsset(c.getAssets(),"fonts/KFGQPC_Uthman_Taha_Naskh_Regular.ttf");
        fontKitab = Typeface.createFromAsset(c.getAssets(),"fonts/kitab.ttf");
        //dbhelper = new DatabaseHelper(c);
        dbhelper = DatabaseHelper.getInstance(c);
        this.activity = activity;
        cd = new ConnectionDetector(c);
        isInternetPresent = cd.isConnectingToInternet();
        //db = DatabaseHelper.getInstance(c).getWritableDatabase();
        mPrefs = PreferenceManager.getDefaultSharedPreferences(activity);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.search_term_list, parent, false);
        RecyclerViewHolder rvHolder = new RecyclerViewHolder(v);
        rvHolder.content_bn.setTypeface(font);
        rvHolder.trans.setTypeface(font);
        return rvHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final RecyclerViewHolder rvHolder = (RecyclerViewHolder) holder;
        final Ayah ayah = ayahs.get(position);
        rvHolder.ayah_index.setText(ayah.getAyah_index());
        String mushaf = mPrefs.getString("mushaf", "IndoPak");
        if(mushaf.equals("ImlaeiSimple")) {
            rvHolder.text_tashkeel.setText(ayah.getText_tashkeel());
        }
        else if(mushaf.equals("Uthmanic")) {
            rvHolder.text_tashkeel.setText(ayah.getText_uthmani());
        }
        else {
            rvHolder.text_tashkeel.setText(ayah.getIndo_pak());
        }
        rvHolder.content_en.setText(ayah.getContent_en());
        rvHolder.content_bn.setText(ayah.getContent_bn());
        rvHolder.ayah_num.setText(ayah.getAyah_key());
        String sajdahText = "";
        if (ayah.getSajdah().equals("0")) {
            sajdahText = "No";
        } else {
            sajdahText = "Yes";
        }
        rvHolder.sajdah.setText("Sajdah: " + sajdahText);

        rvHolder.trans.setText(ayah.getTrans());
        //
        if(mushaf.equals("Tajweed")) {
            rvHolder.text_tajweed.setVisibility(View.VISIBLE);
            rvHolder.ayah_num.setVisibility(View.GONE);
            rvHolder.text_tashkeel.setVisibility(View.GONE);
        }else{
            rvHolder.text_tajweed.setVisibility(View.GONE);
            rvHolder.ayah_num.setVisibility(View.VISIBLE);
            rvHolder.text_tashkeel.setVisibility(View.VISIBLE);
        }
        String mp_arFz = mPrefs.getString("arFontSize", "30");
        String mp_arabicFontFamily = mPrefs.getString("arabicFontFamily", "Noore Huda");
        String fontFamily = "fontUthmani";
        String fontSize = "30px";
        if(mp_arabicFontFamily.equals("Al Majeed Quranic Font")){
            fontFamily = "fontAlmajeed";
        }
        if(mp_arabicFontFamily.equals("Al Qalam Quran")){
            fontFamily = "fontAlQalam";
        }
        if(mp_arabicFontFamily.equals("Noore Huda")){
            fontFamily = "fontUthmani";
        }
        if(mp_arabicFontFamily.equals("Noore Hidayat")){
            fontFamily = "fontNooreHidayat";
        }
        if(mp_arabicFontFamily.equals("Saleem Quran")){
            fontFamily = "fontSaleem";
        }
        if(mp_arabicFontFamily.equals("KFGQPC Uthman Taha Naskh")){
            fontFamily = "fontTahaNaskh";
        }
        if(mp_arabicFontFamily.equals("Arabic Regular")){
            fontFamily = "fontKitab";
        }
        if(!mp_arFz.equals("")){
            fontSize = mp_arFz+"px";
        }
        String appTheme = mPrefs.getString("APP_NIGHT_MODE", "-1");
        String bodyBgColor = "#424242";
        String bodyTxtColor = "#ffffff";
        if (appTheme.equals("1")) {
            bodyBgColor = "#424242";
            bodyTxtColor = "#ffffff";
        }else if (appTheme.equals("0")) {
            bodyBgColor = "#FFFFFF";
            bodyTxtColor = "#000000";
        }else {
            bodyBgColor = "#424242";
            bodyTxtColor = "#ffffff";
        }
        String style = Utils.tajweedCss(fontFamily,fontSize,bodyBgColor,bodyTxtColor);
        String html = "<html><head>"+style+"</head><body>"+ayah.getText_uthmani_tajweed()+"</body></html>";
        rvHolder.text_tajweed.loadDataWithBaseURL(null,html, "text/html; charset=utf-8", "UTF-8",null);
        //
        rvHolder.playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Double secD = Double.parseDouble(ayah.getAudio_duration());
                long sec = secD.longValue();
                long millisecond = TimeUnit.SECONDS.toMillis(sec);
                Utils.preventTwoClick(view, millisecond);
                if (isInternetPresent) {
                    if (checkPermission()) {
                        new BackgroundTask(activity) {

                            @Override
                            public void onPreExecute() {
                                pd = new ProgressDialog(c);
                                pd.setTitle("Processing...");
                                pd.setMessage("Please wait.");
                                pd.setCancelable(true);
                                pd.setIndeterminate(true);
                                pd.show();
                            }

                            @Override
                            public void doInBackground() {
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
                            }

                            @Override
                            public void onPostExecute() {
                                if (pd != null && pd.isShowing()) {
                                    pd.dismiss();
                                }
                            }
                        }.execute();
                        /*new AsyncTask<Void, Void, Void>() {
                            protected void onPreExecute() {
                                pd = new ProgressDialog(c);
                                pd.setTitle("Processing...");
                                pd.setMessage("Please wait.");
                                pd.setCancelable(false);
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
                                if (pd!=null && pd.isShowing()) {
                                    pd.dismiss();
                                }
                            }
                        }.execute();*/
                    } else {
                        requestPermission(); // Code for permission
                    }
                }
            }
        });

        rvHolder.bookmarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SQLiteDatabase db = DatabaseHelper.getInstance(c).getWritableDatabase();
                    Button bookmark = view.findViewById(R.id.bookmarkBtn);
                    String sql = "SELECT * FROM bookmark WHERE ayah_id = " + ayah.getAyah_index();
                    //Log.i("BOOKMARK_SQL", sql);
                    Cursor cursor = db.rawQuery(sql, null);
                    try {
                        if (cursor.moveToFirst()) {
                            db.execSQL("DELETE FROM bookmark WHERE ayah_id = " + ayah.getAyah_index());
                            Toast.makeText(c, "Deleted from bookmark.", Toast.LENGTH_LONG).show();
                            bookmark.setCompoundDrawablesWithIntrinsicBounds(0, android.R.drawable.btn_star, 0, 0);
                        } else {
                            ContentValues values = new ContentValues();
                            values.put("ayah_id", ayah.getAyah_index());
                            DatabaseHelper.getInstance(c).getWritableDatabase().insertOrThrow("bookmark", "", values);
                            Toast.makeText(c, "Added to bookmark.", Toast.LENGTH_LONG).show();
                            bookmark.setCompoundDrawablesWithIntrinsicBounds(0, android.R.drawable.btn_star_big_on, 0, 0);
                        }
                    } catch (Exception e) {
                        Log.i("Bookmark Button", e.getMessage());
                    } finally {
                        if (cursor != null && !cursor.isClosed()) {
                            cursor.close();
                        }
                        db.close();
                    }

                } catch (Exception e) {
                    Log.e("Favorite", e.getMessage());
                }
            }
        });

        rvHolder.wordMeaningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent in = new Intent(c, WordMeaningActivity.class);
                    in.putExtra("ayah_index", ayah.getAyah_index());
                    String mushaf = mPrefs.getString("mushaf", "IndoPak");
                    if(mushaf.equals("ImlaeiSimple")) {
                        in.putExtra("text_tashkeel", ayah.getText_tashkeel());
                    }
                    else if(mushaf.equals("Uthmanic")) {
                        in.putExtra("text_tashkeel", ayah.getText_uthmani());
                    }
                    else{
                        in.putExtra("text_tashkeel", ayah.getIndo_pak());
                    }
                    in.putExtra("content_en", ayah.getContent_en());
                    in.putExtra("content_bn", ayah.getContent_bn());
                    c.startActivity(in);
                } catch (Exception e) {
                    Log.e("Favorite", e.getMessage());
                }
            }
        });


        rvHolder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent in = new Intent(c, ShareVerseActivity.class);
                    in.putExtra("ayah_index", ayah.getAyah_index());
                    String mushaf = mPrefs.getString("mushaf", "IndoPak");
                    if(mushaf.equals("ImlaeiSimple")) {
                        in.putExtra("text_tashkeel", ayah.getText_tashkeel());
                    }
                    else if(mushaf.equals("Uthmanic")) {
                        in.putExtra("text_tashkeel", ayah.getText_uthmani());
                    }
                    else{
                        in.putExtra("text_tashkeel", ayah.getIndo_pak());
                    }
                    in.putExtra("content_en", ayah.getContent_en());
                    in.putExtra("content_bn", ayah.getContent_bn());
                    in.putExtra("ayah_num", ayah.getAyah_num());
                    in.putExtra("surah_id", ayah.getSurah_id());
                    in.putExtra("ayah_key", ayah.getAyah_key());
                    in.putExtra("trans", ayah.getTrans());
                    c.startActivity(in);
                } catch (Exception e) {
                    Log.e("Share", e.getMessage());
                }
            }
        });

        SQLiteDatabase db = DatabaseHelper.getInstance(c).getWritableDatabase();
        String checksql = "SELECT * FROM bookmark WHERE ayah_id = " + ayah.getAyah_index();
        //Log.i("CHECK_BOOKMARK_SQL", checksql);
        Cursor cursor = db.rawQuery(checksql, null);

        try {
            if (cursor.moveToFirst()) {
                rvHolder.bookmarkBtn.setCompoundDrawablesWithIntrinsicBounds(0, android.R.drawable.btn_star_big_on, 0, 0);
            } else {
                rvHolder.bookmarkBtn.setCompoundDrawablesWithIntrinsicBounds(0, android.R.drawable.btn_star, 0, 0);
            }
        } catch (Exception e) {
            Log.i("Bookmark Check", e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.close();
        }

        rvHolder.surah_name.setText("Sura " + ayah.getName_simple() + ", Ayah " + ayah.getAyah_num());

        rvHolder.copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String ayahAraTxt = "";
                    String mushaf = mPrefs.getString("mushaf", "IndoPak");
                    if(mushaf.equals("ImlaeiSimple")) {
                        ayahAraTxt = ayah.getText_tashkeel();
                    }
                    else if(mushaf.equals("Uthmanic")) {
                        ayahAraTxt = ayah.getText_uthmani();
                    }
                    else{
                        ayahAraTxt = ayah.getIndo_pak();
                    }
                    String fullAyat = ayahAraTxt + "\n\n" + ayah.getTrans() + "\n\n" + ayah.getContent_en() + "\n\n" + ayah.getContent_bn() + "\n\nSura " + ayah.getName_simple() + ", Ayah " + ayah.getAyah_num();
                    String label = ayah.getName_simple() + ", Ayah " + ayah.getAyah_num();
                    Log.d(label, fullAyat);
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) c.getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText(label, fullAyat);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(c, "Ayah Copied.", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Log.e("Copied", e.getMessage());
                }
            }
        });

        rvHolder.tafsirs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent in = new Intent(c, TafsirActivity.class);
                    in.putExtra("ayah_index", ayah.getAyah_index());
                    String mushaf = mPrefs.getString("mushaf", "IndoPak");
                    if(mushaf.equals("ImlaeiSimple")) {
                        in.putExtra("text_tashkeel", ayah.getText_tashkeel());
                    }
                    else if(mushaf.equals("Uthmanic")) {
                        in.putExtra("text_tashkeel", ayah.getText_uthmani());
                    }
                    else{
                        in.putExtra("text_tashkeel", ayah.getIndo_pak());
                    }
                    in.putExtra("content_en", ayah.getContent_en());
                    in.putExtra("content_bn", ayah.getContent_bn());
                    in.putExtra("ayah_num", ayah.getAyah_num());
                    in.putExtra("surah_id", ayah.getSurah_id());
                    in.putExtra("ayah_key", ayah.getAyah_key());
                    in.putExtra("trans", ayah.getTrans());
                    c.startActivity(in);
                } catch (Exception e) {
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return true;
        }
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
            ActivityCompat.requestPermissions(activity, permissions(), PERMISSION_REQUEST_CODE);
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

    class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView ayah_index;
        TextView text_tashkeel;
        TextView content_en;
        TextView content_bn;
        TextView sajdah;
        Button playBtn;
        Button wordMeaningButton;
        Button shareButton;
        TextView ayah_num, surah_name;
        Button bookmarkBtn;
        Button copyButton;
        TextView tafsirs;
        TextView trans;
        WebView text_tajweed;

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
            shareButton = (Button) itemView.findViewById(R.id.shareButton);
            ayah_num = (TextView) itemView.findViewById(R.id.ayah_num);
            surah_name = (TextView) itemView.findViewById(R.id.surah_name);
            bookmarkBtn = (Button) itemView.findViewById(R.id.bookmarkBtn);
            copyButton = (Button) itemView.findViewById(R.id.copyButton);
            tafsirs = (TextView) itemView.findViewById(R.id.tafsirs);
            trans = (TextView) itemView.findViewById(R.id.trans);
            text_tajweed = (WebView) itemView.findViewById(R.id.text_tajweed);

            String mp_arabicFontFamily = mPrefs.getString("arabicFontFamily", "Noore Huda");
            String mp_arFz = mPrefs.getString("arFontSize", "30");
            String mp_enFz = mPrefs.getString("enFontSize", "15");
            String mp_bnFz = mPrefs.getString("bnFontSize", "15");
            if (mp_arabicFontFamily.equals("Al Majeed Quranic Font")) {
                text_tashkeel.setTypeface(fontAlmajeed);
            }
            if (mp_arabicFontFamily.equals("Al Qalam Quran")) {
                text_tashkeel.setTypeface(fontAlQalam);
            }
            if (mp_arabicFontFamily.equals("Noore Huda")) {
                text_tashkeel.setTypeface(fontUthmani);
            }
            if (mp_arabicFontFamily.equals("Noore Hidayat")) {
                text_tashkeel.setTypeface(fontNooreHidayat);
            }
            if (mp_arabicFontFamily.equals("Saleem Quran")) {
                text_tashkeel.setTypeface(fontSaleem);
            }
            if(mp_arabicFontFamily.equals("KFGQPC Uthman Taha Naskh")){
                text_tashkeel.setTypeface(fontTahaNaskh);
            }
            if(mp_arabicFontFamily.equals("Arabic Regular")){
                text_tashkeel.setTypeface(fontKitab);
            }
            if (!mp_arFz.equals("")) {
                text_tashkeel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_arFz));
            }
            if (!mp_enFz.equals("")) {
                content_en.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_enFz));
            }
            if (!mp_bnFz.equals("")) {
                content_bn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_bnFz));
                trans.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_bnFz));
            }

            //
            String show_bn_pron = mPrefs.getString("show_bn_pron", "2");
            if (show_bn_pron.equals("-1")) {
                trans.setVisibility(View.GONE);
            }
            String show_en_trans = mPrefs.getString("show_en_trans", "2");
            if (show_en_trans.equals("-1")) {
                content_en.setVisibility(View.GONE);
            }
            String show_bn_trans = mPrefs.getString("show_bn_trans", "2");
            if (show_bn_trans.equals("-1")) {
                content_bn.setVisibility(View.GONE);
            }
        }
    }

}
