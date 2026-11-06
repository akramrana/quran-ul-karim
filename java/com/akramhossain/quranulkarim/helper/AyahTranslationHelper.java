package com.akramhossain.quranulkarim.helper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.akramhossain.quranulkarim.R;
import com.akramhossain.quranulkarim.app.AppController;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class AyahTranslationHelper {

    private static final String TRANSLATION_API =
            "https://quran.codxplore.com/api/v1/app-ai-translate.php";

    private static final String TRANSLATION_SOURCE =
            "https://github.com/fawazahmed0/quran-api";

    private static final String[] LANGUAGE_CODES = {
            "fr", "id", "ru", "ta", "tr", "ur", "hi", "zh"
    };

    private static final String[] LANGUAGE_NAMES = {
            "French", "Indonesian", "Russian", "Tamil",
            "Turkish", "Urdu", "Hindi", "Chinese (Simplified)"
    };

    private static final String[] TRANSLATOR_NAMES = {
            "Muhammad Hamidullah",
            "Indonesian Islamic Affairs Ministry",
            "Elmir Kuliev",
            "Abdulhameed Baqavi",
            "Diyanet İşleri",
            "Fateh Muhammad Jalandhari",
            "Muhammad Farooq Khan & Muhammad Ahmed",
            "Muhammad Ma Jian (马坚)"
    };

    private AyahTranslationHelper() {
        // Utility class
    }

    public static void show(Context c, String surahNumber, String ayahNumber) {
        if (!(c instanceof Activity)) {
            return;
        }
        Dialog dialog = new Dialog(c);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        View sheet = LayoutInflater.from(c)
                .inflate(R.layout.bottom_sheet_ayah_translations, null);

        dialog.setContentView(sheet);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        TextView title = sheet.findViewById(R.id.translationTitle);
        TextView closeButton = sheet.findViewById(R.id.translationClose);
        TextView translationText = sheet.findViewById(R.id.translationText);
        TextView translatorName = sheet.findViewById(R.id.translatorName);
        TextView translationSource = sheet.findViewById(R.id.translationSource);

        Spinner spinner = sheet.findViewById(R.id.languageSpinner);
        ProgressBar progress = sheet.findViewById(R.id.translationProgress);
        ScrollView translationScroll = sheet.findViewById(R.id.translationScroll);

        title.setText("Translations — " + surahNumber + ":" + ayahNumber);

        translationText.setText("Loading translations...");
        translatorName.setText("");
        progress.setVisibility(View.VISIBLE);

        // Close popup
        closeButton.setOnClickListener(v -> dialog.dismiss());

        // Open GitHub attribution
        translationSource.setOnClickListener(v -> {
            Intent intent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(TRANSLATION_SOURCE)
            );

            try {
                c.startActivity(intent);
            } catch (Exception e) {
                Log.e("AyahTranslations", "Unable to open source", e);
            }
        });

        // Language selector
        ArrayAdapter<String> languageAdapter = new ArrayAdapter<>(
                c,
                R.layout.item_translation_language,
                LANGUAGE_NAMES
        );

        languageAdapter.setDropDownViewResource(
                R.layout.item_translation_language
        );

        spinner.setAdapter(languageAdapter);

        final JSONObject[] translations = {null};

        spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id
                    ) {
                        if (translations[0] == null) {
                            return;
                        }

                        displayTranslation(
                                position,
                                translations[0],
                                translationText,
                                translatorName
                        );

                        translationScroll.post(() -> translationScroll.scrollTo(0, 0));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );

        // Show centered dialog
        dialog.show();

        Window window = dialog.getWindow();

        if (window != null) {
            window.setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT)
            );

            window.addFlags(
                    WindowManager.LayoutParams.FLAG_DIM_BEHIND
            );

            DisplayMetrics metrics = c.getResources().getDisplayMetrics();

            WindowManager.LayoutParams params = window.getAttributes();

            params.width = (int) (metrics.widthPixels * 0.90f);

            // 65% of screen height
            params.height = (int) (metrics.heightPixels * 0.60f);

            params.gravity = Gravity.CENTER;
            params.dimAmount = 0.65f;

            window.setAttributes(params);
        }

        // Restrict scrollable text height for long Ayahs
        int screenHeight = c.getResources()
                .getDisplayMetrics().heightPixels;

        translationScroll.setOnHierarchyChangeListener(null);

        translationScroll.post(() -> {
            translationScroll.getLayoutParams().height =
                    WindowManager.LayoutParams.WRAP_CONTENT;

            translationScroll.setFillViewport(false);

            translationScroll.getLayoutParams().height =
                    Math.min(
                            translationScroll.getHeight(),
                            (int) (screenHeight * 0.40f)
                    );

            translationScroll.requestLayout();
        });

        // Prepare API payload
        JSONObject payload = new JSONObject();

        try {
            payload.put("surah_id", Integer.parseInt(surahNumber));
            payload.put("ayah_id", Integer.parseInt(ayahNumber));
        } catch (Exception e) {
            progress.setVisibility(View.GONE);
            translationText.setText("Invalid Surah or Ayah number.");
            return;
        }

        // Unique request tag for this dialog instance
        String requestTag =
                "ayah_translation_" + surahNumber + "_" + ayahNumber
                        + "_" + System.nanoTime();

        Log.d("payload", payload.toString());
        Log.d("TRANSLATION_API",TRANSLATION_API);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                TRANSLATION_API,
                payload,

                response -> {
                    if (!dialog.isShowing()) {
                        return;
                    }

                    progress.setVisibility(View.GONE);

                    if (!response.optBoolean("success", false)) {
                        translationText.setText(
                                response.optString(
                                        "message",
                                        "Unable to load translations."
                                )
                        );
                        return;
                    }

                    JSONObject data = response.optJSONObject("data");

                    if (data == null) {
                        translationText.setText("Invalid API response.");
                        return;
                    }

                    translations[0] = data.optJSONObject("translations");

                    if (translations[0] == null) {
                        translationText.setText("Translations unavailable.");
                        return;
                    }

                    int selectedPosition = Math.max(
                            spinner.getSelectedItemPosition(),
                            0
                    );

                    displayTranslation(
                            selectedPosition,
                            translations[0],
                            translationText,
                            translatorName
                    );

                    translationScroll.post(() -> translationScroll.scrollTo(0, 0));
                },

                error -> {
                    if (!dialog.isShowing()) {
                        return;
                    }

                    progress.setVisibility(View.GONE);

                    translationText.setText(
                            "Unable to load translations. Please check your connection."
                    );

                    translatorName.setText("");

                    Log.e(
                            "AyahTranslations",
                            "Translation API request failed",
                            error
                    );
                }
        );

        request.setRetryPolicy(
                new DefaultRetryPolicy(
                        30000,
                        0,
                        1.0f
                )
        );

        AppController.getInstance()
                .addToRequestQueue(request, requestTag);

        dialog.setOnDismissListener(d ->
                AppController.getInstance()
                        .cancelPendingRequests(requestTag)
        );
    }

    private static void displayTranslation(
            int position,
            JSONObject translations,
            TextView translationText,
            TextView translatorName
    ) {
        String code = LANGUAGE_CODES[position];

        String text = translations.optString(
                code,
                "Translation unavailable."
        );

        translationText.setText(text);

        boolean isRtl = code.equals("ur");

        translationText.setTextDirection(
                isRtl ? View.TEXT_DIRECTION_RTL : View.TEXT_DIRECTION_LTR
        );

        translationText.setGravity(
                isRtl ? Gravity.RIGHT : Gravity.LEFT
        );

        translatorName.setText(
                "Translation: " + TRANSLATOR_NAMES[position]
        );

    }
}
