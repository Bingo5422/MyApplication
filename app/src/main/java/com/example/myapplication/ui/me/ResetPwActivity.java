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
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ResetPwActivity extends AppCompatActivity {
    private ImageView iv_reset_pw_back;
    private EditText et_reset_pw, et_confirm_reset_pw;
    private TextView tv_reset_error;
    private Button btn_reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pw);

        iv_reset_pw_back = findViewById(R.id.iv_reset_pw_back);
        et_reset_pw = findViewById(R.id.et_reset_pw);
        et_confirm_reset_pw = findViewById(R.id.et_confirm_reset_pw);
        btn_reset = findViewById(R.id.btn_reset);
        tv_reset_error = findViewById(R.id.tv_reset_error);

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://172.26.14.175:5000/auth/reset_pw";

                CookieJarImpl cookieJar = new CookieJarImpl(ResetPwActivity.this);
                OkHttpClient client = new OkHttpClient.Builder().cookieJar(cookieJar).build();//创建OkHttpClient对象。
                FormBody.Builder formBody = new FormBody.Builder(); //创建表单请求体
                formBody.add("password", et_reset_pw.getText().toString());
                formBody.add("password_confirm", et_confirm_reset_pw.getText().toString());
                // 为了正常格式的url创建的request对象
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody.build())
                        .build();

                HttpUrl httpurl = request.url();
                // 寻找对应的cookie
                List<Cookie> cookie = client.cookieJar().loadForRequest(httpurl);

                request.newBuilder().addHeader(cookie.get(0).name(),cookie.get(0).value());

                // 如果cookie不为空， 发送请求414
                if(!cookie.isEmpty()){
                    client.newCall(request).enqueue(new Callback() {
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
                                    if(Looper.myLooper()==null)
                                        Looper.prepare();
                                    Toast.makeText(MainActivity.getContext(),
                                            "Password reset successfully", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                } else {
                                    tv_reset_error.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                tv_reset_error.setText(res_json.getString("message"));
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
                    });
                }

            }
        });

        iv_reset_pw_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login_intent = new Intent(ResetPwActivity.this, LoginActivity.class);
                startActivity(login_intent);
            }
        });
    }
}