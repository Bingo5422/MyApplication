package com.example.myapplication.ui.dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.myapplication.Bean.HistoryBean;
import com.example.myapplication.Bean.RecordBean;
import com.example.myapplication.Dao.HistoryDao;
import com.example.myapplication.Dao.RecDataBase;
import com.example.myapplication.Dao.RecordDao;
import com.example.myapplication.Utils.VoiceUtil;
import com.example.myapplication.databinding.ActivityWordDetailBinding;
import com.example.myapplication.ui.recognition.HistoryActivity;

import java.util.Calendar;

public class WordDetailActivity extends AppCompatActivity {

    ActivityWordDetailBinding binding;
    private RecDataBase recDataBase;
    private HistoryDao historyDao;
    private RecordDao recordDao;
    private HistoryBean historyBean;
    private ImageView voice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWordDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        historyBean = (HistoryBean) getIntent().getSerializableExtra("word");

        SharedPreferences sp = getSharedPreferences("sp", Context.MODE_PRIVATE);
        String lan = sp.getString("lan", "Chinese");
        if (lan.equals("Spanish")) {
            binding.tvWord.setText(historyBean.getSpaName());
        } else if (lan.equals("Japanese")) {
            binding.tvWord.setText(historyBean.getJpName());
        } else if (lan.equals("Korean")) {
            binding.tvWord.setText(historyBean.getKorName());
        }else if (lan.equals("French")) {
            binding.tvWord.setText(historyBean.getFraName());
        }else {
            binding.tvWord.setText(historyBean.getName());
        }


        binding.tvWord2.setText(historyBean.getEnName());
        binding.btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.tvWord2.getVisibility() == View.VISIBLE) {
                    binding.tvWord2.setVisibility(View.GONE);
                    binding.btnShow.setText("SHOW PARAPHRASE");
                } else {
                    binding.tvWord2.setVisibility(View.VISIBLE);
                    binding.btnShow.setText("HIDDEN PARAPHRASE");
                }
            }
        });
        recDataBase = Room.databaseBuilder(this, RecDataBase.class, "RecDataBase").allowMainThreadQueries().build();
        historyDao = recDataBase.historyDao();
        recordDao = recDataBase.recordDao();

        binding.btnNewWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                historyDao.updateNum(historyBean.getId(), 0);
                Toast.makeText(WordDetailActivity.this, "Add To New Words NoteBook Success", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnVocabulary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                historyDao.updateNum(historyBean.getId(), 3);

                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH) + 1;
                int day = c.get(Calendar.DAY_OF_MONTH);
                RecordBean bean = new RecordBean();
                bean.setAddDate(year + "-" + month + "-" + day);
                bean.setHisId(historyBean.getId());

                recordDao.addToRecord(bean);

                Toast.makeText(WordDetailActivity.this, "Add To Vocabulary NoteBook Success", Toast.LENGTH_SHORT).show();
            }
        });

        binding.voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (historyBean != null) {
                    if (lan.equals("Spanish")) {
                        String spanish = historyBean.getSpaName();
                        VoiceUtil.voice(WordDetailActivity.this, spanish,"x2_SpEs_Aurora");
                    } else if (lan.equals("Japanese")) {
                        String japanese = historyBean.getSpaName();
                        VoiceUtil.voice(WordDetailActivity.this, japanese,"x2_JaJp_ZhongCun");
                    }else if (lan.equals("Korean")) {
                        String korean = historyBean.getSpaName();
                        VoiceUtil.voice(WordDetailActivity.this, korean,"zhimin");
                    }else if (lan.equals("French")) {
                        String french = historyBean.getSpaName();
                        VoiceUtil.voice(WordDetailActivity.this, french,"x2_FrRgM_Lisa");
                    } else {
                        String chinese = historyBean.getName();
                        VoiceUtil.voice(WordDetailActivity.this, chinese,"aisxping");
                    }

                }
            }
        });

    }
}
