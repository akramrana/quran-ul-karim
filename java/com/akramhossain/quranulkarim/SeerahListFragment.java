package com.akramhossain.quranulkarim;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akramhossain.quranulkarim.app.AppController;
import com.akramhossain.quranulkarim.model.SeerahViewModel;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import androidx.lifecycle.ViewModelProvider;

public class SeerahListFragment extends Fragment {

    private static final String ARG_SECTION = "section";

    private static final String BASE_URL = "https://quran.codxplore.com/api/v1/seerah/";

    private final Object requestTag = new Object();

    private String section;
    private LinearLayout chapterContainer;
    private View loading;
    private View errorContainer;
    private View scrollView;
    private TextView errorText;
    private TextView titleView;
    private TextView subtitleView;
    private Typeface bengaliFont;

    private String resumeChapterId;

    private final List<Chapter> chapters = new ArrayList<>();

    public SeerahListFragment() {
        super(R.layout.fragment_seerah_list);
    }

    public static SeerahListFragment newInstance(String section) {
        return newInstance(section, null);
    }

    public static SeerahListFragment newInstance(
            String section, String chapterId) {

        SeerahListFragment fragment = new SeerahListFragment();

        Bundle args = new Bundle();
        args.putString(ARG_SECTION, section);
        args.putString("resume_chapter_id", chapterId);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        section = requireArguments().getString(ARG_SECTION, "");

        resumeChapterId = requireArguments()
                .getString("resume_chapter_id");

        chapterContainer =
                view.findViewById(R.id.seerahChapterContainer);
        loading = view.findViewById(R.id.seerahListLoading);
        errorContainer =
                view.findViewById(R.id.seerahListErrorContainer);
        scrollView = view.findViewById(R.id.seerahListScroll);
        errorText = view.findViewById(R.id.seerahListError);
        titleView = view.findViewById(R.id.seerahListTitle);
        subtitleView = view.findViewById(R.id.seerahListSubtitle);

        bengaliFont = Typeface.createFromAsset(
                requireContext().getAssets(),
                "fonts/Siyamrupali.ttf"
        );

        subtitleView.setTypeface(bengaliFont);

        view.findViewById(R.id.seerahListBack)
                .setOnClickListener(v ->
                        getParentFragmentManager().popBackStack());

        view.findViewById(R.id.seerahListRetry)
                .setOnClickListener(v -> loadChapters());

        loadChapters();
    }

    private String getFilename() {
        switch (section) {
            case "before_birth":
                return "before-birth.json";
            case "timeline":
                return "timeline.json";
            case "after_passing":
                return "after-passing.json";
            case "family":
                return "family-ahlul-bayt.json";
            case "character":
                return "character-legacy.json";
            default:
                return null;
        }
    }

    private void loadChapters() {
        String filename = getFilename();

        if (filename == null) {
            showError("Unknown Seerah section.");
            return;
        }

        loading.setVisibility(View.VISIBLE);
        errorContainer.setVisibility(View.GONE);
        scrollView.setVisibility(View.GONE);

        String url = BASE_URL + filename;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        if (!section.equals(response.getString("section"))) {
                            throw new Exception("Section mismatch");
                        }

                        JSONObject sectionTitle =
                                response.getJSONObject("title");

                        JSONArray array =
                                response.getJSONArray("chapters");

                        SeerahViewModel viewModel =
                                new ViewModelProvider(requireActivity())
                                        .get(SeerahViewModel.class);

                        viewModel.setChapters(array);

                        List<Chapter> result = new ArrayList<>();

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject item = array.getJSONObject(i);
                            JSONObject title = item.getJSONObject("title");
                            JSONObject description =
                                    item.optJSONObject("description");

                            Chapter chapter = new Chapter();
                            chapter.id = item.getString("id");
                            chapter.order = item.getInt("order");
                            chapter.titleEn = title.optString("en");
                            chapter.titleBn = title.optString("bn");
                            chapter.descriptionBn = description != null
                                    ? description.optString("bn") : "";

                            result.add(chapter);
                        }

                        result.sort(
                                Comparator.comparingInt(c -> c.order)
                        );

                        if (getView() == null) return;

                        chapters.clear();
                        chapters.addAll(result);

                        titleView.setText(sectionTitle.optString("en"));
                        subtitleView.setText(sectionTitle.optString("bn"));

                        renderChapters();

                        openResumeChapter();

                    } catch (Exception e) {
                        showError("Invalid chapter data: " + e.getMessage());
                    }
                },
                error -> {
                    if (getView() == null) return;

                    showError("Unable to load chapters. Please try again.");
                }
        );

        // Disable Volley caching during development
        request.setShouldCache(false);

        request.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        AppController.getInstance().addToRequestQueue(
                request,
                requestTag.toString()
        );
    }

    private void renderChapters() {
        chapterContainer.removeAllViews();

        for (Chapter chapter : chapters) {
            View item = getLayoutInflater().inflate(
                    R.layout.item_seerah_chapter,
                    chapterContainer,
                    false
            );

            TextView number =
                    item.findViewById(R.id.seerahChapterNumber);
            TextView title =
                    item.findViewById(R.id.seerahChapterTitle);
            TextView titleBn =
                    item.findViewById(R.id.seerahChapterTitleBn);
            TextView description =
                    item.findViewById(R.id.seerahChapterDescription);

            number.setText(String.format(
                    java.util.Locale.US, "%02d", chapter.order));

            title.setText(chapter.titleEn);
            titleBn.setText(chapter.titleBn);
            description.setText(chapter.descriptionBn);

            titleBn.setTypeface(bengaliFont);
            description.setTypeface(bengaliFont);

            item.setOnClickListener(v -> {
                getParentFragmentManager()
                        .beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(
                                R.id.seerahFragmentContainer,
                                SeerahReaderFragment.newInstance(section, chapter.id)
                        )
                        .addToBackStack(null)
                        .commit();
            });

            chapterContainer.addView(item);
        }

        loading.setVisibility(View.GONE);
        errorContainer.setVisibility(View.GONE);
        scrollView.setVisibility(View.VISIBLE);
    }

    private void showError(String message) {
        loading.setVisibility(View.GONE);
        scrollView.setVisibility(View.GONE);
        errorContainer.setVisibility(View.VISIBLE);
        errorText.setText(message);
    }

    private static class Chapter {
        String id;
        int order;
        String titleEn;
        String titleBn;
        String descriptionBn;
    }

    @Override
    public void onDestroyView() {
        AppController.getInstance()
                .cancelPendingRequests(requestTag.toString());

        super.onDestroyView();
    }

    private void openResumeChapter() {

        if (resumeChapterId == null
                || resumeChapterId.isEmpty()) {
            return;
        }

        String chapterId = resumeChapterId;

        // Consume the resume request only once.
        resumeChapterId = null;
        requireArguments().remove("resume_chapter_id");

        for (Chapter chapter : chapters) {

            if (chapter.id.equals(chapterId)) {

                getParentFragmentManager()
                        .beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(
                                R.id.seerahFragmentContainer,
                                SeerahReaderFragment.newInstance(
                                        section, chapterId
                                )
                        )
                        .addToBackStack(null)
                        .commit();

                return;
            }
        }
    }
}
