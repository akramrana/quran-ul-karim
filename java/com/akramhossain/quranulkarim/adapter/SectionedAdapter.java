package com.akramhossain.quranulkarim.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.akramhossain.quranulkarim.R;
import com.akramhossain.quranulkarim.model.Reciter;
import com.akramhossain.quranulkarim.model.SectionItem;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SectionedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_HEADER = 0;
    private static final int VIEW_ITEM   = 1;

    private final List<SectionItem> items;
    private final OnReciterClick listener;

    public interface OnReciterClick {
        void onClick(Reciter r);
    }

    public SectionedAdapter(List<SectionItem> items, OnReciterClick listener) {
        this.items = items;
        this.listener = listener;
    }

    @Override public int getItemViewType(int position) {
        return items.get(position).type == SectionItem.Type.HEADER ? VIEW_HEADER : VIEW_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_HEADER) {
            View v = inf.inflate(R.layout.item_header, parent, false);
            return new HeaderVH(v);
        } else {
            View v = inf.inflate(R.layout.item_reciter, parent, false);
            return new ReciterVH(v, listener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int pos) {
        SectionItem it = items.get(pos);
        if (h instanceof HeaderVH) {
            ((HeaderVH) h).bind(it.header);
        } else if (h instanceof ReciterVH) {
            ((ReciterVH) h).bind(it.reciter);
        }
    }

    @Override public int getItemCount() { return items.size(); }

    // --- VHs ---
    static class HeaderVH extends RecyclerView.ViewHolder {
        private final TextView title;
        HeaderVH(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.headerText);
        }
        void bind(Character c) { title.setText(String.valueOf(c)); }
    }

    static class ReciterVH extends RecyclerView.ViewHolder {
        private final TextView name;
        private Reciter current;
        ReciterVH(@NonNull View itemView, OnReciterClick listener) {
            super(itemView);
            name = itemView.findViewById(R.id.reciterName);
            itemView.setOnClickListener(v -> {
                if (listener != null && current != null) listener.onClick(current);
            });
        }
        void bind(Reciter r) {
            current = r;
            name.setText(r.name);
        }
    }
}

