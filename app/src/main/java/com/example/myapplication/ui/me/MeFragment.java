package com.example.myapplication.ui.me;

import android.content.Context;
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
import com.example.myapplication.databinding.FragmentMeBinding;
import com.example.myapplication.Utils.HttpUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MeFragment extends Fragment {

    private FragmentMeBinding binding;
    private Button btn;
    private EditText et_email;
    private EditText et_password;

    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        getActivity().findViewById(R.id.btn_register);
//
//    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        MeViewModel meViewModel =
//                new ViewModelProvider(this).get(MeViewModel.class);

        binding = FragmentMeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        btn = root.findViewById(R.id.btn_register);
        et_email = root.findViewById(R.id.et_email);
        et_password = root.findViewById(R.id.et_password);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://172.26.14.175:5000/auth/register";

                OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
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
                        if (res.equals("success")){
                            Looper.prepare();
                            Toast.makeText(MainActivity.getContext(), "Successfully Registered", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                        else{
                            Looper.prepare();
                            Toast.makeText(MainActivity.getContext(), "Already Registered", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                });
            }
        });

//        final TextView textView = binding.textMe;
//        meViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}