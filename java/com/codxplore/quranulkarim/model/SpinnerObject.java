package com.codxplore.quranulkarim.model;

public class SpinnerObject {

    private  int suraId;
    private String suraName;

    public SpinnerObject ( int suraId , String suraName ) {
        this.suraId = suraId;
        this.suraName = suraName;
    }

    public int getId () {
        return suraId;
    }

    public String getValue () {
        return suraName;
    }

    @Override
    public String toString () {
        return suraName;
    }
}
