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
        host = url.host(); //get the host from the url
        preferences = context.getSharedPreferences("COOKIES", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // make sure that the corresponding host do not have cookie locally
        String exist_cookie_name = preferences.getString(host+"name","");
        String exist_cookie_value = preferences.getString(host+"value","");
        if (exist_cookie_name != "" || exist_cookie_value!= ""){
            // if it exists, clear the previous one
            editor.remove(host+"name");
            editor.remove(host+"value");
        }

        // store the new cookie
        editor.putString(url.host()+"name", cookies.get(0).name());
        editor.putString(url.host()+"value", cookies.get(0).value());
        editor.commit();
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        host = url.host();
        preferences = context.getSharedPreferences("COOKIES", Context.MODE_PRIVATE);

        // load the corresponding cookie from local sharedpreference
        String exist_cookie_name = preferences.getString(host+"name", "");
        String exist_cookie_value = preferences.getString(host+"value", "");


        List<Cookie> cookies = new ArrayList<Cookie>();
        if (exist_cookie_name!="" && exist_cookie_value!="") {
            Cookie cookie = new Cookie.Builder().name(exist_cookie_name)
                    .domain(url.host())
                    .value(exist_cookie_value)
                    .build();
            cookies.add(0, cookie);
        }
        //return the existing cookie
        return exist_cookie_name != null ? cookies : new ArrayList<Cookie>();
    }
}