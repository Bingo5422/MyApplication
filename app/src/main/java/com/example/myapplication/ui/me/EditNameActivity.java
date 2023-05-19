package com.example.myapplication.ui.me;


import static com.example.myapplication.MainActivity.DomainURL;
import static com.example.myapplication.ui.me.MeFragment.client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.Utils.CookieJarImpl;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EditNameActivity extends AppCompatActivity {
    private Button btn_submit_info;
    private EditText et_new_nickname;
    private ImageView edit_name_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_name);
        et_new_nickname = findViewById(R.id.et_new_nickname);
        btn_submit_info = findViewById(R.id.btn_submit_info);
        edit_name_back = findViewById(R.id.edit_name_back);

        edit_name_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditNameActivity.this, EditInfoActivity.class);
                startActivity(intent);
            }
        });

        btn_submit_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        client.newBuilder().cookieJar(cookieJar)
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS).build();

        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("nickname", String.valueOf(et_new_nickname.getText()));
        Request request = new Request.Builder()
                .url(DomainURL + "/info/set_nickname")
                .post(formBody.build())
                .build();

        List<Cookie> cookie = client.cookieJar().loadForRequest(request.url());
        if (!cookie.isEmpty()) {
            request.newBuilder().addHeader(cookie.get(0).name(), cookie.get(0).value());
            Response response = null;
            try {
                response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    JSONObject res_json = null;
                    res_json = new JSONObject(response.body().string());
                    if (res_json.getBoolean("if_success")) {
                        // Update local sharedpreference if new nickname is uploaded successfully
                        SharedPreferences preferences =
                                EditNameActivity.this.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("nickname", String.valueOf(et_new_nickname.getText()));
                        editor.commit();
                        startActivity(new Intent(EditNameActivity.this, EditInfoActivity.class));
                        if (Looper.myLooper() == null)
                            Looper.prepare();
                        Toast t = Toast.makeText(MainActivity.getContext(), "Submit " +
                                "Successfully", Toast.LENGTH_SHORT);
                        t.setGravity(Gravity.CENTER,0,0);
                        t.show();
                        Looper.loop();
                    }else{
                        if (Looper.myLooper() == null)
                            Looper.prepare();
                        Toast t = Toast.makeText(EditNameActivity.this,
                                res_json.getString("msg"), Toast.LENGTH_SHORT);
                        t.setGravity(Gravity.CENTER,0,0);
                        t.show();
                        Looper.loop();
                    }

                } else {
                    if (Looper.myLooper() == null)
                        Looper.prepare();
                    Toast.makeText(MainActivity.getContext(), "Submit failed. Please check" +
                            " the Internet connection.", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            } catch (IOException e) {
                if (Looper.myLooper() == null)
                    Looper.prepare();
                Toast.makeText(EditNameActivity.this, "Unable to edit name. " +
                        "Please check your internet connection.", Toast.LENGTH_SHORT).show();
                Looper.loop();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }
}