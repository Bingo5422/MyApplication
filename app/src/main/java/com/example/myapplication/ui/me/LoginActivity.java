package com.example.myapplication.ui.me;


import static com.example.myapplication.MainActivity.DomainURL;
import static com.example.myapplication.ui.me.MeFragment.client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Looper;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Time;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
    private ImageView iv_login_back, iv_if_visible;
    private EditText et_email, et_password;
    private TextView tv_register, tv_forget;
    boolean if_visible;



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
        iv_if_visible = findViewById(R.id.iv_if_visible);

         if_visible = false;

        String url = DomainURL+"/auth/login";

        // 登录
        btn_login.setOnClickListener(new View.OnClickListener() {

            CookieJarImpl cookieJar = new CookieJarImpl(LoginActivity.this);
            @Override
            public void onClick(View view) {
//                OkHttpClient client = new OkHttpClient.Builder()
//                        .connectTimeout(10,TimeUnit.SECONDS)
//                        .readTimeout(5, TimeUnit.SECONDS)
//                        .writeTimeout(5, TimeUnit.SECONDS)
//                        .cookieJar(cookieJar).build();//创建OkHttpClient对象。

                // 登录为阻塞请求
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体
                        formBody.add("user_email", et_email.getText().toString());//传递键值对参数
                        formBody.add("password", et_password.getText().toString());//传递键值对参数
                        Request request = new Request.Builder()//创建Request 对象。
                                .url(url)
                                .post(formBody.build())//传递请求体
                                .build();
                        Response response = null;
                        try {
                            response = client.newCall(request).execute();
                            if (response.isSuccessful()) {
                                LoginSuccess(response, client, LoginActivity.this);
                            } else {
                                if(Looper.myLooper()==null)
                                    Looper.prepare();
                                Toast.makeText(MainActivity.getContext(), "Unable to login, please check" +
                                        "the Internet connection.", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                        } catch (IOException e) {
                            if(Looper.myLooper()==null)
                                Looper.prepare();
                            Toast.makeText(MainActivity.getContext(), "Unable to login, please check" +
                                    "the Internet connection.", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                            //throw new RuntimeException(e);
                        }

                        // 获得用户其他信息存到sharedpreferences
                        Request photo_request = new Request.Builder()
                                .url(DomainURL + "/info/get_photo")
                                .build();
                        List<Cookie> cookie = client.cookieJar().loadForRequest(photo_request.url());
                        Response info_res = null;
                        if (!cookie.isEmpty()){
                            photo_request.newBuilder().addHeader(cookie.get(0).name(),cookie.get(0).value());
                            try {
                                info_res = client.newCall(photo_request).execute();
                                if(info_res.isSuccessful()){
                                    GetInfoSuccess(LoginActivity.this, info_res);
                                }
                            } catch (IOException e) {
                                if(Looper.myLooper()==null)
                                    Looper.prepare();
                                Toast.makeText(MainActivity.getContext(), "Unable to get info, please check" +
                                        "the Internet connection.", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                                //throw new RuntimeException(e);
                            }
                        }

                        // 登陆成功 跳转回主界面
                        Intent login_intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(login_intent);

                        if(Looper.myLooper()==null)
                            Looper.prepare();
                        Toast.makeText(MainActivity.getContext(), "Login successfully", Toast.LENGTH_SHORT).show();
                        Looper.loop();

                    }
                }).start();

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

        // 密码可见/不可见
        iv_if_visible.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(!if_visible){
                    iv_if_visible.setImageResource(R.drawable.baseline_visibility_24);
//                    et_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    et_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    if_visible = true;
                }else{
                    iv_if_visible.setImageResource(R.drawable.baseline_visibility_off_24);
//                    et_password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    if_visible = false;
                }
            }
        });


    }
        private void LoginSuccess(Response response, OkHttpClient client, Context context){
            JSONObject res_json;
            String res = null;
            try {
                res = response.body().string();
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
                    JSONObject info = new JSONObject(res_json.getString("message"));
                    SharedPreferences preferences = context.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("user_id", info.getString("user_id")); //存储返回的用户名
                    editor.putString("nickname", info.getString("nickname"));
                    editor.commit();

                } else {
                    if(Looper.myLooper()==null)
                        Looper.prepare();
                    Toast.makeText(MainActivity.getContext(), res_json.getString("message"),
                            Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        protected void GetInfoSuccess(Context context, Response response){

            try {
                InputStream is = null;
                byte[] buf = new byte[4096];
                int len = 0;

                String TargetPath = getExternalFilesDir("Load_from_server").getAbsolutePath();
                File saveFile = new File(TargetPath, "photo.jpg");

                FileOutputStream saveImgOut = new FileOutputStream(saveFile);
                is = response.body().byteStream();

                while ((len = is.read(buf)) != -1) {
                    saveImgOut.write(buf, 0, len);
                }
                //存储完成后需要清除相关的进程
                saveImgOut.flush();
                saveImgOut.close();
                SharedPreferences preferences = context.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("photo", saveFile.getAbsolutePath()); //存储返回的用户名
                editor.commit();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

}
