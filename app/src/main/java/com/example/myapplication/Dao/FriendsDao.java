package com.example.myapplication.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.myapplication.Bean.FriendsBean;
import com.example.myapplication.Bean.HistoryBean;
import com.example.myapplication.Bean.RecognitionBean;

import java.util.List;

@Dao
public interface FriendsDao {

    @Query("SELECT * FROM FriendsBean")
    List<FriendsBean> query();
    // @Query("insert into FriendsBean (id,name) values(001,tony)")

    @Insert
    void insert(FriendsBean bean);


}
