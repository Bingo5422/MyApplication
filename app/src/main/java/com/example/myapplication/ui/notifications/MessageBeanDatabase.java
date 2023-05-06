package com.example.myapplication.ui.notifications;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.myapplication.Bean.MessageBean;
import com.example.myapplication.Dao.MessageBeanDao;

@Database(entities = {MessageBean.class}, version = 1, exportSchema = false)
public abstract class MessageBeanDatabase extends RoomDatabase {

    private static volatile MessageBeanDatabase instance;

    public static synchronized MessageBeanDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            MessageBeanDatabase.class, "message_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    public abstract MessageBeanDao messageBeanDao();
}
