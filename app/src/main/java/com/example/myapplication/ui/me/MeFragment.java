package com.example.myapplication.ui.me;

import static com.example.myapplication.MainActivity.DomainURL;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.example.myapplication.Dao.FriendsDao;
import com.example.myapplication.Dao.RecDataBase;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.Utils.CookieJarImpl;
import com.example.myapplication.databinding.FragmentMeBinding;
import com.example.myapplication.ui.dashboard.DetailActivity;
import com.example.myapplication.ui.notifications.FriendDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MeFragment extends Fragment {

    static OkHttpClient client;
//    final static String DomainURL = "http://xintong.pythonanywhere.com";
    private FragmentMeBinding binding;
    private Button btn_login, btn_display, btn_edit_info, btn_synchro;
    private TextView text;
    private ImageView user_photo;
    private JSONObject res;
    private SharedPreferences preferences;
    private FriendsDao friendsDao;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        btn_login = root.findViewById(R.id.btn_login);
        text = root.findViewById(R.id.status);
        user_photo = root.findViewById(R.id.user_photo);

        btn_display = root.findViewById(R.id.btn_display);
        btn_edit_info = root.findViewById(R.id.btn_edit_info);
        btn_synchro = root.findViewById(R.id.btn_synchro);

        //clearTable
//        FriendDatabase mFriendDatabase = Room.databaseBuilder(getContext(), FriendDatabase.class, "friend_db").build();
//        friendDao =  mFriendDatabase.friendDao();

        RecDataBase recDataBase = Room.databaseBuilder(getContext(), RecDataBase.class, "RecDataBase").allowMainThreadQueries().build();
        friendsDao = recDataBase.friendsDao();

        btn_display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btn_login.getText()=="logout") {
                    startActivity(new Intent(MainActivity.getContext(), ServerHistActivity.class));
                }else{
                    if(Looper.myLooper()==null)
                        Looper.prepare();
                    Toast.makeText(MainActivity.getContext(), "Please login.", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

            }
        });
        btn_edit_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btn_login.getText()=="logout") {
                    startActivity(new Intent(MainActivity.getContext(), EditInfoActivity.class));
                }else{
                    if(Looper.myLooper()==null)
                        Looper.prepare();
                    Toast.makeText(MainActivity.getContext(), "Please login.", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        });

        btn_synchro.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(btn_login.getText()=="logout") {
                    Intent intent = new Intent(getActivity(), ServerUploadActivity.class);
                    startActivity(intent);
                }else{
                    if(Looper.myLooper()==null)
                        Looper.prepare();
                    Toast.makeText(MainActivity.getContext(), "Please login.", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

            }
        });

        String url = DomainURL+"/auth/login";

        preferences = getActivity().getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        // 从本地调取基本用户信息先进行显示
        String user = preferences.getString("nickname", "NO USER");
        String photo_path = preferences.getString("photo", "");
        text.setText(user);
        if(photo_path!=""){
            Bitmap bitmap = BitmapFactory.decodeFile(photo_path);
            user_photo.setImageBitmap(bitmap);
        }
        if(user!="NO USER"){
            btn_login.post(new Runnable() {
                @Override
                public void run() {
                    btn_login.setText("logout");
                }
            });
        }

        CookieJarImpl cookieJar = new CookieJarImpl(getActivity());
        client = new OkHttpClient.Builder().cookieJar(cookieJar)
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build();//创建OkHttpClient对象。
//        OkHttpClient client = new OkHttpClient.Builder().cookieJar(cookieJar)
//                .connectTimeout(10, TimeUnit.SECONDS)
//                .writeTimeout(5, TimeUnit.SECONDS)
//                .readTimeout(5, TimeUnit.SECONDS)
//                .build();//创建OkHttpClient对象。
        // 为了正常格式的url创建的request对象
        Request request = new Request.Builder()
                .url(url)
                .build();

        // 寻找对应的cookie
        List<Cookie> cookie = client.cookieJar().loadForRequest(request.url());

        // 如果找到了cookie
        if(!cookie.isEmpty()){
            request.newBuilder().addHeader(cookie.get(0).name(),cookie.get(0).value());

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if(Looper.myLooper()==null)
                        Looper.prepare();
                    Toast.makeText(MainActivity.getContext(), "No Internet connection or " +
                            "there is a server error", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    try {
                        res = new JSONObject(response.body().string());
                        // 如果处于登陆状态，cookie没过期
                        if (res.getBoolean("logon")) {
                            // 如果名字有更新，用新名字显示
                            String nickname = res.getString("nickname");
                            text.post(new Runnable() {
                                @Override
                                public void run() {
                                    text.setText(nickname);
                                }
                            });
                            // 存储新名字到本地SharedPreference
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("nickname", nickname);
                            editor.commit();

                        // cookie过期
                        }else {
                            // 更改显示的用户，并清除user_id数据
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.remove("user_id");
                            editor.remove("nickname");
                            editor.remove("photo");
                            editor.commit();

                            //清除cookie
                            SharedPreferences p_cookie = getActivity()
                                    .getSharedPreferences("COOKIES", Context.MODE_PRIVATE);
                            SharedPreferences.Editor e_cookie = p_cookie.edit();
                            e_cookie.clear();
                            e_cookie.commit();

                            //清除之前用户的好友信息
                            friendsDao.clearTable();

                            text.post(new Runnable() {
                                @Override
                                public void run() {
                                    text.setText("NO USER");
                                }
                            });
                            btn_login.post(new Runnable() {
                                @Override
                                public void run() {
                                    btn_login.setText("login");
                                }
                            });
                            user_photo.post(new Runnable() {
                                @Override
                                public void run() {
                                    user_photo.setImageResource(R.mipmap.default_portrait);
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

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 去登录
                if(btn_login.getText().equals("login")) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
                // 退出登录
                else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            LogoutProcess(client);
                        }
                    }).start();
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

    private void LogoutProcess(OkHttpClient client){
        String logout_url = DomainURL+"/auth/logout";
        Request logout_req = new Request.Builder()
                .url(logout_url)
                .build();
        Response response = null;
        try {
            response = client.newCall(logout_req).execute();
            if(response.isSuccessful()) {
                //清除cookie
                SharedPreferences p_cookie = getActivity().getSharedPreferences("COOKIES", Context.MODE_PRIVATE);
                SharedPreferences.Editor e_cookie = p_cookie.edit();
                e_cookie.clear();
                e_cookie.commit();
                // 清除用户登陆显示
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove("user_id");
                editor.remove("nickname");
                editor.remove("photo");
                editor.commit();
                //清除之前的好友信息
                friendsDao.clearTable();

                text.post(new Runnable() {
                    @Override
                    public void run() {
                        text.setText("NO USER");
                    }
                });
                user_photo.post(new Runnable() {
                    @Override
                    public void run() {
                        user_photo.setImageResource(R.mipmap.default_portrait);
                    }
                });

                btn_login.post(new Runnable() {
                    @Override
                    public void run() {
                        btn_login.setText("login");
                    }
                });
            }
            else{
                if(Looper.myLooper()==null)
                    Looper.prepare();
                Toast.makeText(MainActivity.getContext(), "Server Error. Please check " +
                        "the Internet connection", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        } catch (IOException e) {
//            throw new RuntimeException(e);
            if(Looper.myLooper()==null)
                Looper.prepare();
            Toast.makeText(MainActivity.getContext(), "Server Error. Please check " +
                    "the Internet connection", Toast.LENGTH_SHORT).show();
            Looper.loop();
        }
    }


}