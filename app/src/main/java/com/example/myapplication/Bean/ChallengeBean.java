package com.example.myapplication.Bean;

import android.graphics.Bitmap;

import java.io.Serializable;

public class ChallengeBean implements Serializable {

    private String filename;
    private String filepath;
    private String enName;
    private String jpName;
    private String korName;
    private String FraName;

    @Override
    public String toString() {
        return "ChallengeBean{" +
                "filename='" + filename + '\'' +
                ", filepath='" + filepath + '\'' +
                ", enName='" + enName + '\'' +
                ", jpName='" + jpName + '\'' +
                ", korName='" + korName + '\'' +
                ", FraName='" + FraName + '\'' +
                ", spaName='" + spaName + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String spaName;
    private String name;

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



    public String getFilename() {
        return filename;
    }

    public String getFilepath() {
        return filepath;
    }



    public String getEnName() {
        return enName;
    }






    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }


    public void setEnName(String enName) {
        this.enName = enName;
    }

}
