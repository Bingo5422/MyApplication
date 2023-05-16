package com.example.myapplication.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.myapplication.Bean.ChallengeBean;
import com.example.myapplication.Bean.HistoryBean;
import com.example.myapplication.Bean.MessageBean;

import java.util.List;

@Dao
public interface ChallengeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(ChallengeBean challengeBean);

   @Query("SELECT * FROM challenge_records WHERE groupNum =:group" )
   List<ChallengeBean> queryByGroup(String group);


    @Query("SELECT * FROM challenge_records where name!=:name  ORDER BY RANDOM() LIMIT 3")
    List<ChallengeBean> queryRand3(String name);

    //"from_user = :senderId AND to_user = :receiverId OR from_user = :receiverId AND to_user = :senderId ORDER BY send_time ASC")

}