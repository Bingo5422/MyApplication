package com.example.myapplication.ui.me;

import static com.example.myapplication.ui.me.MeFragment.DomainURL;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import okhttp3.Cookie;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EditNameActivity extends AppCompatActivity {
    private Button btn_submit_info;
    private EditText et_new_nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_name);
        et_new_nickname = findViewById(R.id.et_new_nickname);
        btn_submit_info = findViewById(R.id.btn_submit_info);

        btn_submit_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 登录为阻塞请求
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SubmitSuccessProcess();
                    }
                }).start();
            }
        });
    }
    private void SubmitSuccessProcess(){
        CookieJarImpl cookieJar = new CookieJarImpl(EditNameActivity.this);
        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(cookieJar).build();

        FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体
        formBody.add("nickname", String.valueOf(et_new_nickname.getText()));//传递键值对参数
        Request request = new Request.Builder()//创建Request 对象。
                .url(DomainURL + "/info/set_nickname")
                .post(formBody.build())//传递请求体
                .build();

        List<Cookie> cookie = client.cookieJar().loadForRequest(request.url());
        if (!cookie.isEmpty()) {
            request.newBuilder().addHeader(cookie.get(0).name(), cookie.get(0).value());
            Response response = null;
            try {
                response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    SharedPreferences preferences =
                            EditNameActivity.this.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("nickname", String.valueOf(et_new_nickname.getText()));
                    editor.commit();
                    startActivity(new Intent(EditNameActivity.this, MainActivity.class));
                    Looper.prepare();
                    Toast.makeText(MainActivity.getContext(), "Submit " +
                            "Successfully", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                } else {
                    Looper.prepare();
                    Toast.makeText(MainActivity.getContext(), "Submit failed. Please check" +
                            "the Internet connection.", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}