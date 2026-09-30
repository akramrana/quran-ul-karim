package com.akramhossain.quranulkarim.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.akramhossain.quranulkarim.helper.DatabaseHelper;
import com.akramhossain.quranulkarim.model.Ayah;
import java.util.Calendar;

public class WidgetAyahRepository {

    private static final String PREFS = "daily_ayah_widget";
    private static final String PREF_DAY = "day";
    private static final String PREF_ADVANCE = "advance";

    private WidgetAyahRepository() {
        // Utility class
    }

    public static Ayah getDailyAyah(Context context) {
        SQLiteDatabase db = DatabaseHelper.getInstance(context.getApplicationContext()).getReadableDatabase();

        try {
            String condition =
                    "length(text_tashkeel) >= 30 AND length(text_tashkeel) <= 50 AND length(content_bn) <= 50 AND length(content_en) <= 50";

            int count;

            try (Cursor cursor = db.rawQuery(
                    "SELECT COUNT(*) FROM ayah WHERE " + condition,
                    null
            )) {
                if (!cursor.moveToFirst()) {
                    return null;
                }
                count = cursor.getInt(0);
            }

            if (count == 0) {
                return null;
            }

            int day = getDayNumber();

            android.content.SharedPreferences prefs =
                    context.getApplicationContext()
                            .getSharedPreferences(PREFS, Context.MODE_PRIVATE);

            int advance = prefs.getInt(PREF_DAY, -1) == day
                    ? prefs.getInt(PREF_ADVANCE, 0)
                    : 0;

            int offset = Math.floorMod(day + advance, count);

            String sql =
                    "SELECT ayah.ayah_index, ayah.surah_id, ayah.ayah_num, " +
                            "ayah.text_tashkeel, ayah.content_bn, ayah.content_en, " +
                            "sura.name_simple " +
                            "FROM ayah " +
                            "LEFT JOIN sura ON sura.surah_id = ayah.surah_id " +
                            "WHERE " + condition + " " +
                            "ORDER BY RANDOM()  " +
                            "LIMIT 1";

            try (Cursor cursor = db.rawQuery(sql,null)) {
                if (!cursor.moveToFirst()) {
                    return null;
                }

                Ayah ayah = new Ayah();
                ayah.setAyah_index(cursor.getString(0));
                ayah.setSurah_id(cursor.getString(1));
                ayah.setAyah_num(cursor.getString(2));
                ayah.setText_tashkeel(cursor.getString(3));
                ayah.setContent_bn(cursor.getString(4));
                ayah.setContent_en(cursor.getString(5));
                ayah.setName_simple(cursor.getString(6));

                return ayah;
            }
        }catch (Exception e) {
            Log.e("WidgetAyahRepository", e.getMessage());
        }
        finally {
            db.close();
        }

        return null;
    }

    private static int getDayNumber() {
        Calendar today = Calendar.getInstance();
        int year = today.get(Calendar.YEAR);

        return (year - 1) * 365
                + (year - 1) / 4
                - (year - 1) / 100
                + (year - 1) / 400
                + today.get(Calendar.DAY_OF_YEAR);
    }

    public static void showNextAyah(Context context) {
        android.content.SharedPreferences prefs =
                context.getApplicationContext()
                        .getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        int day = getDayNumber();
        int currentAdvance = prefs.getInt(PREF_DAY, -1) == day
                ? prefs.getInt(PREF_ADVANCE, 0)
                : 0;

        prefs.edit()
                .putInt(PREF_DAY, day)
                .putInt(PREF_ADVANCE, currentAdvance + 1)
                .apply();
    }
}
