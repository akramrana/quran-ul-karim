package com.akramhossain.quranulkarim.model;

public class Reciter {
    public String name;
    public String arabic_name;     // nullable
    public String relative_path;   // e.g. "abdullaah_..."

    public Reciter(String name, String arabic_name, String relative_path) {
        this.name = name;
        this.arabic_name = arabic_name;
        this.relative_path = relative_path;
    }
}
