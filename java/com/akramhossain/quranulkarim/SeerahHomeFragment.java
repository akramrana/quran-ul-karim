package com.akramhossain.quranulkarim;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SeerahHomeFragment extends Fragment {

    Typeface font,fontUthmani;

    public SeerahHomeFragment() {
        super(R.layout.fragment_seerah_home);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        font = Typeface.createFromAsset(
                requireContext().getAssets(),
                "fonts/Siyamrupali.ttf"
        );

        fontUthmani = Typeface.createFromAsset(requireContext().getAssets(),"fonts/KFGQPC_Uthmanic_Script_HAFS_Regular.ttf");

        LinearLayout container =
                view.findViewById(R.id.seerahSectionsContainer);

        addSection(container,
                "Before His Birth",
                "জন্মের পূর্বকথা");

        addSection(container,
                "63-Year Timeline",
                "৬৩ বছরের জীবনপঞ্জি");

        addSection(container,
                "After His Passing",
                "ইন্তেকালের পরবর্তী ঘটনা");

        addSection(container,
                "Family & Ahl al-Bayt",
                "পরিবার ও আহলে বাইত");

        addSection(container,
                "Character & Legacy",
                "চরিত্র ও আদর্শ");

        TextView bn_title = view.findViewById(R.id.bn_title);
        bn_title.setTypeface(font);

        TextView ar_title = view.findViewById(R.id.ar_title);
        ar_title.setTypeface(fontUthmani);
    }

    private void addSection(LinearLayout container,
                            String title,
                            String subtitle) {

        View item = getLayoutInflater().inflate(
                R.layout.item_seerah_section,
                container,
                false
        );

        TextView titleView =
                item.findViewById(R.id.seerahSectionTitle);

        TextView subtitleView =
                item.findViewById(R.id.seerahSectionSubtitle);

        subtitleView.setTypeface(font);

        titleView.setText(title);
        subtitleView.setText(subtitle);

        item.setOnClickListener(v -> {
            // Navigation to SeerahListFragment
            // will be implemented next.
        });

        container.addView(item);
    }
}
