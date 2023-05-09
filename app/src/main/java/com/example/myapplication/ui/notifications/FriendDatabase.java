package com.example.myapplication.ui.notifications;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.myapplication.Bean.FriendsBean;
import com.example.myapplication.Dao.FriendsDao;

@Database(entities = {FriendsBean.class}, version = 1, exportSchema = false)
public abstract class FriendDatabase extends RoomDatabase {
    private static FriendDatabase instance;

    public abstract FriendsDao friendDao();

    public static synchronized FriendDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            FriendDatabase.class, "friend_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
