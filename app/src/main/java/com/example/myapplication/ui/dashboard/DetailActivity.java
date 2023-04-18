package com.example.myapplication.ui.dashboard;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.myapplication.Dao.HistoryDao;
import com.example.myapplication.Dao.RecDataBase;
import com.example.myapplication.Dao.RecordDao;
import com.example.myapplication.databinding.ActivityDetailBinding;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class DetailActivity extends AppCompatActivity {


    private RecDataBase recDataBase;
    private HistoryDao historyDao;
    private RecordDao recordDao;
    private ActivityDetailBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recDataBase = Room.databaseBuilder(this, RecDataBase.class, "RecDataBase").allowMainThreadQueries().build();
        historyDao = recDataBase.historyDao();
        recordDao = recDataBase.recordDao();

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);

        int newWord = historyDao.countToDayNewWord(year + "-" + month + "-" + day);
        binding.tvTodayNewword.setText("" + newWord);

        int vocWord = recordDao.countTodayAddToVocabularyNotebookWords(year + "-" + month + "-" + day);
        binding.tvTodayWords.setText("" + vocWord);

        int tranNum = recordDao.countTranTimes(year + "-" + month + "-" + day);
        binding.tvTodayNum.setText("" + tranNum);


        int size = historyDao.queryNumLow3().size();
        binding.tvTodayNum2.setText("" + size);

        int days = recordDao.countLoginDays();
        binding.tvCountDay.setText("" + days);

        SharedPreferences sp = getSharedPreferences("sp", MODE_PRIVATE);
        long startTime = sp.getLong("startTime", 0);
        long endTime = sp.getLong("endTime", 0);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(endTime-startTime);
        binding.tvTodayTime.setText("" + minutes);
    }
}
