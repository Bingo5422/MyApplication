package com.example.myapplication.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import com.example.myapplication.Bean.MessageBean;

import java.util.List;
@Dao
public interface MessageBeanDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(MessageBean message);

    @Query("SELECT * FROM chat_records WHERE from_user = :senderId AND to_user = :receiverId OR from_user = :receiverId AND to_user = :senderId ORDER BY send_time ASC")
    List<MessageBean> getMessages(String senderId, String receiverId);
}

