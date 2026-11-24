package com.akramhossain.quranulkarim;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.akramhossain.quranulkarim.model.SeerahViewModel;
import com.akramhossain.quranulkarim.util.SeerahPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SeerahReaderFragment extends Fragment {

    private final List<JSONObject> chapters = new ArrayList<>();

    private int currentIndex = 0;
    private String language = "bn";
    private float fontSize = 19f;

    private Typeface bengaliFont;

    private TextView title;
    private TextView subtitle;
    private TextView content;
    private TextView date;
    private TextView chapterInfo;
    private TextView position;
    private TextView previous;
    private TextView next;
    private TextView references;
    private TextView referencesToggle;
    private TextView bangla;
    private TextView english;

    private View referencesCard;
    private ScrollView scrollView;

    private SeerahPreferences preferences;
    private String section;

    public SeerahReaderFragment() {
        super(R.layout.fragment_seerah_reader);
    }

    public static SeerahReaderFragment newInstance(
            String section, String chapterId) {

        SeerahReaderFragment fragment =
                new SeerahReaderFragment();

        Bundle args = new Bundle();
        args.putString("section", section);
        args.putString("chapter_id", chapterId);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        preferences = new SeerahPreferences(requireContext());

        language = preferences.getLanguage();
        fontSize = preferences.getFontSize();

        section = requireArguments().getString("section", "");

        bengaliFont = Typeface.createFromAsset(
                requireContext().getAssets(),
                "fonts/Siyamrupali.ttf"
        );

        title = view.findViewById(R.id.readerTitle);
        subtitle = view.findViewById(R.id.readerSubtitle);
        content = view.findViewById(R.id.readerContent);
        date = view.findViewById(R.id.readerDate);
        chapterInfo = view.findViewById(R.id.readerChapterInfo);
        position = view.findViewById(R.id.readerPosition);
        previous = view.findViewById(R.id.readerPrevious);
        next = view.findViewById(R.id.readerNext);
        references = view.findViewById(R.id.readerReferences);
        referencesToggle = view.findViewById(R.id.readerReferencesToggle);
        referencesCard = view.findViewById(R.id.readerReferencesCard);
        scrollView = view.findViewById(R.id.readerScroll);
        bangla = view.findViewById(R.id.readerBangla);
        english = view.findViewById(R.id.readerEnglish);

        bangla.setTypeface(bengaliFont);

        view.findViewById(R.id.readerBack)
                .setOnClickListener(v ->
                        getParentFragmentManager().popBackStack());

        SeerahViewModel viewModel =
                new ViewModelProvider(requireActivity())
                        .get(SeerahViewModel.class);

        JSONArray array = viewModel.getChapters();

        if (array == null || array.length() == 0) {
            Toast.makeText(requireContext(),
                    "Please reopen the chapter list.",
                    Toast.LENGTH_SHORT).show();

            getParentFragmentManager().popBackStack();
            return;
        }

        chapters.clear();

        for (int i = 0; i < array.length(); i++) {
            JSONObject chapter = array.optJSONObject(i);

            if (chapter != null) {
                chapters.add(chapter);
            }
        }

        chapters.sort(Comparator.comparingInt(
                chapter -> chapter.optInt("order")));

        String chapterId =
                requireArguments().getString("chapter_id", "");

        if (savedInstanceState != null) {
            chapterId = savedInstanceState.getString(
                    "chapter_id", chapterId);

            language = savedInstanceState.getString(
                    "language", "bn");

            fontSize = savedInstanceState.getFloat(
                    "font_size", 19f);
        }

        currentIndex = -1;

        for (int i = 0; i < chapters.size(); i++) {
            if (chapterId.equals(chapters.get(i).optString("id"))) {
                currentIndex = i;
                break;
            }
        }

        if (currentIndex == -1) {
            getParentFragmentManager().popBackStack();
            return;
        }

        bangla.setOnClickListener(v -> {
            language = "bn";
            preferences.setLanguage(language);
            renderChapter(false);
        });

        english.setOnClickListener(v -> {
            language = "en";
            preferences.setLanguage(language);
            renderChapter(false);
        });

        view.findViewById(R.id.readerFontDecrease)
                .setOnClickListener(v -> {
                    fontSize = Math.max(14f, fontSize - 1f);
                    content.setTextSize(fontSize);
                    preferences.setFontSize(fontSize);
                });

        view.findViewById(R.id.readerFontIncrease)
                .setOnClickListener(v -> {
                    fontSize = Math.min(32f, fontSize + 1f);
                    content.setTextSize(fontSize);
                    preferences.setFontSize(fontSize);
                });

        previous.setOnClickListener(v -> {
            if (currentIndex > 0) {
                currentIndex--;
                renderChapter(true);
            }
        });

        next.setOnClickListener(v -> {
            if (currentIndex < chapters.size() - 1) {
                currentIndex++;
                renderChapter(true);
            }
        });

        referencesToggle.setOnClickListener(v -> {
            boolean expanded =
                    references.getVisibility() == View.VISIBLE;

            references.setVisibility(
                    expanded ? View.GONE : View.VISIBLE);

            referencesToggle.setText(expanded
                    ? "References · তথ্যসূত্র  ▾"
                    : "References · তথ্যসূত্র  ▴");
        });

        // Bookmark implementation will be added later.
        view.findViewById(R.id.readerBookmark)
                .setVisibility(View.GONE);

        renderChapter(false);
    }

    private void renderChapter(boolean resetScroll) {

        JSONObject chapter = chapters.get(currentIndex);

        JSONObject chapterTitle = chapter.optJSONObject("title");
        preferences.saveLastRead(
                section,
                chapter.optString("id"),
                localized(chapterTitle, "en"),
                localized(chapterTitle, "bn")
        );

        JSONObject chapterContent = chapter.optJSONObject("content");

        boolean isBangla = language.equals("bn");

        String primaryTitle = localized(chapterTitle, language);

        String secondaryTitle = localized(
                chapterTitle, isBangla ? "en" : "bn");

        String story = localized(chapterContent, language);

        title.setText(primaryTitle);
        subtitle.setText(secondaryTitle);

        title.setTypeface(
                isBangla ? bengaliFont : Typeface.DEFAULT,
                Typeface.BOLD);

        subtitle.setTypeface(
                isBangla ? Typeface.DEFAULT : bengaliFont);

        content.setTypeface(
                isBangla ? bengaliFont : Typeface.DEFAULT);

        content.setTextSize(fontSize);

        if (story.trim().isEmpty()) {
            story = isBangla
                    ? "এই অধ্যায়ের বিষয়বস্তু শীঘ্রই যুক্ত করা হবে।"
                    : "The content of this chapter will be added soon.";
        }

        content.setText(story);

        // Optional date
        Object dateValue = chapter.opt("date");

        String dateText = "";

        if (dateValue instanceof JSONObject) {
            dateText = localized((JSONObject) dateValue, language);
        } else if (dateValue instanceof String) {
            dateText = (String) dateValue;
        }

        date.setText(dateText);
        date.setVisibility(dateText.isEmpty()
                ? View.GONE : View.VISIBLE);

        int chapterNumber = currentIndex + 1;
        int total = chapters.size();

        chapterInfo.setText("Chapter " + chapterNumber + " of " + total);
        position.setText(chapterNumber + " / " + total);

        previous.setEnabled(currentIndex > 0);
        previous.setAlpha(currentIndex > 0 ? 1f : 0.35f);

        next.setEnabled(currentIndex < total - 1);
        next.setAlpha(currentIndex < total - 1 ? 1f : 0.35f);

        bangla.setBackgroundResource(isBangla
                ? R.drawable.bg_seerah_continue : 0);

        english.setBackgroundResource(!isBangla
                ? R.drawable.bg_seerah_continue : 0);

        renderReferences(chapter.optJSONArray("references"));

        if (resetScroll) {
            scrollView.post(() -> scrollView.scrollTo(0, 0));
        }
    }

    private String localized(JSONObject object, String lang) {
        return object == null ? "" : object.optString(lang, "");
    }

    private void renderReferences(JSONArray array) {

        references.setVisibility(View.GONE);
        referencesToggle.setText("References · তথ্যসূত্র  ▾");

        if (array == null || array.length() == 0) {
            referencesCard.setVisibility(View.GONE);
            return;
        }

        referencesCard.setVisibility(View.VISIBLE);

        StringBuilder text = new StringBuilder();

        for (int i = 0; i < array.length(); i++) {

            JSONObject reference = array.optJSONObject(i);

            String value = reference != null
                    ? localized(reference, language)
                    : array.optString(i, "");

            if (!value.isEmpty()) {
                if (text.length() > 0) {
                    text.append("\n\n");
                }

                text.append(i + 1)
                        .append(". ")
                        .append(value);
            }
        }

        references.setTypeface(language.equals("bn")
                ? bengaliFont : Typeface.DEFAULT);

        references.setText(text.toString());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("language", language);
        outState.putFloat("font_size", fontSize);

        if (currentIndex >= 0 && currentIndex < chapters.size()) {
            outState.putString("chapter_id",
                    chapters.get(currentIndex).optString("id"));
        }
    }
}
