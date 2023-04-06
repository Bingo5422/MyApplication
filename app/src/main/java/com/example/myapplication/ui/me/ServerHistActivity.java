package com.example.myapplication.ui.me;

import static com.example.myapplication.ui.me.MeFragment.DomainURL;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Adapter.CheckAdapter;
import com.example.myapplication.Bean.CheckBean;
import com.example.myapplication.Bean.HistoryBean;
import com.example.myapplication.Bean.RecognitionBean;
import com.example.myapplication.Dao.HistoryDao;
import com.example.myapplication.Dao.RecDataBase;
import com.example.myapplication.R;
import com.example.myapplication.Utils.CookieJarImpl;
import com.example.myapplication.Utils.FileUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ServerHistActivity extends AppCompatActivity implements CheckAdapter.CheckItemListener {


    private CheckAdapter mCheckAdapter;
    private RecyclerView check_rcy;

    //全选操作
    private CheckBox check_all_cb;

    //列表数据
    private List<CheckBean> dataArray, checkedList;

    //选中后的数据
    private boolean isSelectAll;

    private Bitmap bitmap;

    private TextView server_hist_delete, server_hist_download;

    private Handler handler;

    private HistoryDao historyDao;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_hist);
        server_hist_delete = findViewById(R.id.server_hist_delete);
        server_hist_download = findViewById(R.id.server_hist_download);

        RecDataBase recDataBase = Room.databaseBuilder(this, RecDataBase.class, "RecDataBase").build();
        historyDao = recDataBase.historyDao();


        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==1){
                    mCheckAdapter.notifyDataSetChanged();
                }
            }
        };


        checkedList = new ArrayList<>();
        initData();
        initViews();


        // 下载按钮触发
        server_hist_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 获取下载目录
                JSONObject json = new JSONObject();
                for(int i=0;i<checkedList.size();i++){

                    // 把获取的文件信息储存在json对象中
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            json.append(Integer.toString(i), checkedList.get(i).getFilename().toString());
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }

                // 发送下载请求
                String url = DomainURL + "/hist/get_zip";

                CookieJarImpl cookieJar = new CookieJarImpl(ServerHistActivity.this);
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(5, TimeUnit.SECONDS)
                        .readTimeout(5, TimeUnit.SECONDS)
                        .cookieJar(cookieJar).build();//创建OkHttpClient对象。

                RequestBody body = RequestBody.create(MediaType.parse("application/json"), String.valueOf(json));

                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();

                // 寻找对应的cookie
                List<Cookie> cookie = client.cookieJar().loadForRequest(request.url());

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
                        FileOutputStream fos = null;
                        // 储存下载文件的目录，目前只是测试用的路径
                        // todo: savePath: 服务器的图片会打包成zip下载到本地的位置，改成需要的路径

                        String savePath = Environment.getDataDirectory().getAbsolutePath()+"/files";

                        try {
                            is = response.body().byteStream();
                            long total = response.body().contentLength();

                            File file = new File(savePath,"pack.zip");
                            fos = new FileOutputStream(file);
                            long sum = 0;
                            while ((len = is.read(buf)) != -1) {
                                fos.write(buf, 0, len);
                                sum += len;
                                int progress = (int) (sum * 1.0f / total * 100);
                                // 下载中
                            }
                            fos.flush();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        String zipPath = savePath+"\\pack.zip";
                        // todo 文件解压缩，zipPath是下载下来的压缩包路径，savePath是解压后输出文件路径
                        FileUtil.unzip(zipPath, savePath+"/photos");

                        // todo: 遍历checkedList，把每个被选中的条目信息保存到本地数据库
                        // todo：每个CheckBean里有相关内容，但不要保存bitmap，用上面解压的图片来保存图片信息
                        for(int i=0;i<checkedList.size()-1;i++){
                            CheckBean bean =checkedList.get(i);
                            if(bean.isChecked()){
                                HistoryBean historyBean=new HistoryBean();
                                historyBean.setName(bean.getName());
                                historyBean.setPath(savePath+"/photos/"+bean.getName());
                                historyBean.setDateTime(bean.getDatetime());
                                historyBean.setCode(bean.getCode());
                                historyBean.setEnName(bean.getEnName());
                                historyBean.setFileName(bean.getFilename());
                                historyDao.insertHistory(historyBean);
                            }
                        }

                        if(Looper.myLooper()==null)
                            Looper.prepare();
                        Toast.makeText(ServerHistActivity.this,
                                "Successfully download",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                });


            }
        });

        server_hist_delete.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
            @Override
            public void onClick(View view) {

                JSONObject json = new JSONObject();
                for(int i=0;i<checkedList.size();i++){
                    // 把获取的文件信息储存在json对象中
                    try {
                        json.append(Integer.toString(i), checkedList.get(i).getFilename());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }

                // 发送下载请求
                String url = DomainURL + "/hist/delete";

                CookieJarImpl cookieJar = new CookieJarImpl(ServerHistActivity.this);
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(5, TimeUnit.SECONDS)
                        .readTimeout(5, TimeUnit.SECONDS)
                        .cookieJar(cookieJar).build();//创建OkHttpClient对象。

                RequestBody body = RequestBody.create(MediaType.parse("application/json"), String.valueOf(json));

                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();

                // 寻找对应的cookie
                List<Cookie> cookie = client.cookieJar().loadForRequest(request.url());

                request.newBuilder().addHeader(cookie.get(0).name(), cookie.get(0).value());

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                        System.out.println("fail to connect to server");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            JSONObject res_json = new JSONObject(response.body().string());
                            if(res_json.getBoolean("if_success")){
                                List<CheckBean> deleteBean=new ArrayList<>();
                                //在原本的数据列表中删除对应的bean
                                for(int i=0;i<checkedList.size();i++){
                                    CheckBean cb = checkedList.get(i);
                                    for(int j=0;j<dataArray.size();j++){
                                        if(cb.getFilename()==dataArray.get(j).getFilename()){
//                                            dataArray.remove(j);
                                            deleteBean.add(dataArray.get(j));
                                        }
                                    }
                                }
                                dataArray.removeAll(deleteBean);
                                // 清空被选择的项目
                                checkedList.clear();

                                // 更新ui
                                Message message = handler.obtainMessage();
                                message.what = 1;
                                handler.sendMessage(message);

                                if(Looper.myLooper()==null)
                                    Looper.prepare();
                                Toast.makeText(ServerHistActivity.this,
                                        "Delete Successfully",Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                            else{
                                Toast.makeText(ServerHistActivity.this,
                                        res_json.getString("message"),Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                });

            }
        });
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
        allPicInfoReq();

    }


    private void allPicInfoReq(){

        String url = DomainURL + "/hist/list";

        CookieJarImpl cookieJar = new CookieJarImpl(this);
        OkHttpClient client = new OkHttpClient.Builder().cookieJar(cookieJar).build();//创建OkHttpClient对象。
        // 为了正常格式的url创建的request对象
        Request request = new Request.Builder()
                .url(url)
                .build();

        // 寻找对应的cookie
        List<Cookie> cookie = client.cookieJar().loadForRequest(request.url());
        request.newBuilder().addHeader(cookie.get(0).name(), cookie.get(0).value());

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("wrong");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject res = new JSONObject(response.body().string());
                    for(int i=0; i<res.length(); i++){
                        JSONObject item = (JSONObject) res.get(Integer.toString(i));
                        CheckBean bean = new CheckBean();

                        bean.setId(item.getInt("id"));
                        bean.setFilename(item.getString("filename"));
                        bean.setCode(item.getString("code"));
                        bean.setEnName(item.getString("enName"));
                        bean.setName(item.getString("name"));
                        bean.setProficiency(item.getInt("proficiency"));
                        dataArray.add(bean);
                    }
                    Message message = handler.obtainMessage();
                    message.what = 1;
                    handler.sendMessage(message);

                    for(int i=0; i<dataArray.size(); i++){
                        picloadReq(dataArray.get(i));
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    // 暂时没用 可能需要调整
    private void picloadReq(CheckBean bean){
        String url = DomainURL+"/hist/download/name?name="+bean.getFilename();

        CookieJarImpl cookieJar = new CookieJarImpl(this);
        OkHttpClient client = new OkHttpClient.Builder().cookieJar(cookieJar).build();//创建OkHttpClient对象。
        // 为了正常格式的url创建的request对象
        Request request = new Request.Builder()
                .url(url)
                .build();

        // 寻找对应的cookie
        List<Cookie> cookie = client.cookieJar().loadForRequest(request.url());
        request.newBuilder().addHeader(cookie.get(0).name(), cookie.get(0).value());

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("wrong");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                byte[] byteArr = response.body().bytes();
                bitmap = BitmapFactory.decodeByteArray(byteArr, 0,byteArr.length);
                bean.setPic(bitmap);
                Message message = handler.obtainMessage();
                message.what = 1;
                handler.sendMessage(message);
            }
        });
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
