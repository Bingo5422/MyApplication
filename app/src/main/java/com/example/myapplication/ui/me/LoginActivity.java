package com.example.myapplication.ui.me;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.Utils.CookieJarImpl;

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
import okhttp3.internal.http2.Header;

public class LoginActivity extends AppCompatActivity {

    private Button btn_login, btn_back;
    private EditText et_email, et_password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btn_login = findViewById(R.id.btn_login_confirm);
        btn_back = findViewById(R.id.btn_login_back);
        et_email = findViewById(R.id.et_email_login);
        et_password = findViewById(R.id.et_password_login);
        String url = "http://172.26.14.175:5000/auth/login";

        btn_login.setOnClickListener(new View.OnClickListener() {

            CookieJarImpl cookieJar = new CookieJarImpl(LoginActivity.this);
            @Override
            public void onClick(View view) {
                OkHttpClient client = new OkHttpClient.Builder().cookieJar(cookieJar).build();//创建OkHttpClient对象。
                FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体
                formBody.add("user_email", et_email.getText().toString());//传递键值对参数
                formBody.add("password", et_password.getText().toString());//传递键值对参数
                Request request = new Request.Builder()//创建Request 对象。
                        .url(url)
                        .post(formBody.build())//传递请求体
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Looper.prepare();
                        Toast.makeText(MainActivity.getContext(), "Server Error", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String res = response.body().string();
                        if (res.equals("success")) {
                            System.out.println("successfully login");
                            //获取返回数据的头部
                            Headers headers = response.headers();
                            HttpUrl loginUrl = request.url();
                            //获取头部的Cookie,注意：可以通过Cooke.parseAll()来获取
                            List<Cookie> cookies = Cookie.parseAll(loginUrl, headers);
                            //防止header没有Cookie的情况
                            if (cookies != null) {
                                //存储到Cookie管理器中
                                client.cookieJar().saveFromResponse(loginUrl, cookies);
                            }

                            Looper.prepare();
                            Toast.makeText(MainActivity.getContext(), "Successfully login", Toast.LENGTH_SHORT).show();
                            Looper.loop();

                        } else {
                            Looper.prepare();
                            Toast.makeText(MainActivity.getContext(), "Wrong", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                });
            }
        });

        // 返回跳转
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


    }
}