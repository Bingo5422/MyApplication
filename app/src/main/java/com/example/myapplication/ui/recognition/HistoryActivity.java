package com.example.myapplication.ui.recognition;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.os.Bundle;
import android.util.Log;

import com.example.myapplication.Adapter.HistoryAdapter;
import com.example.myapplication.Bean.HistoryBean;
import com.example.myapplication.Dao.HistoryDao;
import com.example.myapplication.Dao.RecDataBase;
import com.example.myapplication.R;

import java.util.List;


public class HistoryActivity extends AppCompatActivity {

    private static final String TAG = "HistoryActivity";


    private RecyclerView list;
    private HistoryAdapter adapter;
    private RecDataBase recDataBase;
    private HistoryDao historyDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        list=findViewById(R.id.his_list);
        recDataBase = Room.databaseBuilder(this, RecDataBase.class, "RecDataBase").build();
        historyDao = recDataBase.historyDao();


        //设置布局管理器
        list.setLayoutManager(new LinearLayoutManager(this));
        //设置分割线
        DividerItemDecoration mDivider = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        list.addItemDecoration(mDivider);


        //设置适配器
        adapter = new HistoryAdapter();
        list.setAdapter(adapter);

        loadHistory();



    }

    public void loadHistory(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                List<HistoryBean> l=historyDao.query();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setList(l);
                    }
                });
            }
        }.start();
    }

}