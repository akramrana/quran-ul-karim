package com.akramhossain.quranulkarim.adapter;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.akramhossain.quranulkarim.R;
import com.akramhossain.quranulkarim.ShareVerseActivity;
import com.akramhossain.quranulkarim.WordMeaningActivity;
import com.akramhossain.quranulkarim.helper.AudioPlay;
import com.akramhossain.quranulkarim.helper.DatabaseHelper;
import com.akramhossain.quranulkarim.model.Ayah;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;


public class SearchTermViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context c;
    ArrayList<Ayah> ayahs;
    Typeface font;
    MediaPlayer mp;
    ProgressDialog pd;
    DatabaseHelper dbhelper;
    //SQLiteDatabase db;

    public SearchTermViewAdapter(Context c, ArrayList<Ayah> ayahs) {
        this.c = c;
        this.ayahs = ayahs;
        font = Typeface.createFromAsset(c.getAssets(),"fonts/Siyamrupali.ttf");
        dbhelper = new DatabaseHelper(c);
        //db = dbhelper.getWritableDatabase();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(c).inflate(R.layout.search_term_list,parent,false);
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
                        AudioPlay.stopAudio();
                        AudioPlay.playAudio(c, ayah.getAudio_url());
                        return null;
                    }

                    protected void onPostExecute(Void result) {
                        if (pd!=null) {
                            pd.dismiss();
                        }
                    }
                }.execute();
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
                            bookmark.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.btn_star, 0, 0, 0);
                        } else {
                            ContentValues values = new ContentValues();
                            values.put("ayah_id", ayah.getAyah_index());
                            dbhelper.getWritableDatabase().insertOrThrow("bookmark", "", values);
                            Toast.makeText(c, "Added to bookmark.", Toast.LENGTH_LONG).show();
                            bookmark.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.btn_star_big_on, 0, 0, 0);
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

        SQLiteDatabase db = dbhelper.getWritableDatabase();
        String checksql = "SELECT * FROM bookmark WHERE ayah_id = "+ayah.getAyah_index();
        //Log.i("CHECK_BOOKMARK_SQL", checksql);
        Cursor cursor = db.rawQuery(checksql,null);

        try {
            if (cursor.moveToFirst()) {
                rvHolder.bookmarkBtn.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.btn_star_big_on, 0, 0, 0);
            } else {
                rvHolder.bookmarkBtn.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.btn_star, 0, 0, 0);
            }
        }catch (Exception e){
            Log.i("Bookmark Check", e.getMessage());
        }finally {
            if (cursor != null && !cursor.isClosed()){
                cursor.close();
            }
            db.close();
        }

        rvHolder.surah_name.setText("Sura "+ayah.getName_simple()+", Ayah "+ayah.getAyah_num());
    }

    @Override
    public int getItemCount() {
        return ayahs.size();
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
        TextView ayah_num,surah_name;
        Button bookmarkBtn;

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
        }
    }

}
