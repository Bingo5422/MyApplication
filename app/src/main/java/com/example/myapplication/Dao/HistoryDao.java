package com.example.myapplication.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.myapplication.Bean.HistoryBean;
import com.example.myapplication.Bean.RecognitionBean;

@Dao
public interface HistoryDao {

//    @Query("SELECT * FROM RecognitionBean WHERE code =:code limit 1")
//    RecognitionBean query(String code);
//
//    @Insert
//    void insert(RecognitionBean bean);

    @Insert
    void insertHistory(HistoryBean bean);




}
