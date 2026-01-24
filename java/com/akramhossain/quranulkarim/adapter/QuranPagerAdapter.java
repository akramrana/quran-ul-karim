package com.akramhossain.quranulkarim.adapter;

import com.akramhossain.quranulkarim.QuranPageFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class QuranPagerAdapter extends FragmentStateAdapter {
    public QuranPagerAdapter(@NonNull FragmentActivity fa) { super(fa); }

    @NonNull @Override
    public Fragment createFragment(int position) {
        int page = position + 1; // 1..604
        return QuranPageFragment.newInstance(page);
    }

    @Override
    public int getItemCount() { return 604; }
}
