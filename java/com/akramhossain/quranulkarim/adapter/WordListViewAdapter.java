package com.akramhossain.quranulkarim.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.AsyncTask;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
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
    Typeface fontUthmani, fontAlmajeed, fontAlQalam, fontNooreHidayat, fontSaleem;

    public WordListViewAdapter(Context c, ArrayList<Word> words, Activity activity) {
        this.c = c;
        this.words = words;
        //dbhelper = new DatabaseHelper(c);
        //db = dbhelper.getWritableDatabase();
        cd = new ConnectionDetector(c);
        isInternetPresent = cd.isConnectingToInternet();
        this.activity = activity;

        fontUthmani = Typeface.createFromAsset(c.getAssets(),"fonts/KFGQPC_Uthmanic_Script_HAFS_Regular.ttf");
        fontAlmajeed = Typeface.createFromAsset(c.getAssets(),"fonts/AlMajeedQuranicFont_shiped.ttf");
        fontAlQalam = Typeface.createFromAsset(c.getAssets(),"fonts/AlQalamQuran.ttf");
        fontNooreHidayat = Typeface.createFromAsset(c.getAssets(),"fonts/noorehidayat.ttf");
        fontSaleem = Typeface.createFromAsset(c.getAssets(),"fonts/PDMS_Saleem_QuranFont.ttf");
        mPrefs = PreferenceManager.getDefaultSharedPreferences(activity);
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

        rvHolder.playWordBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
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

        TextView word_id;
        TextView word_arabic;
        TextView word_transliteration;
        TextView word_translation;
        Button playWordBtn;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            word_id = (TextView) itemView.findViewById(R.id.word_id);
            word_arabic = (TextView) itemView.findViewById(R.id.word_arabic);
            word_transliteration = (TextView) itemView.findViewById(R.id.word_transliteration);
            word_translation = (TextView) itemView.findViewById(R.id.word_translation);
            playWordBtn = (Button) itemView.findViewById(R.id.playWordBtn);

            String mp_arabicFontFamily = mPrefs.getString("arabicFontFamily", "Arabic Regular");
            if(mp_arabicFontFamily.equals("Al Majeed Quranic Font")){
                word_arabic.setTypeface(fontAlmajeed);
            }
            if(mp_arabicFontFamily.equals("Al Qalam Quran")){
                word_arabic.setTypeface(fontAlQalam);
            }
            if(mp_arabicFontFamily.equals("Uthmanic Script")){
                word_arabic.setTypeface(fontUthmani);
            }
            if(mp_arabicFontFamily.equals("Noore Hidayat")){
                word_arabic.setTypeface(fontNooreHidayat);
            }
            if(mp_arabicFontFamily.equals("Saleem Quran")){
                word_arabic.setTypeface(fontSaleem);
            }
        }
    }
}
