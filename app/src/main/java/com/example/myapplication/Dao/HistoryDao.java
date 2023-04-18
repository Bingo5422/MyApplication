package com.example.myapplication.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.myapplication.Bean.HistoryBean;
import com.example.myapplication.Bean.RecognitionBean;

import java.util.List;

@Dao
public interface HistoryDao {
    @Query("SELECT * FROM HistoryBean")
    List<HistoryBean> query();

    @Query("SELECT * FROM HistoryBean where num <3")
    List<HistoryBean> queryNumLow3();

    @Query("SELECT * FROM HistoryBean where if_star=1")
    List<HistoryBean> queryCollect();

    @Query("SELECT * FROM HistoryBean where num >=3")
    List<HistoryBean> queryNumUp3();

    @Query("SELECT * FROM HistoryBean where name!=:name  ORDER BY RANDOM() LIMIT 3")
    List<HistoryBean> queryRand3(String name);

    @Query("SELECT * FROM HistoryBean where filename=:filename")
    List<HistoryBean> queryByFilename(String filename);

    @Insert
    void insertHistory(HistoryBean bean);

    @Query("UPDATE HistoryBean set if_star=0 WHERE if_star=1")
    void clearAllStars();

    @Query("UPDATE HistoryBean set num=:newNum WHERE id=:id ")
    void updateNum(int id, int newNum);

    @Query("UPDATE HistoryBean set if_star=:if_star WHERE id=:id ")
    void updateStar(int id, int if_star);

    @Query("UPDATE HistoryBean set if_star=:if_star WHERE filename=:filename")
    void updateStar_byFilename(int if_star, String filename);

    @Query("DELETE from HistoryBean WHERE id=:id")
    void deleteById(int id);

    @Query("SELECT count(*) FROM HistoryBean where addDate!=:today")
    int countToDayNewWord(String today);
}
