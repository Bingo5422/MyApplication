package com.example.myapplication.Adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Bean.CalendarBean;
import com.example.myapplication.Bean.HistoryBean;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class ChallengeAdapter extends RecyclerView.Adapter<ChallengeAdapter.ViewHolder> {
    private Calendar mTodayCalendar = Calendar.getInstance();//当前时间,未修改

    private List<HistoryBean> mlist;
    private List<String> selectedPhotoPath = new ArrayList<>();
    private static final String TAG = "ChallengeAdapter";

    public void setList(List<HistoryBean> list) {
        this.mlist = list;
        notifyDataSetChanged();
    }



    @NonNull
    @Override
    //创建
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_challenge_item,parent,false);
        ViewHolder vh=new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryBean bean = mlist.get(position);

        String path = bean.getPath();
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        holder.photo.setImageBitmap(bitmap);


        String fileName = bean.getFileName();
        Log.d(TAG, "bean.getFileName()"+fileName);


        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // CheckBox 被选中
                    selectedPhotoPath.add(fileName);
                    Log.d(TAG, "选中的照片名字"+selectedPhotoPath);
                } else {
                    // CheckBox 没有被选中

                    selectedPhotoPath.remove(fileName);
                    Log.d(TAG, "取消后选中的照片名字"+selectedPhotoPath.toString());
                }
            }
        });









    }

    public List<String> getSelectedPhotoNameList() {
        return selectedPhotoPath;
    }


    @Override
    //数据条数
    public int getItemCount() {
        return mlist==null?0:mlist.size();

    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView photo;
        CheckBox checkBox;
        public ViewHolder(View view)
        {
            super(view);

            photo = view.findViewById(R.id.image_view);
            checkBox = view.findViewById(R.id.cb);
        }
    }


}
