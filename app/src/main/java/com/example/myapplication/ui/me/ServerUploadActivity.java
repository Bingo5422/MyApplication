package com.example.myapplication.ui.me;

import static com.example.myapplication.ui.me.MeFragment.DomainURL;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
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
import com.example.myapplication.Utils.FileUtil;
import com.example.myapplication.Utils.OkHttpProgress.ProgressHelper;
import com.example.myapplication.Utils.OkHttpProgress.ProgressRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
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

/**
 * Synchronization page. Can handle all downloads and uploads
 */

public class ServerUploadActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_upload_stars, btn_upload_unfamiliar, btn_download_stars, btn_download_unfamiliar;

//    private Handler handler;
    private HistoryDao historyDao;
    private List<Cookie> cookie;
    private JSONObject server_list, star_list;
    private OkHttpClient client;
    //修改info_path, delete_info_path 选择合适的路径保存json文档，该文档可以放在和图片一样的路径
    private String savePath, info_path, download_path, delete_info_path;
    private int fileNum;

    private NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_upload);

        savePath = ServerUploadActivity.this.getFilesDir().getAbsolutePath();
        info_path = savePath + "/info.json";
        download_path = savePath + "/download_list.json";
        delete_info_path = savePath + "/delete.json";
        fileNum = 0;


        RecDataBase recDataBase = Room.databaseBuilder(this, RecDataBase.class, "RecDataBase")
                .allowMainThreadQueries().build();
        historyDao = recDataBase.historyDao();

        btn_upload_stars = findViewById(R.id.btn_upload_stars);
        btn_upload_unfamiliar = findViewById(R.id.btn_upload_unfamiliar);
        btn_download_stars = findViewById(R.id.btn_download_stars);
        btn_download_unfamiliar = findViewById(R.id.btn_download_unfamiliar);


        btn_upload_stars.setOnClickListener(this);

        btn_upload_unfamiliar.setOnClickListener(this);

        btn_download_stars.setOnClickListener(this);

        btn_download_unfamiliar.setOnClickListener(this);

    }

    private void Upload(OkHttpClient client, String url, MultipartBody body, String type){
        /********从这开始都是新加的********/
        String channel_msg = null;
        String content_title = null;
        final int notify_id;
        if(type=="star"){
            channel_msg = "message2";
            content_title = "Uploading your favorites";
            notify_id = 222;
        }else{
            channel_msg = "message3";
            content_title = "Uploading unfamiliar words";
            notify_id = 333;
        }
        // 创建渠道和notification用于展示下载进度
        createMessageNotificationChannel(channel_msg);
        NotificationCompat.Builder notify_builder =
                new NotificationCompat.Builder(ServerUploadActivity.this, channel_msg);
        notify_builder.setSmallIcon(R.drawable.ic_synchro) // //小图标
                .setContentTitle(content_title)  //通知标题
                .setAutoCancel(true);  //点击通知后关闭通知

        // 带监听器request实现
        ProgressRequestListener progressListener = new ProgressRequestListener() {
            @Override
            public void onRequestProgress(long bytesWrite, long contentLength, boolean done) {
                int progress = (int) ((100 * bytesWrite) / contentLength);
                notify_builder.setProgress(100, progress, false);
                notify_builder.setContentText(progress + "%");
                notificationManager.notify(notify_id, notify_builder.build());
            }
        };

        Request request = new Request.Builder()
                .url(url)
                .post(ProgressHelper.addProgressRequestListener(body, progressListener))
                .build();
        /***********到这结束**********/

//        Request request = new Request.Builder()
//                .url(url)
//                .post(body)
//                .build();

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
                JSONObject res_msg = null;
                try {
                    res_msg = new JSONObject(response.body().string().toString());
                    if(res_msg.getBoolean("if_success")){

                        /********从这开始都是新加的********/
                        notify_builder.setContentText("Complete");
                        notificationManager.notify(notify_id, notify_builder.build());
                        /********到这结束********/

                        if(Looper.myLooper()==null)
                            Looper.prepare();
                        Toast.makeText(ServerUploadActivity.this,
                                "Successfully uploaded.",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }else{
                        if(Looper.myLooper()==null)
                            Looper.prepare();
                        Toast.makeText(ServerUploadActivity.this,
                                res_msg.getString("message"),Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        });

    }

    private int UploadCompare(Response response, List<HistoryBean> localList,
                              MultipartBody.Builder multipartBuilder, int fileNum, String type){
        // 根据服务器列表添加文件或删除文件
        // int fileNum = 0;  //用来数添加了几个文件的
        JSONObject info = new JSONObject();
        try {
            String res = response.body().string();
            server_list = new JSONObject(res.toString());
            /**不分类的情况*/
            for(int i=0;i<localList.size();i++) {
                //所有信息都加入info
                HistoryBean bean = localList.get(i);
                JSONObject obj = new JSONObject();

                obj.put("id", bean.getId());
                obj.put("code", bean.getCode());
                obj.put("filename", bean.getFileName());
                obj.put("enName", bean.getEnName());
                obj.put("jpName", bean.getJpName());
                obj.put("korName", bean.getKorName());
                obj.put("FraName", bean.getFraName());
                obj.put("spaName", bean.getSpaName());
                obj.put("name", bean.getName());
                obj.put("datetime", bean.getDateTime());
                obj.put("proficiency", bean.getNum());
                if(type=="star"){
                    obj.put("if_star", bean.getIf_star());
                }else{
                    obj.put("if_star", 0);
                }
                // 备份生词本所有的收藏都设置为空，如果有需要则自己重新同步收藏


                info.put(bean.getFileName(), obj);


                //服务器没有的图片加入multipartbody准备上传
                //上传图片
                if(!server_list.has(bean.getFileName())) {
                    File file = new File(bean.getPath());
                    multipartBuilder.addFormDataPart(Integer.toString(fileNum), bean.getFileName(),
                            (RequestBody.create(MediaType.parse("image/*jpg"), file)));
                    fileNum++;
                }
                else{
                    server_list.remove(bean.getFileName());
                }

            }


        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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
        return fileNum;

    }
    private void localCompare_and_upload(OkHttpClient client, String url,
                                         List<HistoryBean> localList, String type){
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
                fileNum = 0;

                fileNum = UploadCompare(response, localList, multipartBuilder, fileNum, type);

                File info_file = new File(info_path);
                File delete_file = new File(delete_info_path);

                multipartBuilder.addFormDataPart (Integer.toString(fileNum),"info.json",
                        (RequestBody.create(MediaType.parse("application/json;charset=utf-8"), info_file)));
                fileNum++;
                multipartBuilder.addFormDataPart(Integer.toString(fileNum),"delete.json",
                        (RequestBody.create(MediaType.parse("application/json;charset=utf-8"), delete_file)));


                // 发送上传请求
                Upload(client, DomainURL + "/hist/upload?upload="+type, multipartBuilder.build(), type);

            }
        });
    }

    private JSONObject DownloadCompare(Response response, List<HistoryBean> localList) {
        // 根据服务器列表添加文件或删除文件
        // int fileNum = 0;  //用来数添加了几个文件的
        JSONObject info = new JSONObject();
        try {
            String res = response.body().string();
            server_list = new JSONObject(res.toString());
            Iterator keys = server_list.keys();
//            int i=0;
            while(keys.hasNext()){
                Object key = keys.next();
                String filename = key.toString();

                //根据文件名查询本地数据库，图片是否已经存在，若不存在再加入下载列表
                List<HistoryBean> b =  historyDao.queryByFilename(filename);
                if(b.isEmpty()) {
                    info.put(filename, 1);
//                    i++;
                }
            }
            return info;
//            for (int i = 0; i < localList.size(); i++) {
//                HistoryBean bean = localList.get(i);
//
//                if (server_list.has(bean.getFileName())) {
//                    // 如果本地有服务器也有该文件，就不下载了
//                    server_list.remove(bean.getFileName());
//                }
//            }


        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

            // 写download.json到本地
//            FileOutputStream fos = new FileOutputStream(download_path);
//            OutputStreamWriter os = new OutputStreamWriter(fos);
//            BufferedWriter w = new BufferedWriter(os);
//            w.write(info.toString());
//            w.close();
    }

    private void Download(OkHttpClient client, String url, JSONObject info){

        // 发送下载请求，是一个json
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), String.valueOf(info));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        cookie = client.cookieJar().loadForRequest(request.url());
        request.newBuilder().addHeader(cookie.get(0).name(), cookie.get(0).value());

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("fail to connect to server");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[4096];
                int len = 0;
                int sum = 0;
                FileOutputStream fos = null;
                float total = response.body().contentLength();

                // 创建渠道和notification用于展示下载进度
                createMessageNotificationChannel("message4");
                NotificationCompat.Builder notify_builder2 =
                        new NotificationCompat.Builder(ServerUploadActivity.this, "message4");
                notify_builder2.setSmallIcon(R.drawable.ic_synchro) // //小图标
                        .setContentTitle("Downloading unfamiliar words")  //通知标题
                        .setAutoCancel(true);  //点击通知后关闭通知

                try {
                    is = response.body().byteStream();

                    File file = new File(savePath,"pack.zip");
                    fos = new FileOutputStream(file);

                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        notify_builder2.setProgress(100, progress, false);
                        notify_builder2.setContentText(progress + "%");
                        notificationManager.notify(444, notify_builder2.build());
                        // 下载中
                    }
                    fos.flush();
                    notify_builder2.setContentText("Complete");
                    notificationManager.notify(444, notify_builder2.build());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                String zipPath = savePath + "/pack.zip";
                // 文件解压缩，zipPath是下载下来的压缩包路径，savePath是解压后输出文件路径
                FileUtil.unzip(zipPath, savePath+"/photos");

                //读取解压的下载文件信息
                File download_info = new File(savePath+"/photos/download_info.json");
                FileReader fileReader = new FileReader(download_info);
                Reader reader = new InputStreamReader(new FileInputStream(download_info), "Utf-8");
                int ch= 0;
                StringBuffer sb = new StringBuffer();
                while((ch = reader.read()) != -1) {
                    sb.append((char) ch);
                }
                fileReader.close();
                reader.close();

                //转json对象
                JSONObject download_info_json = null;
                try {
                    download_info_json = new JSONObject(sb.toString());
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                // 遍历download_info_json,把里面的信息和对应的图片存储到数据库中
                for(int i=0;i<download_info_json.length();i++) {
                    try {
                        JSONObject item = download_info_json.getJSONObject(String.valueOf(i));

                        HistoryBean historyBean = new HistoryBean();
                        historyBean.setName(item.getString("name"));
                        historyBean.setPath(savePath + "/photos/" + item.getString("filename"));
                        historyBean.setDateTime(item.getString("datetime"));
                        historyBean.setCode(item.getString("code"));
                        historyBean.setEnName(item.getString("enName"));
                        historyBean.setKorName(item.getString("korName"));
                        historyBean.setSpaName(item.getString("spaName"));
                        historyBean.setJpName(item.getString("jpName"));
                        historyBean.setFraName(item.getString("FraName"));
                        historyBean.setFileName(item.getString("filename"));
                        historyBean.setIf_star(item.getInt("if_star"));
                        historyBean.setNum(item.getInt("proficiency"));

                        historyDao.insertHistory(historyBean);


                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }

                if(Looper.myLooper()==null)
                    Looper.prepare();
                Toast.makeText(ServerUploadActivity.this,
                        "Successfully synchronized to local history.",Toast.LENGTH_SHORT).show();
                Looper.loop();


            }
        });
    }

    private void localCompare_and_download(OkHttpClient client, String url,
                                           List<HistoryBean> localList){

        Request request = new Request.Builder()
                .url(url)
                .build();

        cookie = client.cookieJar().loadForRequest(request.url());
        request.newBuilder().addHeader(cookie.get(0).name(), cookie.get(0).value());

//        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);

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

//                int fileNum=0;
                JSONObject info = DownloadCompare(response, localList);

//                File download_file = new File(download_path);
//                multipartBuilder.addFormDataPart (Integer.toString(fileNum),"download_list.json",
//                        (RequestBody.create(MediaType.parse("application/json;charset=utf-8"), download_file)));
//                fileNum++;

                String url = DomainURL + "/hist/download_zip";
                Download(client, url, info);


            }
        });

    }

    private void Star_download(JSONObject star_list){
        JSONObject json = new JSONObject();
        for(int i=0;i<star_list.length();i++){
            try {
                JSONObject item = star_list.getJSONObject(String.valueOf(i));
                String filename = item.getString("filename");
                //根据文件名查询本地数据库，图片是否已经存在，若不存在再加入下载列表
                List<HistoryBean> b =  historyDao.queryByFilename(filename);
                if(b.isEmpty()){
                    json.put(filename,1);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        // 发送下载请求，是一个包含文件名的json文档
        String url = DomainURL + "/hist/download_zip";
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), String.valueOf(json));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        cookie = client.cookieJar().loadForRequest(request.url());
        request.newBuilder().addHeader(cookie.get(0).name(), cookie.get(0).value());

        // 创建渠道和notification用于展示下载进度
        createMessageNotificationChannel("message1");
        NotificationCompat.Builder notify_builder = new NotificationCompat.Builder(ServerUploadActivity.this, "message1");
        notify_builder.setSmallIcon(R.drawable.ic_synchro) // //小图标
                .setContentTitle("Downloading your favorites")  //通知标题
                .setAutoCancel(true);  //点击通知后关闭通知


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("fail to connect to server");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[4096];
                int len = 0;
                int sum = 0;
                FileOutputStream fos = null;
                float total = response.body().contentLength();

                try {
                    is = response.body().byteStream();

                    File file = new File(savePath, "pack.zip");
                    fos = new FileOutputStream(file);

                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        // 下载中
                        //todo: 进度条显示
                        notify_builder.setProgress(100, progress, false);
                        notify_builder.setContentText(progress + "%");
                        notificationManager.notify(111, notify_builder.build());

                    }
                    fos.flush();
                    notify_builder.setContentText("Complete");
                    notificationManager.notify(111, notify_builder.build());
                    is.close();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                String zipPath = savePath + "/pack.zip";
                // 文件解压缩，zipPath是下载下来的压缩包路径，savePath是解压后输出文件路径
                FileUtil.unzip(zipPath, savePath + "/photos");

                // 本地的收藏夹清空
                historyDao.clearAllStars();
                // 遍历star_list，把每个被选中的条目信息保存到本地数据库，如果已经存在，则只把收藏设置为1
                for (int i = 0; i < star_list.length(); i++) {
                    try {
                        JSONObject item = star_list.getJSONObject(String.valueOf(i));
                        if (historyDao.queryByFilename(item.getString("filename")).isEmpty()) {
                            HistoryBean historyBean = new HistoryBean();
                            historyBean.setName(item.getString("name"));
                            historyBean.setPath(savePath + "/photos/" + item.getString("filename"));
                            historyBean.setDateTime(item.getString("datetime"));
                            historyBean.setCode(item.getString("code"));
                            historyBean.setEnName(item.getString("enName"));
                            historyBean.setFraName(item.getString("FraName"));
                            historyBean.setKorName(item.getString("korName"));
                            historyBean.setJpName(item.getString("jpName"));
                            historyBean.setSpaName(item.getString("spaName"));
                            historyBean.setFileName(item.getString("filename"));
                            historyBean.setIf_star(1);
                            historyDao.insertHistory(historyBean);
                        }
                        // 如果本地有，则设置为收藏
                        else {
                            historyDao.updateStar_byFilename(1, item.getString("filename"));
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }

                if (Looper.myLooper() == null)
                    Looper.prepare();
                Toast.makeText(ServerUploadActivity.this,
                        "Successfully synchronized to local star folder.", Toast.LENGTH_SHORT).show();
                Looper.loop();


            }
        });
    }
    private void starCompare_and_download(){
        String url = DomainURL + "/hist/list";
        Request request = new Request.Builder()
                .url(url)
                .build();
        cookie = client.cookieJar().loadForRequest(request.url());
        request.newBuilder().addHeader(cookie.get(0).name(), cookie.get(0).value());

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("wrong");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    star_list = new JSONObject(response.body().string());
                    Star_download(star_list);

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }


    // 创建通知渠道
    private void createMessageNotificationChannel(String msg_channel) {
        //Build.VERSION.SDK_INT 代表操作系统的版本号
        //Build.VERSION_CODES.O 版本号为26 对应的Android8.0版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = this.getString(R.string.app_name);
//            NotificationChannel channel = new NotificationChannel(
//                    MESSAGES_CHANNEL,
//                    name,
//                    NotificationManager.IMPORTANCE_HIGH
//            );
            NotificationChannel channel = new NotificationChannel(
                    msg_channel,
                    name,
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager = this.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onClick(View view) {

        CookieJarImpl cookieJar = new CookieJarImpl(ServerUploadActivity.this);
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .cookieJar(cookieJar).build();//创建OkHttpClient对象。

        if(view.getId()==R.id.btn_upload_stars){
            AlertDialog dialog = new AlertDialog.Builder(ServerUploadActivity.this)
                    .setTitle("Warn")//设置对话框的标题
                    .setMessage("This operation will overwrite all previous records. " +
                            "\nAre you sure you want to continue?")//设置对话框的内容
                    //设置对话框的按钮
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            List<HistoryBean> local_collect = historyDao.queryCollect();
                            localCompare_and_upload(client,
                                    DomainURL + "/hist/filename_list_collect",
                                    local_collect,"star");
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).create();
            dialog.show();

        }
        else if(view.getId()==R.id.btn_upload_unfamiliar){
            AlertDialog dialog = new AlertDialog.Builder(ServerUploadActivity.this)
                    .setTitle("Warn")//设置对话框的标题
                    .setMessage("This operation will overwrite all previous records and requires " +
                            "reuploading the favorites (server favorites will be cleared). " +
                            "\nAre you sure you want to continue?")//设置对话框的内容
                    //设置对话框的按钮
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            List<HistoryBean> local_unfamiliar = historyDao.queryNumLow3();
                            localCompare_and_upload(client,
                                    DomainURL + "/hist/filename_list_unfamiliar",
                                    local_unfamiliar, "unfamiliar");
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).create();
            dialog.show();

        }
        else if(view.getId()==R.id.btn_download_stars){
            starCompare_and_download();
        }
        else if(view.getId()==R.id.btn_download_unfamiliar){
            List<HistoryBean> local_unfamiliar = historyDao.queryNumLow3();
            localCompare_and_download(client,
                    DomainURL + "/hist/filename_list_unfamiliar",
                    local_unfamiliar);
        }
    }
}