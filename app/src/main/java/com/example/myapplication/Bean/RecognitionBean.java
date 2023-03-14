package com.example.myapplication.Bean;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class RecognitionBean {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String code;
    private String enName;
    private String name;
    private String cate;



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

    public String getCate() {
        return cate;
    }

    public void setCate(String cate) {
        this.cate = cate;
    }


    @Override
    public String toString() {
        return "RecognitionBean{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", enName='" + enName + '\'' +
                ", name='" + name + '\'' +
                ", cate='" + cate + '\'' +
                '}';
    }
}

