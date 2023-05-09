package com.example.myapplication.Adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.Bean.MessageBean;
import com.example.myapplication.R;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MessageBean> mMessageList;

    public MessageAdapter(List<MessageBean> messageList, Context context) {
        preferences = context.getSharedPreferences("USER_INFO", MODE_PRIVATE);
        userId =  preferences.getString("user_id", "");
        mMessageList = messageList;
    }

    private Context context ;


    SharedPreferences preferences;
    String userId;
    // 判断是接收的消息还是发送的消息
    @Override
    public int getItemViewType(int position) {
        MessageBean message = mMessageList.get(position);
        if(message.getToUser().equals(userId)) {
            return 1;
        }
        else if (message.getFromUser().equals(userId)) {
            return 2;
        }
        return -1;
    }

    // 创建 ViewHolder
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_receive, parent, false);
            return new ReceiveViewHolder(view);
        } else if (viewType == 2){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_send, parent, false);
            return new SendViewHolder(view);
        }
        return null;
    }

    // 将数据绑定到 ViewHolder 中
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MessageBean message = mMessageList.get(position);
        if (holder instanceof ReceiveViewHolder) {
            ((ReceiveViewHolder) holder).messageTextView.setText(message.getContent());
            ((ReceiveViewHolder) holder).timeTextView.setText(String.valueOf(message.getSendTime()));
        } else {
            ((SendViewHolder) holder).messageTextView.setText(message.getContent());
            ((SendViewHolder) holder).timeTextView.setText(String.valueOf(message.getSendTime()));

        }
    }

    // 获取聊天记录的数量
    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

//    // 格式化日期
//    private String formatDate(Date date) {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
//        return sdf.format(date);
//    }

    // 接收的消息的 ViewHolder
    private static class ReceiveViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView timeTextView;

        ReceiveViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.message_text_view);
            timeTextView = itemView.findViewById(R.id.time_text_view);
        }
    }

    // 发送的消息的 ViewHolder
    private static class SendViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView timeTextView;

        SendViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.message_text_view);
            timeTextView = itemView.findViewById(R.id.time_text_view);
        }
    }
}
