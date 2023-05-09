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
    @Insert
    void insert(MessageBean message);

    @Query("SELECT * FROM chat_records WHERE from_user = :senderId AND to_user = :receiverId ORDER BY send_time ASC")
    List<MessageBean> getMessages(String senderId, String receiverId);
}

