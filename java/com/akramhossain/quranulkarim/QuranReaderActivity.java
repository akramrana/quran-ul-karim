package com.akramhossain.quranulkarim;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.akramhossain.quranulkarim.adapter.QuranPagerAdapter;
import com.akramhossain.quranulkarim.adapter.SurahAutoAdapter;
import com.akramhossain.quranulkarim.helper.DatabaseHelper;
import com.akramhossain.quranulkarim.helper.SimpleTextWatcher;
import com.akramhossain.quranulkarim.model.SurahItem;
import com.akramhossain.quranulkarim.util.Utils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;
import io.sentry.Sentry;

public class QuranReaderActivity extends AppCompatActivity {

    SharedPreferences mPrefs;
    static final int TOTAL_PAGES = 604;

    private static final String TAG = QuranReaderActivity.class.getSimpleName();

    List<SurahItem> surahList;
    int selectedSurahNo = 0;

    private static final int MIN_PAGE = 1;
    private static final int MAX_PAGE = 604;
    ViewPager2 pager;
    SparseIntArray juzStartPage = new SparseIntArray();
    SparseIntArray surahStartPage = new SparseIntArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quran_reader);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        View bottomBar = findViewById(R.id.jumpBar);
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

        mPrefs = getApplicationContext().getSharedPreferences(Utils.PREF_NAME, 0);

        pager = findViewById(R.id.pager);
        TextView tvSurah = findViewById(R.id.tvSurah);
        TextView tvJuz   = findViewById(R.id.tvJuz);
        TextView tvPage  = findViewById(R.id.tvPage);
        TextView tvMeta = findViewById(R.id.tvMeta);

        pager.setAdapter(new QuranPagerAdapter(this));

        // Right-to-left swipe like a Mushaf (optional)
        pager.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        pager.setPageTransformer((page, position) -> {
            float alpha = 1f - Math.min(0.12f, Math.abs(position) * 0.12f);
            page.setAlpha(alpha);
        });

        int lastReadPage = mPrefs.getInt("last_read_quran_page", 1);
        //Jump to last-read page:
        pager.setCurrentItem(lastReadPage - 1, false);

        setTopBottomTexts(lastReadPage, tvSurah, tvJuz, tvPage, tvMeta);

        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                int page = position + 1;
                mPrefs.edit()
                        .putInt("last_read_quran_page", page)
                        .apply();

                setTopBottomTexts(page, tvSurah, tvJuz, tvPage, tvMeta);
            }
        });

        surahList = new ArrayList<>();
        surahList.clear();
        //
        SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
        String sql = "SELECT sid,name_simple FROM sura ORDER BY sid ASC";
        Cursor cursor = db.rawQuery(sql, null);
        try {
            if (cursor.moveToFirst()) {
                do {

                    int iSid = cursor.getInt(cursor.getColumnIndexOrThrow("sid"));
                    String surahName = cursor.getString(cursor.getColumnIndexOrThrow("name_simple")).toString();
                    surahList.add(new SurahItem(iSid, surahName));

                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
            Sentry.captureException(new RuntimeException("SQL Query: " + sql, e));
        }finally {
            if (cursor != null && !cursor.isClosed()){
                cursor.close();
            }
            db.close();
        }
        //
        SQLiteDatabase db1 = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
        String sql1 = "SELECT juz_num, MIN(page_num) AS start_page FROM ayah GROUP BY juz_num ORDER BY juz_num";
        Cursor c = db1.rawQuery(sql1, null);
        try {
            int juzCol  = c.getColumnIndexOrThrow("juz_num");
            int pageCol = c.getColumnIndexOrThrow("start_page");
            while (c.moveToNext()) {
                int juz = c.getInt(juzCol);
                int page = c.getInt(pageCol);
                juzStartPage.put(juz, page);
            }
        } finally {
            c.close();
            db1.close();
        }
        //
        SQLiteDatabase db2 = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
        String sql2 = "SELECT surah_id, MIN(page_num) AS start_page FROM ayah GROUP BY surah_id ORDER BY surah_id";
        Cursor c2 = db2.rawQuery(sql2, null);
        try {
            int surahId  = c2.getColumnIndexOrThrow("surah_id");
            int pageCol = c2.getColumnIndexOrThrow("start_page");
            while (c2.moveToNext()) {
                int sid = c2.getInt(surahId);
                int page = c2.getInt(pageCol);
                surahStartPage.put(sid, page);
            }
        } finally {
            c2.close();
            db2.close();
        }
        //
        Button btnJump = findViewById(R.id.btnJump);
        btnJump.setOnClickListener(v -> showJumpDialog());

    }

    private void setTopBottomTexts(int pageNumber, TextView tvSurah, TextView tvJuz, TextView tvPage, TextView tvMeta) {
        tvPage.setText(pageNumber + " / " + TOTAL_PAGES);
        //
        SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
        String sql = "SELECT\n" +
                "  (\n" +
                "    SELECT GROUP_CONCAT(name_simple, ' • ')\n" +
                "    FROM (\n" +
                "      SELECT DISTINCT s.name_simple\n" +
                "      FROM ayah a2\n" +
                "      JOIN sura s ON s.surah_id = a2.surah_id\n" +
                "      WHERE a2.page_num = ?\n" +
                "      ORDER BY a2.surah_id\n" +
                "    )\n" +
                "  ) AS surah_names,\n" +
                "  MIN(a.juz_num) AS juz_num\n" +
                "FROM ayah a\n" +
                "WHERE a.page_num = ?";
        //Log.i(TAG, sql);
        String page = String.valueOf(pageNumber);
        Cursor cursor = db.rawQuery(sql, new String[]{ page, page });
        try {
            if (cursor.moveToFirst()) {
                String surahNames = cursor.getString(cursor.getColumnIndexOrThrow("surah_names")).toString();
                tvSurah.setText(surahNames);

                int juzNum = cursor.getInt(cursor.getColumnIndexOrThrow("juz_num"));
                tvJuz.setText("Juz " + juzNum);

                tvMeta.setText(surahNames+" • Juz " + juzNum);
            }
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
            Sentry.captureException(new RuntimeException("SQL Query: " + sql, e));
        }
        finally {
            if (cursor != null && !cursor.isClosed()){
                cursor.close();
            }
            db.close();
        }
    }

    private void showJumpDialog() {
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_jump_simple, null);

        EditText etPage  = v.findViewById(R.id.etPage);
        etPage.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    selectedSurahNo = 0;
                }
            }
        });
        EditText etJuz   = v.findViewById(R.id.etJuz);
        etJuz.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    selectedSurahNo = 0;
                }
            }
        });
        AutoCompleteTextView ddSurah = v.findViewById(R.id.ddSurah);

        clearOthers(etPage, etJuz, ddSurah);
        clearOthers(etJuz, etPage, ddSurah);

        //ArrayAdapter<SurahItem> ad = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, surahList);
        SurahAutoAdapter ad = new SurahAutoAdapter(this, surahList);
        ddSurah.setAdapter(ad);
        ddSurah.setThreshold(1);

        ddSurah.setOnItemClickListener((parent, view, position, id) -> {
            SurahItem item = (SurahItem) parent.getItemAtPosition(position);
            selectedSurahNo = item.sid;
            etPage.setText("");
            etJuz.setText("");

            Log.d("selectedSurahNo", "sid=" + item.sid + " label=" + item.name);

        });

        new AlertDialog.Builder(this)
                .setTitle("Jump to")
                .setView(v)
                .setPositiveButton("Go", (d, which) -> {
                    String pageS  = etPage.getText().toString().trim();
                    String juzS   = etJuz.getText().toString().trim();

                    Integer page  = toIntOrNull(pageS);
                    Integer juz   = toIntOrNull(juzS);

                    // Priority: Page > Surah > Juz
                    if (page != null) {
                        Log.d("selected_page",page.toString());
                        jumpToPage(page, true); // pageNo 1..604
                        return;
                    }
                    if (selectedSurahNo != 0) {
                        Log.d("selected_surah_no",String.valueOf(selectedSurahNo));
                        int startPage = surahStartPage.get(selectedSurahNo, 1);
                        jumpToPage(startPage, true);
                        return;
                    }
                    if (juz != null) {
                        Log.d("selected_juz",juz.toString());
                        int startPage = juzStartPage.get(juz, 1);
                        jumpToPage(startPage, true);
                        return;
                    }

                    Toast.makeText(this, "Enter Page or Surah or Juz", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private Integer toIntOrNull(String s) {
        if (s == null || s.isEmpty()) return null;
        try { return Integer.parseInt(s); } catch (Exception e) { return null; }
    }

    private void jumpToPage(int pageNo, boolean smooth) {
        int clamped = Math.max(MIN_PAGE, Math.min(MAX_PAGE, pageNo));
        pager.setCurrentItem(clamped - 1, smooth);
    }

    private void clearOthers(EditText active, View... others) {
        active.addTextChangedListener(new SimpleTextWatcher() {
            boolean selfChange = false;

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (selfChange) return;
                if (s == null || s.length() == 0) return;

                selfChange = true;
                for (View v : others) {
                    if (v instanceof EditText) {
                        ((EditText) v).setText("");
                    } else if (v instanceof AutoCompleteTextView) {
                        ((AutoCompleteTextView) v).setText("");
                    }
                }
                selfChange = false;
            }
        });
    }
}