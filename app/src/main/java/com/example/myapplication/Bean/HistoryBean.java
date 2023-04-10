package com.example.myapplication.Bean;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class HistoryBean implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String path;
    private String code;
    private String enName;
    private String name;
    private int num;
    private String dateTime;
    private String fileName;
    private int if_familiar;
    private int if_star;

    public int getIf_familiar() {
        return if_familiar;
    }

    public void setIf_familiar(int if_familiar) {
        this.if_familiar = if_familiar;
    }

    public int getIf_star() {
        return if_star;
    }

    public void setIf_star(int if_star) {
        this.if_star = if_star;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }


    @Override
    public String toString() {
        return "HistoryBean{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", enName='" + enName + '\'' +
                ", name='" + name + '\'' +
                ", num='" + num + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}

