package com.example.myapplication.ui.test;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.myapplication.Bean.HistoryBean;
import com.example.myapplication.Bean.RecordBean;
import com.example.myapplication.Dao.HistoryDao;
import com.example.myapplication.Dao.RecDataBase;
import com.example.myapplication.Dao.RecordDao;
import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityTest2Binding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

public class Test2Activity extends AppCompatActivity {

    private ActivityTest2Binding binding;
    private HistoryDao historyDao;
    private RecordDao recordDao;
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

    static SharedPreferences sp;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recDataBase = Room.databaseBuilder(this, RecDataBase.class, "RecDataBase").allowMainThreadQueries().build();
        historyDao = recDataBase.historyDao();
        recordDao = recDataBase.recordDao();

        binding = ActivityTest2Binding.inflate(getLayoutInflater());
        sp = getSharedPreferences("sp", Context.MODE_PRIVATE);
        setContentView(binding.getRoot());
        binding.ivA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answerControl(binding.tvA, (ImageView) view);
            }
        });

        binding.ivB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answerControl(binding.tvB, (ImageView) view);
            }
        });

        binding.ivC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answerControl(binding.tvC, (ImageView) view);
            }
        });

        binding.ivD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answerControl(binding.tvD, (ImageView) view);
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

                    binding.ivA.setClickable(true);
                    binding.ivB.setClickable(true);
                    binding.ivC.setClickable(true);
                    binding.ivD.setClickable(true);

                } else if (question == total) {

                    Calendar c = Calendar.getInstance();
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH) + 1;
                    int day = c.get(Calendar.DAY_OF_MONTH);
                    RecordBean bean = new RecordBean();
                    bean.setAddDate(year + "-" + month + "-" + day);
                    bean.setType(1);

                    recordDao.addToRecord(bean);

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

                if (questionsList.isEmpty()) {
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

        binding.ivFlag.setText(correctFlag.getEnName());

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

        String lan = sp.getString("lan", "Chinese");
        if (lan.equals("Spanish")) {
            binding.tvA.setText(options.get(0).getSpaName());
            binding.tvB.setText(options.get(1).getSpaName());
            binding.tvC.setText(options.get(2).getSpaName());
            binding.tvD.setText(options.get(3).getSpaName());
        } else if (lan.equals("Japanese")) {
            binding.tvA.setText(options.get(0).getJpName());
            binding.tvB.setText(options.get(1).getJpName());
            binding.tvC.setText(options.get(2).getJpName());
            binding.tvD.setText(options.get(3).getJpName());
        } else if (lan.equals("Korean")) {
            binding.tvA.setText(options.get(0).getKorName());
            binding.tvB.setText(options.get(1).getKorName());
            binding.tvC.setText(options.get(2).getKorName());
            binding.tvD.setText(options.get(3).getKorName());
        }else if (lan.equals("French")) {
            binding.tvA.setText(options.get(0).getFraName());
            binding.tvB.setText(options.get(1).getFraName());
            binding.tvC.setText(options.get(2).getFraName());
            binding.tvD.setText(options.get(3).getFraName());
        }else {
            binding.tvA.setText(options.get(0).getName());
            binding.tvB.setText(options.get(1).getName());
            binding.tvC.setText(options.get(2).getName());
            binding.tvD.setText(options.get(3).getName());
        }


        binding.ivA.setImageDrawable(Drawable.createFromPath(options.get(0).getPath()));
        binding.ivB.setImageDrawable(Drawable.createFromPath(options.get(1).getPath()));
        binding.ivC.setImageDrawable(Drawable.createFromPath(options.get(2).getPath()));
        binding.ivD.setImageDrawable(Drawable.createFromPath(options.get(3).getPath()));

        // 新加的
        binding.ivA.setBackgroundColor(getResources().getColor(R.color.transparent));
        binding.ivB.setBackgroundColor(getResources().getColor(R.color.transparent));
        binding.ivC.setBackgroundColor(getResources().getColor(R.color.transparent));
        binding.ivD.setBackgroundColor(getResources().getColor(R.color.transparent));


        binding.llEmpty.setVisibility(View.GONE);
    }

    public void answerControl(TextView btn, ImageView iv) {
        String buttonText = btn.getText().toString();
        String correctAnswer = "";
        String lan = sp.getString("lan", "Chinese");
        if (lan.equals("Spanish")) {
            correctAnswer = correctFlag.getSpaName();
        } else if (lan.equals("Japanese")) {
            correctAnswer = correctFlag.getJpName();
        } else if (lan.equals("Korean")) {
            correctAnswer = correctFlag.getKorName();
        }else if (lan.equals("French")) {
            correctAnswer = correctFlag.getFraName();
        }else {
            correctAnswer = correctFlag.getName();
        }

        if (buttonText.equals(correctAnswer)) {
            correct++;
//            iv.setImageDrawable(new ColorDrawable(Color.GREEN));
            iv.setBackgroundColor(getResources().getColor(R.color.text_green));
            HistoryBean bean = questionsList.get(question);
            int num = bean.getNum();
            historyDao.updateNum(bean.getId(), num + 1);
            bean.setNum(num + 1);
        } else {
            wrong++;
//            iv.setImageDrawable(new ColorDrawable(Color.BLUE));
            iv.setBackgroundColor(getResources().getColor(R.color.wrong_choice));

            if (binding.tvA.getText().toString().equals(correctAnswer)) {
//                binding.ivA.setImageDrawable(new ColorDrawable(Color.GREEN));
                binding.ivA.setBackgroundColor(getResources().getColor(R.color.text_green));
            }
            if (binding.tvB.getText().toString().equals(correctAnswer)) {
//                binding.ivB.setImageDrawable(new ColorDrawable(Color.GREEN));
                binding.ivB.setBackgroundColor(getResources().getColor(R.color.text_green));
            }
            if (binding.tvC.getText().toString().equals(correctAnswer)) {
//                binding.ivC.setImageDrawable(new ColorDrawable(Color.GREEN));
                binding.ivC.setBackgroundColor(getResources().getColor(R.color.text_green));
            }
            if (binding.tvD.getText().toString().equals(correctAnswer)) {
//                binding.ivD.setImageDrawable(new ColorDrawable(Color.GREEN));
                binding.ivD.setBackgroundColor(getResources().getColor(R.color.text_green));
            }
        }
        binding.ivA.setClickable(false);
        binding.ivB.setClickable(false);
        binding.ivC.setClickable(false);
        binding.ivD.setClickable(false);

        binding.tvCorrect.setText("Correct: " + correct);
        binding.tvWrong.setText("Wrong: " + wrong);

        buttonControl = true;
    }
}