package com.example.myapplication.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class ResultChallengeActivity extends AppCompatActivity {
    private TextView tvSuccessRate, tvTotalWrong, tvTotalEmpty, tvTotalCorrect;
    private Button btnAgain, btnQuit;
    int correct, wrong, empty;

    private static final String TAG = "ResultActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        tvTotalCorrect = findViewById(R.id.tvTotalCorrect);
        tvTotalWrong = findViewById(R.id.tvTotalWrong);
        tvTotalEmpty = findViewById(R.id.tvTotalEmpty);
        tvSuccessRate = findViewById(R.id.tvSuccessRate);

        btnAgain = findViewById(R.id.btnAgain);
        btnQuit = findViewById(R.id.btnQuit);

        correct = getIntent().getIntExtra("correct", 0);
        wrong = getIntent().getIntExtra("wrong", 0);
        empty = getIntent().getIntExtra("empty", 0);

        String friendName = getIntent().getStringExtra("friend_name");
        String friendID = getIntent().getStringExtra("friend_id");

        tvTotalCorrect.setText("Correct Answers: " + correct);
        tvTotalWrong.setText("Wrong Answers: " + wrong);
        tvTotalEmpty.setText("Empty Answers: " + empty);
        tvSuccessRate.setText("Success Rate: " + (correct * 10) + "%");

        btnAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        String re = "Correct: "+ correct +"\n"+"Wrong"+wrong;


        Log.d(TAG, "result里的 re"+re);

        btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent newIntent = new Intent(view.getContext(), MainActivity.class);
//                newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(newIntent);
                Intent intent = new Intent(ResultChallengeActivity.this, ChatActivity.class);

                // 在第三个页面（Activity C）的某个地方调用以下代码

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                intent.putExtra("friend_name",friendName);
                intent.putExtra("friend_id", friendID);

                intent.putExtra("grade",re);




                startActivity(intent);
            }
        });
    }
}