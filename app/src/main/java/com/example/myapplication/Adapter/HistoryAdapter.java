package com.example.myapplication.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Bean.HistoryBean;
import com.example.myapplication.Dao.HistoryDao;
import com.example.myapplication.R;

import java.util.List;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<HistoryBean> list;
   private HistoryDao historyDao;

    private SharedPreferences sp;
    public interface Listener{
        void onClickListener(HistoryBean bean);
        void onDelClickListener(HistoryBean bean,int index);
    }
    private Listener listener;

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setList(List<HistoryBean> list) {
        this.list=list;
    }
    public void setDao(HistoryDao historyDao) {
        this.historyDao = historyDao;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    @NonNull
    @Override
    //Create
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_history_item,parent,false);
        ViewHolder vh=new ViewHolder(v);
        return vh;
    }

    @Override
    //binding data
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        HistoryBean bean = list.get(position);


        holder.enName.setText(bean.getEnName());

        sp = holder.itemView.getContext().getSharedPreferences("sp", Context.MODE_PRIVATE);
        String lan = sp.getString("lan", "Chinese");
        if (lan.equals("Spanish")) {
            holder.name.setText(bean.getSpaName());
        } else if (lan.equals("Japanese")) {
            holder.name.setText(bean.getJpName());
        } else if (lan.equals("Korean")) {
            holder.name.setText(bean.getKorName());
        } else if (lan.equals("French")) {
            holder.name.setText(bean.getFraName());
        } else {
            holder.name.setText(bean.getName());
        }
        //
        String path = bean.getPath();
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        holder.image.setImageBitmap(bitmap);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.onClickListener(bean);
                }
            }
        });



//        //左滑删除事件
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onDelClickListener(bean, position);
                }
            }
        });

        if (bean.getIf_star() == 0) {
            holder.star.setImageResource(android.R.drawable.btn_star_big_off);
        } else {
            holder.star.setImageResource(android.R.drawable.btn_star_big_on);
        }

        holder.star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bean.getIf_star() == 0) {
                    historyDao.updateStar(bean.getId(), 1);
                    bean.setIf_star(1);
                } else {
                    historyDao.updateStar(bean.getId(), 0);
                    bean.setIf_star(0);
                }
                notifyDataSetChanged();

            }
        });


    }



    @Override
    //Number of data
    public int getItemCount() {
        return list==null?0:list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView enName;
        TextView name;
        TextView delete;
        ImageView image;
        ImageView star;
        public ViewHolder(View view)
        {
            super(view);
            image=view.findViewById(R.id.his_photo);
            enName=view.findViewById(R.id.his_enName);
            name=view.findViewById(R.id.his_name);
            delete=view.findViewById(R.id.tv_del);
            star=view.findViewById(R.id.his_star);


        }
    }


}
