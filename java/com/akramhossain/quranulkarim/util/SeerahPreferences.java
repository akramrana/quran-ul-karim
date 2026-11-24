package com.akramhossain.quranulkarim.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SeerahPreferences {

    private final SharedPreferences prefs;

    public SeerahPreferences(Context context) {
        prefs = context.getSharedPreferences(
                "seerah_preferences",
                Context.MODE_PRIVATE
        );
    }

    public String getLanguage() {
        return prefs.getString("language", "bn");
    }

    public void setLanguage(String language) {
        prefs.edit().putString("language", language).apply();
    }

    public float getFontSize() {
        return prefs.getFloat("font_size", 19f);
    }

    public void setFontSize(float size) {
        prefs.edit().putFloat("font_size", size).apply();
    }

    public void saveLastRead(String section,
                             String chapterId,
                             String titleEn,
                             String titleBn) {

        prefs.edit()
                .putString("last_section", section)
                .putString("last_chapter_id", chapterId)
                .putString("last_title_en", titleEn)
                .putString("last_title_bn", titleBn)
                .apply();
    }

    public String getLastSection() {
        return prefs.getString("last_section", "");
    }

    public String getLastChapterId() {
        return prefs.getString("last_chapter_id", "");
    }

    public String getLastTitleEn() {
        return prefs.getString("last_title_en", "");
    }

    public String getLastTitleBn() {
        return prefs.getString("last_title_bn", "");
    }

    public boolean hasLastRead() {
        return !getLastSection().isEmpty()
                && !getLastChapterId().isEmpty();
    }
}
