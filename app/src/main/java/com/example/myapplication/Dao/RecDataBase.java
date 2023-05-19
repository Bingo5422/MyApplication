package com.example.myapplication.Dao;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.myapplication.Bean.FriendsBean;
import com.example.myapplication.Bean.HistoryBean;
import com.example.myapplication.Bean.RecognitionBean;
import com.example.myapplication.Bean.RecordBean;

//RecognitionBean这张表和HistoryBean表


@Database(entities = {RecognitionBean.class, HistoryBean.class, FriendsBean.class, RecordBean.class},version = 1)
public abstract class RecDataBase extends RoomDatabase {

    public abstract RecognitionDao recognitionDao();

    public abstract HistoryDao historyDao();
    public abstract RecordDao recordDao();
    public abstract  FriendsDao friendsDao();

}
