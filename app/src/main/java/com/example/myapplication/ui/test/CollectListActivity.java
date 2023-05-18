package com.example.myapplication.ui.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;

import com.example.myapplication.Adapter.WordAdapter;
import com.example.myapplication.Bean.HistoryBean;
import com.example.myapplication.Dao.HistoryDao;
import com.example.myapplication.Dao.RecDataBase;
import com.example.myapplication.MainActivity;
import com.example.myapplication.databinding.ActivityWordBinding;

import java.util.ArrayList;
import java.util.List;

public class CollectListActivity extends AppCompatActivity {
    public static final int MAX_COUNT = 50;
    ActivityWordBinding binding;
    WordAdapter adapter;
    List<HistoryBean> dataList = new ArrayList<>();
    private RecDataBase recDataBase;
    private HistoryDao historyDao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dataList.isEmpty()){
                    Toast.makeText(CollectListActivity.this, "You need to add words", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(v.getContext(), WordDetailActivity.class);
                intent.putExtra("word", dataList.get(0));
                intent.putExtra("form", 3);
                intent.putExtra("index", 0);
                v.getContext().startActivity(intent);
            }
        });

        adapter = new WordAdapter();
        binding.rv.setLayoutManager(new LinearLayoutManager(this));
        binding.rv.setAdapter(adapter);

        recDataBase = Room.databaseBuilder(this, RecDataBase.class, "RecDataBase").allowMainThreadQueries().build();
        historyDao = recDataBase.historyDao();
        adapter.setDao(historyDao);
        adapter.setForm(3);
        dataList.addAll(historyDao.queryCollect());
        adapter.setList(dataList);
        binding.tvCount.setText(dataList.size() + "/"+ MainActivity.MAX_COUNT);
        adapter.notifyDataSetChanged();

        binding.wordListBack.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
