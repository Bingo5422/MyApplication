package com.example.myapplication.ui.notifications;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.myapplication.Bean.FriendsBean;
import com.example.myapplication.Bean.MessageBean;
import com.example.myapplication.Dao.MessageBeanDao;
import com.example.myapplication.R;
import com.example.myapplication.Adapter.MessageAdapter;
import com.example.myapplication.Utils.CookieJarImpl;
import com.example.myapplication.ui.me.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import androidx.appcompat.widget.Toolbar;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private EditText mInputEditText;

    private Context context;
    private Button mSendButton;
    private MessageAdapter mAdapter;
    private List<MessageBean> mMessageList = new ArrayList<>();
    private MessageBeanDao mMessBeanDao;

    private static MessageBeanDatabase mMessageBeanDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        String url = "http://192.168.61.21:5000/auth/login";

        // 初始化 RecyclerView 和适配器
        mRecyclerView = findViewById(R.id.recycler_view);
        mAdapter = new MessageAdapter(mMessageList,this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);


        // 初始化输入框和发送按钮
        mInputEditText = findViewById(R.id.input_edit_text);
        mSendButton = findViewById(R.id.send_button);

        // 获取数据库实例和聊天记录 Dao
        MessageBeanDatabase db = MessageBeanDatabase.getInstance(this);
        mMessBeanDao = db.messageBeanDao();

        // 从 Intent 中获取对方用户的 ID 和名字
        Intent intent = getIntent();
        String friendId = intent.getStringExtra("friend_id");
        String friendName = intent.getStringExtra("friend_name");

        // 设置 ActionBar 的标题为好友的名字
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(friendName);


        // 加载历史聊天记录
        loadChatRecords(friendId);

        // 设置发送按钮的点击事件
        mSendButton.setOnClickListener(new View.OnClickListener() {
            CookieJarImpl cookieJar = new CookieJarImpl(ChatActivity.this);
            @Override
            public void onClick(View v) {

                // 获取输入框中的文本
                String messageText = mInputEditText.getText().toString().trim();
                OkHttpClient client = new OkHttpClient.Builder().cookieJar(cookieJar).build();//创建OkHttpClient对象。
                FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体

                if (!messageText.isEmpty()) {
                    // 创建一条新的消息
                    formBody.add("user_id2", friendId);//传递键值对参数
                    formBody.add("message", mInputEditText.getText().toString());//传递键值对参数
                    formBody.add("challenge", "0");//传递键值对参数
                    Request request = new Request.Builder()//创建Request 对象。
                            .url(url)
                            .post(formBody.build())//传递请求体
                            .build();

                    MessageBean message = new MessageBean();
                    message.setContent(messageText);
                    message.setFromUser("当前用户的 ID");
                    message.setToUser(friendId);
                    message.setSendTime(new Date().getTime());

                    // 将消息插入到数据库中
                    insertChatRecord(message);

                    // 清空输入框
                    mInputEditText.setText("");

                    // 将消息添加到 RecyclerView 中
                    mMessageList.add(message);
                    mAdapter.notifyItemInserted(mMessageList.size() - 1);
                    mRecyclerView.smoothScrollToPosition(mMessageList.size() - 1);
                }
            }
        });
    }

    // 加载历史聊天记录
    private void loadChatRecords(String friendId) {
        SharedPreferences preferences = getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 从数据库中查询指定好友的聊天记录 sharedPreference

                String userId = preferences.getString("user_id", "");
                // 在 ChatActivity 中获取 Intent 对象
                Intent intent = getIntent();

                // 获取 "friendId" 的值
                String friendId = intent.getStringExtra("friendId");
                List<MessageBean> chatRecords = mMessBeanDao.getMessages(friendId,userId);

                // 将聊天记录添加到消息列表中
                mMessageList.addAll(chatRecords);

                // 刷新 RecyclerView
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //用于通知 RecyclerView 的 Adapter
                        // 数据集发生了变化，从而触发 RecyclerView 进行刷新操作，更新显示的数据。
                        mAdapter.notifyDataSetChanged();
                        //用于将 RecyclerView 滚动到最后一条消息的位置。mMessageList.size() - 1 表示最后一条
                        // 消息在数据集中的位置，smoothScrollToPosition()
                        //方法会平滑地滚动 RecyclerView 到指定位置，从而确保用户可以看到最新的消息。
                        mRecyclerView.smoothScrollToPosition(mMessageList.size() - 1>0?mMessageList.size() - 1:0);
                    }
                });
            }
        }).start();
    }

    // 插入一条聊天记录到数据库中
    private void insertChatRecord(MessageBean message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mMessBeanDao.insert(message);
            }
        }).start();
    }

        public static void getmessage(){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://192.168.61.21:5000/challenge/getmessage")
                .build();
        try {
            Response response = client.newCall(request).execute();
            String jsonResponse = response.body().string();

            // 解析json
            JSONObject jsonObject = new JSONObject(jsonResponse);
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject messageObj = jsonObject.getJSONObject(key);
                boolean challenge = messageObj.getBoolean("challenge");
                String message = messageObj.getString("message");
                String to = messageObj.getString("to");
                String message_from = messageObj.getString("message_from");
                String time = messageObj.getString("time");
                Long timelong = Long.valueOf(time);
                MessageBean messageBean = new MessageBean(message_from,to,message,timelong,challenge);
                mMessageBeanDatabase.messageBeanDao().insert(messageBean);



//                FriendsBean friend = new FriendsBean();
//                friend.setEmail(email);
//                friend.setName(res.getString("nickname"));
//                mFriendDatabaase.friendDao().addFriend(friend);
                // TODO: 在这里处理接收到的消息
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}



