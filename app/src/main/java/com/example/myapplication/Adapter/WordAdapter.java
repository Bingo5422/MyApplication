package com.example.myapplication.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Bean.HistoryBean;
import com.example.myapplication.Dao.HistoryDao;
import com.example.myapplication.R;
import com.example.myapplication.ui.dashboard.WordDetailActivity;

import java.util.ArrayList;
import java.util.List;


public class WordAdapter extends RecyclerView.Adapter<WordAdapter.ViewHolder> {

    private List<HistoryBean> list = new ArrayList<>();
    HistoryDao historyDao;

    public void setDao(HistoryDao historyDao) {
        this.historyDao = historyDao;
    }

    public void setList(List<HistoryBean> list) {
        this.list.clear();
        this.list.addAll(list);
    }

    @NonNull
    @Override
    //创建
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_word_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    //绑定数据
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryBean bean = list.get(position);
        holder.enName.setText(bean.getEnName());
        holder.name.setText(bean.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), WordDetailActivity.class);
                intent.putExtra("word",bean);
                v.getContext().startActivity(intent);
            }
        });

        if (bean.getIf_star() == 0) {
            holder.ivStar.setImageResource(android.R.drawable.btn_star_big_off);
        } else {
            holder.ivStar.setImageResource(android.R.drawable.btn_star_big_on);
        }

        holder.ivStar.setOnClickListener(new View.OnClickListener() {
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
    //数据条数
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView enName;
        TextView name;
        ImageView ivStar;

        public ViewHolder(View view) {
            super(view);
            enName = view.findViewById(R.id.tvEnName);
            name = view.findViewById(R.id.tvName);
            ivStar = view.findViewById(R.id.iv);
        }
    }


}
