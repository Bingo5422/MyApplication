package com.example.myapplication.ui.recognition;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.myapplication.R;
import com.example.myapplication.Utils.PhotoUtil;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class ChoiceActivity extends AppCompatActivity {

    private ImageView back;
    private String picPath;
    private Button camera;
    private Button album;
    private File file;
    private static final String TAG = "ChoiceActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);
        back=findViewById(R.id.cho_back);
        camera=findViewById(R.id.camera);
        album=findViewById(R.id.album);

        picPath = this.getFilesDir().getAbsolutePath() + File.separator + "photos";
        File temp = new File(picPath);

        //创建文件夹
        if (!temp.exists()) {
            temp.mkdirs();
            Log.d(TAG, "onCreate: 文件夹创建了 路径为 = " + temp.getAbsolutePath());
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = getFileName();

                Log.d(TAG, "!!!!!!!!" + file.getAbsolutePath());
                //Call the system camera
                Intent intent_cam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //Generate a uri based on picPath
                Uri uri;
                if (Build.VERSION.SDK_INT >= 24) {
                    uri = FileProvider.getUriForFile(ChoiceActivity.this,"com.example.myapplication.provider", file);
                } else {
                    uri = Uri.fromFile(file);
                }
                //Set the location where the image is saved
                intent_cam.putExtra(MediaStore.EXTRA_OUTPUT,uri);
                //Launch
                startActivityForResult(intent_cam,1);
            }
        });

        album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent_album = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent_album, 2);
            }
        });




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

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //判断是否为当前请求
        if (requestCode == 1 && resultCode == RESULT_OK) {

            PhotoUtil.compressPhoto(file, 4);
            String absolutePath = file.getAbsolutePath();
            Intent intent = new Intent(ChoiceActivity.this, PhotoRecActivity.class);
            intent.putExtra("path", absolutePath);
            startActivity(intent);

        }

        //在相册里面选择好相片之后调回到现在的这个activity中
        if(requestCode==2 && resultCode == RESULT_OK){
            try {

                Uri selectedImage = data.getData(); //获取系统返回的照片的Uri
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);//从系统表中查询指定Uri对应的照片
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String path = cursor.getString(columnIndex);  //获取照片路径
                cursor.close();


                file = new File(path);
                PhotoUtil.compressPhoto(file, 4);
                String absolutePath = file.getAbsolutePath();
                Log.d(TAG, "???"+absolutePath);
                Intent intent = new Intent(ChoiceActivity.this, PhotoRecActivity.class);
                intent.putExtra("path", absolutePath);
                startActivity(intent);
            } catch (Exception e) {
                // TODO Auto-generatedcatch block
                e.printStackTrace();
            }
        }




    }
}