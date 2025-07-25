package com.akramhossain.quranulkarim.model;

public class TafsirBook {

    private String tafsir_book_id;

    private String name_bangla;

    private String name_english;

    private String name_arabic;

    private String thumb;

    private String pdf_list_url;

    public String getTafsir_book_id() {
        return tafsir_book_id;
    }

    public void setTafsir_book_id(String tafsir_book_id) {
        this.tafsir_book_id = tafsir_book_id;
    }

    public String getName_bangla() {
        return name_bangla;
    }

    public void setName_bangla(String name_bangla) {
        this.name_bangla = name_bangla;
    }

    public String getName_english() {
        return name_english;
    }

    public void setName_english(String name_english) {
        this.name_english = name_english;
    }

    public String getName_arabic() {
        return name_arabic;
    }

    public void setName_arabic(String name_arabic) {
        this.name_arabic = name_arabic;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getPdf_list_url() {
        return pdf_list_url;
    }

    public void setPdf_list_url(String pdf_list_url) {
        this.pdf_list_url = pdf_list_url;
    }
}
