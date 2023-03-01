package com.example.myapplication;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private Button btn_click = null;
    private String picPath;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        picPath = getFilesDir().getAbsolutePath() + File.separator + "photos";
        File temp = new File(picPath);

        if(!temp.exists()){
            temp.mkdirs();//创建文件夹
            Log.d(TAG,"创建的文件夹路径为"+temp.getAbsolutePath());
        }



        btn_click = (Button) findViewById(R.id.main_click);
        btn_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(picPath,"temp.png");
                if(!file.exists()){
                    try {
                        file.createNewFile();//创建文件
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                Toast.makeText(MainActivity.this,"Identifying",Toast.LENGTH_LONG).show();
                //Call the system camera
                Intent intent_cam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //Generate a uri based on picPath
                Uri uri;
                if (Build.VERSION.SDK_INT >= 24) {
                    uri = FileProvider.getUriForFile(MainActivity.this,"com.example.myapplication.provider", file);
                } else {
                    uri = Uri.fromFile(file);
                }
                //Set the location where the image is saved
                intent_cam.putExtra(MediaStore.EXTRA_OUTPUT,uri);
                //Launch
                startActivityForResult(intent_cam,1);




            }
        });


    }


}