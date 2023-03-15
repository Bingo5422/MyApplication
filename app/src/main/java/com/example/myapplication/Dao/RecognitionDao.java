package com.example.myapplication.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.myapplication.Bean.RecognitionBean;

import java.util.List;

@Dao
public interface RecognitionDao {

    @Query("SELECT * FROM RecognitionBean WHERE code =:code limit 1")
    RecognitionBean query(String code);

    @Insert
    void insert(RecognitionBean bean);



}
