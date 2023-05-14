package com.example.myapplication.ui.home;

import static android.content.Context.ALARM_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.Utils.PhotoUtil;
import com.example.myapplication.Utils.translate.TransApi;
import com.example.myapplication.databinding.FragmentHomeBinding;
import com.example.myapplication.ui.recognition.ChoiceActivity;
import com.example.myapplication.ui.recognition.HistoryActivity;
import com.example.myapplication.ui.recognition.PhotoRecActivity;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private AlarmManager alarmManager;  //闹钟管理器
    private PendingIntent pendingIntent;
    private ImageView main_camera = null;
    private ImageView main_history = null;
    private String picPath;
    private File file;
    private int checkedItem = 0;
    private static final String TAG = "HomeFragment";
    private SharedPreferences sp;

    private int item=0;
    String[] stringArray;
    Context context;




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        Locale.setDefault(Locale.ENGLISH);


        sp = getActivity().getSharedPreferences("sp", Context.MODE_PRIVATE);
        String lan = sp.getString("lan", "");
        stringArray = getResources().getStringArray(R.array.lan);
        for (int i = 0; i < stringArray.length; i++) {
            if (lan.equals(stringArray[i])) {
                checkedItem = i;
            }
        }


    }



    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        HomeViewModel homeViewModel =
//                new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        main_camera = binding.mainCamera;
        binding.menuSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext())
                        .setSingleChoiceItems(R.array.lan, checkedItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                checkedItem = which;
                                sp.edit().putString("lan", stringArray[checkedItem]).commit();
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
        main_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent_choice = new Intent(getContext(), ChoiceActivity.class);
                startActivity(intent_choice);
            }
        });

        //查看历史记录的按钮监听
        main_history = binding.recHistory;
        main_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), HistoryActivity.class);
                startActivity(intent);
                Log.d(TAG, "跳到历史界面");
            }
        });


        //获取闹钟管理器
        alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        binding.timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setClock(view);
            }
        });

        binding.calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CalendarActivity.class);
                startActivity(intent);
                Log.d(TAG, "跳到日历界面");
            }
        });












//        final TextView textView = binding.textHome;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }
    //这上面是初始化fragment自带的代码


    public void setClock(View view){
        //获取当前系统时间
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        //弹出闹钟框
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),AlertDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar c = Calendar.getInstance();    //获取日期对象

                c.set(Calendar.HOUR_OF_DAY, hourOfDay); //设置闹钟小时数
                c.set(Calendar.MINUTE, minute); //设置闹钟分钟数
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);

                Intent intent = new Intent(getContext(), TimeReceiver.class);
                //创建pendingIntent
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(),0X102, intent,0);
                //设置闹钟
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
                Toast.makeText(getContext(), "The alarm is set successfully.", Toast.LENGTH_SHORT).show();
            }
        },hour,minute, true);
        timePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", timePickerDialog);
        timePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "CANCEL", timePickerDialog);
        timePickerDialog.show();



    }







    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }



}