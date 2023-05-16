package com.example.myapplication.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.net.CookieStore;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class CookieJarImpl implements CookieJar {

    private SharedPreferences preferences;
    private Context context;
    private String host;

    public CookieJarImpl(Context context){
        this.context = context;
    }


    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        host = url.host(); //获取url对应host
        preferences = context.getSharedPreferences("COOKIES", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // 确保对应host没有cookie
        String exist_cookie_name = preferences.getString(host+"name","");
        String exist_cookie_value = preferences.getString(host+"value","");
        if (exist_cookie_name != "" || exist_cookie_value!= ""){
            // 删除对应key中的数据
            editor.remove(host+"name");
            editor.remove(host+"value");
        }
//         保存cookie, 对应方式为 host url - cookie
//        long expireDate = cookies.get(0).expiresAt();
//        Boolean bool = expireDate > System.currentTimeMillis();
//        long now = System.currentTimeMillis();
//        long one_day_after = now + 86400*1000;
//        Date tomorrow = new Date(one_day_after);

//        long total_second = expireDate/1000;
//        long total_minutes = total_second/60;
//
//        String x = String.valueOf(expireDate);
//        String x13 = x.substring(0,x.length()-4);
//        Date x_date = new Date(Long.valueOf(x13));
//        long to_now = expireDate - now;
//        long to_now_sec = to_now/1000;
//
//        Date datenow = new Date();
//        Date expire = new Date(expireDate+System.currentTimeMillis());
        //主服务器对应名字
        editor.putString(url.host()+"name", cookies.get(0).name());
        editor.putString(url.host()+"value", cookies.get(0).value());
        editor.commit();
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        host = url.host();
        preferences = context.getSharedPreferences("COOKIES", Context.MODE_PRIVATE);

        // 调取对应url的cookie
        String exist_cookie_name = preferences.getString(host+"name", "");
        String exist_cookie_value = preferences.getString(host+"value", "");

        // 创建一个新的cookie列表
        List<Cookie> cookies = new ArrayList<Cookie>();
        //把之前的字符串全都当作cookie的名字放入
        if (exist_cookie_name!="" && exist_cookie_value!="") {
            Cookie cookie = new Cookie.Builder().name(exist_cookie_name)
                    .domain(url.host())
                    .value(exist_cookie_value)
                    .build();
            //添加到列表里
            cookies.add(0, cookie);
        }
        //注：这里不能返回null，否则会报NULLException的错误。
        //原因：当Request 连接到网络的时候，OkHttp会调用loadForRequest()
        return exist_cookie_name != null ? cookies : new ArrayList<Cookie>();
    }
}