package com.example.myapplication.ui.dashboard;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.myapplication.Bean.HistoryBean;
import com.example.myapplication.Dao.HistoryDao;
import com.example.myapplication.Dao.RecDataBase;
import com.example.myapplication.databinding.ActivityTest2Binding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Test2Activity extends AppCompatActivity {

    private ActivityTest2Binding binding;
    private HistoryDao historyDao;
    private RecDataBase recDataBase;
    private List<HistoryBean> questionsList = new ArrayList<>();
    private List<HistoryBean> wrongOptionsList = new ArrayList<>();
    HashSet<HistoryBean> mixOptions = new HashSet<>();
    ArrayList<HistoryBean> options = new ArrayList<>();

    boolean buttonControl = false;
    private int correct = 0;
    private int wrong = 0;
    private int question = 0;
    private int total = 10;

    private HistoryBean correctFlag;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recDataBase = Room.databaseBuilder(this, RecDataBase.class, "RecDataBase").allowMainThreadQueries().build();
        historyDao = recDataBase.historyDao();
        binding = ActivityTest2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answerControl(binding.btnA);
            }
        });

        binding.btnB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answerControl(binding.btnB);
            }
        });

        binding.btnC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answerControl(binding.btnC);
            }
        });

        binding.btnD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answerControl(binding.btnD);
            }
        });

        binding.ivNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!buttonControl) {
                    Toast.makeText(Test2Activity.this, "please select an answer", Toast.LENGTH_SHORT).show();
                    return;
                }
                question++;
                if (buttonControl && question < total) {
                    loadQuestions();

                    binding.btnA.setClickable(true);
                    binding.btnB.setClickable(true);
                    binding.btnC.setClickable(true);
                    binding.btnD.setClickable(true);

//                    binding.btnA.setBackgroundColor(getResources().getColor(R.color.button));
//                    binding.btnB.setBackgroundColor(getResources().getColor(R.color.button));
//                    binding.btnC.setBackgroundColor(getResources().getColor(R.color.button));
//                    binding.btnD.setBackgroundColor(getResources().getColor(R.color.button));
                } else if (question == total) {

                    Intent intent = new Intent(Test2Activity.this, ResultActivity.class);
                    intent.putExtra("correct", correct);
                    intent.putExtra("wrong", wrong);
                    startActivity(intent);
                    finish();


                }

                buttonControl = false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                questionsList.clear();
                questionsList.addAll(historyDao.queryNumLow3());

                if(questionsList.isEmpty()){
                    return;
                }

                if (questionsList.size() < 10) {
                    total = questionsList.size();
                } else {
                    total = 10;
                }

                wrongOptionsList.clear();
                wrongOptionsList.addAll(historyDao.queryRand3(questionsList.get(question).getName()));

                binding.llEmpty.post(new Runnable() {
                    @Override
                    public void run() {
                        loadQuestions();
                    }
                });

            }
        }).start();
    }

    public void loadQuestions() {
        if (wrongOptionsList.size() < 3) {
            return;
        }
        binding.tvQuestion.setText("Question: " + (question + 1));

        correctFlag = questionsList.get(question);

        binding.ivFlag.setText(correctFlag.getName());

        wrongOptionsList.clear();
        wrongOptionsList.addAll(historyDao.queryRand3(questionsList.get(question).getName()));

        mixOptions.clear();
        mixOptions.add(correctFlag);
        mixOptions.add(wrongOptionsList.get(0));
        mixOptions.add(wrongOptionsList.get(1));
        mixOptions.add(wrongOptionsList.get(2));

        options.clear();
        for (HistoryBean flg : mixOptions) {
            options.add(flg);
        }

        binding.btnA.setText(options.get(0).getEnName());
        binding.btnB.setText(options.get(1).getEnName());
        binding.btnC.setText(options.get(2).getEnName());
        binding.btnD.setText(options.get(3).getEnName());

        binding.btnA.setBackground(Drawable.createFromPath(options.get(0).getPath()));
        binding.btnB.setBackground(Drawable.createFromPath(options.get(1).getPath()));
        binding.btnC.setBackground(Drawable.createFromPath(options.get(2).getPath()));
        binding.btnD.setBackground(Drawable.createFromPath(options.get(3).getPath()));


        binding.llEmpty.setVisibility(View.GONE);
    }

    public void answerControl(Button btn) {
        String buttonText = btn.getText().toString();
        String correctAnswer = correctFlag.getEnName();

        if (buttonText.equals(correctAnswer)) {
            correct++;
            btn.setBackgroundColor(Color.GREEN);
            HistoryBean bean = questionsList.get(question);
            int num = bean.getNum();
            historyDao.updateNum(bean.getId(), num + 1);
            bean.setNum(num + 1);
        } else {
            wrong++;
            btn.setBackgroundColor(Color.BLUE);

            if (binding.btnA.getText().toString().equals(correctAnswer)) {
                binding.btnA.setBackgroundColor(Color.GREEN);
            }
            if (binding.btnB.getText().toString().equals(correctAnswer)) {
                binding.btnB.setBackgroundColor(Color.GREEN);
            }
            if (binding.btnC.getText().toString().equals(correctAnswer)) {
                binding.btnC.setBackgroundColor(Color.GREEN);
            }
            if (binding.btnD.getText().toString().equals(correctAnswer)) {
                binding.btnD.setBackgroundColor(Color.GREEN);
            }
        }
        binding.btnA.setClickable(false);
        binding.btnB.setClickable(false);
        binding.btnC.setClickable(false);
        binding.btnD.setClickable(false);

        binding.tvCorrect.setText("Correct: " + correct);
        binding.tvWrong.setText("Wrong: " + wrong);

        buttonControl = true;
    }
}