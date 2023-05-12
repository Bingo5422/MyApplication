package com.example.myapplication.Bean;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "challenge_records")
public class ChallengeBean  {
    @PrimaryKey(autoGenerate = false)
    @NonNull
    @ColumnInfo(name = "filename")
    private String filename;
    @ColumnInfo(name = "filepath")
    private String filepath;
    @ColumnInfo(name = "enName")
    private String enName;
    @ColumnInfo(name = "jpName")
    private String jpName;
    @ColumnInfo(name = "korName")
    private String korName;
    @ColumnInfo(name = "FraName")
    private String FraName;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    @ColumnInfo(name = "code")
    private String code;

    public ChallengeBean(String filename, String filepath,
                         String enName, String jpName, String korName,


                         String fraName, String code, String group, String spaName, String name) {
        this.filename = filename;
        this.filepath = filepath;
        this.enName = enName;
        this.jpName = jpName;
        this.korName = korName;
        FraName = fraName;
        this.code = code;
        this.group = group;
        this.spaName = spaName;
        this.name = name;
    }

    public ChallengeBean() {
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @ColumnInfo(name = "group")
    private String group;
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
