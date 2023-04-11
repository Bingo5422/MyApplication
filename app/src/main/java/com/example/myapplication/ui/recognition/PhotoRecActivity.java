package com.example.myapplication.ui.recognition;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Bean.HistoryBean;
import com.example.myapplication.Bean.RecognitionBean;
import com.example.myapplication.Dao.HistoryDao;
import com.example.myapplication.Dao.RecDataBase;
import com.example.myapplication.Dao.RecognitionDao;
import com.example.myapplication.R;
import com.example.myapplication.Utils.RecBack;
import com.example.myapplication.Utils.RecognitionUtil;
import com.example.myapplication.Utils.VoiceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PhotoRecActivity extends AppCompatActivity {

    private ImageView recPhoto;
    private Bitmap bitmap;
    private String photoPath;
    private TextView recStart;
    private TextView recShow;
    private ImageView recVoice;

    private RecDataBase recDataBase;
    private RecognitionDao recDao;
    private HistoryDao historyDao;
    private RecognitionBean bean;
    private Button recBack;

    private static final String TAG = "PhotoRecActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_rec);
        recPhoto=findViewById(R.id.rec_photo);
        recShow=findViewById(R.id.Rec_result);
        recVoice=findViewById(R.id.Rec_voice);
        recBack=findViewById(R.id.title_back);
        recBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        Intent intent = getIntent();
        photoPath = intent.getStringExtra("path");//intent传来的照片路径
        bitmap = BitmapFactory.decodeFile(photoPath);
        recPhoto.setImageBitmap(bitmap);

        recStart=findViewById(R.id.rec_start);


        recDataBase = Room.databaseBuilder(this, RecDataBase.class, "RecDataBase").build();
        recDao = recDataBase.recognitionDao();

        historyDao = recDataBase.historyDao();

        loadExcel();


        //识别结果
        recStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PhotoRecActivity.this,"identifying",Toast.LENGTH_LONG).show();
                RecognitionUtil.startRecognition(photoPath, new RecBack() {
                    @Override
                    public void onFinished(String result) {
                        Log.d(TAG,"识别成功，result=="+result);
                        try{
                            JSONObject obj = new JSONObject(result);
                            JSONObject dataobj = obj.getJSONObject("data");
                            JSONArray array = dataobj.getJSONArray("fileList");
                            JSONObject jsonObject = array.getJSONObject(0);

                            String label = jsonObject.getString("label");
                            bean = (RecognitionBean) recDao.query(label);

                            //切换回主线程
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if(bean==null){
                                        Log.d(TAG,"没找到");
                                        Toast.makeText(PhotoRecActivity.this,"The item was not found. Please take another picture",Toast.LENGTH_LONG).show();
                                    }else {
                                        Log.d(TAG,"找到了，bean=="+bean);
                                        show(bean);
                                    }
                                }
                            });



                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        //读单词
        recVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bean!=null){
                    String chinese = bean.getName();
                    VoiceUtil.voice(PhotoRecActivity.this,chinese);
                }
            }
        });


    }

    private void loadExcel() {
        new Thread() {
            @Override
            public void run() {
                super.run();

                try {
                    InputStream in = getResources().openRawResource(R.raw.excel);
                    List<RecognitionBean> listData = RecognitionUtil.read(in);
//                    for (int i = 0; i < listData.size(); i++) {
//                        if (i>10){
//                            break;
//                        }
//                        Log.d(TAG, "run: 得到的数据 == "+ listData.get(i));
//                    }
                    //Log.d(TAG, "run: 总共的数据量 == "+ listData.size());

                    //把Excel放入数据库
                    try {
                        for (int i = 0; i < listData.size(); i++) {
                            RecognitionBean bean = listData.get(i);
                            recDao.insert(bean);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Log.d(TAG, "run: 已经添加到数据库了...");

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
        }.start();
    }
    private void show(RecognitionBean bean){
        if(bean!=null){
            String s = bean.getEnName()+"\n"+bean.getName();
            recShow.setText(s);
            saveResult(bean);
        }

    }


    //保存图片路径到数据库
    public void saveResult(RecognitionBean bean){

        new Thread(){
            @Override
            public void run() {
                super.run();
                HistoryBean historyBean = new HistoryBean();

                String time = showTime();
                String fileName=getFileName();


                Log.d(TAG, "time: "+time);
                Log.d(TAG, "fileName: "+fileName);

                historyBean.setPath(photoPath);
                historyBean.setId(bean.getId());
                historyBean.setCode(bean.getCode());
                historyBean.setEnName(bean.getEnName());
                historyBean.setName(bean.getName());
                historyBean.setDateTime(time);
                historyBean.setFileName(fileName);


                historyDao.insertHistory(historyBean);

            }
        }.start();

        Log.d(TAG, "saveResult: 插入历史数据成功");
    }

    public String showTime(){
        Date date = new Date();
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd :HH:mm:ss");


        return dateFormat.format(date);
    }

    public String getFileName(){
        String[] strs = photoPath.split("/");
        return strs[7];
    }


}