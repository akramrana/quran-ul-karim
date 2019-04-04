package com.codxplore.quranulkarim.adapter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codxplore.quranulkarim.R;
import com.codxplore.quranulkarim.helper.DatabaseHelper;
import com.codxplore.quranulkarim.model.Word;

import java.util.ArrayList;

public class WordListViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    Context c;
    ArrayList<Word> words;
    DatabaseHelper dbhelper;
    SQLiteDatabase db;

    public WordListViewAdapter(Context c, ArrayList<Word> words) {
        this.c = c;
        this.words = words;
        dbhelper = new DatabaseHelper(c);
        db = dbhelper.getWritableDatabase();
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

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            word_id = (TextView) itemView.findViewById(R.id.word_id);
            word_arabic = (TextView) itemView.findViewById(R.id.word_arabic);
            word_transliteration = (TextView) itemView.findViewById(R.id.word_transliteration);
            word_translation = (TextView) itemView.findViewById(R.id.word_translation);
        }
    }
}
