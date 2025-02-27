package com.akramhossain.quranulkarim.adapter;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.AsyncTask;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
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
import com.akramhossain.quranulkarim.helper.AudioPlay;
import com.akramhossain.quranulkarim.model.Word;
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

public class WordListViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    Context c;
    ArrayList<Word> words;
    //DatabaseHelper dbhelper;
    //SQLiteDatabase db;
    ProgressDialog pd;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    private Activity activity;
    private static final int PERMISSION_REQUEST_CODE = 100;
    SharedPreferences mPrefs;
    Typeface font, fontUthmani, fontAlmajeed, fontAlQalam, fontNooreHidayat, fontSaleem, fontTahaNaskh, fontKitab;

    public WordListViewAdapter(Context c, ArrayList<Word> words, Activity activity) {
        this.c = c;
        this.words = words;
        //dbhelper = new DatabaseHelper(c);
        //db = DatabaseHelper.getInstance(c).getWritableDatabase();
        cd = new ConnectionDetector(c);
        isInternetPresent = cd.isConnectingToInternet();
        this.activity = activity;

        font = Typeface.createFromAsset(c.getAssets(),"fonts/Siyamrupali.ttf");
        fontUthmani = Typeface.createFromAsset(c.getAssets(),"fonts/KFGQPC_Uthmanic_Script_HAFS_Regular.ttf");
        fontAlmajeed = Typeface.createFromAsset(c.getAssets(),"fonts/AlMajeedQuranicFont_shiped.ttf");
        fontAlQalam = Typeface.createFromAsset(c.getAssets(),"fonts/AlQalamQuran.ttf");
        fontNooreHidayat = Typeface.createFromAsset(c.getAssets(),"fonts/noorehidayat.ttf");
        fontSaleem = Typeface.createFromAsset(c.getAssets(),"fonts/PDMS_Saleem_QuranFont.ttf");
        fontTahaNaskh = Typeface.createFromAsset(c.getAssets(),"fonts/KFGQPC_Uthman_Taha_Naskh_Regular.ttf");
        fontKitab = Typeface.createFromAsset(c.getAssets(),"fonts/kitab.ttf");
        mPrefs = c.getSharedPreferences(Utils.PREF_NAME, 0);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(c).inflate(R.layout.word_list,parent,false);
        RecyclerViewHolder rvHolder = new RecyclerViewHolder(v);
        return rvHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RecyclerViewHolder rvHolder= (RecyclerViewHolder) holder;
        final Word word = words.get(position);
        rvHolder.word_id.setText(word.getWord_id());
        rvHolder.word_arabic.setText(word.getArabic());
        rvHolder.word_transliteration.setText(word.getTransliteration());
        rvHolder.word_translation.setText(word.getTranslation());
        rvHolder.word_bangla.setText(word.getBangla());

        rvHolder.playWordBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Utils.preventTwoClick(view, 2000);
                if (isInternetPresent) {
                    if (checkPermission()) {
                        new BackgroundTask(activity) {

                            @Override
                            public void onPreExecute(){
                                pd = new ProgressDialog(c);
                                pd.setTitle("Processing...");
                                pd.setMessage("Please wait.");
                                pd.setCancelable(true);
                                pd.setIndeterminate(true);
                                pd.show();
                            }

                            @Override
                            public void doInBackground() {
                                String[] data = word.getAyah_key().split(":", 2);
                                String sura = data[0];
                                String ayat = data[1];
                                String position = word.getWords_id();
                                int slen = sura.length();
                                int alen = ayat.length();
                                int plen = position.length();
                                //
                                if (slen == 2) {
                                    sura = "0" + sura;
                                } else if (slen == 1) {
                                    sura = "00" + sura;
                                }

                                if (alen == 2) {
                                    ayat = "0" + ayat;
                                } else if (alen == 1) {
                                    ayat = "00" + ayat;
                                }

                                if (plen == 2) {
                                    position = "0" + position;
                                } else if (plen == 1) {
                                    position = "00" + position;
                                }
                                String mp3name = sura + "_" + ayat + "_" + position + ".mp3";
                                Log.d("MP3 Name", mp3name);
                                String mp3Url = "https://verses.quran.com/wbw/" + mp3name;
                                //
                                String mPath = c.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/";
                                String fullPath = mPath + mp3name;
                                Log.d("MP3 Url:", mp3Url);
                                Log.d("File Path:", mPath);
                                Log.d("Full File Path:", fullPath);
                                File file = new File(fullPath);
                                if (file.exists()) {
                                    Log.d("File Path:", "Exist!");
                                    AudioPlay.stopAudio();
                                    AudioPlay.playAudio(c, fullPath);
                                } else {
                                    Log.d("File Path:", "Not Exist Downloading!");
                                    downloadFile(mp3Url, mp3name, mPath);
                                    AudioPlay.stopAudio();
                                    AudioPlay.playAudio(c, fullPath);
                                }
                                //AudioPlay.stopAudio();
                                //AudioPlay.playAudio(c, mp3Url);
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
                                pd.setCancelable(true);
                                pd.setIndeterminate(true);
                                pd.show();
                            }

                            protected Void doInBackground(Void... params) {
                                String[] data = word.getAyah_key().split(":", 2);
                                String sura = data[0];
                                String ayat = data[1];
                                String position = word.getPosition();
                                int slen = sura.length();
                                int alen = ayat.length();
                                int plen = position.length();
                                //
                                if (slen == 2) {
                                    sura = "0" + sura;
                                } else if (slen == 1) {
                                    sura = "00" + sura;
                                }

                                if (alen == 2) {
                                    ayat = "0" + ayat;
                                } else if (alen == 1) {
                                    ayat = "00" + ayat;
                                }

                                if (plen == 2) {
                                    position = "0" + position;
                                } else if (plen == 1) {
                                    position = "00" + position;
                                }
                                String mp3name = sura + "_" + ayat + "_" + position + ".mp3";
                                Log.d("MP3 Name", mp3name);
                                String mp3Url = "https://verses.quran.com/wbw/" + mp3name;
                                //
                                String mPath = c.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/";
                                String fullPath = mPath + mp3name;
                                Log.d("File Path:", mPath);
                                Log.d("Full File Path:", fullPath);
                                File file = new File(fullPath);
                                if (file.exists()) {
                                    Log.d("File Path:", "Exist!");
                                    AudioPlay.stopAudio();
                                    AudioPlay.playAudio(c, fullPath);
                                } else {
                                    Log.d("File Path:", "Not Exist Downloading!");
                                    downloadFile(mp3Url, mp3name, mPath);
                                    AudioPlay.stopAudio();
                                    AudioPlay.playAudio(c, fullPath);
                                }
                                //AudioPlay.stopAudio();
                                //AudioPlay.playAudio(c, mp3Url);
                                return null;
                            }

                            protected void onPostExecute(Void result) {
                                if (pd != null && pd.isShowing()) {
                                    pd.dismiss();
                                }
                            }
                        }.execute();*/
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
    }

    @Override
    public int getItemCount() {
        return words.size();
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

        TextView word_id;
        TextView word_arabic;
        TextView word_transliteration;
        TextView word_translation;
        TextView word_bangla;
        Button playWordBtn;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            word_id = (TextView) itemView.findViewById(R.id.word_id);
            word_arabic = (TextView) itemView.findViewById(R.id.word_arabic);
            word_transliteration = (TextView) itemView.findViewById(R.id.word_transliteration);
            word_translation = (TextView) itemView.findViewById(R.id.word_translation);
            playWordBtn = (Button) itemView.findViewById(R.id.playWordBtn);
            word_bangla = (TextView) itemView.findViewById(R.id.word_bangla);

            String mp_arabicFontFamily = mPrefs.getString("arabicFontFamily", "Noore Huda");
            String mp_arFz = mPrefs.getString("arFontSize", "30");
            String mp_enFz = mPrefs.getString("enFontSize", "15");
            String mp_bnFz = mPrefs.getString("bnFontSize", "15");

            if(mp_arabicFontFamily.equals("Al Majeed Quranic Font")){
                word_arabic.setTypeface(fontAlmajeed);
            }
            if(mp_arabicFontFamily.equals("Al Qalam Quran")){
                word_arabic.setTypeface(fontAlQalam);
            }
            if(mp_arabicFontFamily.equals("Noore Huda")){
                word_arabic.setTypeface(fontUthmani);
            }
            if(mp_arabicFontFamily.equals("Noore Hidayat")){
                word_arabic.setTypeface(fontNooreHidayat);
            }
            if(mp_arabicFontFamily.equals("Saleem Quran")){
                word_arabic.setTypeface(fontSaleem);
            }
            if(mp_arabicFontFamily.equals("KFGQPC Uthman Taha Naskh")){
                word_arabic.setTypeface(fontTahaNaskh);
            }
            if(mp_arabicFontFamily.equals("Arabic Regular")){
                word_arabic.setTypeface(fontKitab);
            }
            if(!mp_arFz.equals("") && mp_arFz !=null ){
                try {
                    word_arabic.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_arFz));
                }catch (NumberFormatException e) {
                    Log.e("WRONG_FONT_SIZE", "Error parsing number: ", e);
                }
            }
            if(!mp_enFz.equals("") && mp_enFz != null){
                try {
                    word_transliteration.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_enFz));
                    word_translation.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_enFz));
                }catch (NumberFormatException e) {
                    Log.e("WRONG_FONT_SIZE", "Error parsing number: ", e);
                }
            }
            if(!mp_bnFz.equals("") && mp_bnFz != null){
                try {
                    word_bangla.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_bnFz));
                }catch (NumberFormatException e) {
                    Log.e("WRONG_FONT_SIZE", "Error parsing number: ", e);
                }
            }
            word_bangla.setTypeface(font);
        }
    }
}
