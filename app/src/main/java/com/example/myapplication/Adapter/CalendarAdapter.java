package com.example.myapplication.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Bean.CalendarBean;
import com.example.myapplication.Bean.HistoryBean;
import com.example.myapplication.Bean.RecordBean;
import com.example.myapplication.Dao.HistoryDao;
import com.example.myapplication.R;

import java.util.Calendar;
import java.util.List;


public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {
    private Calendar mTodayCalendar = Calendar.getInstance();//current time, not modified

    private List<CalendarBean> mlist;

    public void setList(List<CalendarBean> list) {
        this.mlist = list;
        notifyDataSetChanged();
    }



    @NonNull
    @Override
    //Create
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_calendar_item,parent,false);
        ViewHolder vh=new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CalendarBean bean = mlist.get(position);
        boolean today = bean.isToday();
        boolean study = bean.isStudy();


        holder.days.setText(bean.getDate()+"");

        if(bean.getCurrentMonth()==mTodayCalendar.get(Calendar.MONTH)){
            holder.days.setTextColor(Color.BLACK);
        }else{
            holder.days.setTextColor(Color.GRAY);
        }



        if (today) {
            holder.heart.setVisibility(View.VISIBLE);
        }else {
            holder.heart.setVisibility(View.GONE);
        }


        if (study & !today) {
            holder.star.setVisibility(View.VISIBLE);
        }else {
            holder.star.setVisibility(View.GONE);
        }






    }

    @Override
    //
    public int getItemCount() {
        return mlist==null?0:mlist.size();

    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView days;
        ImageView heart;
        ImageView star;
        public ViewHolder(View view)
        {
            super(view);
            days = view.findViewById(R.id.date);
            heart = view.findViewById(R.id.heart);
            star = view.findViewById(R.id.star);
        }
    }


}
