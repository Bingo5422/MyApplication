package com.example.myapplication.ui.recognition;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

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
import com.example.myapplication.Utils.RecBack;
import com.example.myapplication.Utils.RecognitionUtil;
import com.example.myapplication.Utils.VoiceUtil;
import com.example.myapplication.Utils.translate.TransApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_rec);
        sp = getSharedPreferences("sp", Context.MODE_PRIVATE);

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



        //识别结果
        recStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(PhotoRecActivity.this, "identifying", Toast.LENGTH_LONG).show();
                RecognitionUtil.startRecognition(photoPath, new RecBack() {
                    @Override
                    public void onFinished(String result) {
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
                                Log.d(TAG, "找到了，bean==" + bean);

                                if (bean != null) {

                                    //日语
                                    String text = TransApi.getTransResult(bean.getName(), "zh", "jp");
                                    Log.d(TAG, "text:" + text);
                                    //西班牙语
                                    Thread.sleep(1000);
                                    String text2 = TransApi.getTransResult(bean.getName(), "zh", "spa");
                                    Log.d(TAG, "text2:" + text2);
                                    //韩语
                                    Thread.sleep(1000);
                                    String text3 = TransApi.getTransResult(bean.getName(), "zh", "kor");
                                    Log.d(TAG, "text3:" + text3);
                                    //法语
                                    Thread.sleep(1000);
                                    String text4 = TransApi.getTransResult(bean.getName(), "zh", "fra");
                                    Log.d(TAG, "text4:" + text4);

                                    try {
                                        JSONObject tranJson = new JSONObject(text);
                                        if (tranJson.has("trans_result")) {
                                            String textJp = tranJson.getJSONArray("trans_result").getJSONObject(0).getString("dst");
                                            bean.setJpName(textJp);
                                            Log.d(TAG, "日语翻译"+textJp);
                                        } else {
                                            recShow.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Log.d(TAG, "日语翻译失败");
                                                    Toast.makeText(PhotoRecActivity.this, "translate fail", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                            return;
                                        }

                                        JSONObject tranJson2 = new JSONObject(text2);
                                        if (tranJson2.has("trans_result")) {
                                            String textSpa = tranJson2.getJSONArray("trans_result").getJSONObject(0).getString("dst");
                                            bean.setSpaName(textSpa);
                                            Log.d(TAG, "西班牙语翻译"+textSpa);
                                        } else {
                                            recShow.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Log.d(TAG, "西班牙语翻译失败");
                                                    Toast.makeText(PhotoRecActivity.this, "translate fail", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                            return;
                                        }

                                        JSONObject tranJson3 = new JSONObject(text3);
                                        if (tranJson3.has("trans_result")) {
                                            String textKor = tranJson3.getJSONArray("trans_result").getJSONObject(0).getString("dst");
                                            bean.setKorName(textKor);
                                            Log.d(TAG, "韩语翻译"+textKor);
                                        } else {
                                            recShow.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Log.d(TAG, "韩语翻译失败");
                                                    Toast.makeText(PhotoRecActivity.this, "translate fail", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                            return;
                                        }

                                        JSONObject tranJson4 = new JSONObject(text4);
                                        if (tranJson4.has("trans_result")) {
                                            String textFra = tranJson4.getJSONArray("trans_result").getJSONObject(0).getString("dst");
                                            bean.setFraName(textFra);
                                            Log.d(TAG, "法语翻译"+textFra);
                                        } else {
                                            recShow.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Log.d(TAG, "法语翻译失败");
                                                    Toast.makeText(PhotoRecActivity.this, "translate fail", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                            return;
                                        }




                                    } catch (JSONException e) {
                                        recShow.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Log.d(TAG, "翻译api解析JSON失败");
                                                Toast.makeText(PhotoRecActivity.this, "JSON parse fail", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                        return;
                                    }

                                    StringBuffer sb = new StringBuffer();
                                    String lan = sp.getString("lan", "Chinese");
                                    if (lan.equals("Spanish")) {
                                        sb.append(bean.getSpaName());
                                    } else if (lan.equals("Japanese")) {
                                        sb.append(bean.getJpName());
                                    } else if(lan.equals("Korean")){
                                        sb.append(bean.getKorName());
                                    }else if(lan.equals("French")){
                                        sb.append(bean.getFraName());
                                    }else {
                                        sb.append(bean.getName());
                                    }

                                    sb.append("\n").append(bean.getEnName());
                                    recShow.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            recShow.setText(sb.toString());
                                        }
                                    });


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
                                    historyBean.setJpName(bean.getJpName());
                                    historyBean.setSpaName(bean.getSpaName());
                                    historyBean.setKorName(bean.getKorName());
                                    historyBean.setFraName(bean.getFraName());
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
            }
        });

        //读单词
        recVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bean != null) {
                    String lan = sp.getString("lan", "Chinese");
                    if (lan.equals("Spanish")) {
                        String spanish = bean.getSpaName();
                        VoiceUtil.voice(PhotoRecActivity.this, spanish,"x2_SpEs_Aurora");
                    } else if (lan.equals("Japanese")) {
                        String japanese = bean.getSpaName();
                        VoiceUtil.voice(PhotoRecActivity.this, japanese,"x2_JaJp_ZhongCun");
                    } else if (lan.equals("Korean")) {
                        String korean = bean.getKorName();
                        VoiceUtil.voice(PhotoRecActivity.this, korean,"zhimin");
                    } else if (lan.equals("French")) {
                        String french = bean.getFraName();
                        VoiceUtil.voice(PhotoRecActivity.this, french,"x2_FrRgM_Lisa");
                    } else {
                        String chinese = bean.getName();
                        VoiceUtil.voice(PhotoRecActivity.this, chinese,"aisxping");
                    }

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
        String[] strs = photoPath.split("/");
        return strs[7];
    }


}