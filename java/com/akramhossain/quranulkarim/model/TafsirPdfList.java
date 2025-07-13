package com.akramhossain.quranulkarim.model;

public class TafsirPdfList {

    private String tafsir_pdf_list_id;

    private String name_bangla;

    private String name_english;

    private String file_name;

    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getName_english() {
        return name_english;
    }

    public void setName_english(String name_english) {
        this.name_english = name_english;
    }

    public String getName_bangla() {
        return name_bangla;
    }

    public void setName_bangla(String name_bangla) {
        this.name_bangla = name_bangla;
    }

    public String getTafsir_pdf_list_id() {
        return tafsir_pdf_list_id;
    }

    public void setTafsir_pdf_list_id(String tafsir_pdf_list_id) {
        this.tafsir_pdf_list_id = tafsir_pdf_list_id;
    }
}
