package com.example.myapplication.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Bean.CheckBean;
import com.example.myapplication.R;

import java.util.List;

/**
 *  用来浏览云端上传记录的Adapter
 */
public class CheckAdapter extends RecyclerView.Adapter<CheckAdapter.ViewHolder>{
    private Context mContext;
    private List<CheckBean> mData;
    private CheckItemListener mCheckListener;
    private SharedPreferences sp;



    public CheckAdapter(Context mContext, List<CheckBean> mData, CheckItemListener mCheckListener){
        this.mContext = mContext;
        this.mData = mData;
        this.mCheckListener = mCheckListener;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.server_hist_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder( final ViewHolder holder, final int position) {
        final CheckBean bean = mData.get(position);
        holder.item_server_photo.setImageBitmap(bean.getPic());
//        holder.item_filename_tv.setText(bean.getFilename());
        sp = holder.itemView.getContext().getSharedPreferences("sp", Context.MODE_PRIVATE);
        String lan = sp.getString("lan", "Chinese");
        if (lan.equals("Spanish")) {
            holder.item_enName_tv.setText(bean.getSpaName());
        } else if (lan.equals("Japanese")) {
            holder.item_enName_tv.setText(bean.getJpName());
        } else if (lan.equals("Korean")) {
            holder.item_enName_tv.setText(bean.getKorName());
        } else if (lan.equals("French")) {
            holder.item_enName_tv.setText(bean.getFraName());
        } else {
            holder.item_enName_tv.setText(bean.getEnName());
        }
//        holder.item_enName_tv.setText(bean.getEnName());
        holder.item_datetime_tv.setText(bean.getDatetime());
        holder.item_cb.setChecked(bean.isChecked());
        holder.item_cb.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                bean.setChecked(!bean.isChecked());
                holder.item_cb.setChecked(bean.isChecked());
                if(null!=mCheckListener){
                    mCheckListener.itemChecked(bean, holder.item_cb.isChecked());
                }
                notifyDataSetChanged();
            }
        });
    }



    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView item_enName_tv, item_datetime_tv, item_filename_tv;
        ImageView item_server_photo;
        private CheckBox item_cb;
        private LinearLayout item_content_ll;
        public ViewHolder(View itemView){
            super(itemView);
            item_enName_tv = itemView.findViewById(R.id.item_enName_tv);
            item_datetime_tv = itemView.findViewById(R.id.item_datetime_tv);
//            item_filename_tv = itemView.findViewById(R.id.item_filename_tv);
            item_cb = itemView.findViewById(R.id.item_cb);
            item_content_ll = itemView.findViewById(R.id.item_content_ll);
            item_server_photo = itemView.findViewById(R.id.item_server_photo);

        }
    }

    public interface CheckItemListener{
        void itemChecked(CheckBean checkBean, boolean isChecked);
    }
}
