package com.example.myapplication.ui.me;
import static com.example.myapplication.ui.me.MeFragment.DomainURL;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentMeBinding;

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

public class RegisterActivity extends AppCompatActivity {

    private Button btn_register, btn_send_code;
    private EditText et_email, et_password, et_confirm_pw, et_captcha;
    private TextView tv_error_info;
    private ImageView iv_back_to_login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btn_register = findViewById(R.id.btn_register);
        btn_send_code = findViewById(R.id.btn_send_code);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        et_confirm_pw = findViewById(R.id.et_confirm_pw);
        et_captcha = findViewById(R.id.et_captcha);
        tv_error_info = findViewById(R.id.tv_error_info);
        iv_back_to_login = findViewById(R.id.iv_back_to_login);


        // 提交注册
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = DomainURL+"/auth/register";

                OkHttpClient client = new OkHttpClient(); //创建OkHttpClient对象。
                FormBody.Builder formBody = new FormBody.Builder(); //创建表单请求体
                formBody.add("user_email", et_email.getText().toString()); //传递键值对参数
                formBody.add("password", et_password.getText().toString());
                formBody.add("password_confirm", et_confirm_pw.getText().toString());
                formBody.add("captcha", et_captcha.getText().toString());

                Request request = new Request.Builder()//创建Request 对象。
                        .url(url)
                        .post(formBody.build())//传递请求体
                        .build();

                // 创建一个call请求，并把请求添加到调度中
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
                                Looper.prepare();
                                Toast.makeText(MainActivity.getContext(), "Successfully Registered", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            } else {
                                tv_error_info.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            tv_error_info.setText(res_json.getString("message"));
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
        });

        // 发送验证码
        btn_send_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = DomainURL+"/auth/captcha/email?email="+et_email.getText();

                OkHttpClient client = new OkHttpClient(); //创建OkHttpClient对象。
                Request request = new Request.Builder()//创建Request 对象。
                        .url(url)
                        .build();
                client.newCall(request).enqueue(new Callback(){
                    @Override
                    public void onFailure(Call call, IOException e) {
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

        // 返回登录界面
        iv_back_to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

    }
}
