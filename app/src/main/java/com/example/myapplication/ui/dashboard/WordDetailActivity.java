package com.example.myapplication.ui.dashboard;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.myapplication.Bean.HistoryBean;
import com.example.myapplication.Dao.HistoryDao;
import com.example.myapplication.Dao.RecDataBase;
import com.example.myapplication.databinding.ActivityWordDetailBinding;

public class WordDetailActivity extends AppCompatActivity {

    ActivityWordDetailBinding binding;
    private RecDataBase recDataBase;
    private HistoryDao historyDao;
    private HistoryBean historyBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWordDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        historyBean = (HistoryBean) getIntent().getSerializableExtra("word");
        binding.tvWord.setText(historyBean.getName());
        binding.tvWord2.setText(historyBean.getEnName());
        binding.btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.tvWord2.getVisibility() == View.VISIBLE) {
                    binding.tvWord2.setVisibility(View.GONE);
                    binding.btnShow.setText("SHOW PARAPHRASE");
                } else {
                    binding.tvWord2.setVisibility(View.VISIBLE);
                    binding.btnShow.setText("HIDDEN PARAPHRASE");
                }
            }
        });
        recDataBase = Room.databaseBuilder(this, RecDataBase.class, "RecDataBase").allowMainThreadQueries().build();
        historyDao = recDataBase.historyDao();

        binding.btnNewWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                historyDao.updateNum(historyBean.getId(), 0);
                Toast.makeText(WordDetailActivity.this, "Add To New Words NoteBook Success", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnVocabulary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                historyDao.updateNum(historyBean.getId(), 3);
                Toast.makeText(WordDetailActivity.this, "Add To Vocabulary NoteBook Success", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
