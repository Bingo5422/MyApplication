package com.example.myapplication.ui.notifications;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.myapplication.R;

public class JumpActivity extends AppCompatActivity {
    LottieAnimationView lottieAnimationView;
    RelativeLayout relativeLayout;
    ImageView ivback;
    TextView tvRe;
    Button reBack;
    String re;

    private static final String TAG = "JumpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jump);

        Intent intent = getIntent();
        String groupNum = intent.getStringExtra("group");
        String friendName = intent.getStringExtra("friend_name");
        String friendId = intent.getStringExtra("friend_id");

        ivback = findViewById(R.id.jump_back);
        ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        lottieAnimationView = findViewById(R.id.lo);
        lottieAnimationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(JumpActivity.this,ReceiveChallengeActivity.class);
                intent1.putExtra("group", groupNum);
                intent1.putExtra("friend_name",friendName);
                intent1.putExtra("friend_id", friendId);
                startActivity(intent1);
            }
        });



    }


}