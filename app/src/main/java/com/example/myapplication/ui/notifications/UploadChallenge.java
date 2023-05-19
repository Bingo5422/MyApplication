package com.example.myapplication.ui.notifications;

import static com.example.myapplication.MainActivity.DomainURL;
import static com.example.myapplication.ui.notifications.ChatActivity.changeDate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.myapplication.Adapter.ChallengeAdapter;
import com.example.myapplication.Bean.ChallengeBean;
import com.example.myapplication.Bean.HistoryBean;
import com.example.myapplication.Bean.MessageBean;
import com.example.myapplication.Dao.HistoryDao;
import com.example.myapplication.Dao.RecDataBase;
import com.example.myapplication.R;
import com.example.myapplication.Utils.CookieJarImpl;
import com.example.myapplication.Utils.FileUtil;
import com.example.myapplication.ui.me.ServerUploadActivity;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Challenge;
import okhttp3.Cookie;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadChallenge extends AppCompatActivity {

    private RecDataBase recDataBase;

    private HistoryDao historyDao;
    private Button back;
    private RecyclerView challenge_rv;
    private ChallengeAdapter challengeAdapter;
    private static final String TAG = "UploadChallenge";
    private String folderPath, info_path, download_path, delete_info_path;
    public static final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpeg");
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json");

    public static String group;
    private List<Cookie> cookie;

    private static Context context;

    public static String url;
    static String friendId;
    static String userId;
    static File filejson;

    static CookieJarImpl cookieJar;
    static OkHttpClient client;
    static Random r;
    public static void setContext(Context context) {
        UploadChallenge.context = context;
    }
    private List<HistoryBean> dataList = new ArrayList<>();
    private List<ChallengeBean> selectedPhotoDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_challenge);

        cookieJar = new CookieJarImpl(UploadChallenge.this);
        client = new OkHttpClient.Builder().cookieJar(cookieJar).build();


        //Define group
        r = new Random();
        group = r.nextInt(10000000) +"";

        //Get the id of your friend who's ready to take the challenge, and get your own id
        Intent intent = getIntent();
        friendId = intent.getStringExtra("friend_id");
        userId = intent.getStringExtra("userId");

        url = DomainURL+"/challenge/download_zip";
        recDataBase = Room.databaseBuilder(this, RecDataBase.class, "RecDataBase").allowMainThreadQueries().build();
        historyDao = recDataBase.historyDao();

        //Create a challenge folder
        String folderName = "challenge";
        folderPath = UploadChallenge.this.getFilesDir().getAbsolutePath() + "/" + folderName;
        File folder = new File(folderPath);
        if (!folder.exists()) {
            boolean success = folder.mkdirs();
            if (success) {
                // Folder created successfully
                Log.d(TAG, "Folder created at path: " + folderPath);
            } else {
                // Folder creation failure
                Log.e(TAG, "Failed to create folder at path: " + folderPath);
            }
        } else {
            // Folder already exists
            Log.d(TAG, "Folder already exists at path: " + folderPath);
        }


        back = findViewById(R.id.challenge_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPhotoDataList = challengeAdapter.getSelectedPhotoDataList();
                Log.d(TAG, "onCreate: 收到返回的照片名list："+selectedPhotoDataList.toString());
                if(selectedPhotoDataList.size()<4){
                    Toast.makeText(UploadChallenge.this,"Please select at least 4 photos to send challenge!",Toast.LENGTH_LONG).show();

                }else {
                    try {
                        filejson = packjson(selectedPhotoDataList,folderPath);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    List<File> fileList = packPhoto(selectedPhotoDataList);
                    try {
                        uploadFiles(fileList,filejson);

                        Intent intentback = new Intent(UploadChallenge.this, ChatActivity.class);
                        intentback.putExtra("group", group);
                        //startActivity(intentback);
                        setResult(RESULT_OK, intentback);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    finish();
                    Toast.makeText(UploadChallenge.this,"Your challenge is sent!",Toast.LENGTH_LONG).show();

                }
            }
        });


        challenge_rv = findViewById(R.id.challenge_list);



        //Grid layout 3 columns
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,3);
        challenge_rv.setLayoutManager(gridLayoutManager);

        challengeAdapter = new ChallengeAdapter();

        dataList.addAll(historyDao.query());

        challengeAdapter.setList(dataList);
        challenge_rv.setAdapter(challengeAdapter);


    }


    public static List<File> packPhoto(List<ChallengeBean> dataList){
        Iterator<ChallengeBean> iterator = dataList.iterator();
        List<File> photolist= new ArrayList<>();
        while (iterator.hasNext()) {
            ChallengeBean bean = iterator.next();
            File file = new File(bean.getFilepath());
            photolist.add(file);
        }
        return photolist;
    }

    public static File  packjson(List<ChallengeBean> challengeList, String folderPath) throws IOException, JSONException {

        List<File> jsonList = new ArrayList<>();
        // 创建Gson对象
        Gson gson = new Gson();

        // Convert the List<ChallengeBean> to a JSON string
        String json = gson.toJson(challengeList);

        JSONArray jsonArray = new JSONArray(json);
        JSONObject outputJson = new JSONObject();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObj = jsonArray.getJSONObject(i);
            JSONObject newObj = new JSONObject();
            newObj.put("filename", jsonObj.getString("filename"));
            newObj.put("enName", jsonObj.getString("enName"));
            newObj.put("name", jsonObj.getString("name"));
            newObj.put("jpName", jsonObj.getString("jpName"));
            newObj.put("code", "null");//todo selectedlist 里面没有code这个参数，不知道为啥
            newObj.put("spaName", jsonObj.getString("spaName"));
            newObj.put("korName", jsonObj.getString("korName"));
            newObj.put("FraName", jsonObj.getString("FraName"));
            newObj.put("challenge_group", group);
            outputJson.put(String.valueOf(i), newObj);
        }

        String output = outputJson.toString();
// 创建File对象
        Random r = new Random();
        int randomnum = r.nextInt(1000000);
        String filePath = folderPath+"/prejson"+randomnum;

        File file = new File(filePath);

// 将JSON字符串写入文件
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(output);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }


    public  void uploadFiles(List<File> fileList, File jsonfile) throws IOException {


        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        int i = 0;
        for (; i < fileList.size(); i++) {
            File file = fileList.get(i);

            String filename = file.getName();
            RequestBody fileBody = RequestBody.create(MEDIA_TYPE_JPG, file);
            builder.addFormDataPart(i+"", filename, fileBody);
        }
        String jsonFilename = jsonfile.getName();
        RequestBody jsonBody = RequestBody.create(MEDIA_TYPE_JSON, jsonfile);

        builder.addFormDataPart(i+"", jsonFilename, jsonBody);

        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
                .url(DomainURL+"/challenge/upload")
                .post(requestBody)
                .build();
//        Response response = client.newCall(request).execute();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
            }
        }

        );

    }





}