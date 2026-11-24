package com.akramhossain.quranulkarim.model;

import androidx.lifecycle.ViewModel;
import org.json.JSONArray;

public class SeerahViewModel extends ViewModel {

    private JSONArray chapters;

    public void setChapters(JSONArray chapters) {
        this.chapters = chapters;
    }

    public JSONArray getChapters() {
        return chapters;
    }
}