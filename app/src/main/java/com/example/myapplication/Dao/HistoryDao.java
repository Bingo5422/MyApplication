package com.example.myapplication.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.myapplication.Bean.HistoryBean;
import com.example.myapplication.Bean.RecognitionBean;

import java.util.List;

@Dao
public interface HistoryDao {

    @Query("SELECT * FROM HistoryBean")
    List<HistoryBean> query();
    @Query("SELECT * FROM HistoryBean ORDER BY RANDOM() LIMIT 3")
    List<HistoryBean> queryRand3();

    @Insert
    void insertHistory(HistoryBean bean);


}
