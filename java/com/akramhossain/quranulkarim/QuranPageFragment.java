package com.akramhossain.quranulkarim;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import androidx.fragment.app.Fragment;

public class QuranPageFragment extends Fragment {
    private static final String ARG_PAGE = "page";

    public static QuranPageFragment newInstance(int page) {
        Bundle b = new Bundle();
        b.putInt(ARG_PAGE, page);
        QuranPageFragment f = new QuranPageFragment();
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_quran_page, container, false);
        PhotoView img = v.findViewById(R.id.img);

        int page = requireArguments().getInt(ARG_PAGE);

        // asset path example: assets/quran_pages/1.png ... 604.png
        String assetPath = "file:///android_asset/quran_pages/" + page + ".png";

        Glide.with(this)
                .load(assetPath)
                .dontAnimate()
                .into(img);

        return v;
    }
}
