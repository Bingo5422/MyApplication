package com.example.myapplication.ui.me;

import static com.example.myapplication.ui.me.MeFragment.DomainURL;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.DialogInterface;
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

    /**
     * 这里是收藏界面，回头再改名
     */
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
    private JSONObject json_list;
    private List<Cookie> cookie;
    private OkHttpClient client;
    private HistoryDao historyDao;
    private AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_hist);
        server_hist_delete = findViewById(R.id.server_hist_delete);
//        server_hist_download = findViewById(R.id.server_hist_download);

        RecDataBase recDataBase = Room.databaseBuilder(this, RecDataBase.class, "RecDataBase")
                .allowMainThreadQueries().build();
        historyDao = recDataBase.historyDao();


        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==1){ //更新列表
                    mCheckAdapter.notifyDataSetChanged();
                }
                if(msg.what==2){ //更新alert dialog
                    if(dialog.isShowing()){
                        dialog.setMessage("Deleting");
                    }
                }
            }
        };

        CookieJarImpl cookieJar = new CookieJarImpl(ServerHistActivity.this);
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .cookieJar(cookieJar).build();//创建OkHttpClient对象。


        checkedList = new ArrayList<>();
        initData();
        initViews();

// download部分取消
//        /**下载按钮触发。这里就是同步到本地！！会覆盖本地的收藏夹**/
//        server_hist_download.setOnClickListener(new View.OnClickListener() {
//
//            //todo 加一个同步提示
//            @Override
//            public void onClick(View view) {
//                JSONObject json = new JSONObject();
//                for(int i=0;i<dataArray.size();i++){
//                    // 把获取的文件信息储存在json对象中
//                    try {
//                        String filename = dataArray.get(i).getFilename();
//                        //根据文件名查询本地数据库，图片是否已经存在，若不存在再加入下载列表
//                        List<HistoryBean> b =  historyDao.queryByFilename(filename);
//                        if(b.isEmpty()){
//                            json.put(filename, 1);
//                        }
//
//                    } catch (JSONException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//
//                // 发送下载请求，是一个包含文件名的json文档
//                String url = DomainURL + "/hist/download_zip";
//                RequestBody body = RequestBody.create(MediaType.parse("application/json"), String.valueOf(json));
//                Request request = new Request.Builder()
//                        .url(url)
//                        .post(body)
//                        .build();
//
//                cookie = client.cookieJar().loadForRequest(request.url());
//                request.newBuilder().addHeader(cookie.get(0).name(), cookie.get(0).value());
//
//
//                client.newCall(request).enqueue(new Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                        System.out.println("fail to connect to server");
//                    }
//
//                    @Override
//                    public void onResponse(Call call, Response response) throws IOException {
//                        InputStream is = null;
//                        byte[] buf = new byte[4096];
//                        int len = 0;
//                        FileOutputStream fos = null;
//                        // savePath: 服务器的图片会打包成zip下载到本地的位置，改成需要的路径
//                        String savePath = ServerHistActivity.this.getFilesDir().getAbsolutePath();
//                        try {
//                            is = response.body().byteStream();
//
//                            File file = new File(savePath,"pack.zip");
//                            fos = new FileOutputStream(file);
//
//                            while ((len = is.read(buf)) != -1) {
//                                fos.write(buf, 0, len);
////                        sum += len;
////                        int progress = (int) (sum * 1.0f / total * 100);
//                                // 下载中
//                            }
//                            fos.flush();
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        }
//
//                        String zipPath = savePath + "/pack.zip";
//                        // 文件解压缩，zipPath是下载下来的压缩包路径，savePath是解压后输出文件路径
//                        FileUtil.unzip(zipPath, savePath+"/photos");
//
//                        // 本地的收藏夹清空
//                        historyDao.clearAllStars();
//                        // 遍历dataArray，把每个被选中的条目信息保存到本地数据库，如果已经存在，则只把收藏设置为1
//                        // 每个CheckBean里有相关内容，但不要保存bitmap，用上面解压的图片来保存图片信息
//                        for(int i=0;i<dataArray.size();i++){
//                            CheckBean bean = dataArray.get(i);
//                            // 如果本地没有对应的数据，存相关信息
//                            if(historyDao.queryByFilename(bean.getFilename()).isEmpty()) {
//                                HistoryBean historyBean = new HistoryBean();
//                                historyBean.setName(bean.getName());
//                                historyBean.setPath(savePath + "/photos/" + bean.getFilename());
//                                historyBean.setDateTime(bean.getDatetime());
//                                historyBean.setCode(bean.getCode());
//                                historyBean.setEnName(bean.getEnName());
//                                historyBean.setNum(bean.getProficiency());
//                                historyBean.setFraName(bean.getFraName());
//                                historyBean.setJpName(bean.getJpName());
//                                historyBean.setSpaName(bean.getSpaName());
//                                historyBean.setKorName(bean.getKorName());
//                                historyBean.setFileName(bean.getFilename());
//                                historyBean.setIf_star(1);
//                                historyDao.insertHistory(historyBean);
//                            }
//                            // 如果本地有，则设置为收藏
//                            else{
//                                historyDao.updateStar_byFilename(1, bean.getFilename());
//                            }
//                        }
//
//                        if(Looper.myLooper()==null)
//                            Looper.prepare();
//                        Toast.makeText(ServerHistActivity.this,
//                                "Successfully synchronized to local star folder.",Toast.LENGTH_SHORT).show();
//                        Looper.loop();
//
//
//                    }
//                });
//
////                checkedList.clear(); // 清空被选择的所有项目
//
//            }
//        });

        server_hist_delete.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
            @Override
            public void onClick(View view) {

                dialog = new AlertDialog.Builder(ServerHistActivity.this)
                        .setTitle("Warn")//设置对话框的标题
                        .setMessage("Are you sure to delete? The operation cannot be undone.")//设置对话框的内容
                        //设置对话框的按钮
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Message message = handler.obtainMessage();
                                message.what = 2;
                                handler.sendMessage(message);

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
                                RequestBody body = RequestBody.create(MediaType.parse("application/json"),
                                        String.valueOf(json));
                                Request request = new Request.Builder()
                                        .url(url)
                                        .post(body)
                                        .addHeader(cookie.get(0).name(), cookie.get(0).value())
                                        .build();

                                client.newCall(request).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        dialog.dismiss();
                                        if(Looper.myLooper()==null)
                                            Looper.prepare();
                                        Toast.makeText(ServerHistActivity.this,
                                                "Delete Successfully",Toast.LENGTH_SHORT).show();
                                        Looper.loop();
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

                                                dialog.dismiss();

                                                if(Looper.myLooper()==null)
                                                    Looper.prepare();
                                                Toast.makeText(ServerHistActivity.this,
                                                        "Delete Successfully",Toast.LENGTH_SHORT).show();
                                                Looper.loop();
                                            }
                                            else{
                                                dialog.dismiss();
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
                        }).create();
                dialog.show();



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
                    json_list = new JSONObject(response.body().string());
                    for(int i=0; i<json_list.length(); i++){
                        JSONObject item = (JSONObject) json_list.get(Integer.toString(i));
                        CheckBean bean = new CheckBean();

                        bean.setId(item.getInt("id"));
                        bean.setFilename(item.getString("filename"));
                        bean.setCode(item.getString("code"));
                        bean.setEnName(item.getString("enName"));
                        bean.setName(item.getString("name"));
                        bean.setFraName(item.getString("FraName"));
                        bean.setJpName(item.getString("jpName"));
                        bean.setKorName(item.getString("korName"));
                        bean.setSpaName(item.getString("spaName"));
                        bean.setDatetime(item.getString("datetime"));
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

    private void picloadReq(CheckBean bean){
        String url = DomainURL+"/hist/preview/name?name="+bean.getFilename();

        // 为了正常格式的url创建的request对象
        Request request = new Request.Builder()
                .url(url)
                .addHeader(cookie.get(0).name(), cookie.get(0).value())
                .build();

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
