package com.example.myapplication;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.databinding.ActivityMainBinding;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    public static final String CHANNEL_ID = "default";

    private static Context context;

    public static Context getContext() {
        return context;
    }

    private SharedPreferences sp;
    long startTime = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();

        //创建通知渠道
        createNotificationChannel();

        // 语音合成APPID
        SpeechUtility.createUtility(context, SpeechConstant.APPID + "=54d1647f");

        sp = getSharedPreferences("sp", MODE_PRIVATE);

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        String key = year + "-" + month + "-" + day;

        String today = sp.getString(key, "");
        if (TextUtils.isEmpty(today)) {
            startTime = sp.getLong("startTime", System.currentTimeMillis());
            sp.edit().putLong("startTime", startTime).commit();
            sp.edit().putLong("endTime", 0).commit();
            sp.edit().putString(key, key).commit();
        } else {

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Calendar cd = Calendar.getInstance();
                cd.setTime(sdf.parse(today));
                cd.add(Calendar.DATE, -1);

                int oldyear = cd.get(Calendar.YEAR);
                int oldmonth = cd.get(Calendar.MONTH) + 1;
                int oldday = cd.get(Calendar.DAY_OF_MONTH);
                String oldkey = oldyear + "-" + oldmonth + "-" + oldday;
                sp.edit().remove(oldkey).commit();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

        }


        if (
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        ||
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
        }




        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications,
//                R.id.navigation_me).build();
        // 建立fragment容器的控制器，这个容器就是页面的上的fragment容器
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        // 启动
        NavigationUI.setupWithNavController(binding.navView, navController);

//        Intent intent = getIntent();
//        int mFlag = intent.getIntExtra("flag", 0);
//        if (mFlag == 3) { //判断获取到的flag值
//            navController.navigate(R.id.navigation_me);
//        }

    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        int mFlag = intent.getIntExtra("flag", 0);
//        if (mFlag == 3) { //判断获取到的flag值
//            Navigation.findNavController(this, R.id.nav_host_fragment_activity_main).navigate(R.id.navigation_me);
//        }
//    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Default Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Default notification channel for the app");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sp.edit().putLong("endTime", System.currentTimeMillis()).commit();
    }
}