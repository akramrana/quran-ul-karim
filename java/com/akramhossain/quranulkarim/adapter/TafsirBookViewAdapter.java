package com.akramhossain.quranulkarim.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.akramhossain.quranulkarim.R;
import com.akramhossain.quranulkarim.model.TafsirBook;
import com.akramhossain.quranulkarim.util.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import androidx.recyclerview.widget.RecyclerView;

public class TafsirBookViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context c;
    ArrayList<TafsirBook> tafsirBook;
    private Activity activity;
    Typeface font;
    SharedPreferences mPrefs;
    String screen;

    public TafsirBookViewAdapter(Context c, ArrayList<TafsirBook> hadithBook, Activity activity, String screen) {
        this.c = c;
        this.tafsirBook = hadithBook;
        this.activity = activity;
        this.screen = screen;
        mPrefs = c.getSharedPreferences(Utils.PREF_NAME, 0);
        font = Typeface.createFromAsset(c.getAssets(), "fonts/Siyamrupali.ttf");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.tafsir_book_list, parent, false);
        RecyclerViewHolder rvHolder = new RecyclerViewHolder(v);
        rvHolder.name_bangla.setTypeface(font);
        return rvHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RecyclerViewHolder rvHolder= (RecyclerViewHolder) holder;
        TafsirBook tb = tafsirBook.get(position);
        rvHolder.tafsir_book_id.setText(tb.getTafsir_book_id());
        rvHolder.name_bangla.setText(tb.getName_bangla());
        rvHolder.name_english.setText(tb.getName_english());
        rvHolder.name_arabic.setText(tb.getName_arabic());
        Picasso.get().load(tb.getThumb()).into(rvHolder.thumb);
    }

    @Override
    public int getItemCount() {
        return tafsirBook.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView tafsir_book_id;
        TextView name_bangla;
        TextView name_english;
        TextView name_arabic;
        ImageView thumb;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            tafsir_book_id = (TextView) itemView.findViewById(R.id.tafsir_book_id);
            name_bangla = (TextView) itemView.findViewById(R.id.name_bangla);
            name_english = (TextView) itemView.findViewById(R.id.name_english);
            name_arabic = (TextView) itemView.findViewById(R.id.name_arabic);
            thumb = (ImageView) itemView.findViewById(R.id.thumb);
        }
    }

}
