package com.example.myapplication.ui.me;


import static com.example.myapplication.MainActivity.DomainURL;
import static com.example.myapplication.ui.me.MeFragment.client;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Adapter.UploadCheckAdapter;
import com.example.myapplication.Bean.ChallengeBean;
import com.example.myapplication.Bean.CheckBean;
import com.example.myapplication.Bean.HistoryBean;
import com.example.myapplication.Dao.HistoryDao;
import com.example.myapplication.Dao.RecDataBase;
import com.example.myapplication.MainActivity;
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

    private HistoryDao historyDao;
    private List<Cookie> cookie;
    private JSONObject server_list, star_list;

    private String savePath, info_path, download_path, delete_info_path;
    private int fileNum;

    private NotificationManager notificationManager;
    private ImageView synchro_back;

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
        synchro_back = findViewById(R.id.synchro_back);


        btn_upload_stars.setOnClickListener(this);

        btn_upload_unfamiliar.setOnClickListener(this);

        btn_download_stars.setOnClickListener(this);

        btn_download_unfamiliar.setOnClickListener(this);

        synchro_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    // Upload function. Files are uploaded in this method
    private void Upload(OkHttpClient client, String url, MultipartBody body, String type){
        /**Notification channel configuration for different type of uploading**/
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
        // create notification channel to show progress
        createMessageNotificationChannel(channel_msg);
        NotificationCompat.Builder notify_builder =
                new NotificationCompat.Builder(ServerUploadActivity.this, channel_msg);
        notify_builder.setSmallIcon(R.drawable.ic_synchro)
                .setContentTitle(content_title)
                .setAutoCancel(true);

        // The implementation of the listener
        ProgressRequestListener progressListener = new ProgressRequestListener() {
            @Override
            public void onRequestProgress(long bytesWrite, long contentLength, boolean done) {
                // set the progress in the progressbar
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

        cookie = client.cookieJar().loadForRequest(request.url());
        request.newBuilder().addHeader(cookie.get(0).name(), cookie.get(0).value());
        client.newCall(request).enqueue(new Callback(){
            @Override
            public void onFailure(Call call, IOException e) {
                notify_builder.setContentText("Failed");
                notificationManager.notify(notify_id, notify_builder.build());
                if (Looper.myLooper()==null)
                    Looper.prepare();
                Toast.makeText(ServerUploadActivity.this,
                        "Unable to upload. Please check your internet connection.",Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject res_msg = null;
                try {
                    res_msg = new JSONObject(response.body().string().toString());
                    if(res_msg.getBoolean("if_success")){

                        /**upload finished, set the notification to show complete*/
                        notify_builder.setProgress(100, 100, false);
                        notify_builder.setContentText("Complete");
                        notificationManager.notify(notify_id, notify_builder.build());

                        if(Looper.myLooper()==null)
                            Looper.prepare();
                        Toast.makeText(ServerUploadActivity.this,
                                "Successfully uploaded.",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }else{
                        /**upload failed, show relevant message */
                        notify_builder.setContentText("Interrupted");
                        notificationManager.notify(notify_id, notify_builder.build());

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

    // the method to get server list and compare with local list for uploading
    private int UploadCompare(Response response, List<HistoryBean> localList,
                              MultipartBody.Builder multipartBuilder, int fileNum, String type){

        JSONObject info = new JSONObject();
        try {
            String res = response.body().string();
            server_list = new JSONObject(res.toString());

            for(int i=0;i<localList.size();i++) {

                // all information need to be uploaded
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
                    // all the unfamiliar words uploaded are marked as not favorite
                    obj.put("if_star", 0);
                }
                info.put(bean.getFileName(), obj);


                // the files that server does not have will be uploaded by adding to the multipartbody
                if(!server_list.has(bean.getFileName())) {
                    File file = new File(bean.getPath());
                    String filetype = bean.getFileName().split("\\.")[1];
                    multipartBuilder.addFormDataPart(Integer.toString(fileNum), bean.getFileName(),
                            (RequestBody.create(MediaType.parse("image/*"+filetype), file)));
                    fileNum++;
                }
                else{ // if the server has the file, remove it from the server list
                    server_list.remove(bean.getFileName());
                }

            }


        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            // write the info.json to local. This file contains the information to be uploaded
            FileOutputStream fos = new FileOutputStream(info_path);
            OutputStreamWriter os = new OutputStreamWriter(fos);
            BufferedWriter w = new BufferedWriter(os);
            w.write(info.toString());
            w.close();

            // write the delete.json to local. this file contains the files to be deleted on server
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

    // synthesize the compare and upload process
    private void localCompare_and_upload(OkHttpClient client, String url,
                                         List<HistoryBean> localList, String type){

        Request request = new Request.Builder()
                .url(url)
                .build();

        cookie = client.cookieJar().loadForRequest(request.url());
        request.newBuilder().addHeader(cookie.get(0).name(), cookie.get(0).value());

        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (Looper.myLooper()==null)
                    Looper.prepare();
                Toast.makeText(ServerUploadActivity.this,
                        "Unable to upload. Please check your internet connection.",Toast.LENGTH_SHORT).show();
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


                // Upload
                Upload(client, DomainURL + "/hist/upload?upload="+type, multipartBuilder.build(), type);

            }
        });
    }

    // compare files before downloading
    private JSONObject DownloadCompare(Response response, List<HistoryBean> localList) {
        JSONObject info = new JSONObject();
        try {
            String res = response.body().string();
            server_list = new JSONObject(res.toString());
            Iterator keys = server_list.keys();
            while(keys.hasNext()){
                Object key = keys.next();
                String filename = key.toString();

                // Query the local database according to the file
                // name to check whether the image already exists.
                // If it does not exist, add it to the download list
                List<HistoryBean> b =  historyDao.queryByFilename(filename);
                if(b.isEmpty()) {
                    info.put(filename, 1);
                }
            }
            return info;


        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    // download files
    private void Download(OkHttpClient client, String url, JSONObject info){

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
                if(Looper.myLooper()==null)
                    Looper.prepare();
                Toast.makeText(ServerUploadActivity.this,
                        "Unable to download. Please check your internet connection.",Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[4096];
                int len = 0;
                int sum = 0;
                FileOutputStream fos = null;
                float total = response.body().contentLength();

                createMessageNotificationChannel("message4");
                NotificationCompat.Builder notify_builder2 =
                        new NotificationCompat.Builder(ServerUploadActivity.this, "message4");
                notify_builder2.setSmallIcon(R.drawable.ic_synchro)
                        .setContentTitle("Downloading unfamiliar words")
                        .setAutoCancel(true);

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
                        // downloading
                    }
                    fos.flush();
                    notify_builder2.setContentText("Complete");
                    notificationManager.notify(444, notify_builder2.build());
                    is.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                String zipPath = savePath + "/pack.zip";
                //unzip the downloaded zip
                FileUtil.unzip(zipPath, savePath+"/photos");

                //read the corresponding information
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

                JSONObject download_info_json = null;
                try {
                    download_info_json = new JSONObject(sb.toString());
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                // traverse download_info_json, store the information to the local database
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

    // synthesize the downloading compare and downloading process
    private void localCompare_and_download(OkHttpClient client, String url,
                                           List<HistoryBean> localList){

        Request request = new Request.Builder()
                .url(url)
                .build();

        cookie = client.cookieJar().loadForRequest(request.url());
        request.newBuilder().addHeader(cookie.get(0).name(), cookie.get(0).value());

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if(Looper.myLooper()==null)
                    Looper.prepare();
                Toast.makeText(ServerUploadActivity.this,
                        "Unable to download. Please check your internet connection",Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                JSONObject info = DownloadCompare(response, localList);


                String url = DomainURL + "/hist/download_zip";
                Download(client, url, info);


            }
        });

    }

    // method for downloading favorites
    private void Star_download(JSONObject star_list){
        JSONObject json = new JSONObject();
        for(int i=0;i<star_list.length();i++){
            try {
                JSONObject item = star_list.getJSONObject(String.valueOf(i));
                String filename = item.getString("filename");
                List<HistoryBean> b =  historyDao.queryByFilename(filename);
                if(b.isEmpty()){
                    json.put(filename,1);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        String url = DomainURL + "/hist/download_zip";
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), String.valueOf(json));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        cookie = client.cookieJar().loadForRequest(request.url());
        request.newBuilder().addHeader(cookie.get(0).name(), cookie.get(0).value());

        createMessageNotificationChannel("message1");
        NotificationCompat.Builder notify_builder = new NotificationCompat.Builder(ServerUploadActivity.this, "message1");
        notify_builder.setSmallIcon(R.drawable.ic_synchro)
                .setContentTitle("Downloading your favorites")
                .setAutoCancel(true);


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if(Looper.myLooper()==null)
                    Looper.prepare();
                Toast.makeText(ServerUploadActivity.this,
                        "Unable to download." +
                                " Please check your internet connection.",Toast.LENGTH_SHORT).show();
                Looper.loop();
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
                    if (!file.getParentFile().exists()) {
                        if (!file.getParentFile().mkdirs()) {
                            Log.e("Error", "Failed to create directory");
                        }
                    }
                    fos = new FileOutputStream(file);

                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        // downloading
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
                FileUtil.unzip(zipPath, savePath + "/photos");

                // clear the local favorites
                historyDao.clearAllStars();
                /**Iterate over the star_list, saving the information for each selected item
                 * to the local database, and only set if_star to 1 if it already exists**/
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
                        // if the file is available locally
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

    // synthesize favorite download process
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
                if(Looper.myLooper()==null)
                    Looper.prepare();
                Toast.makeText(ServerUploadActivity.this,
                        "Unable to download." +
                                " Please check your internet connection.",Toast.LENGTH_SHORT).show();
                Looper.loop();
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


    // method for creating notification channel
    private void createMessageNotificationChannel(String msg_channel) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = this.getString(R.string.app_name);

            NotificationChannel channel = new NotificationChannel(
                    msg_channel,
                    name,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager = this.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onClick(View view) {

        CookieJarImpl cookieJar = new CookieJarImpl(ServerUploadActivity.this);
        client.newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .cookieJar(cookieJar).build();//创建OkHttpClient对象。


        if(view.getId()==R.id.btn_upload_stars){
            AlertDialog dialog = new AlertDialog.Builder(ServerUploadActivity.this)
                    .setTitle("Note")
                    .setMessage("Note: This operation will overwrite your server favorites. " +
                            "\n\nAre you sure you want to continue?")

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
                    .setTitle("Note")
                    .setMessage("Note: This operation will overwrite your server unfamiliar words and " +
                            "the server favorites will be affected. " +
                            "\n\nAre you sure to continue?")

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
            AlertDialog dialog = new AlertDialog.Builder(ServerUploadActivity.this)
                    .setTitle("Note")
                    .setMessage("Note: This operation will overwrite your local favorites" +
                            "\n\nAre you sure to continue?")

                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            starCompare_and_download();
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
        else if(view.getId()==R.id.btn_download_unfamiliar){
            AlertDialog dialog = new AlertDialog.Builder(ServerUploadActivity.this)
                    .setTitle("Note")
                    .setMessage(
                            "Note: All unfamiliar words downloaded will not be marked as favorites.\n\n" +
                                    "Are you sure you want to continue?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            List<HistoryBean> local_unfamiliar = historyDao.queryNumLow3();
                            localCompare_and_download(client,
                                    DomainURL + "/hist/filename_list_unfamiliar",
                                    local_unfamiliar);
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
    }
}