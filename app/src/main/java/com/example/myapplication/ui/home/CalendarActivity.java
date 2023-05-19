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
    private Calendar mTodayCalendar = Calendar.getInstance();//current time, not modified


    private int month;
    private int year;

    //Use history to judge whether have learned
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
        cardBackground.setAlpha(0.6f);//background opacity


        cardTitle = findViewById(R.id.card_title);
        cardList = findViewById(R.id.card_list);



        //Grid layout 7 columns
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,7);
        cardList.setLayoutManager(gridLayoutManager);
        calendarAdapter = new CalendarAdapter();
        cardList.setAdapter(calendarAdapter);


        Calendar calendar = Calendar.getInstance();
        year = calendar.get(YEAR);//2023
        month = calendar.get(Calendar.MONTH);//4


        //year month title
        initTitle(year,month);

        //The first day of the month is the day of the week
        firstDayInMonth = getFirstDayOfMonth(year, month);
        Log.d(TAG, "year"+year + ", month"+ (month+1) + "first day is" + (firstDayInMonth-1));

        //Data input
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

        //switch month
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
                //data input
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
                //data input
                List<CalendarBean> calendarData = setMonth(firstDayInMonth,year,month);
                calendarAdapter.setList(calendarData);

            }
        });






    }

    public void initTitle(int year, int month){
        month = month +1;//month starts at 0
        cardTitle.setText(""+year+"   "+month);
    }

    public static int getFirstDayOfMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);//first day of the month

        Log.d(TAG, "It is currently the " +calendar.get(Calendar.DAY_OF_WEEK)+" day of the week");//monday is the next day
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

        List<CalendarBean> data = new ArrayList<>();//The data of the entire gridview, including three months
//        List<CalendarBean> lastMonth = new ArrayList<>();
//        List<CalendarBean> nextMonth = new ArrayList<>();

        int needAdd = firstDay - 1;
        int lastMonthDay = monthCount(monthLast,yearLast);
        int currentMonthDay = monthCount(month,year);
        int nextMonthDay = monthCount(monthNext,yearNext);
        Log.d(TAG, "setMonth: Current month: "+(month+1)+"，has "+currentMonthDay+" days");
        Log.d(TAG, "setMonth: Last month: "+(monthLast+1)+"，has "+lastMonthDay+" days");
        Log.d(TAG, "setMonth: Next month: "+(monthNext+1)+", has "+nextMonthDay+" days");


        //Fill in the last days of the previous month
        //When firstDay = 1, it is Sunday, and the first day of the current month is already on Sunday,
        // so there is no need to add the supplementary days of the previous month
        if (firstDay != 1) {
            int monthFormatInt = monthLast;
            monthFormatInt++;//The month of the saved addDate starts from 1
            //month 5 is 05
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

        //fill current month
        int monthFormatInt = month;//The month of the saved addDate starts from 1
        //month 5 is 05
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


        //fill next month
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