package com.example.myapplication.ui.notifications;

import static com.example.myapplication.MainActivity.DomainURL;

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
    int messagenum, lastmessagenum;
    static String userId;
    static CookieJarImpl cookieJar;
    static OkHttpClient client;
    private ChallengeDao challengeDao;
    private ChallengeBeanDatabase challengeBeanDatabase;


    private static MessageBeanDatabase mMessageBeanDatabase;
    private SharedPreferences downSp;
    private boolean firsttime;
    private ArrayList<Integer> messagelistnum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        challengeBeanDatabase = Room.databaseBuilder(this, ChallengeBeanDatabase.class, "challenge_db").allowMainThreadQueries().build();
        challengeDao = challengeBeanDatabase.challengeDao();
        cookieJar = new CookieJarImpl(ChatActivity.this);
        client = new OkHttpClient.Builder().cookieJar(cookieJar).build();

        firsttime = true;

//
        messagelistnum = new ArrayList<>();
        messagelistnum.add(0);


        lastmessagenum = 0;
//        String url = "http://192.168.113.21:5000/";
        String url = DomainURL;

        urldown = DomainURL + "/challenge/download_zip";
        //Create a challenge folder
        String folderName = "challengedown";
        folderPath = ChatActivity.this.getFilesDir().getAbsolutePath() + "/" + folderName;

        //Know who you are
        preferences = getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        userId = preferences.getString("user_id", "");

        Log.d("user", "onCreate: " + userId);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView = findViewById(R.id.recycler_view);
        mAdapter = new MessageAdapter(mMessageList, this);

        //Download(client,"4836372");

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        //create database
        mMessageBeanDatabase = Room.databaseBuilder(getApplicationContext(), MessageBeanDatabase.class, "message_db").allowMainThreadQueries().build();

        // Initializes the input box and send button
        mInputEditText = findViewById(R.id.input_edit_text);
        mSendButton = findViewById(R.id.send_button);

        challengeButton = findViewById(R.id.challenge_button);

        mAdapter.setListener(new MessageAdapter.Listener() {

            @Override
            public void onClickListener(MessageBean bean) {

                downSp = getSharedPreferences(bean.getContent(), Context.MODE_PRIVATE);
                String isLoad = downSp.getString("isDown", "");

                //Download(client,bean.getContent());
                if (!isLoad.equals("1")) {
                    Download(client, bean.getContent());
                }

                downSp = getSharedPreferences(bean.getContent(), MODE_PRIVATE);
                SharedPreferences.Editor editor = downSp.edit();
                editor.putString("isDown", "1");
                editor.commit();


                Intent intent = new Intent(ChatActivity.this, JumpActivity.class);
                intent.putExtra("group", bean.getContent());
                intent.putExtra("friend_name", friendName);
                intent.putExtra("friend_id", friendId);
                intent.putExtra("userId", userId);
                startActivity(intent);


            }
        });

        //Download(client,"9806114");
        challengeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatActivity.this, UploadChallenge.class);
                intent.putExtra("friend_name", friendName);
                intent.putExtra("friend_id", friendId);
                intent.putExtra("userId", userId);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        // Get the database instance and chat log Dao
        MessageBeanDatabase db = MessageBeanDatabase.getInstance(this);
        mMessBeanDao = db.messageBeanDao();

        // Gets the ID and name of the other user from the Intent
        Intent intent = getIntent();
        friendId = intent.getStringExtra("friend_id");
        friendName = intent.getStringExtra("friend_name");

        // Set the title of the ActionBar to your friend's name
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(friendName);
//        getmessage(client);
//        loadChatRecords(friendId);

        //Refresh every 0.5 seconds
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                getmessage(client);
                loadChatRecords(friendId);
            }
        }, 0, 500);


        mSendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Gets the text in the input box
                String messageText = mInputEditText.getText().toString().trim();

                FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体

                if (!messageText.isEmpty()) {
                    MessageBean message = new MessageBean();
                    Random r = new Random();
                    String id = r.nextInt(10000000) + 50 + "";
                    // Create a new message
                    formBody.add("user_id2", friendId);//传递键值对参数
                    formBody.add("message", mInputEditText.getText().toString());//传递键值对参数
                    formBody.add("challenge", "0");//传递键值对参数
                    formBody.add("ID", id);
                    sendmessage(formBody, client);


                    message.setId(id);
                    message.setContent(messageText);
                    message.setFromUser(userId);
                    message.setToUser(friendId);
                    message.setChallenge(false);
                    String date_temp = new Date().toString();
                    try {
                        message.setSendTime(changeDate(date_temp, 2));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    Log.d("user", "onClick: messagebean" + message.getFromUser());

                    // Insert the message into the database
                    insertChatRecord(message);
                    // mMessageBeanDatabase.messageBeanDao().insert(message);
                    // Clear input field
                    mInputEditText.setText("");

                    mMessageList.add(message);
                    mAdapter.setList(mMessageList);
                    mAdapter.notifyItemInserted(mMessageList.size() - 1 > 0 ? mMessageList.size() - 1 : 0);
                    mRecyclerView.smoothScrollToPosition(mMessageList.size() - 1 > 0 ? mMessageList.size() - 1 : 0);

                }
            }
        });
        loadChatRecords(friendId);

    }

    // Load the history chat
    private void loadChatRecords(String friendId) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                // Queries the chat record sharedPreference of the specified friend from the database
                List<MessageBean> chatRecords = mMessBeanDao.getMessages(friendId, userId);
                if (firsttime) {
                    firsttime = false;
                    //chatRecords.addAll(mMessBeanDao.getMessages(userId,friendId));
                    // Add the chat to the message list
                    mMessageList.addAll(chatRecords);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Notifies the Adapter for RecyclerView
                            // The data set has changed, triggering a refresh operation for RecyclerView to update the displayed data.
                            mAdapter.setList(mMessageList);
                            mRecyclerView.smoothScrollToPosition(mMessageList.size() - 1 > 0 ? mMessageList.size() - 1 : 0);
                            mAdapter.notifyDataSetChanged();

                        }
                    });
                    messagelistnum.add(chatRecords.size() - 1);
                } else {
                    int lastIdx = chatRecords.size() - 1;
                    messagelistnum.add(lastIdx);
                    int mnum = messagelistnum.size();
                    if (messagelistnum.get(mnum - 1) > messagelistnum.get(mnum - 2)) {
                        //Just add the last message
                        MessageBean lastElementchat = chatRecords.get(lastIdx);
                        MessageBean lastElementmM;
                        int index = (mMessageList.size() - 1) < 0 ? 0 : (mMessageList.size() - 1);
                        if( index == 0){
                            mMessageList.add(lastElementchat);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Notifies the Adapter for RecyclerView
                                    // The data set has changed, triggering a refresh operation for RecyclerView to update the displayed data.
                                    mAdapter.setList(mMessageList);
                                    mRecyclerView.smoothScrollToPosition(mMessageList.size() - 1 > 0 ? mMessageList.size() - 1 : 0);
                                    mAdapter.notifyDataSetChanged();
                                }
                            });
                            lastElementmM = null;
                        }else {
                            lastElementmM = mMessageList.get(index);
                            if (!lastElementmM.getSendTime().equals(lastElementchat.getSendTime())) {
                                mMessageList.add(lastElementchat);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Notifies the Adapter for RecyclerView
                                        // The data set has changed, triggering a refresh operation for RecyclerView to update the displayed data.
                                        mAdapter.setList(mMessageList);
                                        mRecyclerView.smoothScrollToPosition(mMessageList.size() - 1 > 0 ? mMessageList.size() - 1 : 0);
                                        mAdapter.notifyDataSetChanged();

                                    }
                                });
                            }
                        }

                    }

                }
            }
        }).start();
    }

    // Insert a chat record into the database
    private void insertChatRecord(MessageBean message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mMessBeanDao.insert(message);
            }
        }).start();
    }

    private void getmessage(OkHttpClient client) {
        Request request = new Request.Builder()
//                .url("http://192.168.24.21:5000/challenge/getmessage")
                .url(DomainURL + "/challenge/getmessage")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.d("getmessage", "onfailure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String contentType = response.header("Content-Type");
                    if (contentType != null && contentType.contains("application/json")) {
                        try {
                            response = client.newCall(request).execute();
                            String jsonResponse = response.body().string();

                            // Parse json
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            Iterator<String> keys = jsonObject.keys();
                            messagenum = jsonObject.length();

                            if (messagenum > lastmessagenum) {
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

                    } else {
                        // The response content is in HTML format
                        Log.d("html", "html");
                    }
                } else {
                    // Response failure
                    // ...
                }
            }
        });


    }

    public static void sendmessage(FormBody.Builder formBody, OkHttpClient client) {

        Request request = new Request.Builder()//Create a Request object.
                .url(DomainURL + "/challenge/sendmessage")
                .post(formBody.build())//Transfer request body
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.d("sendmessage", "onfailure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String contentType = response.header("Content-Type");
                    if (contentType != null && contentType.contains("application/json")) {
                        Log.d("send", "success");
                    } else {
                        // The response content is in HTML format
                        Log.d("send", "html");
                    }
                } else {
                    // Response failure
                }
            }
        });


    }

    public static String changeDate(String time, int choose) throws ParseException {
        if (choose == 1) {
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
        } else {
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


    public boolean Download(OkHttpClient client, String group) {


        FormBody.Builder formBody = new FormBody.Builder();//Create the form request body

        formBody.add("group", group);//Pass key-value pair parameters

        Request request = new Request.Builder()
                .url(urldown)
                .post(formBody.build())
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("fail to connect to server");
            }


            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                // byte[] buf = new byte[1024];
                byte[] buf = new byte[(int) response.body().contentLength()];
                int len = 0;

                FileOutputStream fos = null;
                Random r = new Random();
                int ranPath = r.nextInt(100000);
                try {
                    is = response.body().byteStream();

                    File file = new File(folderPath + ranPath, "pack.zip");
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
                // Decompress files. zipPath is the path of the downloaded package
                FileUtil.unzip(zipPath, newpath);

                // Read the message.json file and parse it into the ChallengeBean object
                String filePath = newpath + "/messages.json";
                try {
                    //Read file contents
                    InputStream inputStream = new FileInputStream(new File(filePath));
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line = null;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    inputStream.close();

                    // Iterate through the JSON object and convert it to the ChallengeBean object
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
                        String path = newpath + "/" + filename;
                        String challenge_group = challengeObj.getString("challenge_group");
                        ChallengeBean challengeBean = new ChallengeBean(filename, path, enName, jpName, korName, FraName, code, challenge_group, spaName, name);
                        challengeDao.insert(challengeBean);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
        return true;
    }


    // Process the result returned by the Activity UploadChallenge
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            String group = data.getStringExtra("group");

            FormBody.Builder formBody = new FormBody.Builder();//Create the form request body

            MessageBean message = new MessageBean();
            Random r = new Random();
            String id = r.nextInt(10000000) + "";
            // Create a new message
            formBody.add("user_id2", friendId);//Pass key-value pair parameters
            formBody.add("message", group);//Pass key-value pair parameters
            formBody.add("challenge", "1");//Pass key-value pair parameters
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

            // Insert the message into the database
            insertChatRecord(message);
            // mMessageBeanDatabase.messageBeanDao().insert(message);
            // Clear input field

            // Add the message to RecyclerView
            mMessageList.add(message);
            mAdapter.setList(mMessageList);
            mAdapter.notifyItemInserted(mMessageList.size() - 1 > 0 ? mMessageList.size() - 1 : 0);
            mRecyclerView.smoothScrollToPosition(mMessageList.size() - 1 > 0 ? mMessageList.size() - 1 : 0);

        }

    }

}



