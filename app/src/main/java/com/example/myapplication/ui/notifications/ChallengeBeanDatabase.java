package com.example.myapplication.ui.notifications;



import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.myapplication.Bean.ChallengeBean;
import com.example.myapplication.Bean.MessageBean;
import com.example.myapplication.Dao.ChallengeDao;
import com.example.myapplication.Dao.MessageBeanDao;

@Database(entities = {ChallengeBean.class}, version = 1, exportSchema = false)
public abstract class ChallengeBeanDatabase extends RoomDatabase {

    private static volatile ChallengeBeanDatabase instance;

    public static synchronized ChallengeBeanDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            ChallengeBeanDatabase.class, "challenge_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    public abstract ChallengeDao challengeDao();
}
