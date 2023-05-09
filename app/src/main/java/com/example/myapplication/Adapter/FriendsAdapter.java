//package com.example.myapplication.Adapter;
//
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.myapplication.Bean.FriendsBean;
//import com.example.myapplication.Bean.HistoryBean;
//import com.example.myapplication.Dao.FriendsDao;
//import com.example.myapplication.R;
//import com.example.myapplication.ui.notifications.ChatActivity;
//import java.util.List;
//
//public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {
//
//
//    private List<FriendsBean> list;
//    private  OnItemClickListener mOnItemClickListener ;
//    private FriendsDao friendsDao;
//
////    public FriendsAdapter(List<FriendsBean> friendList, OnItemClickListener onItemClickListener) {
////        list = friendList;
////        mOnItemClickListener = onItemClickListener;
////    }
//
//    public void setList(List<FriendsBean> list) {
//        this.list = list;
//        notifyDataSetChanged();//刷新
//    }
//    public void setDao(FriendsDao friendsDao) {
//        this.friendsDao = friendsDao;
//    }
//
//    public interface OnItemClickListener {
//        void onItemClick(FriendsBean friend);
//    }
//
//
//    @NonNull
//    @Override
//    //创建
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_friends_item, parent, false);
//        ViewHolder vh = new ViewHolder(v);
//        return vh;
//    }
//
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//
//        FriendsBean bean = list.get(position);
//        holder.id.setText(bean.getId()+"");
//        holder.name.setText(bean.getName());
//
////        String path = bean.getPath();
////        Bitmap bitmap = BitmapFactory.decodeFile(path);
//        //holder.image.setImageBitmap(bitmap);
////        holder.itemView.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                // 点击列表项时，跳转到聊天界面，并传递选中的好友信息
////                Intent intent = new Intent(this.context, ChatActivity.class);
////                intent.putExtra("friend_name", friend);
////                context.startActivity(intent);
////            }
////        });
//
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(v.getContext(), ChatActivity.class);
//                intent.putExtra("friend_name",bean.getName());
//                intent.putExtra("friend_id", bean.getEmail());
//                v.getContext().startActivity(intent);
//            }
//        });
//
//
//    }
//
//    public int getItemCount() {
//        return list == null ? 0 : list.size();
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//        TextView id;
//        TextView name;
//        ImageView image;
//
//        private FriendsBean mFriend;
//
//        public ViewHolder(View view) {
//            super(view);
//            image = view.findViewById(R.id.fri_photo);
//            name = view.findViewById(R.id.fri_name);
//            id = view.findViewById(R.id.fri_id);
//
//        }
//
//        void bind(FriendsBean friend) {
//            mFriend = friend;
//            id.setText(friend.getId());
//            name.setText(friend.getName());
//        }
//
//        @Override
//        public void onClick(View v) {
//            if (mOnItemClickListener != null) {
//                mOnItemClickListener.onItemClick(mFriend);
//            }
//        }
//    }
//
//
//}
package com.example.myapplication.Adapter;

import android.content.Intent;
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
import com.example.myapplication.Dao.FriendsDao;
import com.example.myapplication.R;
import com.example.myapplication.ui.notifications.ChatActivity;
import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {


    private List<FriendsBean> list;
    private  OnItemClickListener mOnItemClickListener ;
    private FriendsDao friendsDao;

//    public FriendsAdapter(List<FriendsBean> friendList, OnItemClickListener onItemClickListener) {
//        list = friendList;
//        mOnItemClickListener = onItemClickListener;
//    }

    public void setList(List<FriendsBean> list) {
        this.list = list;
        notifyDataSetChanged();//刷新
    }
    public void setDao(FriendsDao friendsDao) {
        this.friendsDao = friendsDao;
    }

    public interface OnItemClickListener {
        void onItemClick(FriendsBean friend);
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
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 点击列表项时，跳转到聊天界面，并传递选中的好友信息
//                Intent intent = new Intent(this.context, ChatActivity.class);
//                intent.putExtra("friend_name", friend);
//                context.startActivity(intent);
//            }
//        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ChatActivity.class);
                intent.putExtra("friend_name",bean.getName());
                intent.putExtra("friend_id", bean.getEmail());
                v.getContext().startActivity(intent);
            }
        });


    }

    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView id;
        TextView name;
        ImageView image;

        private FriendsBean mFriend;

        public ViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.fri_photo);
            name = view.findViewById(R.id.fri_name);
            id = view.findViewById(R.id.fri_id);

        }

        void bind(FriendsBean friend) {
            mFriend = friend;
            id.setText(friend.getId());
            name.setText(friend.getName());
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(mFriend);
            }
        }
    }


}