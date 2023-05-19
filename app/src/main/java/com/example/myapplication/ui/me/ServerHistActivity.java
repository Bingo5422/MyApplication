package com.example.myapplication.ui.me;


import static com.example.myapplication.MainActivity.DomainURL;
import static com.example.myapplication.ui.me.MeFragment.client;

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
import android.widget.ImageView;
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
     * The activity for viewing server favorites
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
    private TextView server_hist_delete, tv_server_hist_num;
    private Handler handler;
    private JSONObject json_list;
    private List<Cookie> cookie;
//    private OkHttpClient client;

    private AlertDialog dialog;
    private ImageView server_hist_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_hist);
        server_hist_delete = findViewById(R.id.server_hist_delete);
        tv_server_hist_num = findViewById(R.id.tv_server_hist_num);
        server_hist_back = findViewById(R.id.server_hist_back);


        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==1){ //if the message code is 1, update the list
                    tv_server_hist_num.setText("("+dataArray.size()+"/50)");
                    mCheckAdapter.notifyDataSetChanged();
                }
                if(msg.what==2){ //if the message code is 2, show the dialog
                    if(dialog.isShowing()){
                        dialog.setMessage("Deleting");
                    }
                }
            }
        };

        CookieJarImpl cookieJar = new CookieJarImpl(ServerHistActivity.this);
        client.newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .cookieJar(cookieJar).build();


        checkedList = new ArrayList<>();
        initData(); // initialize data to be display
        initViews(); // initialize view

        server_hist_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        server_hist_delete.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
            @Override
            public void onClick(View view) {
                // alert dialog for deletion confirmation
                dialog = new AlertDialog.Builder(ServerHistActivity.this)
                        .setTitle("Note")
                        .setMessage("Are you sure to delete? This operation cannot be undone.")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })

                        // if confirmed, send delete request
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Message message = handler.obtainMessage();
                                message.what = 2;
                                handler.sendMessage(message);

                                JSONObject json = new JSONObject();
                                // add the selected object to a json to send to the server for deleting
                                for(int i=0;i<checkedList.size();i++){
                                    try {
                                        json.append(Integer.toString(i), checkedList.get(i).getFilename());
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                }

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
                                                "Unable to delete. Please check your " +
                                                        "internet connection",Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {

                                        try {
                                            JSONObject res_json = new JSONObject(response.body().string());
                                            if(res_json.getBoolean("if_success")){
                                                List<CheckBean> deleteBean=new ArrayList<>();
                                                // delete the bean from the original data list
                                                for(int i=0;i<checkedList.size();i++){
                                                    CheckBean cb = checkedList.get(i);
                                                    for(int j=0;j<dataArray.size();j++){
                                                        if(cb.getFilename()==dataArray.get(j).getFilename()){
                                                            deleteBean.add(dataArray.get(j));
                                                        }
                                                    }
                                                }
                                                dataArray.removeAll(deleteBean);
                                                // clear the check list as the item checked are deleted
                                                checkedList.clear();

                                                // send message to refresh the list
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
        check_rcy = findViewById(R.id.check_rcy);
        check_all_cb = findViewById(R.id.check_all_cb);
        // linear adapter
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        check_rcy.setLayoutManager(linearLayoutManager);
        // dataArray: all data will be put in this list for display
        mCheckAdapter = new CheckAdapter(this, dataArray, this);
        check_rcy.setAdapter(mCheckAdapter);

        // if all item are checked
        check_all_cb.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                isSelectAll = !isSelectAll;
                checkedList.clear();
                if(isSelectAll){
                    checkedList.addAll(dataArray);
                }
                //set each item to checked
                for(CheckBean checkBean : dataArray){
                    checkBean.setChecked(isSelectAll);
                }
                mCheckAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initData(){
        dataArray = new ArrayList<>();
        String url = DomainURL + "/hist/list";  //obtain data list from the server
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
                Toast.makeText(ServerHistActivity.this,
                        "The record could not be loaded." +
                                " Please check your internet connection.",Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    json_list = new JSONObject(response.body().string());
                    // add the information to the list to be displayed
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
                    handler.sendMessage(message);  // refresh the UI

                    for(int i=0; i<dataArray.size(); i++){
                        picloadReq(dataArray.get(i)); // for each item, send a picture downloading request
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
            public void onFailure(Call call, IOException e) {}

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

    // overwrite the interface provided by the adapter
    @Override
    public void itemChecked(CheckBean checkBean, boolean isChecked) {
        if (isChecked) {
        //if an item is checked and not already exist in the checked list
            if (!checkedList.contains(checkBean)) {
                checkedList.add(checkBean); // add it to the checked list
            }
        } else {
            if (checkedList.contains(checkBean)) {
                checkedList.remove(checkBean);
            }
        }
        // if all the items are checked
        if (checkedList.size() == dataArray.size()) {
            check_all_cb.setChecked(true);
        } else {
            check_all_cb.setChecked(false);
        }
    }

}
