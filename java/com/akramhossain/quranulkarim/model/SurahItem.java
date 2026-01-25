package com.akramhossain.quranulkarim.model;

public class SurahItem {

    public final int sid;
    public final String name;

    public SurahItem(int sid, String name) {
        this.sid = sid;
        this.name = name;
    }

    @Override public String toString() {
        return name; // what dropdown displays
    }

}
