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
    private String comName;
    private String enName;
    private String jpName;
    private String spaName;
    private String name;
    private int num;
    private String dateTime;
    private String fileName;
    private int if_familiar;
    private int if_star;
    private String newName;
    private String addDate;
    private String addTime;
    private int addUserId = 0;

    private String korName;
    private String FraName;

    public String getKorName() {
        return korName;
    }

    public void setKorName(String korName) {
        this.korName = korName;
    }

    public void setFraName(String fraName) {
        FraName = fraName;
    }
    public String getFraName() {
        return FraName;
    }

    public String getAddDate() {
        return addDate;
    }

    public void setAddDate(String addDate) {
        this.addDate = addDate;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public int getAddUserId() {
        return addUserId;
    }

    public void setAddUserId(int addUserId) {
        this.addUserId = addUserId;
    }

    public String getComName() {
        return comName;
    }

    public void setComName(String comName) {
        this.comName = comName;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getJpName() {
        return jpName;
    }

    public void setJpName(String jpName) {
        this.jpName = jpName;
    }

    public String getSpaName() {
        return spaName;
    }

    public void setSpaName(String spaName) {
        this.spaName = spaName;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

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
                ", name='" + name + '\'' +
                ", num='" + num + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}

