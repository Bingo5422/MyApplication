//package com.example.myapplication.ui.notifications;
//
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.ContentView;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.lifecycle.ViewModelProvider;
//import androidx.recyclerview.widget.DividerItemDecoration;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import androidx.room.Room;
//import androidx.fragment.app.FragmentActivity;
//
//import com.example.myapplication.Adapter.FriendsAdapter;
//import com.example.myapplication.Adapter.HistoryAdapter;
//import com.example.myapplication.Bean.FriendsBean;
//import com.example.myapplication.Dao.FriendsDao;
//import com.example.myapplication.Dao.RecDataBase;
//import com.example.myapplication.R;
//import com.example.myapplication.Utils.CookieJarImpl;
//import com.example.myapplication.databinding.FragmentMeBinding;
//import com.example.myapplication.databinding.FragmentNotificationsBinding;
//import com.example.myapplication.ui.recognition.HistoryActivity;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Random;
//
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;
//
//public class NotificationsFragment extends Fragment  {
//
//    private FragmentNotificationsBinding binding;
//    private FriendsBean friendsBean;
//    private RecDataBase recDataBase;
//    private FriendsDao friendsDao;
//    private RecyclerView list;
//    private FriendsAdapter adapter;
//    private View contentView;
//    private ImageView add_fir;
//    private static final String TAG = "N0";
//
//
//
//
//
////    @SuppressLint("ResourceType")
////    public void onCreate(@Nullable Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////
//////        Intent intent = new Intent(getContext(), FriendsListActivity.class);
//////        startActivity(intent);
////
////    }
//
//
//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             ViewGroup container, Bundle savedInstanceState) {
//        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
//        View root = binding.getRoot();
////        contentView = inflater.inflate(R.layout.activity_friends_list, container, false);
//
////        NotificationsViewModel notificationsViewModel =
////                new ViewModelProvider(this).get(NotificationsViewModel.class);
//
////    binding = FragmentNotificationsBinding.inflate(inflater, container, false);
////    View root = binding.getRoot();
//        list= root.findViewById(R.id.fri_list1);
//        recDataBase = Room.databaseBuilder(getContext(), RecDataBase.class, "RecDataBase").allowMainThreadQueries().build();
//        friendsDao = recDataBase.friendsDao();
//        friendsBean = new FriendsBean();
//
////        //先放几个数据
////        FriendsBean f1 = new FriendsBean();
////        f1.setName("Nick");
////        f1.setEmail("123456@qq.com");
////        FriendsBean f2 = new FriendsBean();
////        f2.setName("Tom");
//////        f2.setEmail("12345678@qq.com");
////        friendsDao.insert(f1);
////        friendsDao.insert(f2);
//
//        // ImageView add_fir = root.findViewById(R.id.add_fir1);
//        add_fir=binding.addFir1;
//        add_fir.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), AddFriendActivity.class);
//                intent.putExtra("friendId", friendsBean.getId());
//                startActivity(intent);
//            }
//        });
////        insert();
////        l.addAll(friendsDao.query());
////        adapter.setList(l);
////        adapter.setDao(friendsDao);
//
//
//
//        //设置布局管理器
//        list.setLayoutManager(new LinearLayoutManager(this.getContext()));
//        //设置分割线
//        DividerItemDecoration mDivider = new DividerItemDecoration(this.getContext(),DividerItemDecoration.VERTICAL);
//        list.addItemDecoration(mDivider);
//        //设置适配器
//
//        adapter = new FriendsAdapter();
//        list.setAdapter(adapter);
//
//
//
//        //设置适配器
////        adapter = new FriendsAdapter(l, new FriendsAdapter.OnItemClickListener() {
////            @Override
////            public void onItemClick(FriendsBean friend) {
////                // 点击某个好友后进入聊天页面
////                Intent intent = new Intent(getContext(), ChatActivity.class);
////                intent.putExtra("friendId", friendsBean.getId());
////                startActivity(intent);
////            }
////        });
//
//
//        loadFriend();
//        loadHistory();
//
//        // final TextView textView = binding.textNotifications;
//        //notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
//        return root;
//
//
//    }
//
//
//
//    private void loadFriend() {
//        //需要传一个context 先这么写
//        CookieJarImpl cookieJar = new CookieJarImpl(getContext());
//        // CookieJarImpl cookieJar = new CookieJarImpl(NotificationsFragment.getContext());
//        OkHttpClient client = new OkHttpClient.Builder().cookieJar(cookieJar).build();
//        Request request = new Request.Builder()
//                .url("http://192.168.184.21:5000/addfriends/list")
//                .build();
//        client.newCall(request).enqueue(new Callback() {
//
//            @Override
//            public void onFailure(Call call, IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                try {
//                    JSONObject res = new JSONObject(response.body().string());
//
//                    Iterator<String> keys = res.keys();
//
//                    String value = null;
//                    String email = null;
//                    while (keys.hasNext()) {
//                        email = keys.next();
//                        value = res.getString(email);
//                        Log.d(TAG,"email"+email);
//                        Log.d(TAG,"va"+value);
//                        FriendsBean friend = new FriendsBean();
//                        friend.setEmail(email);
//                        friend.setName(value);
//                        friendsDao.insert(friend);
//                    }
//
//                } catch (JSONException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        });
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        binding = null;
//    }
//
////    public void insert(){
////        new Thread(){
////            @Override
////            public void run() {
////                super.run();
////                Random r = new Random();
////                friendsBean.setId(r.nextInt(1000000));
////                friendsBean.setName("Tony");
////                friendsBean.setPath("");
////                friendsDao.insert(friendsBean);
////            }
////        }.start();
////
////    }
//
//
//
//
//    public void loadHistory(){
//        new Thread(){
//            @Override
//            public void run() {
//                super.run();
//                List<FriendsBean> l = friendsDao.query();
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        adapter.setList(l);
//                    }
//                });
//            }
//        }.start();
//    }
//
//
//}

package com.example.myapplication.ui.notifications;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ContentView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.fragment.app.FragmentActivity;

import com.example.myapplication.Adapter.FriendsAdapter;
import com.example.myapplication.Adapter.HistoryAdapter;
import com.example.myapplication.Bean.FriendsBean;
import com.example.myapplication.Dao.FriendsDao;
import com.example.myapplication.Dao.RecDataBase;
import com.example.myapplication.R;
import com.example.myapplication.Utils.CookieJarImpl;
import com.example.myapplication.databinding.FragmentMeBinding;
import com.example.myapplication.databinding.FragmentNotificationsBinding;
import com.example.myapplication.ui.recognition.HistoryActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NotificationsFragment extends Fragment  {

    private FragmentNotificationsBinding binding;
    private FriendsBean friendsBean;
    private RecDataBase recDataBase;
    private FriendsDao friendsDao;
    private RecyclerView list;
    private FriendsAdapter adapter;
    private View contentView;
    private ImageView add_fir;
    private static final String TAG = "N0";





//    @SuppressLint("ResourceType")
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
////        Intent intent = new Intent(getContext(), FriendsListActivity.class);
////        startActivity(intent);
//
//    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
//        contentView = inflater.inflate(R.layout.activity_friends_list, container, false);

//        NotificationsViewModel notificationsViewModel =
//                new ViewModelProvider(this).get(NotificationsViewModel.class);

//    binding = FragmentNotificationsBinding.inflate(inflater, container, false);
//    View root = binding.getRoot();
        list= root.findViewById(R.id.fri_list1);
        recDataBase = Room.databaseBuilder(getContext(), RecDataBase.class, "RecDataBase").allowMainThreadQueries().build();
        friendsDao = recDataBase.friendsDao();
        friendsBean = new FriendsBean();

//        //先放几个数据
//        FriendsBean f1 = new FriendsBean();
//        f1.setName("Nick");
//        f1.setEmail("123456@qq.com");
//        FriendsBean f2 = new FriendsBean();
//        f2.setName("Tom");
////        f2.setEmail("12345678@qq.com");
//        friendsDao.insert(f1);
//        friendsDao.insert(f2);

        // ImageView add_fir = root.findViewById(R.id.add_fir1);
        add_fir=binding.addFir1;
        add_fir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddFriendActivity.class);
                startActivity(intent);
            }
        });
//        insert();
//        l.addAll(friendsDao.query());
//        adapter.setList(l);
//        adapter.setDao(friendsDao);



        //设置布局管理器
        list.setLayoutManager(new LinearLayoutManager(this.getContext()));
        //设置分割线
        DividerItemDecoration mDivider = new DividerItemDecoration(this.getContext(),DividerItemDecoration.VERTICAL);
        list.addItemDecoration(mDivider);
        //设置适配器

        adapter = new FriendsAdapter();
        list.setAdapter(adapter);



        //设置适配器
//        adapter = new FriendsAdapter(l, new FriendsAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(FriendsBean friend) {
//                // 点击某个好友后进入聊天页面
//                Intent intent = new Intent(getContext(), ChatActivity.class);
//                intent.putExtra("friendId", friendsBean.getId());
//                startActivity(intent);
//            }
//        });


        loadFriend();
//        loadHistory();

        // final TextView textView = binding.textNotifications;
        //notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;


    }



    private void loadFriend() {
        //需要传一个context 先这么写
        CookieJarImpl cookieJar = new CookieJarImpl(getContext());
        // CookieJarImpl cookieJar = new CookieJarImpl(NotificationsFragment.getContext());
        OkHttpClient client = new OkHttpClient.Builder().cookieJar(cookieJar).build();
        Request request = new Request.Builder()
                .url("http://192.168.117.21:5000/addfriends/list")
                .build();
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(null, "load friend failed");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject res = new JSONObject(response.body().string());

                    Iterator<String> keys = res.keys();

                    String value = null;
                    String email = null;
                    while (keys.hasNext()) {
                        email = keys.next();
                        value = res.getString(email);
                        Log.d(TAG,"email"+email);
                        Log.d(TAG,"va"+value);
                        FriendsBean friend = new FriendsBean();
                        friend.setEmail(email);
                        friend.setName(value);
                        try{
                            friendsDao.insert(friend);
                        }catch (Exception e){
                            Log.e("Exception",e.toString());
                        }

                    }
                    loadHistory();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
                List<FriendsBean> l = friendsDao.query();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setList(l);
                    }
                });
            }
        }.start();
    }


}