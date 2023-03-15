package com.example.myapplication.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.net.CookieStore;
import java.util.ArrayList;
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

//        for (Cookie cookie : cookies) {
//            String name = cookie.name();
//            //如果已经存在
//            //全部清空
//            if(preferences.getString(name,"null") != null){
//                editor.clear();
//            }
//            editor.putString(name, cookie.value());
//        }
//
//        editor.commit();

        // 确保对应host没有cookie
        String exist_cookie_name = preferences.getString(host+"name","null");
        String exist_cookie_value = preferences.getString(host+"value",null);
        if (exist_cookie_name != null && exist_cookie_value!=null){
            // 删除对应key中的数据
            editor.remove(host+"name");
            editor.remove(host+"value");
        }
//         保存cookie, 对应方式为 host url - cookie
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
        String exist_cookie_name = preferences.getString(host+"name", null);
        String exist_cookie_value = preferences.getString(host+"value", null);

        // 创建一个新的cookie列表
        List<Cookie> cookies = new ArrayList<Cookie>();
        //把之前的字符串全都当作cookie的名字放入
        if (exist_cookie_name!=null && exist_cookie_value!= null) {
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