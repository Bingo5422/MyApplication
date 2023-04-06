package com.example.myapplication.Dao;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.myapplication.Bean.FriendsBean;
import com.example.myapplication.Bean.HistoryBean;
import com.example.myapplication.Bean.RecognitionBean;

//RecognitionBean这张表和HistoryBean表


@Database(entities = {RecognitionBean.class, HistoryBean.class, FriendsBean.class},version = 1)

public abstract class RecDataBase extends RoomDatabase {

    public abstract RecognitionDao recognitionDao();

    public abstract HistoryDao historyDao();

    public abstract  FriendsDao friendsDao();

}
