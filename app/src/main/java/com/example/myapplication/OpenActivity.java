package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;

public class OpenActivity extends AppCompatActivity {
    ImageView bg,name;
    LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open);
        bg = findViewById(R.id.bg);
        bg.setAlpha(0.5f);
        name = findViewById(R.id.name);
        lottieAnimationView = findViewById(R.id.lottie);


        //bg.animate().translationY(-3000).setDuration(1000).setStartDelay(4000);
        name.animate().translationY(3000).setDuration(1000).setStartDelay(4000);
        lottieAnimationView.animate().translationY(3000).setDuration(1000).setStartDelay(4000);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 在延迟后执行的代码
                Intent intent = new Intent(OpenActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 4700);

    }
}