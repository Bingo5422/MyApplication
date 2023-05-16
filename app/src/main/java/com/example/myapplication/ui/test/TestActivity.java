package com.example.myapplication.ui.test;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.example.myapplication.databinding.ActivityTestBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

public class TestActivity extends AppCompatActivity {

    private ActivityTestBinding binding;
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
    private RecordDao recordDao;
    static SharedPreferences sp;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recDataBase = Room.databaseBuilder(this, RecDataBase.class, "RecDataBase").allowMainThreadQueries().build();
        historyDao = recDataBase.historyDao();
        recordDao = recDataBase.recordDao();
//        将ActivityTestBinding对象与该Activity关联起来
        binding = ActivityTestBinding.inflate(getLayoutInflater());
//        存储和读取应用程序的配置信息
        sp = getSharedPreferences("sp", Context.MODE_PRIVATE);
//        设置布局文件
        setContentView(binding.getRoot());
//        为按钮A B C D 设置点击事件，当用户点击按钮时，会触发answerControl方法来处理用户的答题行为
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
//      加载下一题的逻辑
        binding.ivNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//              用户是否选择了答案
                if (!buttonControl) {
                    Toast.makeText(TestActivity.this, "please select an answer", Toast.LENGTH_SHORT).show();
                    return;
                }
                question++;
                if (buttonControl && question < total) {
//                  加载下一题
                    loadQuestions();
//                  重置按钮的可点击属性
                    binding.btnA.setClickable(true);
                    binding.btnB.setClickable(true);
                    binding.btnC.setClickable(true);
                    binding.btnD.setClickable(true);

                    //这里改了一下按钮背景
                    binding.btnA.setBackground(getResources().getDrawable(R.drawable.test_options_btn));
                    binding.btnB.setBackground(getResources().getDrawable(R.drawable.test_options_btn));
                    binding.btnC.setBackground(getResources().getDrawable(R.drawable.test_options_btn));
                    binding.btnD.setBackground(getResources().getDrawable(R.drawable.test_options_btn));

                } else if (question == total) {

                    Calendar c = Calendar.getInstance();
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH) + 1;
                    int day = c.get(Calendar.DAY_OF_MONTH);
                    RecordBean bean = new RecordBean();
                    bean.setAddDate(year + "-" + month + "-" + day);
                    bean.setType(1);

                    recordDao.addToRecord(bean);

//                  跳转到结果展示页面ResultActivity
                    Intent intent = new Intent(TestActivity.this, ResultActivity.class);
                    intent.putExtra("correct", correct);
                    intent.putExtra("wrong", wrong);
//                  启动ResultActivity页面
                    startActivity(intent);
                    finish();

                }

                buttonControl = false;
            }
        });
    }

    @Override
//    Activity销毁时，释放ViewBinding对象的引用，以避免内存泄漏
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
//  Activity恢复时启动一个新线程，并执行其中的代码
    public void onResume() {
        super.onResume();
//      包含需要在新线程中执行的代码，在新线程中执行的代码可以执行任何需要在后台线程中执行的操作，如计算
        new Thread(new Runnable() {
            @Override
            public void run() {
                questionsList.clear();
//              数据库中查询到的历史记录中答对少于三次
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

//                这里？？？

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
//      第一题的序号是1不是0
        binding.tvQuestion.setText("Question: " + (question + 1));

        correctFlag = questionsList.get(question);
//      该题目图片显示在 ImageView 控件上
        binding.ivFlag.setImageBitmap(BitmapFactory.decodeFile(correctFlag.getPath()));

        wrongOptionsList.clear();
        wrongOptionsList.addAll(historyDao.queryRand3(questionsList.get(question).getName()));
//      存储着混合选项的列表
        mixOptions.clear();
        mixOptions.add(correctFlag);
        mixOptions.add(wrongOptionsList.get(0));
        mixOptions.add(wrongOptionsList.get(1));
        mixOptions.add(wrongOptionsList.get(2));
//      将 mixOptions 列表中的元素添加到 options 列表中，完成选项设置
        options.clear();
        for (HistoryBean flg : mixOptions) {
            options.add(flg);
        }
//      获取一个名为 "lan" 的偏好设置值，如果找不到则使用默认值 "Chinese"
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

//      可能是一个加载中的进度条，视图不可见
        binding.llEmpty.setVisibility(View.GONE);
    }
//  设置正确答案的名称
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
//        处理用户的答案是否正确
        if (buttonText.equals(correctAnswer)) {
            correct++;
            btn.setBackground(getResources().getDrawable(R.drawable.test_true_btn));
//            btn.setBackgroundColor(Color.GREEN);
//            更新问题对象中的答对次数属性
            HistoryBean bean = questionsList.get(question);
            int num = bean.getNum();
            historyDao.updateNum(bean.getId(), num + 1);
            bean.setNum(num + 1);
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
//        已经回答过的按钮变为不可点击状态
        binding.btnA.setClickable(false);
        binding.btnB.setClickable(false);
        binding.btnC.setClickable(false);
        binding.btnD.setClickable(false);

        binding.tvCorrect.setText("Correct: " + correct);
        binding.tvWrong.setText("Wrong: " + wrong);

        buttonControl = true;
    }
}