package com.example.myapplication.ui.me;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
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
    private Button btn;
    private TextView text;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        btn = root.findViewById(R.id.btn_login);
        text = root.findViewById(R.id.status);

        String url = "http://172.26.14.175:5000/auth/login";

        CookieJarImpl cookieJar = new CookieJarImpl(getActivity());

        OkHttpClient client = new OkHttpClient.Builder().cookieJar(cookieJar).build();//创建OkHttpClient对象。

        // 为了正常格式的url创建的request对象
        Request request = new Request.Builder()
                .url(url)
                .build();
        HttpUrl httpurl = request.url();

        // 寻找对应的cookie
        List<Cookie> cookie = client.cookieJar().loadForRequest(httpurl);
        // 如果找到了
        if(!cookie.isEmpty()){
            // 创建一个带有header的request
            Request r_with_header = new Request.Builder().url(url)
                    .header(cookie.get(0).name(),cookie.get(0).value())
                    .build();

            client.newCall(r_with_header).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Looper.prepare();
                    Toast.makeText(MainActivity.getContext(), "Server Error", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String res = response.body().string();
                    if (res.equals("not login")) {
                        Looper.prepare();
                        Toast.makeText(MainActivity.getContext(), "Have not login", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    } else {
                        System.out.println(res);
                        text.setText(res);
                        Looper.prepare();
                        Toast.makeText(MainActivity.getContext(), "get account", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }
            });

        }


        // 点击login按钮跳转到登录界面
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
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