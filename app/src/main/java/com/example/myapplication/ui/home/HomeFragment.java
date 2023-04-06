package com.example.myapplication.ui.home;

import android.content.Intent;
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
import com.example.myapplication.databinding.FragmentHomeBinding;
import com.example.myapplication.ui.recognition.HistoryActivity;
import com.example.myapplication.ui.recognition.PhotoRecActivity;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class HomeFragment extends Fragment{

    private FragmentHomeBinding binding;
    private ImageView main_camera = null;
    private ImageView main_history = null;
    private String picPath;
    private File file;
    private static final String TAG = "HomeFragment";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        picPath = getContext().getFilesDir().getAbsolutePath() + File.separator + "photos";
        File temp = new File(picPath);

        //创建文件夹
        if (!temp.exists()) {
            temp.mkdirs();
            Log.d(TAG, "onCreate: 文件夹创建了 路径为 = " + temp.getAbsolutePath());
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

        main_camera.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {


                File file = getFileName();

                Log.d(TAG, "!!!!!!!!" + file.getAbsolutePath());
                //Call the system camera
                Intent intent_cam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //Generate a uri based on picPath
                Uri uri;
                if (Build.VERSION.SDK_INT >= 24) {
                    uri = FileProvider.getUriForFile(getContext(),"com.example.myapplication.provider", file);
                } else {
                    uri = Uri.fromFile(file);
                }
                //Set the location where the image is saved
                intent_cam.putExtra(MediaStore.EXTRA_OUTPUT,uri);
                //Launch
                startActivityForResult(intent_cam,1);

            }
        });

        //查看历史记录的按钮监听
        main_history=binding.recHistory;
        main_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), HistoryActivity.class);
                startActivity(intent);
                Log.d(TAG,"跳到历史界面");
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //判断是否为当前请求
        if (requestCode == 1) {

            PhotoUtil.compressPhoto(file, 4);
            String absolutePath = file.getAbsolutePath();
            Intent intent = new Intent(getContext(), PhotoRecActivity.class);
            intent.putExtra("path", absolutePath);
            startActivity(intent);

        }
    }

    public File getFileName(){
        String name = UUID.randomUUID().toString();

        file = new File(picPath, name+".jpg");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Log.d(TAG, "fileName: 随机文件创建了，路径为 = " + file.getAbsolutePath());
        return file;
    }
}