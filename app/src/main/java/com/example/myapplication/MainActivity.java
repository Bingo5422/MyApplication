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
    // The URL of the server
   public final static String DomainURL = "http://xintong.pythonanywhere.com";

 //   public final static String DomainURL = "http://172.26.14.175:5000";
 //   public final static String DomainURL = "http://192.168.24.21:5000";
//    public final static String DomainURL = "http://192.168.1.59:5000";



    private ActivityMainBinding binding;
    public static final String CHANNEL_ID = "default";

    private static Context context;

    public static Context getContext() {
        return context;
    }

    private SharedPreferences sp;
    long startTime = -1;
    public static String today = "";
    public static final int MAX_COUNT = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();

        // Create notification Channel
        createNotificationChannel();

        // Voice Synthesis APPID
        SpeechUtility.createUtility(context, SpeechConstant.APPID + "=13bc7120");

        sp = getSharedPreferences("sp", MODE_PRIVATE);

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);

        today = year + "-" + month + "-" + day;

        startTime = sp.getLong(today+"startTime",System.currentTimeMillis());
        sp.edit().putLong(today+"startTime", startTime).commit();
        sp.edit().putLong(today+"endTime", startTime).commit();
        if (
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        ||
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
        }



        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        // Start
        NavigationUI.setupWithNavController(binding.navView, navController);

    }



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
        sp.edit().putLong(MainActivity.today+"endTime", System.currentTimeMillis()).commit();
    }
}