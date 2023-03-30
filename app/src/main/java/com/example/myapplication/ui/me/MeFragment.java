package com.example.myapplication.ui.me;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.Utils.CookieJarImpl;
import com.example.myapplication.databinding.FragmentMeBinding;
import com.example.myapplication.Utils.HttpUtil;

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

public class MeFragment extends Fragment {

    private FragmentMeBinding binding;
    private Button btn, btn_display;
    private TextView text;
    private JSONObject res;
    private SharedPreferences preferences;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        btn = root.findViewById(R.id.btn_login);
        text = root.findViewById(R.id.status);

        btn_display = root.findViewById(R.id.btn_display);

        btn_display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.getContext(), ServerHistActivity.class));
            }
        });

        String url = "http://172.26.14.175:5000/auth/login";

        preferences = getActivity().getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        // 从本地调取基本用户信息先进行显示
        String user = preferences.getString("user_id", "NO USER");
        text.setText(user);
        if(user!="NO USER"){
            btn.post(new Runnable() {
                @Override
                public void run() {
                    btn.setText("logout");
                }
            });
        }

        CookieJarImpl cookieJar = new CookieJarImpl(getActivity());
        OkHttpClient client = new OkHttpClient.Builder().cookieJar(cookieJar).build();//创建OkHttpClient对象。
        // 为了正常格式的url创建的request对象
        Request request = new Request.Builder()
                .url(url)
                .build();
        HttpUrl httpurl = request.url();
        // 寻找对应的cookie
        List<Cookie> cookie = client.cookieJar().loadForRequest(httpurl);

        // 如果找到了cookie
        if(!cookie.isEmpty()){
            // 创建一个带有header的request
            Request r_with_header = new Request.Builder().url(url)
                    .header(cookie.get(0).name(),cookie.get(0).value())
                    .build();

            client.newCall(r_with_header).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if(Looper.myLooper()==null)
                        Looper.prepare();
                    Toast.makeText(MainActivity.getContext(), "Server Error", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    try {
                        res = new JSONObject(response.body().string());
                        // 如果处于登陆状态，cookie没过期
                        if (res.getBoolean("logon")) {
                            // 如果名字有更新，用新名字显示
                            String user_id = res.getString("user_id");
                            text.post(new Runnable() {
                                @Override
                                public void run() {
                                    text.setText(user_id);
                                }
                            });
                            // 存储新名字到本地SharedPreference
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("user_id", user_id);
                            editor.commit();
//                            btn.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    btn.setText("logout");
//                                }
//                            });

                        // cookie过期
                        }else {
                            // 更改显示的用户，并清除user_id数据
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.remove("user_id");
                            editor.commit();
                            text.setText("NO USER");
                            btn.post(new Runnable() {
                                @Override
                                public void run() {
                                        btn.setText("login");
                                }
                            });

                            if(Looper.myLooper()==null)
                                Looper.prepare();
                            Toast.makeText(MainActivity.getContext(),
                                    "Authentication expired, please login", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 去登录
                if(btn.getText().equals("login")) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }

                // 退出登录
                else{
                    String logout_url = "http://172.26.14.175:5000/auth/logout";
                    Request logout_req = new Request.Builder()
                            .url(logout_url)
                            .build();
                    client.newCall(logout_req).enqueue(new Callback(){
                        @Override
                        public void onFailure(Call call, IOException e) {
                            if(Looper.myLooper()==null)
                                Looper.prepare();
                            Toast.makeText(MainActivity.getContext(), "Server Error. Please check" +
                                "the Internet connection", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            // 清除用户登陆显示
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.remove("user_id");
                            editor.commit();
                            text.post(new Runnable() {
                                @Override
                                public void run() {
                                    text.setText("NO USER");
                                }
                            });

                            btn.post(new Runnable() {
                                @Override
                                public void run() {
                                    btn.setText("login");
                                }
                            });
                        }
                    });
                }
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}