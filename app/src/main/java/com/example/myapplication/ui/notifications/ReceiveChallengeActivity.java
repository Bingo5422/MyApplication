package com.example.myapplication.ui.notifications;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.myapplication.Bean.ChallengeBean;
import com.example.myapplication.Bean.HistoryBean;
import com.example.myapplication.Dao.ChallengeDao;
import com.example.myapplication.Dao.HistoryDao;
import com.example.myapplication.Dao.RecDataBase;
import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityReceiveChallengeBinding;
import com.example.myapplication.ui.test.ResultActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ReceiveChallengeActivity extends AppCompatActivity {


    private ActivityReceiveChallengeBinding binding;
    private ChallengeDao challengeDao;
    private ChallengeBeanDatabase challengeBeanDatabase;
    private List<ChallengeBean> questionsList = new ArrayList<>();
    private List<ChallengeBean> wrongOptionsList = new ArrayList<>();
    HashSet<ChallengeBean> mixOptions = new HashSet<>();
    ArrayList<ChallengeBean> options = new ArrayList<>();

    boolean buttonControl = false;
    private int correct = 0;
    private int wrong = 0;
    private int question = 0;
    private int total;

    private ChallengeBean correctFlag;
    static SharedPreferences sp;
    private String groupNum;
    private static final String TAG = "ReceiveChallengeActivit";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //需要receive的时候传来groupNum
        Intent intent = getIntent();
        groupNum = intent.getStringExtra("group");

        Log.d(TAG, "onCreate: 传来的groupNum："+groupNum);
        //构建数据库
        challengeBeanDatabase = Room.databaseBuilder(this, ChallengeBeanDatabase.class, "challenge_db").allowMainThreadQueries().build();
        challengeDao = challengeBeanDatabase.challengeDao();
        binding = ActivityReceiveChallengeBinding.inflate(getLayoutInflater());





        sp = getSharedPreferences("sp", Context.MODE_PRIVATE);
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
                    Toast.makeText(ReceiveChallengeActivity.this, "please select an answer", Toast.LENGTH_SHORT).show();
                    return;
                }
                question++;
                if (buttonControl && question < total) {
                    loadQuestions();

                    binding.btnA.setClickable(true);
                    binding.btnB.setClickable(true);
                    binding.btnC.setClickable(true);
                    binding.btnD.setClickable(true);

                    //这里改了一下按钮背景
                    binding.btnA.setBackground(getResources().getDrawable(R.drawable.test_options_btn));
                    binding.btnB.setBackground(getResources().getDrawable(R.drawable.test_options_btn));
                    binding.btnC.setBackground(getResources().getDrawable(R.drawable.test_options_btn));
                    binding.btnD.setBackground(getResources().getDrawable(R.drawable.test_options_btn));

//                    binding.btnA.setBackgroundColor(getResources().getColor(R.color.button));
//                    binding.btnB.setBackgroundColor(getResources().getColor(R.color.button));
//                    binding.btnC.setBackgroundColor(getResources().getColor(R.color.button));
//                    binding.btnD.setBackgroundColor(getResources().getColor(R.color.button));
                } else if (question == total) {

                    Intent intent = new Intent(ReceiveChallengeActivity.this, ResultActivity.class);
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

                questionsList.addAll(challengeDao.queryByGroup(groupNum));

                if (questionsList.isEmpty()) {
                    return;
                }

//                if (questionsList.size() < 10) {
//                    total = questionsList.size();
//                } else {
//                    total = 10;
//                }
                total = questionsList.size();//好友发来几条做几条

                wrongOptionsList.clear();
                wrongOptionsList.addAll(challengeDao.queryRand3(questionsList.get(question).getName()));

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

        binding.ivFlag.setImageBitmap(BitmapFactory.decodeFile(correctFlag.getFilepath()));

        wrongOptionsList.clear();
        wrongOptionsList.addAll(challengeDao.queryRand3(questionsList.get(question).getName()));

        mixOptions.clear();
        mixOptions.add(correctFlag);
        mixOptions.add(wrongOptionsList.get(0));
        mixOptions.add(wrongOptionsList.get(1));
        mixOptions.add(wrongOptionsList.get(2));

        options.clear();
        for (ChallengeBean flg : mixOptions) {
            options.add(flg);
        }

        String lan = sp.getString("lan", "Chinese");
        if (lan.equals("Spanish")) {
            binding.btnA.setText(options.get(0).getSpaName());
            binding.btnB.setText(options.get(1).getSpaName());
            binding.btnC.setText(options.get(2).getSpaName());
            binding.btnD.setText(options.get(3).getSpaName());
        } else if (lan.equals("Japanese")) {
            binding.btnA.setText(options.get(0).getJpName());
            binding.btnB.setText(options.get(1).getJpName());
            binding.btnC.setText(options.get(2).getJpName());
            binding.btnD.setText(options.get(3).getJpName());
        } else if (lan.equals("Korean")) {
            binding.btnA.setText(options.get(0).getKorName());
            binding.btnB.setText(options.get(1).getKorName());
            binding.btnC.setText(options.get(2).getKorName());
            binding.btnD.setText(options.get(3).getKorName());
        }else if (lan.equals("French")) {
            binding.btnA.setText(options.get(0).getFraName());
            binding.btnB.setText(options.get(1).getFraName());
            binding.btnC.setText(options.get(2).getFraName());
            binding.btnD.setText(options.get(3).getFraName());
        }else {
            binding.btnA.setText(options.get(0).getName());
            binding.btnB.setText(options.get(1).getName());
            binding.btnC.setText(options.get(2).getName());
            binding.btnD.setText(options.get(3).getName());
        }


        binding.llEmpty.setVisibility(View.GONE);
    }

    public void answerControl(Button btn) {
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
            btn.setBackground(getResources().getDrawable(R.drawable.test_true_btn));
//            btn.setBackgroundColor(Color.GREEN);
              //这里是熟练度，应该不用
//            ChallengeBean bean = questionsList.get(question);
//            int num = bean.getNum();
//            historyDao.updateNum(bean.getId(), num + 1);
//            bean.setNum(num + 1);
        } else {
            wrong++;
            btn.setBackground(getResources().getDrawable(R.drawable.test_wrong_btn));
            //btn.setBackgroundColor(Color.RED); //这里改了错误时候的颜色 原来是color.blue

            if (binding.btnA.getText().toString().equals(correctAnswer)) {
                binding.btnA.setBackground(getResources().getDrawable(R.drawable.test_true_btn));
//                binding.btnA.setBackgroundColor(Color.GREEN);
            }
            if (binding.btnB.getText().toString().equals(correctAnswer)) {
                binding.btnB.setBackground(getResources().getDrawable(R.drawable.test_true_btn));
//                binding.btnB.setBackgroundColor(Color.GREEN);
            }
            if (binding.btnC.getText().toString().equals(correctAnswer)) {
                binding.btnC.setBackground(getResources().getDrawable(R.drawable.test_true_btn));
//                binding.btnC.setBackgroundColor(Color.GREEN);
            }
            if (binding.btnD.getText().toString().equals(correctAnswer)) {
                binding.btnD.setBackground(getResources().getDrawable(R.drawable.test_true_btn));
//                binding.btnD.setBackgroundColor(Color.GREEN);
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

