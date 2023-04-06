package com.example.myapplication.Bean;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class HistoryBean {
    @PrimaryKey(autoGenerate = true)


    private int id;
    private String path;
    private String code;
    private String enName;
    private String name;

    private String dateTime;
    private String fileName;

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath(){
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

    public String getDateTime(){
        return dateTime;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName(){
        return fileName;
    }


    @Override
    public String toString() {
        return "HistoryBean{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", enName='" + enName + '\'' +
                ", name='" + name + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}

