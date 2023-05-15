package com.example.myapplication.Adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Bean.FriendsBean;
import com.example.myapplication.Bean.MessageBean;
import com.example.myapplication.R;
import com.example.myapplication.ui.notifications.ChatActivity;
import com.example.myapplication.ui.notifications.ReceiveChallengeActivity;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MessageBean> mMessageList;


    // 创建一个布局参数对象
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
    );


    public void setList(List<MessageBean> list) {
        this.mMessageList = list;
        notifyDataSetChanged();//刷新
    }

    public MessageAdapter(List<MessageBean> messageList, Context context) {
        //知道自己是谁
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
        sortBeansByTimestamp(mMessageList);
        MessageBean message = mMessageList.get(position);
        Boolean isChallenge = message.getChallenge();

            if (holder instanceof ReceiveViewHolder) {
                if(isChallenge){
                    ((ReceiveViewHolder) holder).iv.setVisibility(View.VISIBLE);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(v.getContext(), ReceiveChallengeActivity.class);
                            intent.putExtra("group",message.getContent());
                            v.getContext().startActivity(intent);
                        }
                    });
                }else {

                    ((ReceiveViewHolder) holder).messageTextView.setText(message.getContent());
                    ((ReceiveViewHolder) holder).timeTextView.setText(String.valueOf(message.getSendTime()));
                }

            } else {
                if(isChallenge){
                    ((SendViewHolder) holder).iv.setVisibility(View.VISIBLE);
                    ((SendViewHolder) holder).messageTextView.setVisibility(View.GONE);
                    ((SendViewHolder) holder).timeTextView.setVisibility(View.GONE);

                }else{
                    ((SendViewHolder) holder).iv.setVisibility(View.GONE);
                    ((SendViewHolder) holder).messageTextView.setText(message.getContent());
                    ((SendViewHolder) holder).timeTextView.setText(String.valueOf(message.getSendTime()));
                }

            }
        }

    public void sortBeansByTimestamp(List<MessageBean> mMessageList) {
        Collections.sort(mMessageList, new Comparator<MessageBean>() {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            @Override
            public int compare(MessageBean b1, MessageBean b2) {
                try {
                    Date date1 = dateFormat.parse(b1.getSendTime());
                    Date date2 = dateFormat.parse(b2.getSendTime());
                    return date1.compareTo(date2);
                } catch (Exception e) {
                    // Handle exception
                    return 0;
                }
            }
        });
    }



    // 获取聊天记录的数量
    @Override
    public int getItemCount() {
        return mMessageList.size() ;
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

        ImageView iv;
        ReceiveViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.message_text_view);
            timeTextView = itemView.findViewById(R.id.time_text_view);
            iv =itemView.findViewById(R.id.heart);
        }
    }

    // 发送的消息的 ViewHolder
    private static class SendViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView timeTextView;
        ImageView iv;


        SendViewHolder(View itemView) {
            super(itemView);

            messageTextView = itemView.findViewById(R.id.message_text_view);
            timeTextView = itemView.findViewById(R.id.time_text_view);
            iv =itemView.findViewById(R.id.heart);
        }
    }
}
