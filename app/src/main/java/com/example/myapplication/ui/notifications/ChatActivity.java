package com.example.myapplication.ui.notifications;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.myapplication.Adapter.HistoryAdapter;
import com.example.myapplication.Bean.ChallengeBean;
import com.example.myapplication.Bean.FriendsBean;
import com.example.myapplication.Bean.HistoryBean;
import com.example.myapplication.Bean.MessageBean;
import com.example.myapplication.Dao.ChallengeDao;
import com.example.myapplication.Dao.MessageBeanDao;
import com.example.myapplication.R;
import com.example.myapplication.Adapter.MessageAdapter;
import com.example.myapplication.Utils.CookieJarImpl;
import com.example.myapplication.Utils.FileUtil;
import com.example.myapplication.Utils.VoiceUtil;
import com.example.myapplication.ui.me.LoginActivity;
import com.example.myapplication.ui.recognition.HistoryActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Challenge;
import okhttp3.Cookie;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import androidx.appcompat.widget.Toolbar;
import androidx.room.Room;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private EditText mInputEditText;

    private Context context;
    private Button mSendButton;

    private static String folderPath;
    private Button challengeButton;

    private static final int REQUEST_CODE = 1;
    private MessageAdapter mAdapter;
    private List<MessageBean> mMessageList = new ArrayList<>();
    private MessageBeanDao mMessBeanDao;
    String friendId;
    String friendName;
    // 定义请求码和结果码

    private static final int RESULT_OK = Activity.RESULT_OK;



    SharedPreferences preferences;
    public static String urldown;
    int messagenum,lastmessagenum;
    static String userId;
    static CookieJarImpl cookieJar;
    static OkHttpClient client;
    private ChallengeDao challengeDao;
    private ChallengeBeanDatabase challengeBeanDatabase;

    private int listnum = 0;

    private static MessageBeanDatabase mMessageBeanDatabase;
    private SharedPreferences downSp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        challengeBeanDatabase = Room.databaseBuilder(this, ChallengeBeanDatabase.class, "challenge_db").allowMainThreadQueries().build();
        challengeDao = challengeBeanDatabase.challengeDao();
        cookieJar = new CookieJarImpl(ChatActivity.this);
        client = new OkHttpClient.Builder().cookieJar(cookieJar).build();

//        Intent intent2 = new Intent();
//        String result = intent2.getStringExtra("grade");
//        String fId = intent2.getStringExtra("friend_id");
//        String fName = intent2.getStringExtra("friend_name");
//
//
//
//        if(result!=null) {
//            FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体
//            MessageBean message = new MessageBean();
//            Random r = new Random();
//            String id = r.nextInt(10000000) + 50 + "";
//            // 创建一条新的消息
//            formBody.add("user_id2", fId);//传递键值对参数
//            formBody.add("message", result);//传递键值对参数
//            formBody.add("challenge", "0");//传递键值对参数
//            formBody.add("ID", id);
//            sendmessage(formBody, client);
//            message.setId(id);
//            message.setContent(result);
//            message.setFromUser(userId);
//            message.setToUser(fId);
//            message.setChallenge(false);
//            String date_temp = new Date().toString();
//            try {
//                message.setSendTime(changeDate(date_temp, 2));
//            } catch (ParseException e) {
//                throw new RuntimeException(e);
//            }
//            Log.d("user", "onClick: messagebean" + message.getFromUser());
//
//            // 将消息插入到数据库中
//            insertChatRecord(message);
//            mMessageList.add(message);
//            mAdapter.setList(mMessageList);
//            mAdapter.notifyItemInserted(mMessageList.size() - 1>0?mMessageList.size() - 1:0);
//            mRecyclerView.smoothScrollToPosition(mMessageList.size() - 1>0?mMessageList.size() - 1:0);
//
//        }



        lastmessagenum = 0;
        String url = "http://192.168.24.21:5000/";

        urldown = "http://192.168.24.21:5000/challenge/download_zip";
        //创建challenge文件夹
        String folderName = "challengedown";
        folderPath = ChatActivity.this.getFilesDir().getAbsolutePath() + "/" + folderName;

        //知道自己是谁
        preferences = getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        userId = preferences.getString("user_id", "");

        Log.d("user", "onCreate: "+userId);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView = findViewById(R.id.recycler_view);
        mAdapter = new MessageAdapter(mMessageList,this);

        //Download(client,"4836372");

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        //创建database
        mMessageBeanDatabase = Room.databaseBuilder(getApplicationContext(), MessageBeanDatabase.class, "message_db").allowMainThreadQueries().build();

        // 初始化输入框和发送按钮
        mInputEditText = findViewById(R.id.input_edit_text);
        mSendButton = findViewById(R.id.send_button);

        challengeButton = findViewById(R.id.challenge_button);

        mAdapter.setListener(new MessageAdapter.Listener() {

            @Override
            public void onClickListener(MessageBean bean) {

                downSp = getSharedPreferences(bean.getContent(), Context.MODE_PRIVATE);
                String isLoad = downSp.getString("isDown","");

                //Download(client,bean.getContent());
                if(!isLoad.equals("1")){
                    Download(client,bean.getContent());
                }

                downSp = getSharedPreferences(bean.getContent(),MODE_PRIVATE);
                SharedPreferences.Editor editor =downSp.edit();
                editor.putString("isDown","1");
                editor.commit();

//                new AlertDialog.Builder(ChatActivity.this).setMessage("Do you want to challenge now?")
//                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                Intent intent = new Intent(ChatActivity.this, ReceiveChallengeActivity.class);
//                                intent.putExtra("group", bean.getContent());
//                                startActivity(intent);
//                            }
//                        }).show();

                Intent intent = new Intent(ChatActivity.this,JumpActivity.class);
                intent.putExtra("group", bean.getContent());
                intent.putExtra("friend_name",friendName);
                intent.putExtra("friend_id", friendId);
                intent.putExtra("userId",userId);
                startActivity(intent);


            }
        });

        //Download(client,"9806114");
        challengeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatActivity.this, UploadChallenge.class);
                intent.putExtra("friend_name",friendName);
                intent.putExtra("friend_id", friendId);
                intent.putExtra("userId",userId);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        // 获取数据库实例和聊天记录 Dao
        MessageBeanDatabase db = MessageBeanDatabase.getInstance(this);
        mMessBeanDao = db.messageBeanDao();

        // 从 Intent 中获取对方用户的 ID 和名字
        Intent intent = getIntent();
        friendId = intent.getStringExtra("friend_id");
        friendName = intent.getStringExtra("friend_name");

        // 设置 ActionBar 的标题为好友的名字
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(friendName);
//        getmessage(client);
//        loadChatRecords(friendId);

        //每过0.5秒刷新一次
        Timer timer = new Timer();
        timer.schedule(new TimerTask(){
            public void run(){
                getmessage(client);
               // loadChatRecords(friendId);
            }
        }, 1,5000);//schedule{TimerTask()方法调用，延时执行时间，循环执行时间间隔}



        // 加载历史聊天记录


        // 设置发送按钮的点击事件
        mSendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // 获取输入框中的文本
                String messageText = mInputEditText.getText().toString().trim();

                FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体

                if (!messageText.isEmpty()) {
                    MessageBean message = new MessageBean();
                    Random r = new Random();
                    String id = r.nextInt(10000000) + 50+"";
                    // 创建一条新的消息
                    formBody.add("user_id2", friendId);//传递键值对参数
                    formBody.add("message", mInputEditText.getText().toString());//传递键值对参数
                    formBody.add("challenge", "0");//传递键值对参数
                    formBody.add("ID",id);
                    sendmessage(formBody,client);


                    message.setId(id);
                    message.setContent(messageText);
                    message.setFromUser(userId);
                    message.setToUser(friendId);
                    message.setChallenge(false);
                    String date_temp = new Date().toString();
                    try {
                        message.setSendTime(changeDate(date_temp,2));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    Log.d("user", "onClick: messagebean"+message.getFromUser());

                    // 将消息插入到数据库中
                    insertChatRecord(message);
                   // mMessageBeanDatabase.messageBeanDao().insert(message);
                    // 清空输入框
                    mInputEditText.setText("");
//todo
//                    // 将消息添加到 RecyclerView 中
                    mMessageList.add(message);
                    mAdapter.setList(mMessageList);
                    mAdapter.notifyItemInserted(mMessageList.size() - 1>0?mMessageList.size() - 1:0);
                    mRecyclerView.smoothScrollToPosition(mMessageList.size() - 1>0?mMessageList.size() - 1:0);

                }
            }
        });



loadChatRecords(friendId);

    }

    // 加载历史聊天记录
    private void loadChatRecords(String friendId) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 从数据库中查询指定好友的聊天记录 sharedPreference
                List<MessageBean> chatRecords = mMessBeanDao.getMessages(friendId,userId);


                    //chatRecords.addAll(mMessBeanDao.getMessages(userId,friendId));
                    // 将聊天记录添加到消息列表中
                    mMessageList.addAll(chatRecords);

                    // 刷新 RecyclerView
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //用于通知 RecyclerView 的 Adapter
                            // 数据集发生了变化，从而触发 RecyclerView 进行刷新操作，更新显示的数据。
                            mAdapter.setList(mMessageList);
                            mRecyclerView.smoothScrollToPosition(mMessageList.size() - 1>0?mMessageList.size() - 1:0);
                            mAdapter.notifyDataSetChanged();

                            //用于将 RecyclerView 滚动到最后一条消息的位置。mMessageList.size() - 1 表示最后一条
                            // 消息在数据集中的位置，smoothScrollToPosition()
                            //方法会平滑地滚动 RecyclerView 到指定位置，从而确保用户可以看到最新的消息。
                        }
                    });


            }
        }).start();
    }

    // 插入一条聊天记录到数据库中
    private  void insertChatRecord(MessageBean message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mMessBeanDao.insert(message);
            }
        }).start();
    }

        private void getmessage(OkHttpClient client){
        Request request = new Request.Builder()
                .url("http://192.168.24.21:5000/challenge/getmessage")
                .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    Log.d("getmessage","onfailure");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String contentType = response.header("Content-Type");
                        if (contentType != null && contentType.contains("application/json")) {
                            try {
                                response = client.newCall(request).execute();
                                String jsonResponse = response.body().string();

                                // 解析json
                                JSONObject jsonObject = new JSONObject(jsonResponse);
                                Iterator<String> keys = jsonObject.keys();
                                messagenum = jsonObject.length();//todo
                                //todo 写报告的时候详细说
                                ArrayList<String> grouplist =  new ArrayList<>();

                                if (messagenum>lastmessagenum) {
                                    while (keys.hasNext()) {
                                        String key = keys.next();
                                        JSONObject messageObj = jsonObject.getJSONObject(key);
                                        boolean challenge = messageObj.getBoolean("challenge");
                                        String content = messageObj.getString("message");
                                        String to = messageObj.getString("to");
                                        String message_from = messageObj.getString("message_from");
                                        String time = messageObj.getString("time");
                                        MessageBean messageBean = new MessageBean(key, message_from, to, content, changeDate(time, 1), challenge);
                                        mMessageBeanDatabase.messageBeanDao().insert(messageBean);

//                                        mMessageList.add(messageBean);
//                                        mAdapter.setList(mMessageList);
////                                        mAdapter.notifyItemInserted(mMessageList.size() - 1 > 0 ? mMessageList.size() - 1 : 0);
////                                        mRecyclerView.smoothScrollToPosition(mMessageList.size() - 1 > 0 ? mMessageList.size() - 1 : 0);

                                    }
                                }
                                lastmessagenum = messagenum;
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }

                        // ...
                    } else {
                        // 响应内容为 HTML 格式
                        Log.d("html","html");
                    }
                } else {
                    // 响应失败
                    // ...
                }
            }
        });


    }

    public static void sendmessage(FormBody.Builder formBody, OkHttpClient client){

        Request request = new Request.Builder()//创建Request 对象。
                .url("http://192.168.24.21:5000/challenge/sendmessage")
                .post(formBody.build())//传递请求体
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.d("sendmessage","onfailure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String contentType = response.header("Content-Type");
                    if (contentType != null && contentType.contains("application/json")) {
                        Log.d("send","success");
                    } else {
                        // 响应内容为 HTML 格式
                        Log.d("send","html");
                    }
                } else {
                    // 响应失败
                }
            }
        });


    }
    public static String changeDate(String time ,int choose) throws ParseException {
        if(choose==1) {
            SimpleDateFormat parser = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
            Date date = parser.parse(time);

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String formattedTime = formatter.format(date);
            Date date8 = formatter.parse(formattedTime);


            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date8);
            calendar.add(Calendar.HOUR_OF_DAY, -8);
            date8 = calendar.getTime();
            String fin = formatter.format(date8);
            return fin;
        } else   {
            SimpleDateFormat parser = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
            Date date = parser.parse(time);

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String formattedTime = formatter.format(date);
            Date date8 = formatter.parse(formattedTime);


            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date8);
            calendar.add(Calendar.HOUR_OF_DAY, 0);
            date8 = calendar.getTime();
            String fin = formatter.format(date8);
            return fin;
        }

    }


    public boolean Download(OkHttpClient client, String group){


        FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体

        formBody.add("group", group);//传递键值对参数

        Request request = new Request.Builder()
                .url(urldown)
                .post(formBody.build())
                .build();

//        cookie = client.cookieJar().loadForRequest(request.url());
//        request.newBuilder().addHeader(cookie.get(0).name(), cookie.get(0).value());

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("fail to connect to server");
            }


            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
               // byte[] buf = new byte[1024];
                byte[] buf = new byte[(int)response.body().contentLength()];
                int len = 0;

                FileOutputStream fos = null;
                Random r = new Random();
                int ranPath = r.nextInt(100000);
                try {
                    is = response.body().byteStream();

                    File file = new File(folderPath+ranPath,"pack.zip");
                    if (!file.getParentFile().exists()) {
                        if (!file.getParentFile().mkdirs()) {
                            Log.e("Error", "Failed to create directory");
                        }
                    }
                    fos = new FileOutputStream(file);

                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                String newpath = folderPath + ranPath;

                String zipPath = newpath + "/pack.zip";
                // 文件解压缩，zipPath是下载下来的压缩包路径
                FileUtil.unzip(zipPath, newpath);

                // 读取message.json文件并解析为ChallengeBean对象
                String filePath = newpath+"/messages.json";
                try {
                    // 读取文件内容
                    InputStream inputStream = new FileInputStream(new File(filePath));
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line = null;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    inputStream.close();

                    // 遍历JSON对象并转化为ChallengeBean对象
                    JSONObject jsonObject = new JSONObject(stringBuilder.toString());
                    Iterator<String> keys = jsonObject.keys();
                    while (keys.hasNext()) {

                        String key = keys.next();
                        JSONObject challengeObj = jsonObject.getJSONObject(key);
                        String code = challengeObj.getString("code");
                        String enName = challengeObj.getString("enName");
                        String FraName = challengeObj.getString("FraName");
                        String filename = challengeObj.getString("filename");
                        String jpName = challengeObj.getString("jpName");
                        String spaName = challengeObj.getString("spaName");
                        String korName = challengeObj.getString("korName");
                        String name = challengeObj.getString("name");
                        String path = newpath+"/"+filename;
                        String challenge_group = challengeObj.getString("challenge_group");
                        ChallengeBean challengeBean = new ChallengeBean(filename,path, enName,jpName,korName,FraName,code,challenge_group,spaName,name);
                        challengeDao.insert(challengeBean);

                    }


                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //读取解压的下载文件信息
                //              File download_info = new File(folderPath+"/photos/messages.json");
//                FileReader fileReader = new FileReader(download_info);
//                Reader reader = new InputStreamReader(new FileInputStream(download_info), "Utf-8");
//                int ch= 0;
//                StringBuffer sb = new StringBuffer();
//                while((ch = reader.read()) != -1) {
//                    sb.append((char) ch);
//                }
//                fileReader.close();
//                reader.close();
//
//                //转json对象
//                JSONObject download_info_json = null;
//                try {
//                    download_info_json = new JSONObject(sb.toString());
//                } catch (JSONException e) {
//                    throw new RuntimeException(e);
//                }
//
//                // 遍历download_info_json,把里面的信息和对应的图片存储到数据库中
//                for(int i=0;i<download_info_json.length();i++) {
//                    try {
//
//
//                        JSONObject item = download_info_json.getJSONObject(String.valueOf(i));
//
//
//                        ChallengeBean challengeBean = new ChallengeBean();
//                        challengeBean.setName(item.getString("name"));
//                        challengeBean.setFilepath(folderPath + "/photos/" + item.getString("filename"));
//                   //     challengeBean.setDateTime(item.getString("datetime"));
//                        challengeBean.setCode(item.getString("code"));
//                        challengeBean.setEnName(item.getString("enName"));
//                        challengeBean.setKorName(item.getString("korName"));
//                        challengeBean.setSpaName(item.getString("spaName"));
//                        challengeBean.setJpName(item.getString("jpName"));
//                        challengeBean.setFraName(item.getString("FraName"));
//                        challengeBean.setFilename(item.getString("filename"));
//                    //    challengeBean.setIf_star(item.getInt("if_star"));
//                    //    challengeBean.setNum(item.getInt("proficiency"));
//
//                       mChallengeBeanDatabase.challengeDao().insert(challengeBean);
//                             //  mMessageBeanDatabase.messageBeanDao().insert(messageBean);
//                    } catch (JSONException e) {
//                        throw new RuntimeException(e);
//                    }
//                }

            }
        });
    return true;
    }


    // 处理 Activity UploadChallenge 返回的结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            String group = data.getStringExtra("group");

            FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体

            MessageBean message = new MessageBean();
            Random r = new Random();
            String id = r.nextInt(10000000) + "";
            // 创建一条新的消息
            formBody.add("user_id2", friendId);//传递键值对参数
            formBody.add("message", group);//传递键值对参数
            formBody.add("challenge", "1");//传递键值对参数
            formBody.add("ID", id);
            sendmessage(formBody, client);


            message.setId(id);
            message.setContent(group);
            message.setFromUser(userId);
            message.setToUser(friendId);
            message.setChallenge(true);
            String date_temp = new Date().toString();
            try {
                message.setSendTime(changeDate(date_temp, 2));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            Log.d("user", "onClick: messagebean" + message.getFromUser());

            // 将消息插入到数据库中
            insertChatRecord(message);
            // mMessageBeanDatabase.messageBeanDao().insert(message);
            // 清空输入框

            // 将消息添加到 RecyclerView 中
            mMessageList.add(message);
            mAdapter.setList(mMessageList);
            mAdapter.notifyItemInserted(mMessageList.size() - 1 > 0 ? mMessageList.size() - 1 : 0);
            mRecyclerView.smoothScrollToPosition(mMessageList.size() - 1 > 0 ? mMessageList.size() - 1 : 0);

        }
//        if (requestCode == 000 && resultCode == RESULT_OK) {
//
//           int re = data.getIntExtra("result",0);
//           String result  = re+"";
//            FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体
//
//            MessageBean message = new MessageBean();
//            Random r = new Random();
//            String id = r.nextInt(10000000) + "";
//            // 创建一条新的消息
//            formBody.add("user_id2", friendId);//传递键值对参数
//            formBody.add("message", result);//传递键值对参数
//            formBody.add("challenge", "1");//传递键值对参数
//            formBody.add("ID", id);
//            sendmessage(formBody, client);
//
//
//            message.setId(id);
//            message.setContent(result);
//            message.setFromUser(userId);
//            message.setToUser(friendId);
//            message.setChallenge(true);
//            String date_temp = new Date().toString();
//            try {
//                message.setSendTime(changeDate(date_temp, 2));
//            } catch (ParseException e) {
//                throw new RuntimeException(e);
//            }
//            Log.d("user", "onClick: messagebean" + message.getFromUser());
//
//            // 将消息插入到数据库中
//            insertChatRecord(message);
//            // mMessageBeanDatabase.messageBeanDao().insert(message);
//            // 清空输入框
//
//            // 将消息添加到 RecyclerView 中
//            mMessageList.add(message);
//            mAdapter.setList(mMessageList);
//            mAdapter.notifyItemInserted(mMessageList.size() - 1 > 0 ? mMessageList.size() - 1 : 0);
//            mRecyclerView.smoothScrollToPosition(mMessageList.size() - 1 > 0 ? mMessageList.size() - 1 : 0);
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//        }
    }

}



