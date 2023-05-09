package com.example.myapplication.ui.notifications;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.myapplication.Adapter.ChallengeAdapter;
import com.example.myapplication.Bean.HistoryBean;
import com.example.myapplication.Dao.HistoryDao;
import com.example.myapplication.Dao.RecDataBase;
import com.example.myapplication.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    public static final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpeg");
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json");
    private List<HistoryBean> dataList = new ArrayList<>();
    private List<String> selectedPhotoNameList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_challenge);


        recDataBase = Room.databaseBuilder(this, RecDataBase.class, "RecDataBase").allowMainThreadQueries().build();
        historyDao = recDataBase.historyDao();




        back = findViewById(R.id.challenge_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPhotoNameList = challengeAdapter.getSelectedPhotoNameList();
                Log.d(TAG, "onCreate: 收到返回的照片名list："+selectedPhotoNameList);
                finish();
                Toast.makeText(UploadChallenge.this,"Your challenge is sent!", Toast.LENGTH_LONG).show();
            }
        });


        challenge_rv = findViewById(R.id.challenge_list);



        //网格布局 3列
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,3);
        challenge_rv.setLayoutManager(gridLayoutManager);

        challengeAdapter = new ChallengeAdapter();

        dataList.addAll(historyDao.query());

        challengeAdapter.setList(dataList);
        challenge_rv.setAdapter(challengeAdapter);



    }

    public static List<File> packPhoto(List<HistoryBean> dataList){

        return null;
    }


    public void uploadFiles(List<File> fileList, List<File> jsonList) throws IOException {
        OkHttpClient client = new OkHttpClient();

        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        for (int i = 0; i < fileList.size(); i++) {
            File file = fileList.get(i);
            File jsonFile = jsonList.get(i);

            String filename = file.getName();
            RequestBody fileBody = RequestBody.create(MEDIA_TYPE_JPG, file);
            builder.addFormDataPart("files", filename, fileBody);

            String jsonFilename = jsonFile.getName();
            RequestBody jsonBody = RequestBody.create(MEDIA_TYPE_JSON, jsonFile);
            builder.addFormDataPart("json", jsonFilename, jsonBody);
        }

        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
                .url("http://yourserver/upload")
                .post(requestBody)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
    }



}