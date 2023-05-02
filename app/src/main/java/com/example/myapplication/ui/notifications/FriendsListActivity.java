package com.example.myapplication.ui.notifications;



import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.myapplication.Adapter.FriendsAdapter;
import com.example.myapplication.Adapter.HistoryAdapter;
import com.example.myapplication.Bean.FriendsBean;
import com.example.myapplication.Bean.HistoryBean;
import com.example.myapplication.Dao.FriendsDao;
import com.example.myapplication.Dao.HistoryDao;
import com.example.myapplication.Dao.RecDataBase;
import com.example.myapplication.R;

import java.util.List;
import java.util.Random;


public class FriendsListActivity extends AppCompatActivity {

    private static final String TAG = "FriendsActivity";


    private RecyclerView list;
    private FriendsAdapter adapter;
    private RecDataBase recDataBase;
    private List<FriendsBean> l;
    private FriendsDao friendsDao;
    private FriendsBean friendsBean;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);


        list=findViewById(R.id.fri_list);
        recDataBase = Room.databaseBuilder(this, RecDataBase.class, "RecDataBase").build();
        friendsDao = recDataBase.friendsDao();
        friendsBean = new FriendsBean();
//        insert();



        //设置布局管理器
        list.setLayoutManager(new LinearLayoutManager(this));
        //设置分割线
        DividerItemDecoration mDivider = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        list.addItemDecoration(mDivider);


        //设置适配器

//        adapter = new FriendsAdapter(l, new FriendsAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(FriendsBean friend) {
//                // 点击某个好友后进入聊天页面
//                Intent intent = new Intent(FriendsListActivity.this, ChatActivity.class);
//                intent.putExtra("friendId", friendsBean.getId());
//                startActivity(intent);
//            }
//        });
//        list.setAdapter(adapter);


        loadHistory();



    }
//    public void insert(){
//        new Thread(){
//            @Override
//            public void run() {
//                super.run();
//                Random r = new Random();
//                friendsBean.setId(r.nextInt(1000000));
//                friendsBean.setName("Tony");
//                friendsBean.setPath("");
//                friendsDao.insert(friendsBean);
//            }
//        }.start();
//
//    }




    public void loadHistory(){
        new Thread(){
            @Override
            public void run() {
                super.run();
              l= friendsDao.query();
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