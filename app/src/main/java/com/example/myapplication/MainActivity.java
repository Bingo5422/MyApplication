package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.myapplication.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static Context context;
    public static Context getContext() {
        return context;
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();


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


}