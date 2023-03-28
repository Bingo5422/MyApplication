package com.example.myapplication.ui.dashboard;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import com.example.myapplication.Bean.HistoryBean;
import com.example.myapplication.Dao.HistoryDao;
import com.example.myapplication.Dao.RecDataBase;
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentDashboardBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private HistoryDao historyDao;
    private RecDataBase recDataBase;
    private List<HistoryBean> questionsList = new ArrayList<>();
    private List<HistoryBean> wrongOptionsList = new ArrayList<>();
    HashSet<HistoryBean> mixOptions = new HashSet<>();
    ArrayList<HistoryBean> options = new ArrayList<>();

    boolean buttonControl = false;
    private int correct = 0;
    private int wrong = 0;
    private int empty = 0;
    private int question = 0;
    private int total = 10;

    private HistoryBean correctFlag;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recDataBase = Room.databaseBuilder(getContext(), RecDataBase.class, "RecDataBase").allowMainThreadQueries().build();
        historyDao = recDataBase.historyDao();

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDashboardBinding.inflate(inflater, container, false);

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
                question++;

                if (!buttonControl && question < total) {
                    empty++;
                    binding.tvEmpty.setText("Empty: " + empty);
                    loadQuestions();
                } else if (buttonControl && question < total) {
                    loadQuestions();

                    binding.btnA.setClickable(true);
                    binding.btnB.setClickable(true);
                    binding.btnC.setClickable(true);
                    binding.btnD.setClickable(true);

                    binding.btnA.setBackgroundColor(getResources().getColor(R.color.button));
                    binding.btnB.setBackgroundColor(getResources().getColor(R.color.button));
                    binding.btnC.setBackgroundColor(getResources().getColor(R.color.button));
                    binding.btnD.setBackgroundColor(getResources().getColor(R.color.button));
                } else if (question == total) {
                    question = 0;
                    empty= 0;
                    correct = 0;
                    wrong=0;
                    binding.tvEmpty.setText("Empty: " + empty);

                    Intent intent = new Intent(getContext(), ResultActivity.class);
                    intent.putExtra("correct", correct);
                    intent.putExtra("wrong", wrong);
                    intent.putExtra("empty", empty);
                    startActivity(intent);
                }

                buttonControl = false;
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                questionsList.clear();
                questionsList.addAll(historyDao.query());

                if(questionsList.size()<10){
                    total = questionsList.size();
                }else{
                    total = 10;
                }

                wrongOptionsList.clear();
                wrongOptionsList.addAll(historyDao.queryRand3());

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
        if(wrongOptionsList.size()<3){
            return;
        }
        binding.tvQuestion.setText("Question: " + (question + 1));

        correctFlag = questionsList.get(question);

        binding.ivFlag.setImageBitmap(BitmapFactory.decodeFile(correctFlag.getPath()));

        wrongOptionsList.clear();
        wrongOptionsList.addAll(historyDao.queryRand3());

        mixOptions.clear();
        mixOptions.add(correctFlag);
        mixOptions.add(wrongOptionsList.get(0));
        mixOptions.add(wrongOptionsList.get(1));
        mixOptions.add(wrongOptionsList.get(2));

        options.clear();
        for (HistoryBean flg : mixOptions) {
            options.add(flg);
        }

        binding.btnA.setText(options.get(0).getName());
        binding.btnB.setText(options.get(1).getName());
        binding.btnC.setText(options.get(2).getName());
        binding.btnD.setText(options.get(3).getName());


        binding.llEmpty.setVisibility(View.GONE);
    }

    public void answerControl(Button btn) {
        String buttonText = btn.getText().toString();
        String correctAnswer = correctFlag.getName();

        if (buttonText.equals(correctAnswer)) {
            correct++;
            btn.setBackgroundColor(Color.GREEN);
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