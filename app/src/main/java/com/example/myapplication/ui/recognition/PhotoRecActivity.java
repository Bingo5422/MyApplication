package com.example.myapplication.ui.recognition;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.myapplication.Utils.BaiDuApi.BaiDuRecUtil;
import com.example.myapplication.Utils.RecBack;
import com.example.myapplication.Utils.RecognitionUtil;
import com.example.myapplication.Utils.VoiceUtil;
import com.example.myapplication.Utils.translate.TransApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
    private static SharedPreferences sp;
    private SharedPreferences ex;

    private String name;
    private String enName;

    private String textFra;
    private String textSpa;
    private String textJp;
    private String textKor;
    private String currentApi;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_rec);



        recPhoto = findViewById(R.id.rec_photo);
        recShow = findViewById(R.id.Rec_result);
        recVoice = findViewById(R.id.Rec_voice);
        recBack = findViewById(R.id.title_back);
        recBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        Intent intent = getIntent();
        photoPath = intent.getStringExtra("path");//intent传来的照片路径
        Log.d(TAG, "??" + photoPath);
        bitmap = BitmapFactory.decodeFile(photoPath);
        recPhoto.setImageBitmap(bitmap);



        recStart = findViewById(R.id.rec_start);


        recDataBase = Room.databaseBuilder(this, RecDataBase.class, "RecDataBase").build();
        recDao = recDataBase.recognitionDao();

        historyDao = recDataBase.historyDao();

        ex = getSharedPreferences("Excel", Context.MODE_PRIVATE);
        String isLoad = ex.getString("isLoad","");

        if(!isLoad.equals("1")){
            loadExcel();
        }

        Log.d(TAG, "已经加载过excel了" );




        currentApi = intent.getStringExtra("api");
        Log.d(TAG, "传到识别的API是：" + currentApi);




        //识别结果
        recStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(PhotoRecActivity.this, "identifying", Toast.LENGTH_LONG).show();


                if(currentApi.equals("Xunfei")){
                    //讯飞
                    RecognitionUtil.startRecognition(photoPath, new RecBack() {
                        @Override
                        public void onFinished(String result) {
                            Log.d(TAG, "当前正在使用讯飞的api" + currentApi);
                            Log.d(TAG, "识别成功，result==" + result);

                            try {
                                JSONObject obj = new JSONObject(result);
                                JSONObject dataobj = obj.getJSONObject("data");
                                JSONArray array = dataobj.getJSONArray("fileList");
                                JSONObject jsonObject = array.getJSONObject(0);

                                String label = jsonObject.getString("label");
                                bean = (RecognitionBean) recDao.query(label);
                                if (bean == null) {
                                    recShow.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.d(TAG, "没找到");
                                            Toast.makeText(PhotoRecActivity.this, "The item was not found. Please take another picture", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                } else {
                                    Log.d(TAG, "讯飞的识别结果，找到了，bean==" + bean);
                                    name = bean.getName();
                                    enName = bean.getEnName();
                                    if (bean != null) {
                                        //先判断目标语言，减少等待时间
                                        String to = decideLan(name,enName);
                                        getTargetLan(name,enName,to);
                                        //日语
                                        Thread.sleep(1000);
                                        String text = TransApi.getTransResult(name, "zh", "jp");
                                        Log.d(TAG, "text:" + text);
                                        //西班牙语
                                        Thread.sleep(1000);
                                        String text2 = TransApi.getTransResult(name, "zh", "spa");
                                        Log.d(TAG, "text2:" + text2);
                                        //韩语
                                        Thread.sleep(1000);
                                        String text3 = TransApi.getTransResult(name, "zh", "kor");
                                        Log.d(TAG, "text3:" + text3);
                                        //法语
                                        Thread.sleep(1000);
                                        String text4 = TransApi.getTransResult(name, "zh", "fra");
                                        Log.d(TAG, "text4:" + text4);

                                        textJp=JsonToString(text);
                                        textSpa=JsonToString(text2);
                                        textKor=JsonToString(text3);
                                        textFra=JsonToString(text4);
                                        Log.d(TAG, "日语翻译"+textJp);
                                        Log.d(TAG, "西班牙语翻译"+textSpa);
                                        Log.d(TAG, "韩语翻译"+textKor);
                                        Log.d(TAG, "法语翻译"+textFra);


                                        HistoryBean historyBean = new HistoryBean();
                                        String time = showTime();
                                        String fileName = getFileName();
                                        Log.d(TAG, "time: " + time);
                                        Log.d(TAG, "fileName: " + fileName);

                                        historyBean.setPath(photoPath);
                                        historyBean.setId(bean.getId());
                                        historyBean.setCode(bean.getCode());
                                        historyBean.setEnName(bean.getEnName());
                                        historyBean.setName(bean.getName());
                                        historyBean.setDateTime(time);
                                        historyBean.setFileName(fileName);
                                        historyBean.setJpName(textJp);
                                        historyBean.setSpaName(textSpa);
                                        historyBean.setKorName(textKor);
                                        historyBean.setFraName(textFra);
                                        String[] s = time.split(" ");
                                        historyBean.setAddDate(s[0]);
                                        historyBean.setAddTime(s[1]);
                                        historyDao.insertHistory(historyBean);
                                    }

                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                }else if(currentApi.equals("Baidu")){

                    //百度
                    BaiDuRecUtil.baiduRec(photoPath, new RecBack() {
                        @Override
                        public void onFinished(String re) {
                            Log.d(TAG, "当前正在使用百度的api：" + currentApi);
                            Log.d(TAG, "百度的识别结果："+re);
                            try{
                                JSONObject obj = new JSONObject(re);
                                JSONArray reArray = obj.getJSONArray("result");
                                JSONObject keyword = reArray.getJSONObject(0);
                                name = keyword.getString("keyword");
                                Log.d(TAG, "百度的中文识别结果："+name);

                                //英语
                                String tranJsonEn = TransApi.getTransResult(name, "zh", "en");
                                JSONObject enJson = new JSONObject(tranJsonEn);
                                if (enJson.has("trans_result")) {
                                    enName = enJson.getJSONArray("trans_result").getJSONObject(0).getString("dst");
                                    Thread.sleep(1000);
                                    Log.d(TAG, "英语翻译"+enName);
                                } else {
                                    recShow.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.d(TAG, "英语翻译失败");
                                            Toast.makeText(PhotoRecActivity.this, "translate fail", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    return;
                                }

                                String to = decideLan(name,enName);
                                //提前打印输出
                                getTargetLan(name,enName,to);

                                //日语
                                Thread.sleep(1000);
                                String text = TransApi.getTransResult(name, "zh", "jp");
                                Log.d(TAG, "text:" + text);
                                //西班牙语
                                Thread.sleep(1000);
                                String text2 = TransApi.getTransResult(name, "zh", "spa");
                                Log.d(TAG, "text2:" + text2);
                                //韩语
                                Thread.sleep(1000);
                                String text3 = TransApi.getTransResult(name, "zh", "kor");
                                Log.d(TAG, "text3:" + text3);
                                //法语
                                Thread.sleep(1000);
                                String text4 = TransApi.getTransResult(name, "zh", "fra");
                                Log.d(TAG, "text4:" + text4);


                                textJp=JsonToString(text);
                                textSpa=JsonToString(text2);
                                textKor=JsonToString(text3);
                                textFra=JsonToString(text4);
                                Log.d(TAG, "日语翻译"+textJp);
                                Log.d(TAG, "西班牙语翻译"+textSpa);
                                Log.d(TAG, "韩语翻译"+textKor);
                                Log.d(TAG, "法语翻译"+textFra);


                                HistoryBean historyBean = new HistoryBean();
                                String time = showTime();
                                String fileName = getFileName();
                                Log.d(TAG, "time: " + time);
                                Log.d(TAG, "fileName: " + fileName);

                                historyBean.setPath(photoPath);
//                            historyBean.setId(bean.getId());
//                            historyBean.setCode(bean.getCode());
                                historyBean.setEnName(enName);
                                historyBean.setName(name);
                                historyBean.setDateTime(time);
                                historyBean.setFileName(fileName);
                                historyBean.setJpName(textJp);
                                historyBean.setSpaName(textSpa);
                                historyBean.setKorName(textKor);
                                historyBean.setFraName(textFra);
                                String[] s = time.split(" ");
                                historyBean.setAddDate(s[0]);
                                historyBean.setAddTime(s[1]);
                                historyDao.insertHistory(historyBean);



                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }


                        }
                    });
                }













            }
        });

        //读单词
//        recVoice.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                    String lan = sp.getString("lan", "Chinese");
//                    if (lan.equals("Spanish")) {
//                        String spanish = textSpa;
//                        VoiceUtil.voice(PhotoRecActivity.this, spanish,"x2_SpEs_Aurora");
//                    } else if (lan.equals("Japanese")) {
//                        String japanese = textJp;
//                        VoiceUtil.voice(PhotoRecActivity.this, japanese,"x2_JaJp_ZhongCun");
//                    } else if (lan.equals("Korean")) {
//                        String korean = textKor;
//                        VoiceUtil.voice(PhotoRecActivity.this, korean,"zhimin");
//                    } else if (lan.equals("French")) {
//                        String french = textFra;
//                        VoiceUtil.voice(PhotoRecActivity.this, french,"x2_FrRgM_Lisa");
//                    } else {
//                        //String chinese = name;
//                        VoiceUtil.voice(PhotoRecActivity.this, name,"aisxping");
//                    }
//
//
//            }
//        });


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

                    Log.d(TAG, "run: 超大的Excel已经添加到数据库了...");
                    ex = getSharedPreferences("Excel",MODE_PRIVATE);
                    SharedPreferences.Editor editor =ex.edit();
                    editor.putString("isLoad","1");
                    editor.commit();

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
        }.start();
    }


    public String showTime() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd :HH:mm:ss");

        return dateFormat.format(date);
    }

    public String getFileName() {

        File tempFile =new File( photoPath.trim());
        String fileName = tempFile.getName();
        return fileName;
    }


    public void getTargetLan(String name,String enName,String to) throws JSONException {

        String text = TransApi.getTransResult(name, "zh", to);
        JSONObject tranJson = new JSONObject(text);
        if (tranJson.has("trans_result")) {
            String targetName = tranJson.getJSONArray("trans_result").getJSONObject(0).getString("dst");
            Log.d(TAG, "目标提前翻译成功："+targetName);
            recShow.post(new Runnable() {
                @SuppressLint("SetTextI18n")
                @Override
                public void run() {
                    recShow.setText(targetName+"\n"+enName);
                }
            });
            recVoice.post(new Runnable() {
                @Override
                public void run() {
                    recVoice.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (to.equals("Spa")) {
                                VoiceUtil.voice(PhotoRecActivity.this, targetName,"x2_SpEs_Aurora");
                            } else if (to.equals("jp")) {
                                VoiceUtil.voice(PhotoRecActivity.this, targetName,"x2_JaJp_ZhongCun");
                            } else if (to.equals("kor")) {
                                VoiceUtil.voice(PhotoRecActivity.this, targetName,"zhimin");
                            } else if (to.equals("fra")) {
                                VoiceUtil.voice(PhotoRecActivity.this, targetName,"x2_FrRgM_Lisa");
                            }

                        }
                    });
                }
            });

        } else {
            Log.d(TAG, "目标提前翻译失败或选的是中文");
        }

    }

    public String decideLan(String name,String enName){

        sp = getSharedPreferences("sp", Context.MODE_PRIVATE);
        String lan = sp.getString("lan", "Chinese");
        String to=null ;
        if(lan.equals("Japanese")){
            to = "jp";
        }else if(lan.equals("Korean")){
            to = "kor";
        }else if(lan.equals("Spanish")){
            to="spa";
        } else if (lan.equals("French")) {
            to = "fra";
        }else{
            recShow.post(new Runnable() {
                @SuppressLint("SetTextI18n")
                @Override
                public void run() {
                    Log.d(TAG, "提前翻译中文");
                    recShow.setText(name + "\n" + enName);
                }
            });
            recVoice.post(new Runnable() {
                @Override
                public void run() {
                    recVoice.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            VoiceUtil.voice(PhotoRecActivity.this, name,"aisxping");
                        }
                    });
                }
            });
        }
        Log.d(TAG, "to的值为："+to);
        return to;
    }

    public String JsonToString(String text) throws JSONException {
        JSONObject tranJson = new JSONObject(text);
        String textTrans = null;
        if (tranJson.has("trans_result")) {
            textTrans = tranJson.getJSONArray("trans_result").getJSONObject(0).getString("dst");
        } else {
            recShow.post(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "Json转换String失败");
                    Toast.makeText(PhotoRecActivity.this, "translate fail", Toast.LENGTH_LONG).show();
                }
            });
        }
        return textTrans;
    }

}