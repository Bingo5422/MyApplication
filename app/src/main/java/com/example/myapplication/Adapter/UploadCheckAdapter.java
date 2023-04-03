package com.example.myapplication.Adapter;

import android.content.Context;
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
 * 用来浏览本地记录，然后选择进行上传的Adapter
 */
public class UploadCheckAdapter extends RecyclerView.Adapter<UploadCheckAdapter.ViewHolder>{

    private Context mContext;
    private List<CheckBean> mData;
    private UploadCheckAdapter.CheckItemListener mCheckListener;

    public UploadCheckAdapter(Context mContext, List<CheckBean> mData,
                              UploadCheckAdapter.CheckItemListener mCheckListener){
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final CheckBean bean = mData.get(position);
        holder.item_photo_ul.setImageBitmap(bean.getPic());
        holder.item_filename_tv_ul.setText(bean.getFilename());
        holder.item_enName_tv_ul.setText(bean.getEnName());
        holder.item_datetime_tv_ul.setText(bean.getDatetime());
        holder.item_cb_ul.setChecked(bean.isChecked());
        holder.item_cb_ul.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                bean.setChecked(!bean.isChecked());
                holder.item_cb_ul.setChecked(bean.isChecked());
                if(null!=mCheckListener){
                    mCheckListener.itemChecked(bean, holder.item_cb_ul.isChecked());
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView item_enName_tv_ul, item_datetime_tv_ul, item_filename_tv_ul;
        ImageView item_photo_ul;
        private CheckBox item_cb_ul;
        private LinearLayout item_content_ll_ul;
        public ViewHolder(View itemView){
            super(itemView);
            item_enName_tv_ul = itemView.findViewById(R.id.item_enName_tv_ul);
            item_datetime_tv_ul = itemView.findViewById(R.id.item_datetime_tv_ul);
            item_filename_tv_ul = itemView.findViewById(R.id.item_filename_tv_ul);
            item_cb_ul = itemView.findViewById(R.id.item_cb_ul);
            item_content_ll_ul = itemView.findViewById(R.id.item_content_ll_ul);
            item_photo_ul = itemView.findViewById(R.id.item_photo_ul);
        }
    }

    public interface CheckItemListener{
        void itemChecked(CheckBean checkBean, boolean isChecked);
    }
}
