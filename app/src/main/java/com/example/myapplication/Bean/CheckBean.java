package com.example.myapplication.Bean;

import android.graphics.Bitmap;

import java.io.Serializable;

public class CheckBean implements Serializable {

    private int id;
    private String user_id;

    private Bitmap pic; // 网络图片预览
    private String filename;
    private String filepath;
    private String code;
    private String enName;
    private String jpName;

    public String getJpName() {
        return jpName;
    }

    public void setJpName(String jpName) {
        this.jpName = jpName;
    }

    public String getKorName() {
        return korName;
    }

    public void setKorName(String korName) {
        this.korName = korName;
    }

    public String getFraName() {
        return FraName;
    }

    public void setFraName(String fraName) {
        FraName = fraName;
    }

    public String getSpaName() {
        return spaName;
    }

    public void setSpaName(String spaName) {
        this.spaName = spaName;
    }

    private String korName;
    private String FraName;
    private String spaName;
    private String name;

    private String datetime;
    private int proficiency;
    private boolean isChecked;

    public Bitmap getPic() {
        return pic;
    }

    public int getId() {
        return id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getFilename() {
        return filename;
    }

    public String getFilepath() {
        return filepath;
    }

    public String getCode() {
        return code;
    }

    public String getEnName() {
        return enName;
    }

    public String getName() {
        return name;
    }

    public String getDatetime() {
        return datetime;
    }

    public int getProficiency() {
        return proficiency;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPic(Bitmap pic) {
        this.pic = pic;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public void setProficiency(int proficiency) {
        this.proficiency = proficiency;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isChecked() {
        return isChecked;
    }
}
