package com.example.myapplication.ui.me;

import static com.example.myapplication.ui.me.MeFragment.DomainURL;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Adapter.UploadCheckAdapter;
import com.example.myapplication.Bean.CheckBean;
import com.example.myapplication.Bean.HistoryBean;
import com.example.myapplication.Dao.HistoryDao;
import com.example.myapplication.Dao.RecDataBase;
import com.example.myapplication.R;
import com.example.myapplication.Utils.CookieJarImpl;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ServerUploadActivity extends AppCompatActivity {

    private Button btn_upload_stars, btn_upload_unfamiliar;

    //选中后的数据

//    private Handler handler;
    private HistoryDao historyDao;
    private List<Cookie> cookie;
    private JSONObject server_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_upload);


        RecDataBase recDataBase = Room.databaseBuilder(this, RecDataBase.class, "RecDataBase")
                .allowMainThreadQueries().build();
        historyDao = recDataBase.historyDao();

        btn_upload_stars = findViewById(R.id.btn_upload_stars);
        btn_upload_unfamiliar = findViewById(R.id.btn_upload_unfamiliar);

        CookieJarImpl cookieJar = new CookieJarImpl(ServerUploadActivity.this);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .cookieJar(cookieJar).build();//创建OkHttpClient对象。

        List<HistoryBean> local_collect = historyDao.queryCollect();
        List<HistoryBean> local_unfamiliar = historyDao.queryNumLow3();



//        handler = new Handler(Looper.getMainLooper()){
//            @Override
//            public void handleMessage(Message msg) {
//                if(msg.what==1){
//                    mCheckAdapter.notifyDataSetChanged();
//                }
//            }
//        };

        btn_upload_stars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                localCompare_and_upload(client,
                        DomainURL + "/hist/filename_list_collect",
                        local_collect);
            }
        });

        btn_upload_unfamiliar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                localCompare_and_upload(client,
                        DomainURL + "/hist/filename_list_unfamiliar",
                        local_unfamiliar);
            }
        });

    }

    private void Upload(OkHttpClient client, String url, MultipartBody body){
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        cookie = client.cookieJar().loadForRequest(request.url());
        request.newBuilder().addHeader(cookie.get(0).name(), cookie.get(0).value());
        client.newCall(request).enqueue(new Callback(){
            @Override
            public void onFailure(Call call, IOException e) {
                if(Looper.myLooper()==null)
                    Looper.prepare();
                Toast.makeText(ServerUploadActivity.this,
                        "Fail to upload.",Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(Looper.myLooper()==null)
                    Looper.prepare();
                Toast.makeText(ServerUploadActivity.this,
                        "Successfully uploaded.",Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        });

    }
    private void localCompare_and_upload(OkHttpClient client, String url,
                                         List<HistoryBean> localList){
        //先发送一个请求获得服务器列表

        Request request = new Request.Builder()
                .url(url)
                .build();

        cookie = client.cookieJar().loadForRequest(request.url());
        request.newBuilder().addHeader(cookie.get(0).name(), cookie.get(0).value());

        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if(Looper.myLooper()==null)
                    Looper.prepare();
                Toast.makeText(ServerUploadActivity.this,
                        "Fail to get server list.",Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // 根据服务器列表添加文件或删除文件
                int fileNum = 0;  //用来数添加了几个文件的
                JSONObject info = new JSONObject();
                try {
                    server_list = new JSONObject(response.body().string());
                    //对本地问价进行循环
                    for(int i=0;i<localList.size();i++){
                        HistoryBean bean = localList.get(i);
                        // 如果服务器没有该文件，加入上传体

                        if(!server_list.has(bean.getFileName())){
                            File file = new File(bean.getPath());
                            multipartBuilder.addFormDataPart(Integer.toString(fileNum), bean.getFileName(),
                                    (RequestBody.create(MediaType.parse("image/*jpg"), file)));
                            JSONObject obj = new JSONObject();

                            obj.put("id", bean.getId());
                            obj.put("code", bean.getCode());
                            obj.put("enName", bean.getEnName());
                            obj.put("name", bean.getName());
                            obj.put("datetime", bean.getDateTime());
                            obj.put("proficiency", bean.getNum());
                            obj.put("if_star", bean.getIf_star());

                            //加到info.json里
                            String obj_string = obj.toString();
                            info.put(Integer.toString(fileNum), obj);
                            fileNum++;
                        }else{
                            // 如果服务器有，则从获得的服务器列表中删除该条目
                            server_list.remove(bean.getFileName());
                        }
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                //修改info_path, delete_info_path 选择合适的路径保存json文档，该文档可以放在和图片一样的路径
                String savePath = ServerUploadActivity.this.getFilesDir().getAbsolutePath();
                String info_path = savePath + "/info.json";
                String delete_info_path = savePath + "/delete.json";
                try {
                    // 写info.json到本地
                    FileOutputStream fos = new FileOutputStream(info_path);
                    OutputStreamWriter os = new OutputStreamWriter(fos);
                    BufferedWriter w = new BufferedWriter(os);
                    w.write(info.toString());
                    w.close();

                    // 写delete.json到本地
                    fos = new FileOutputStream(delete_info_path);
                    os = new OutputStreamWriter(fos);
                    w = new BufferedWriter(os);
                    w.write(server_list.toString());
                    w.close();

                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                File info_file = new File(info_path);
                File delete_file = new File(delete_info_path);
                multipartBuilder.addFormDataPart (Integer.toString(fileNum),"info.json",
                        (RequestBody.create(MediaType.parse("application/json;charset=utf-8"), info_file)));
                fileNum++;
                multipartBuilder.addFormDataPart(Integer.toString(fileNum),"delete.json",
                        (RequestBody.create(MediaType.parse("application/json;charset=utf-8"), delete_file)));

                // 发送上传请求
                Upload(client, DomainURL + "/hist/upload", multipartBuilder.build());

            }
        });
    }


}