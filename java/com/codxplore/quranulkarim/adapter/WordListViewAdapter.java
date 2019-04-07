package com.codxplore.quranulkarim.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.codxplore.quranulkarim.ConnectionDetector;
import com.codxplore.quranulkarim.R;
import com.codxplore.quranulkarim.helper.AudioPlay;
import com.codxplore.quranulkarim.helper.DatabaseHelper;
import com.codxplore.quranulkarim.model.Word;

import java.util.ArrayList;

public class WordListViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    Context c;
    ArrayList<Word> words;
    //DatabaseHelper dbhelper;
    //SQLiteDatabase db;
    ProgressDialog pd;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;

    public WordListViewAdapter(Context c, ArrayList<Word> words) {
        this.c = c;
        this.words = words;
        //dbhelper = new DatabaseHelper(c);
        //db = dbhelper.getWritableDatabase();
        cd = new ConnectionDetector(c);
        isInternetPresent = cd.isConnectingToInternet();
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
                            AudioPlay.stopAudio();
                            AudioPlay.playAudio(c, mp3Url);
                            return null;
                        }

                        protected void onPostExecute(Void result) {
                            if (pd != null) {
                                pd.dismiss();
                            }
                        }
                    }.execute();
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
        }
    }
}
