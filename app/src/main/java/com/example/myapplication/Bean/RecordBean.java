package com.example.myapplication.Bean;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class RecordBean implements Serializable {
    public RecordBean() {
    }

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String addDate;//
    private String addTime;
    private int addUserId = 0;
    private int type = 0;
    private int hisId;


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getHisId() {
        return hisId;
    }

    public void setHisId(int hisId) {
        this.hisId = hisId;
    }

    @Override
    public String toString() {
        return "RecordBean{" +
                "id=" + id +
                ", addDate='" + addDate + '\'' +
                ", addTime='" + addTime + '\'' +
                ", addUserId=" + addUserId +
                ", type=" + type +
                ", hisId=" + hisId +
                '}';
    }
}

