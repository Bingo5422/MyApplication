package com.example.myapplication.ui.me;

import static com.example.myapplication.MainActivity.DomainURL;
import static com.example.myapplication.ui.me.MeFragment.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.Utils.ButtonUtil;
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


        // sbumit registration information
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String url = DomainURL+"/auth/register";

                FormBody.Builder formBody = new FormBody.Builder();
                formBody.add("user_email", et_email.getText().toString());
                formBody.add("password", et_password.getText().toString());
                formBody.add("password_confirm", et_confirm_pw.getText().toString());
                formBody.add("captcha", et_captcha.getText().toString());

                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody.build())
                        .build();


                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        if (Looper.myLooper()==null)
                            Looper.prepare();
                        Toast t = Toast.makeText(MainActivity.getContext(), "Server Error." +
                                " Please check the Internet connection", Toast.LENGTH_SHORT);
                        t.setGravity(Gravity.CENTER,0,0);
                        t.show();
                        Looper.loop();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        JSONObject res_json;
                        String res = response.body().string();
                        try {
                            res_json = new JSONObject(res);
                            if (res_json.getBoolean("if_success")) {
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                if (Looper.myLooper()==null)
                                    Looper.prepare();
                                Toast t = Toast.makeText(
                                        MainActivity.getContext(), "Successfully Registered. Please login.", Toast.LENGTH_SHORT);
                                t.setGravity(Gravity.CENTER,0,0);
                                t.show();
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

        // button for sending code
        btn_send_code.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!ButtonUtil.isFastDoubleClick(30000, RegisterActivity.this, R.id.btn_send_code)){
                    if (et_email.length()!=0) {
                        String url = DomainURL + "/auth/captcha/email?email=" + et_email.getText();

                        Request request = new Request.Builder()
                                .url(url)
                                .build();
                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                if (Looper.myLooper()==null)
                                    Looper.prepare();
                                Toast t = Toast.makeText(MainActivity.getContext(), "Server Error." +
                                        " Please check the Internet connection", Toast.LENGTH_SHORT);
                                t.setGravity(Gravity.CENTER,0,0);
                                t.show();
                                Looper.loop();
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                JSONObject res_json2;
                                String res = response.body().string();
                                try {
                                    res_json2 = new JSONObject(res);
                                    if (res_json2.getBoolean("if_send")) {
                                        if (Looper.myLooper()==null)
                                            Looper.prepare();
                                        Toast t = Toast.makeText(MainActivity.getContext(), "Email sent.", Toast.LENGTH_SHORT);
                                        t.setGravity(Gravity.CENTER,0,0);
                                        t.show();
                                        Looper.loop();
                                    }
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }

                            }
                        });
                    }else{
                        tv_error_info.setText("Please finish the form.");
                    }
                }
            }
        });

        iv_back_to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

    }



}
