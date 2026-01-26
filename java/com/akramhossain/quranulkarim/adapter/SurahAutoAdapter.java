package com.akramhossain.quranulkarim.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.akramhossain.quranulkarim.model.SurahItem;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SurahAutoAdapter extends ArrayAdapter<SurahItem> {

    private final List<SurahItem> all;
    private final List<SurahItem> filtered = new ArrayList<>();

    public SurahAutoAdapter(@NonNull Context ctx, @NonNull List<SurahItem> items) {
        super(ctx, android.R.layout.simple_list_item_1, new ArrayList<>(items));
        this.all = new ArrayList<>(items);
        this.filtered.addAll(items);
    }

    @Override public int getCount() { return filtered.size(); }
    @Override public SurahItem getItem(int position) { return filtered.get(position); }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView tv = (TextView) super.getView(position, convertView, parent);
        SurahItem it = getItem(position);
        tv.setText(it.sid + " • " + it.name);
        return tv;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String q = normalize(constraint == null ? "" : constraint.toString());

                List<SurahItem> out = new ArrayList<>();
                if (q.isEmpty()) {
                    out.addAll(all);
                } else {
                    for (SurahItem it : all) {
                        String name = normalize(it.name);
                        // also let "mur" match "al-mursalat" by removing "al"
                        String nameNoAl = name.startsWith("al") ? name.substring(2) : name;

                        if (name.contains(q) || nameNoAl.contains(q) || String.valueOf(it.sid).contains(q)) {
                            out.add(it);
                        }
                    }
                }

                FilterResults r = new FilterResults();
                r.values = out;
                r.count = out.size();
                return r;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filtered.clear();
                filtered.addAll((List<SurahItem>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    private String normalize(String s) {
        // lowercase + remove spaces, hyphen, apostrophe etc
        return s.toLowerCase()
                .replaceAll("[^a-z0-9]+", ""); // keeps letters+numbers only
    }
}
