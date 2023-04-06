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

import com.example.myapplication.Bean.FriendsBean;
import com.example.myapplication.Bean.HistoryBean;
import com.example.myapplication.R;

import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {


    private List<FriendsBean> list;

    public void setList(List<FriendsBean> list) {
        this.list = list;
        notifyDataSetChanged();//刷新
    }

    @NonNull
    @Override
    //创建
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_friends_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        FriendsBean bean = list.get(position);
        holder.id.setText(bean.getId()+"");
        holder.name.setText(bean.getName());

//        String path = bean.getPath();
//        Bitmap bitmap = BitmapFactory.decodeFile(path);
        //holder.image.setImageBitmap(bitmap);


    }

    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView id;
        TextView name;
        ImageView image;

        public ViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.fri_photo);
            name = view.findViewById(R.id.fri_name);
            id = view.findViewById(R.id.fri_id);

        }
    }


}