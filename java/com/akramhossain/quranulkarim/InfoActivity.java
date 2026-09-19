package com.akramhossain.quranulkarim;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import io.sentry.Sentry;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akramhossain.quranulkarim.app.AppController;
import com.akramhossain.quranulkarim.helper.DatabaseHelper;
import com.akramhossain.quranulkarim.util.Utils;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class InfoActivity extends AppCompatActivity {

    public static String suraId;
    public static String suraName;
    public static String suraNameArabic;
    String infoText, infoTafhimText;
    Typeface font;
    SharedPreferences mPrefs;
    TextView info_content_tafhim, info_content;
    Button tafhim,fezilalil_quran, aiSummaryButton;

    public String active_tafsir = "";
    private static final String TAG = InfoActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            suraId = extras.getString("sura_id");
            suraName = extras.getString("sura_name");
            suraNameArabic = extras.getString("sura_name_arabic");
        }

        mPrefs = getApplicationContext().getSharedPreferences(Utils.PREF_NAME, 0);
        font = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/Siyamrupali.ttf");

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_info);

        View rootView = findViewById(R.id.topAboutBar);
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Apply padding to avoid overlap with status/navigation bars
            view.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    view.getPaddingBottom()
            );
            return insets;
        });

        View bottomBar = findViewById(R.id.scrollView);
        ViewCompat.setOnApplyWindowInsetsListener(bottomBar, (v, insets) -> {
            int bottomInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
            v.setPadding(
                    v.getPaddingLeft(),
                    v.getPaddingTop(),
                    v.getPaddingRight(),
                    bottomInset
            );
            return insets;
        });

        TextView info_title = (TextView) findViewById(R.id.info_title);
        info_title.setText(suraNameArabic);

        TextView info_sub_title = (TextView) findViewById(R.id.info_sub_title);
        info_sub_title.setText(suraName);

        info_content = (TextView) findViewById(R.id.info_content);
        info_content_tafhim = (TextView) findViewById(R.id.info_content_tafhim);

        //TextView info_content_ttl = (TextView) findViewById(R.id.info_content_ttl);
        //info_content_ttl.setTypeface(font);
        active_tafsir = "fezilalil";

        tafhim = (Button) findViewById(R.id.tafhim);
        tafhim.setTypeface(font);
        //
        fezilalil_quran = (Button) findViewById(R.id.fezilalil_quran);
        fezilalil_quran.setTypeface(font);
        //fezilalil_quran.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_color));
        fezilalil_quran.setBackground(ContextCompat.getDrawable(InfoActivity.this, R.drawable.bg_card_v2));
        fezilalil_quran.setTextColor(ContextCompat.getColor(InfoActivity.this, R.color.text_primary_v2));

        tafhim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                info_content_tafhim.setVisibility(View.VISIBLE);
                info_content.setVisibility(View.GONE);

                //tafhim.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_color));
                tafhim.setBackground(ContextCompat.getDrawable(InfoActivity.this, R.drawable.bg_card_v2));
                tafhim.setTextColor(ContextCompat.getColor(InfoActivity.this, R.color.text_primary_v2));

                //fezilalil_quran.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                fezilalil_quran.setBackground(ContextCompat.getDrawable(InfoActivity.this, R.drawable.bg_card));
                fezilalil_quran.setTextColor(ContextCompat.getColor(InfoActivity.this, R.color.text_primary));

                active_tafsir = "tafhim";
            }
        });
        fezilalil_quran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                info_content_tafhim.setVisibility(View.GONE);
                info_content.setVisibility(View.VISIBLE);

                //tafhim.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                tafhim.setBackground(ContextCompat.getDrawable(InfoActivity.this, R.drawable.bg_card));
                tafhim.setTextColor(ContextCompat.getColor(InfoActivity.this, R.color.text_primary));


                //fezilalil_quran.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_color));
                fezilalil_quran.setBackground(ContextCompat.getDrawable(InfoActivity.this, R.drawable.bg_card_v2));
                fezilalil_quran.setTextColor(ContextCompat.getColor(InfoActivity.this, R.color.text_primary_v2));

                active_tafsir = "fezilalil";
            }
        });

        getFezilalilTafsirFromLocalDB();
        getIntroduction();

        aiSummaryButton = (Button) findViewById(R.id.aiSummaryButton);

        aiSummaryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                askAi();
            }
        });

    }

    private void getIntroduction(){
        SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
        String sql = "select * from introduction where sura_id = "+suraId;
        Log.i("SQL", sql);
        Cursor cursor = db.rawQuery(sql,null);
        try {
            if (cursor.moveToFirst()) {
                infoTafhimText = cursor.getString(1);
                info_content_tafhim.setText(Html.fromHtml(infoTafhimText,Html.FROM_HTML_MODE_LEGACY));
                info_content_tafhim.setTypeface(font);

                String mp_bnFz = mPrefs.getString("bnFontSize", "15");
                if(!mp_bnFz.equals("") && mp_bnFz != null){
                    try {
                        info_content_tafhim.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_bnFz));
                    }catch (NumberFormatException e) {
                        Log.e("WRONG_FONT_SIZE", "Error parsing number: ", e);
                    }
                }
            }
        }
        catch (Exception e){
            Log.e("Info", e.getMessage());
            //throw new RuntimeException("SQL Query: " + sql, e);
            Sentry.captureException(new RuntimeException("SQL Query: " + sql, e));
        }finally {
            if (cursor != null && !cursor.isClosed()){
                cursor.close();
            }
            db.close();
        }
    }

    private void getFezilalilTafsirFromLocalDB(){
        SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
        String banglaSuraId = getDigitBanglaFromEnglish(suraId);
        String banglaVerseId = getDigitBanglaFromEnglish("0");
        String sql = "select tafsir_text\n" +
                "from tafsir_fezilalil_quran\n" +
                "where sura_id = '"+banglaSuraId+"' and verse_id = '"+banglaVerseId+"'";

        Log.i("SQL", sql);
        Cursor cursor = db.rawQuery(sql,null);
        try {
            if (cursor.moveToFirst()) {
                infoText = cursor.getString(0);
                info_content.setText(Html.fromHtml(infoText,Html.FROM_HTML_MODE_LEGACY));
                info_content.setTypeface(font);
                String mp_bnFz = mPrefs.getString("bnFontSize", "15");
                if(!mp_bnFz.equals("") && mp_bnFz !=null ){
                    try {
                        info_content.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_bnFz));
                    }catch (NumberFormatException e) {
                        Log.e("WRONG_FONT_SIZE", "Error parsing number: ", e);
                    }
                }
            }
        }
        catch (Exception e){
            Log.e("Info", e.getMessage());
            //throw new RuntimeException("SQL Query: " + sql, e);
            Sentry.captureException(new RuntimeException("SQL Query: " + sql, e));
        }finally {
            if (cursor != null && !cursor.isClosed()){
                cursor.close();
            }
            db.close();
        }
    }

    public static final String getDigitBanglaFromEnglish(String number) {
        char[] banglaDigits = { '০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯' };
        if (number == null)
            return new String("");
        StringBuilder builder = new StringBuilder();
        try {
            for (int i = 0; i < number.length(); i++) {
                if (Character.isDigit(number.charAt(i))) {
                    if (((int) (number.charAt(i)) - 48) <= 9) {
                        builder.append(banglaDigits[(int) (number.charAt(i)) - 48]);
                    } else {
                        builder.append(number.charAt(i));
                    }
                } else {
                    builder.append(number.charAt(i));
                }
            }
        } catch (Exception e) {
            return new String("");
        }
        return builder.toString();
    }

    private void askAi(){
        Log.i(TAG, active_tafsir);

        String label = suraName;
        String tafsirTxt = "";
        String lang = "Bangla";

        if (active_tafsir.equals("tafhim")) {
            tafsirTxt = info_content_tafhim.getText().toString();
        } else if (active_tafsir.equals("fezilalil")) {
            tafsirTxt = info_content.getText().toString();
        }

        if(canUseAiSummary(tafsirTxt)) {
            Log.d(label, tafsirTxt);
            requestAiSummary("brief",suraId,active_tafsir,"",lang,tafsirTxt);
        }
    }

    private boolean canUseAiSummary(String tafsirTxt) {

        if (tafsirTxt == null)
            return false;

        String text = tafsirTxt.toLowerCase().trim();

        String[] blockedTexts = {
                "please check ayah",
                "for complete tafsir",
                "complete tafsir",
                "tafsir not available"
        };

        for(String blocked : blockedTexts){

            if(text.contains(blocked.toLowerCase())){
                return false;
            }
        }

        return true;
    }

    private void requestAiSummary(String type, String surahId, String tafsirId, String ayahId, String lang, String originalTxt) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.bottom_ai_text);

        TextView infoTitle = dialog.findViewById(R.id.infoTitle);
        infoTitle.setText("✨ AI Summary");

        TextView infoTxt = dialog.findViewById(R.id.infoTxt);
        infoTxt.setTypeface(font);

        ProgressBar progressBar = dialog.findViewById(R.id.progressBar);

        TextView disclaimerTxt = dialog.findViewById(R.id.disclaimerTxt);

        dialog.show();

        Window window = dialog.getWindow();

        if(window != null){
            window.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }

        String url = "https://quran.codxplore.com/api/v1/app-ai-summarize.php";
        try {
            JSONObject json = new JSONObject();
            json.put("type", type);               // tafsir / brief
            json.put("surah_id", surahId);
            json.put("tafsir_id", tafsirId);
            json.put("lang", lang);               // Bangla, English etc
            json.put("original_txt", originalTxt);

            if (ayahId != null) json.put("ayah_id", ayahId);
            else json.put("ayah_id", JSONObject.NULL);

            progressBar.setVisibility(View.VISIBLE);

            disclaimerTxt.setVisibility(View.VISIBLE);

            infoTxt.setText("\uD83E\uDD16 Analyzing Text...");

            String payload = json.toString();

            Log.d("payload",payload);

            Log.d("PAYLOAD_SIZE", "Length = " + payload.length());

            for (int i = 0; i < payload.length(); i += 1000) {
                int end = Math.min(payload.length(), i + 1000);
                Log.d("PAYLOAD", payload.substring(i, end));
            }

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, json,
                    response -> {
                        progressBar.setVisibility(View.GONE);
                        disclaimerTxt.setVisibility(View.GONE);

                        try {
                            Log.d("AI_RESPONSE", response.toString());
                            boolean success = response.getBoolean("success");
                            if (success) {
                                JSONObject data = response.getJSONObject("data");
                                String summary = data.getString("ai_txt");
                                boolean cached = response.getBoolean("cached");
                                Log.d("AI", summary);
                                if (cached) {
                                    Log.d("AI", "loaded from DB");
                                }
                                // show in bottom sheet
                                String[] words = summary.split("\\s+");

                                Handler handler = new Handler(Looper.getMainLooper());
                                StringBuilder currentText = new StringBuilder();
                                for(int i=0;i<words.length;i++){
                                    int index=i;
                                    handler.postDelayed(() -> {
                                        currentText.append(words[index]).append(" ");
                                        infoTxt.setText(currentText.toString());
                                    },i*80); // speed
                                }

                            }else{
                                infoTxt.setText("\uD83E\uDD16 Unable to prepare AI summary at the moment. Please try again later.");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    },
                    error -> {
                        progressBar.setVisibility(View.GONE);
                        disclaimerTxt.setVisibility(View.GONE);
                        Log.e("AI_ERROR", error.toString());
                        Toast.makeText(getApplicationContext(), "AI failed", Toast.LENGTH_LONG).show();
                    });

            request.setRetryPolicy(new DefaultRetryPolicy(60000,0,1f));
            request.setShouldCache(false);
            AppController.getInstance().addToRequestQueue(request, "req_ai_summary");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}