package com.example.myapplication.ui.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.Utils.PhotoUtil;
import com.example.myapplication.Utils.translate.TransApi;
import com.example.myapplication.databinding.FragmentHomeBinding;
import com.example.myapplication.ui.recognition.ChoiceActivity;
import com.example.myapplication.ui.recognition.HistoryActivity;
import com.example.myapplication.ui.recognition.PhotoRecActivity;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ImageView main_camera = null;
    private ImageView main_history = null;
    private String picPath;
    private File file;
    private int checkedItem = 0;
    private static final String TAG = "HomeFragment";
    private SharedPreferences sp;
    String[] stringArray;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getActivity().getSharedPreferences("sp", Context.MODE_PRIVATE);
        String lan = sp.getString("lan", "");
        stringArray = getResources().getStringArray(R.array.lan);
        for (int i = 0; i < stringArray.length; i++) {
            if (lan.equals(stringArray[i])) {
                checkedItem = i;
            }
        }
//        file = new File(picPath, "temp.jpg");
//        //创建文件
//        if (!file.exists()) {
//            try {
//                file.createNewFile();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        HomeViewModel homeViewModel =
//                new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        main_camera = binding.mainCamera;
        binding.menuSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext())
                        .setSingleChoiceItems(R.array.lan, checkedItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                checkedItem = which;
                                sp.edit().putString("lan", stringArray[checkedItem]).commit();
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
        main_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent_choice = new Intent(getContext(), ChoiceActivity.class);
                startActivity(intent_choice);


//                File file = getFileName();
//
//                Log.d(TAG, "!!!!!!!!" + file.getAbsolutePath());
//                //Call the system camera
//                Intent intent_cam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                //Generate a uri based on picPath
//                Uri uri;
//                if (Build.VERSION.SDK_INT >= 24) {
//                    uri = FileProvider.getUriForFile(getContext(),"com.example.myapplication.provider", file);
//                } else {
//                    uri = Uri.fromFile(file);
//                }
//                //Set the location where the image is saved
//                intent_cam.putExtra(MediaStore.EXTRA_OUTPUT,uri);
//                //Launch
//                startActivityForResult(intent_cam,1);

            }
        });

        //查看历史记录的按钮监听
        main_history = binding.recHistory;
        main_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), HistoryActivity.class);
                startActivity(intent);
                Log.d(TAG, "跳到历史界面");
            }
        });


//        final TextView textView = binding.textHome;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }
    //这上面是初始化fragment自带的代码


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}