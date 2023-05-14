package com.example.myapplication.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.myapplication.Bean.ChallengeBean;
import com.example.myapplication.Bean.MessageBean;

import java.util.List;

@Dao
public interface ChallengeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(ChallengeBean challengeBean);

//    @Query("SELECT * FROM challenge_records WHERE group = :group" )
//            //"from_user = :senderId AND to_user = :receiverId OR from_user = :receiverId AND to_user = :senderId ORDER BY send_time ASC")
//    List<ChallengeBean> getMchallenge(String group);
}