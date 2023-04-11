package com.example.myapplication.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import com.example.myapplication.Bean.FriendsBean;
import com.example.myapplication.Dao.FriendsDao;
import com.example.myapplication.Dao.RecDataBase;
import com.example.myapplication.databinding.FragmentNotificationsBinding;
import com.example.myapplication.ui.recognition.HistoryActivity;

import java.util.Random;

public class NotificationsFragment extends Fragment {

private FragmentNotificationsBinding binding;
private FriendsBean friendsBean;
private RecDataBase recDataBase;
private FriendsDao friendsDao;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(getContext(), FriendsListActivity.class);
        startActivity(intent);

    }


    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

    binding = FragmentNotificationsBinding.inflate(inflater, container, false);
    View root = binding.getRoot();



       // final TextView textView = binding.textNotifications;
        //notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;


    }

@Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}