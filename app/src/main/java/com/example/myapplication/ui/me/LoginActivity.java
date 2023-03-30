package com.example.myapplication.ui.me;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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


public class LoginActivity extends AppCompatActivity {

    private Button btn_login;
    private ImageView iv_login_back;
    private EditText et_email, et_password;
    private TextView tv_register, tv_forget;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btn_login = findViewById(R.id.btn_login_confirm);
        iv_login_back = findViewById(R.id.iv_login_back);
        et_email = findViewById(R.id.et_email_login);
        et_password = findViewById(R.id.et_password_login);
        tv_register = findViewById(R.id.tv_go_register);
        tv_forget = findViewById(R.id.tv_forget);

        String url = "http://172.26.14.175:5000/auth/login";

        // 登录
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

                LoginCallback callback = new LoginCallback(client, LoginActivity.this);
                client.newCall(request).enqueue(callback);

            }
        });

        // 返回跳转
        iv_login_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login_intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(login_intent);
            }
        });

        // go to the register page
        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent register_intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(register_intent);
            }
        });

        // 进入忘记密码界面
        tv_forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgetPwActivity.class));
            }
        });

    }

    class LoginCallback implements Callback{
        private OkHttpClient client;
        private Context context;
        private SharedPreferences preferences;

        public LoginCallback(OkHttpClient client, Context context){
            this.client = client;
            this.context = context;
        }
        @Override
        public void onFailure(Call call, IOException e) {
            Looper.prepare();
            Toast.makeText(MainActivity.getContext(), "Unable to login, please check" +
                    "the Internet connection.", Toast.LENGTH_SHORT).show();
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
                    HttpUrl loginUrl = response.request().url();
                    //获取头部的Cookie,注意：可以通过Cooke.parseAll()来获取
                    List<Cookie> cookies = Cookie.parseAll(loginUrl, headers);
                    //防止header没有Cookie的情况
                    if (cookies != null) {
                        //存储到Cookie管理器中
                        client.cookieJar().saveFromResponse(loginUrl, cookies);
                    }

                    // 存储用户基本信息到用户sharedpreference中
                    preferences = context.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("user_id", res_json.getString("message")); //存储返回的用户名
                    editor.commit();

                    // 登陆成功 跳转回主界面
                    Intent login_intent = new Intent(context, MainActivity.class);
                    startActivity(login_intent);

                    if(Looper.myLooper()==null)
                        Looper.prepare();
                    Toast.makeText(MainActivity.getContext(), "Login successfully", Toast.LENGTH_SHORT).show();
                    Looper.loop();


                } else {
                    Looper.prepare();
                    Toast.makeText(MainActivity.getContext(), res_json.getString("message"),
                            Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }


}