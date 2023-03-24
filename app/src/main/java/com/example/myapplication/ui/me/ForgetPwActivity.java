package com.example.myapplication.ui.me;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.Utils.CookieJarImpl;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ForgetPwActivity extends AppCompatActivity {

    private Button btn_verify, btn_forget_send_code;
    private EditText et_email_forget, et_forget_captcha;
    private TextView tv_forget_error;

    private ImageView iv_forget_pw_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pw);

        iv_forget_pw_back = findViewById(R.id.iv_forget_pw_back);
        btn_forget_send_code = findViewById(R.id.btn_forget_send_code);
        btn_verify = findViewById(R.id.btn_verify);
        et_email_forget = findViewById(R.id.et_email_forget);
        et_forget_captcha = findViewById(R.id.et_forget_captcha);
        tv_forget_error = findViewById(R.id.tv_forget_error);


        // 点击提交表单
        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://172.26.14.175:5000/auth/forget_pw";
                CookieJarImpl cookieJar = new CookieJarImpl(ForgetPwActivity.this);
                OkHttpClient client = new OkHttpClient.Builder().cookieJar(cookieJar).build(); //创建OkHttpClient对象。
                FormBody.Builder formBody = new FormBody.Builder(); //创建表单请求体
                formBody.add("user_email", et_email_forget.getText().toString()); //传递键值对参数
                formBody.add("captcha", et_forget_captcha.getText().toString());

                Request request = new Request.Builder()//创建Request 对象。
                        .url(url)
                        .post(formBody.build())//传递请求体
                        .build();

                // 创建一个call请求，并把请求添加到调度中
                client.newCall(request).enqueue(new VerifyCallback(client));
            }
        });

        // 点击发送验证码
        btn_forget_send_code.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                String url = "http://172.26.14.175:5000/auth//captcha/email?email="+et_email_forget.getText();
                OkHttpClient client = new OkHttpClient(); //创建OkHttpClient对象。
                Request request = new Request.Builder()//创建Request 对象。
                        .url(url)
                        .build();
                client.newCall(request).enqueue(new Callback(){
                    @Override
                    public void onFailure(Call call, IOException e) {
                        if(Looper.myLooper()==null)
                            Looper.prepare();
                        Toast.makeText(MainActivity.getContext(), "Server Error." +
                                " Please check the Internet connection", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        JSONObject res_json2;
                        String res = response.body().string();
                        try {
                            res_json2 = new JSONObject(res);
                            if(res_json2.getBoolean("if_send")){
                                if(Looper.myLooper()==null)
                                    Looper.prepare();
                                Toast.makeText(MainActivity.getContext(), "Email sent.", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                });
            }
        });

        // 点击返回上一界面
        iv_forget_pw_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login_intent = new Intent(ForgetPwActivity.this, LoginActivity.class);
                startActivity(login_intent);
            }
        });
    }

    class VerifyCallback implements Callback{
        private OkHttpClient client;

        public VerifyCallback(OkHttpClient client){
            this.client = client;
        }
        @Override
        public void onFailure(Call call, IOException e) {
            Looper.prepare();
            Toast.makeText(MainActivity.getContext(), "Server Error." +
                    " Please check the Internet connection", Toast.LENGTH_SHORT).show();
            Looper.loop();
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            JSONObject res_json;
            String res = response.body().string();
            try {
                res_json = new JSONObject(res);
                if (res_json.getBoolean("if_success")) {

                    //获取返回数据的头部
                    Headers headers = response.headers();
                    HttpUrl Url = response.request().url();
                    //获取头部的Cookie,注意：可以通过Cooke.parseAll()来获取
                    List<Cookie> cookies = Cookie.parseAll(Url, headers);
                    //防止header没有Cookie的情况
                    if (cookies != null) {
                        client.cookieJar().saveFromResponse(Url, cookies);
                    }
                    Intent intent = new Intent(ForgetPwActivity.this, ResetPwActivity.class);
                    startActivity(intent);
                } else {
                    tv_forget_error.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                tv_forget_error.setText(res_json.getString("message"));
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

    }
}