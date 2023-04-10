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
import android.widget.CheckBox;
import android.widget.TextView;

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
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class ServerUploadActivity extends AppCompatActivity
        implements UploadCheckAdapter.CheckItemListener {

    private UploadCheckAdapter mCheckAdapter;
    private RecyclerView check_rcy_upload;
    private CheckBox check_all_cb_upload;
    private TextView server_upload;

    //列表数据
    private List<CheckBean> dataArray, checkedList;

    //选中后的数据
    private boolean isSelectAll;
    private Handler handler;
    private HistoryDao historyDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_upload);
        server_upload = findViewById(R.id.server_upload);

        checkedList = new ArrayList<>();
        RecDataBase recDataBase = Room.databaseBuilder(this, RecDataBase.class, "RecDataBase").build();
        historyDao = recDataBase.historyDao();
        initData();
        initViews();


        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==1){
                    mCheckAdapter.notifyDataSetChanged();
                }
            }
        };

        server_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 创建请求体
                MultipartBody.Builder builder = new MultipartBody.Builder();

                /**
                 * 把选择文件的信息写成json文档，格式例子如下:
                 * {
                 *   "0":
                 *      {"id": "000",
                 * 	    "code": "0000",
                 * 	    "enName": "mountain",
                 * 	    "name": "山",
                 * 	    "proficiency": "0"},
                 *
                 *   "1":
                 *      {"id": "001",
                 * 	    "code": "0001",
                 * 	    "enName": "beach",
                 * 	    "name": "海滩",
                 * 	    "proficiency": "0"}
                 * }
                 */
                JSONObject info = new JSONObject();
                // 遍历被选择的每一项
                for(int i=0;i<checkedList.size();i++){

                    //获取当前bean
                    CheckBean bean = checkedList.get(i);
                    String filepath = bean.getFilepath();
                    String filetype = bean.getFilename().split(".")[1];
                    File f = new File(filepath);
                    builder.addFormDataPart("files",Integer.toString(i),
                            (RequestBody.create(MediaType.parse("image/"+filetype), f)));
                    JSONObject obj = new JSONObject();
                    // 把获取的文件信息储存在json对象中
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            obj.put("id", bean.getId());
                            obj.put("code", bean.getCode());
                            obj.put("enName", bean.getEnName());
                            obj.put("proficiency", bean.getProficiency());

                            //加到json里
                            info.put(Integer.toString(i), info);
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
                RequestBody requestBody = builder.build();

                //todo 修改json_path, 选择合适的路径保存json文档，该文档可以放在和图片一样的路径
                String savePath = Environment.getDataDirectory().getAbsolutePath()+"/files/";
                String json_path = savePath + "info.json";
                try {
                    FileOutputStream fos = new FileOutputStream(json_path);
                    OutputStreamWriter os = new OutputStreamWriter(fos);
                    BufferedWriter w = new BufferedWriter(os);
                    w.write(info.toString());
                    w.close();
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                File json_file = new File(json_path);

                // 添加info.json文件在最后
                builder.addFormDataPart("files",Integer.toString(checkedList.size()),
                        (RequestBody.create(MediaType.parse("application/json;charset=utf-8"), json_file)));


                // 发送文件的网络请求
                String url = DomainURL + "/hist/upload";

                CookieJarImpl cookieJar = new CookieJarImpl(ServerUploadActivity.this);
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(5, TimeUnit.SECONDS)
                        .readTimeout(5, TimeUnit.SECONDS)
                        .cookieJar(cookieJar).build();//创建OkHttpClient对象。


                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();

                // 寻找对应的cookie
                List<Cookie> cookie = client.cookieJar().loadForRequest(request.url());

                request.newBuilder().addHeader(cookie.get(0).name(), cookie.get(0).value());
            }
        });

    }

    private void initData(){
        dataArray = new ArrayList<>();
        List<HistoryBean> list = historyDao.query();
        for(int i=0; i<20; i++){
            HistoryBean h=list.get(i);
            CheckBean bean = new CheckBean();
            // todo  这里做从本地数据库调取相关信息并保存到bean中
            bean.setEnName(h.getEnName());
            bean.setCode(h.getCode());
            bean.setDatetime(h.getDateTime());
            bean.setFilename(h.getFileName());
            bean.setFilepath(h.getPath());
            bean.setName(h.getName());

            // 放到dataArray里，dataArray是会显示出来的列表
            dataArray.add(bean);
        }

    }

    private void initViews(){
        check_rcy_upload = findViewById(R.id.check_rcy_upload);
        check_all_cb_upload = findViewById(R.id.check_all_cb_upload);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        check_rcy_upload.setLayoutManager(linearLayoutManager);

        mCheckAdapter = new UploadCheckAdapter(this, dataArray, this);
        check_rcy_upload.setAdapter(mCheckAdapter);

        check_all_cb_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSelectAll = !isSelectAll;
                checkedList.clear();

                if (isSelectAll) {//全选处理
                    checkedList.addAll(dataArray);
                }
                for (CheckBean checkBean : dataArray) {
                    checkBean.setChecked(isSelectAll);
                }

                mCheckAdapter.notifyDataSetChanged();
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
            check_all_cb_upload.setChecked(true);
        } else {
            check_all_cb_upload.setChecked(false);
        }
    }

}