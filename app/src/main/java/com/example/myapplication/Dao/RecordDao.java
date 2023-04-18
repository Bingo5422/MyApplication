package com.example.myapplication.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.myapplication.Bean.RecordBean;

@Dao
public interface RecordDao {

    @Insert
    long addToRecord(RecordBean bean);

    @Query("SELECT count(*) FROM RecordBean where addDate=:today and type=0")
    int countTodayAddToVocabularyNotebookWords(String today);

    @Query("SELECT count(*) FROM RecordBean where addDate=:today and type=1")
    int countTranTimes(String today);

    @Query("SELECT DISTINCT addDate FROM RecordBean")
    int countLoginDays();

}
