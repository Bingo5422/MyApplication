package com.example.myapplication.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
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

//
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void addFriend(FriendsBean friend);
//
        @Query("SELECT * FROM FriendsBean WHERE id = :Id")
        FriendsBean getId(int Id);
    @Query("SELECT COUNT(*) FROM FriendsBean")
    int getFriendCount();


    @Insert
    void insert(FriendsBean bean);

    @Query("DELETE FROM FriendsBean")
    void clearTable();


}
