package com.akramhossain.quranulkarim.model;

public class Reciter {
    public String name;
    public String arabic_name;     // nullable
    public String relative_path;   // e.g. "abdullaah_..."
    public String id;

    public Reciter(String name, String arabic_name, String relative_path, String id) {
        this.name = name;
        this.arabic_name = arabic_name;
        this.relative_path = relative_path;
        this.id = id;
    }
}
