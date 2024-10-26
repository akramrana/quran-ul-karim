package com.akramhossain.quranulkarim.model;

public class Leaderboard {

    private String user_id;
    private String name;
    private String country_id;
    private String iso;
    private String iso3;
    private String right_ans;
    private String wrong_ans;
    private String total_ans;
    private String flag;
    private String rank;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry_id() {
        return country_id;
    }

    public void setCountry_id(String country_id) {
        this.country_id = country_id;
    }

    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = iso;
    }

    public String getIso3() {
        return iso3;
    }

    public void setIso3(String iso3) {
        this.iso3 = iso3;
    }

    public String getRight_ans() {
        return right_ans;
    }

    public void setRight_ans(String right_ans) {
        this.right_ans = right_ans;
    }

    public String getWrong_ans() {
        return wrong_ans;
    }

    public void setWrong_ans(String wrong_ans) {
        this.wrong_ans = wrong_ans;
    }

    public String getTotal_ans() {
        return total_ans;
    }

    public void setTotal_ans(String total_ans) {
        this.total_ans = total_ans;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }
}
