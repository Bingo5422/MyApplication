package com.example.myapplication.Bean;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class CalendarBean implements Serializable {


    public CalendarBean() {

    }
    @PrimaryKey(autoGenerate = true)
    private int date;
    private boolean isToday;
    private boolean isStudy;
    private int currentMonth;

    public int getCurrentMonth() {
        return currentMonth;
    }

    public void setCurrentMonth(int currentMonth) {
        this.currentMonth = currentMonth;
    }

    public CalendarBean(int date, boolean isToday, boolean isStudy) {

    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public boolean isToday() {
        return isToday;
    }

    public void setToday(boolean today) {
        isToday = today;
    }

    public boolean isStudy() {
        return isStudy;
    }

    public void setStudy(boolean study) {
        isStudy = study;
    }


}

