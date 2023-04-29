package com.example.myapplication.ui.home;

import static java.util.Calendar.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.Adapter.CalendarAdapter;
import com.example.myapplication.Bean.CalendarBean;
import com.example.myapplication.Bean.HistoryBean;
import com.example.myapplication.Dao.HistoryDao;
import com.example.myapplication.Dao.RecDataBase;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class CalendarActivity extends AppCompatActivity {

    private static final String TAG = "CalendarActivity";
    private ImageView calBack;
    private TextView cardTitle;
    private ImageView cardBackground;
    private RecyclerView cardList;
    private CalendarAdapter calendarAdapter;
    private CardView last;
    private CardView next;
    private int firstDayInMonth;
    public final static List<Integer> day31 = Arrays.asList(1, 3, 5, 7, 8, 10, 12);
    public final static List<Integer> day30 = Arrays.asList(4, 6, 9, 11);
    private Calendar mTodayCalendar = Calendar.getInstance();//当前时间，未修改


    private int month;
    private int year;

    //用历史记录判断是否学过
    boolean isStudy;
    private RecDataBase recDataBase;
    private HistoryDao historyDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        recDataBase = Room.databaseBuilder(this, RecDataBase.class, "RecDataBase").allowMainThreadQueries().build();
        historyDao = recDataBase.historyDao();




        calBack = findViewById(R.id.cal_back);
        calBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cardBackground = findViewById(R.id.card_background);
        cardBackground.setAlpha(0.6f);//背景不透明度


        cardTitle = findViewById(R.id.card_title);
        cardList = findViewById(R.id.card_list);



        //网格布局 7列
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,7);
        cardList.setLayoutManager(gridLayoutManager);
        calendarAdapter = new CalendarAdapter();
        cardList.setAdapter(calendarAdapter);


        Calendar calendar = Calendar.getInstance();
        year = calendar.get(YEAR);//2023
        month = calendar.get(Calendar.MONTH);//4


        //年月标题
        initTitle(year,month);

        //当月月份第一天是一周中的第几天
        firstDayInMonth = getFirstDayOfMonth(year, month);
        Log.d(TAG, year + "年"+ (month+1) + "月的第一天是星期" + (firstDayInMonth-1));

        //填充数据
        List<CalendarBean> calendarData = setMonth(firstDayInMonth,year,month);
        calendarAdapter.setList(calendarData);

//        int i = 26;
//
//        month++;
//        String monthFormat;
//        if(month<10){
//            monthFormat = "0"+month;
//        }else{
//            monthFormat = month+"";
//        }
//        Log.d(TAG, "onCreate: monthFormat: "+monthFormat);
//        String eachDay = year+"-"+monthFormat+"-"+i;
//        isStudy = historyDao.queryByDate(eachDay);
//        Log.d(TAG, "onCreate: eachDay: "+eachDay+" ,isStudy: "+isStudy);

        //切换月份
        last = findViewById(R.id.last);
        next = findViewById(R.id.next);


        last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(month==0){
                    year--;
                    month=11;
                }else{
                    month--;
                }
                initTitle(year,month);
                firstDayInMonth = getFirstDayOfMonth(year, month);
                Log.d(TAG, year + "年"+ (month+1) + "月的第一天是星期" + (firstDayInMonth-1));
                //填充数据
                List<CalendarBean> calendarData = setMonth(firstDayInMonth,year,month);
                calendarAdapter.setList(calendarData);

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(month==11){
                    year++;
                    month=0;
                }else{
                    month++;
                }
                initTitle(year,month);
                firstDayInMonth = getFirstDayOfMonth(year, month);
                Log.d(TAG, year + "年"+ (month+1) + "月的第一天是星期" + (firstDayInMonth-1));
                //填充数据
                List<CalendarBean> calendarData = setMonth(firstDayInMonth,year,month);
                calendarAdapter.setList(calendarData);

            }
        });






    }

    public void initTitle(int year, int month){
        month = month +1;//月份从0开始
        cardTitle.setText(""+year+"   "+month);
    }

    public static int getFirstDayOfMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);//当月第一天

        Log.d(TAG, "当前是一周的第 "+calendar.get(Calendar.DAY_OF_WEEK)+"天");//星期一是第二天
        return calendar.get(Calendar.DAY_OF_WEEK);
    }


    public List<CalendarBean> setMonth(int firstDay,int year,int month){

        int yearLast = year;
        int yearNext = year;
        int monthLast = month-1;
        int monthNext = month+1;


        if(month == 0){
            yearLast = year-1;
            monthLast = 11;
        }

        if(month==11){
            monthNext=0;
            yearNext=year+1;
        }

        List<CalendarBean> data = new ArrayList<>();//整个gridview的数据，包括三个月
//        List<CalendarBean> lastMonth = new ArrayList<>();
//        List<CalendarBean> nextMonth = new ArrayList<>();

        int needAdd = firstDay - 1;
        int lastMonthDay = monthCount(monthLast,yearLast);
        int currentMonthDay = monthCount(month,year);
        int nextMonthDay = monthCount(monthNext,yearNext);
        Log.d(TAG, "setMonth: 本月是"+(month+1)+"月"+"，有"+currentMonthDay+"天");
        Log.d(TAG, "setMonth: 上月是"+(monthLast+1)+"月"+"，有"+lastMonthDay+"天");
        Log.d(TAG, "setMonth: 下月是"+(monthNext+1)+"月"+"，有"+nextMonthDay+"天");


        //填充上月最后几天
        //firstDay = 1时，就是星期日，当前月第一天已经处于星期日，不需要添加上一个月补充天数
        if (firstDay != 1) {
            int monthFormatInt = monthLast;
            monthFormatInt++;//存的addDate的月份是从1开始的
            //5月是05，
            String monthFormat;
            if(monthFormatInt<10){
                monthFormat = "0" + monthFormatInt;
            }else{
                monthFormat = monthFormatInt + "";
            }
            Log.d(TAG, "onCreate: monthFormat: "+monthFormat);
            for (int i = needAdd-1; i >=0; i--) {
                int addlast = lastMonthDay-i;
                CalendarBean lastBean = new CalendarBean();
                lastBean.setDate(addlast);
                lastBean.setCurrentMonth(monthLast);

                String eachDay = year+"-"+monthFormat+"-"+i;
                isStudy = historyDao.queryByDate(eachDay);
                Log.d(TAG, "onCreate: eachDay: "+eachDay+" ,isStudy: "+ isStudy);

                if(isStudy){
                    lastBean.setStudy(true);
                }

                data.add(lastBean);
            }
        }

        //填充当月
        int monthFormatInt = month;//存的addDate的月份是从1开始的
        //5月是05，
        monthFormatInt++;
        String monthFormat;
        if(monthFormatInt<10){
            monthFormat = "0" + monthFormatInt;
        }else{
            monthFormat = monthFormatInt + "";
        }

        for (int i= 1; i<=currentMonthDay;i++){

            CalendarBean currentBean = new CalendarBean(0 , false, false);
            currentBean.setDate(i);
            currentBean.setCurrentMonth(month);

            if (year == mTodayCalendar.get(YEAR) && month == mTodayCalendar.get(MONTH)) {
                int dayOfMonth = mTodayCalendar.get(DAY_OF_MONTH);
                if (i == dayOfMonth){
                    currentBean.setToday(true);
                }
            }




            String eachDay = year+"-"+monthFormat+"-"+i;
            isStudy = historyDao.queryByDate(eachDay);
            Log.d(TAG, "onCreate: eachDay: "+eachDay+" ,isStudy: "+ isStudy);

            if(isStudy){
                currentBean.setStudy(true);
            }

            data.add(currentBean);
        }

        int needAddNext = 42-data.size();


        //填充下月
        for(int i = 1; i<=needAddNext;i++){
            CalendarBean nextBean = new CalendarBean();
            nextBean.setDate(i);
            nextBean.setCurrentMonth(monthNext);
            data.add(nextBean);
        }

        return data;




    }







    public static int monthCount(int month,int year) {
        // 月份按从0开始计算
        if (month < 0) {
            month = 0;
        }
        if (month > 11) {
            month = 11;
        }

        month = month + 1;
        if (day31.contains(month)) {
            return 31;
        } else if (day30.contains(month)) {
            return 30;
        }
        //闰年
        if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
            return 29;
        } else {
            return 28;
        }

    }







}