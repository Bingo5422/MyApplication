package com.example.myapplication.Adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Bean.HistoryBean;
import com.example.myapplication.R;

import java.util.List;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<HistoryBean> list;

    public void setList(List<HistoryBean> list) {
        this.list=list;
        notifyDataSetChanged();//刷新


    }

    @NonNull
    @Override
    //创建
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_history_item,parent,false);
        ViewHolder vh=new ViewHolder(v);
        return vh;
    }

    @Override
    //绑定数据
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryBean bean = list.get(position);
        holder.enName.setText(bean.getEnName());
        holder.name.setText(bean.getName());

        String path = bean.getPath();
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        holder.image.setImageBitmap(bitmap);



    }

    @Override
    //数据条数
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView enName;
        TextView name;
        ImageView image;

        public ViewHolder(View view)
        {
            super(view);
            image=view.findViewById(R.id.his_photo);
            enName=view.findViewById(R.id.his_enName);
            name=view.findViewById(R.id.his_name);

        }
    }



}
