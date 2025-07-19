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
import com.akramhossain.quranulkarim.model.TafsirPdfList;
import com.akramhossain.quranulkarim.util.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class PdfBookViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context c;
    ArrayList<TafsirPdfList> pdfBookList;
    private Activity activity;
    Typeface font;
    SharedPreferences mPrefs;
    String screen;

    public PdfBookViewAdapter(Context c, ArrayList<TafsirPdfList> pdfBookList, Activity activity, String screen) {
        this.c = c;
        this.pdfBookList = pdfBookList;
        this.activity = activity;
        this.screen = screen;
        mPrefs = c.getSharedPreferences(Utils.PREF_NAME, 0);
        font = Typeface.createFromAsset(c.getAssets(), "fonts/Siyamrupali.ttf");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.pdf_book_list, parent, false);
        RecyclerViewHolder rvHolder = new RecyclerViewHolder(v);
        rvHolder.name_bangla.setTypeface(font);
        return rvHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RecyclerViewHolder rvHolder= (RecyclerViewHolder) holder;
        TafsirPdfList tb = pdfBookList.get(position);
        rvHolder.name_bangla.setText(tb.getName_bangla());
        rvHolder.name_english.setText(tb.getName_english());
        rvHolder.file_name.setText(tb.getFile_name());
        rvHolder.url.setText(tb.getUrl());
        Picasso.get().load(tb.getThumb()).into(rvHolder.thumb);
    }

    @Override
    public int getItemCount() {
        return pdfBookList.size();
    }


    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView name_bangla;
        TextView name_english;
        TextView file_name;
        TextView url;
        ImageView thumb;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            name_bangla = (TextView) itemView.findViewById(R.id.name_bangla);
            name_english = (TextView) itemView.findViewById(R.id.name_english);
            file_name = (TextView) itemView.findViewById(R.id.file_name);
            url = (TextView) itemView.findViewById(R.id.url);
            thumb = (ImageView) itemView.findViewById(R.id.thumb);
        }
    }
}
