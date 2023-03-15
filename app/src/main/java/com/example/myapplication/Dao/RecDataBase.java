package com.example.myapplication.Dao;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.myapplication.Bean.RecognitionBean;

//RecognitionBean这张表
@Database(entities = {RecognitionBean.class},version = 1)
public abstract class RecDataBase extends RoomDatabase {

    public abstract RecognitionDao recognitionDao();

}
