package com.akramhossain.quranulkarim.model;

public class SurahItem {

    public final int sid;
    public final String name;
    public final String ayat;

    public SurahItem(int sid, String name, String ayat) {
        this.sid = sid;
        this.name = name;
        this.ayat = ayat;
    }

    @Override public String toString() {
        return name; // what dropdown displays
    }

}
