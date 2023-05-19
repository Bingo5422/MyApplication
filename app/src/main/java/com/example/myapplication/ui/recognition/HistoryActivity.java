package com.example.myapplication.ui.recognition;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.myapplication.Adapter.HistoryAdapter;
import com.example.myapplication.Bean.HistoryBean;
import com.example.myapplication.Dao.HistoryDao;
import com.example.myapplication.Dao.RecDataBase;
import com.example.myapplication.R;
import com.example.myapplication.Utils.VoiceUtil;

import java.util.ArrayList;
import java.util.List;


public class HistoryActivity extends AppCompatActivity {

    private static final String TAG = "HistoryActivity";


    private SlideRecyclerView list;
    private HistoryAdapter adapter;
    private RecDataBase recDataBase;
    private HistoryDao historyDao;
    private ImageView hisBack;
    private List<HistoryBean> dataList = new ArrayList<>();
    private static SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        hisBack=findViewById(R.id.his_back);

        sp = getSharedPreferences("sp", Context.MODE_PRIVATE);



        hisBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        list = findViewById(R.id.his_list);
        recDataBase = Room.databaseBuilder(this, RecDataBase.class, "RecDataBase").allowMainThreadQueries().build();
        historyDao = recDataBase.historyDao();



        //Set layout manager
        list.setLayoutManager(new LinearLayoutManager(this));
        //set dividing line
        DividerItemDecoration mDivider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        list.addItemDecoration(mDivider);

        //Set the adapter

        adapter = new HistoryAdapter();
        dataList.addAll(historyDao.query());
        adapter.setList(dataList);


        adapter.setDao(historyDao);
        list.setAdapter(adapter);


        adapter.setListener(new HistoryAdapter.Listener() {
            @Override
            public void onClickListener(HistoryBean bean) {
                if(bean!=null){
                    String lan = sp.getString("lan", "Chinese");
                    if (lan.equals("Spanish")) {
                        String spanish = bean.getSpaName();
                        VoiceUtil.voice(HistoryActivity.this, spanish,"x2_SpEs_Aurora");
                    } else if (lan.equals("Japanese")) {
                        String japanese = bean.getSpaName();
                        VoiceUtil.voice(HistoryActivity.this, japanese,"x2_JaJp_ZhongCun");
                    } else if (lan.equals("Korean")) {
                        String korean = bean.getKorName();
                        VoiceUtil.voice(HistoryActivity.this, korean,"zhimin");
                    } else if (lan.equals("French")) {
                        String french = bean.getFraName();
                        VoiceUtil.voice(HistoryActivity.this, french,"x2_FrRgM_Lisa");
                    } else {
                        String chinese = bean.getName();
                        VoiceUtil.voice(HistoryActivity.this, chinese,"aisxping");
                    }
                }

            }

            @Override
            public void onDelClickListener(HistoryBean bean, int index) {
                new AlertDialog.Builder(HistoryActivity.this).setMessage("Do Your Really Want To Delete This Item?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                historyDao.deleteById(bean.getId());
                                dataList.remove(index);
                                adapter.notifyDataSetChanged();
                            }
                        }).show();
            }
        });

    }


}