package com.example.myapplication.ui.me;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.example.myapplication.Adapter.CheckAdapter;
import com.example.myapplication.Adapter.HistoryAdapter;
import com.example.myapplication.Bean.CheckBean;
import com.example.myapplication.Bean.HistoryBean;
import com.example.myapplication.Dao.HistoryDao;
import com.example.myapplication.Dao.RecDataBase;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class ServerHistActivity extends AppCompatActivity implements CheckAdapter.CheckItemListener {

    private CheckAdapter mCheckAdapter;
    private RecyclerView check_rcy;

    //全选操作
    private CheckBox check_all_cb;

    //列表数据
    private List<CheckBean> dataArray, checkedList;

    //选中后的数据
    private boolean isSelectAll;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_hist);
        checkedList = new ArrayList<>();
        initData();
        initViews();
    }


    private void initViews(){
        //get the recycle view
        check_rcy = findViewById(R.id.check_rcy);
        //选择所有的checkbox
        check_all_cb = findViewById(R.id.check_all_cb);
        // 创建线性布局管理器
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        check_rcy.setLayoutManager(linearLayoutManager);
        // dataArray:所有数据
        mCheckAdapter = new CheckAdapter(this, dataArray, this);
        check_rcy.setAdapter(mCheckAdapter);

        // 如果全选
        check_all_cb.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                isSelectAll = !isSelectAll;
                checkedList.clear();
                if(isSelectAll){
                    // 全选了则所有list都加入被选择的list
                    checkedList.addAll(dataArray);
                }
                //给每一个具体的项目都设置为已选择
                for(CheckBean checkBean : dataArray){
                    checkBean.setChecked(isSelectAll);
                }
                mCheckAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initData(){
        dataArray = new ArrayList<>();
        

        // todo: 长度也需要自己设置一下
        for(int i=0; i<20; i++){
            CheckBean bean = new CheckBean();
            // todo: 回头根据服务器返回的名字做修改
            bean.setOrder(String.valueOf(i+1));
            bean.setName("name_"+i);
            bean.setContent("content"+i);
            //bean.setTime
            dataArray.add(bean);
        }
    }

    @Override
    public void itemChecked(CheckBean checkBean, boolean isChecked) {
        //处理Item点击选中回调事件
        if (isChecked) {
        //选中处理
            if (!checkedList.contains(checkBean)) {
                checkedList.add(checkBean);
            }
        } else {//未选中处理
            if (checkedList.contains(checkBean)) {
                checkedList.remove(checkBean);
            }
        }
        //判断列表数据是否全部选中
        if (checkedList.size() == dataArray.size()) {
            check_all_cb.setChecked(true);
        } else {
            check_all_cb.setChecked(false);
        }
    }
}

//    public void loadHistory(){
//        new Thread(){
//            @Override
//            public void run() {
//                super.run();
//                List<HistoryBean> l = historyDao.query();
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        adapter.setList(l);
//                    }
//                });
//            }
//        }.start();
//    }
