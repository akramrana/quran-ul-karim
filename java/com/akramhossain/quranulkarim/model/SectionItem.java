package com.akramhossain.quranulkarim.model;

public class SectionItem {
    public enum Type { HEADER, RECITER }

    public final Type type;
    public final Character header; // only when type == HEADER
    public final Reciter reciter;  // only when type == RECITER

    private SectionItem(Type type, Character header, Reciter reciter) {
        this.type = type;
        this.header = header;
        this.reciter = reciter;
    }

    public static SectionItem header(char c) { return new SectionItem(Type.HEADER, c, null); }
    public static SectionItem reciter(Reciter r) { return new SectionItem(Type.RECITER, null, r); }
}

