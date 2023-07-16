package com.akramhossain.quranulkarim.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
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
import com.akramhossain.quranulkarim.task.BackgroundTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class BookmarkViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context c;
    ArrayList<Ayah> ayahs;
    Typeface font, fontUthmani, fontAlmajeed, fontAlQalam, fontNooreHidayat, fontSaleem;
    MediaPlayer mp;
    ProgressDialog pd;
    DatabaseHelper dbhelper;
    private Activity activity;
    private static final int PERMISSION_REQUEST_CODE = 100;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    SharedPreferences mPrefs;
    //SQLiteDatabase db;

    public BookmarkViewAdapter(Context c, ArrayList<Ayah> ayahs, Activity activity) {
        this.c = c;
        this.ayahs = ayahs;
        font = Typeface.createFromAsset(c.getAssets(), "fonts/Siyamrupali.ttf");
        fontUthmani = Typeface.createFromAsset(c.getAssets(), "fonts/KFGQPC_Uthmanic_Script_HAFS_Regular.ttf");
        fontAlmajeed = Typeface.createFromAsset(c.getAssets(), "fonts/AlMajeedQuranicFont_shiped.ttf");
        fontAlQalam = Typeface.createFromAsset(c.getAssets(), "fonts/AlQalamQuran.ttf");
        fontNooreHidayat = Typeface.createFromAsset(c.getAssets(), "fonts/noorehidayat.ttf");
        fontSaleem = Typeface.createFromAsset(c.getAssets(), "fonts/PDMS_Saleem_QuranFont.ttf");
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
        View v = LayoutInflater.from(c).inflate(R.layout.sura_bookmark_list, parent, false);
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
        if(mushaf.equals("Uthmanic")) {
            rvHolder.text_tashkeel.setText(ayah.getText_tashkeel());
        }else {
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

        rvHolder.playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

        rvHolder.wordMeaningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent in = new Intent(c, WordMeaningActivity.class);
                    in.putExtra("ayah_index", ayah.getAyah_index());
                    in.putExtra("text_tashkeel", ayah.getText_tashkeel());
                    in.putExtra("content_en", ayah.getContent_en());
                    in.putExtra("content_bn", ayah.getContent_bn());
                    c.startActivity(in);
                } catch (Exception e) {
                    Log.e("Favorite", e.getMessage());
                }
            }
        });

        rvHolder.removeBookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SQLiteDatabase db = DatabaseHelper.getInstance(c).getWritableDatabase();
                    String sql = "SELECT * FROM bookmark WHERE ayah_id = " + ayah.getAyah_index();
                    Cursor cursor = db.rawQuery(sql, null);
                    try {
                        if (cursor.moveToFirst()) {
                            db.execSQL("DELETE FROM bookmark WHERE ayah_id = " + ayah.getAyah_index());
                            Toast.makeText(c, "Bookmark Deleted.", Toast.LENGTH_LONG).show();
                            ayahs.remove(rvHolder.getAdapterPosition());
                            notifyItemRemoved(rvHolder.getAdapterPosition());
                        }
                    } catch (Exception e) {
                        Log.i("bookmark Check", e.getMessage());
                    } finally {
                        if (cursor != null && !cursor.isClosed()) {
                            cursor.close();
                        }
                        db.close();
                    }
                } catch (Exception e) {
                    Log.e("Delete Favorite", e.getMessage());
                }
            }
        });

        rvHolder.shareButton.setOnClickListener(new View.OnClickListener() {
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
                    in.putExtra("trans", ayah.getTrans());
                    c.startActivity(in);
                } catch (Exception e) {
                    Log.e("Share", e.getMessage());
                }
            }
        });

        rvHolder.surah_name.setText("Sura " + ayah.getName_simple() + ", Ayah " + ayah.getAyah_num());

        rvHolder.copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String fullAyat = ayah.getText_tashkeel() + "\n\n" + ayah.getTrans() + "\n\n" + ayah.getContent_en() + "\n\n" + ayah.getContent_bn() + "\n\nSura " + ayah.getName_simple() + ", Ayah " + ayah.getAyah_num();
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
                    in.putExtra("text_tashkeel", ayah.getText_tashkeel());
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
        TextView tafsirs;
        Button wordMeaningButton;
        Button removeBookmarkButton;
        Button shareButton;
        TextView ayah_num, surah_name;
        Button copyButton;
        TextView trans;

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
            surah_name = (TextView) itemView.findViewById(R.id.surah_name);
            copyButton = (Button) itemView.findViewById(R.id.copyButton);
            tafsirs = (TextView) itemView.findViewById(R.id.tafsirs);
            trans = (TextView) itemView.findViewById(R.id.trans);
            //
            String mp_arabicFontFamily = mPrefs.getString("arabicFontFamily", "Arabic Regular");
            String mp_arFz = mPrefs.getString("arFontSize", "30");
            String mp_enFz = mPrefs.getString("enFontSize", "15");
            String mp_bnFz = mPrefs.getString("bnFontSize", "15");
            if (mp_arabicFontFamily.equals("Al Majeed Quranic Font")) {
                text_tashkeel.setTypeface(fontAlmajeed);
            }
            if (mp_arabicFontFamily.equals("Al Qalam Quran")) {
                text_tashkeel.setTypeface(fontAlQalam);
            }
            if (mp_arabicFontFamily.equals("Uthmanic Script")) {
                text_tashkeel.setTypeface(fontUthmani);
            }
            if (mp_arabicFontFamily.equals("Noore Hidayat")) {
                text_tashkeel.setTypeface(fontNooreHidayat);
            }
            if (mp_arabicFontFamily.equals("Saleem Quran")) {
                text_tashkeel.setTypeface(fontSaleem);
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
