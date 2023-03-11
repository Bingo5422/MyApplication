package com.example.myapplication.ui.home;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.Utils.RecognitionUtil;

import java.io.File;
import java.io.IOException;

public class PhotoRecActivity extends AppCompatActivity {

    private ImageView recPhoto;
    private Bitmap bitmap;
    private String photoPath;
    private TextView recStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_rec);
        recPhoto=findViewById(R.id.rec_photo);
        Intent intent = getIntent();
        photoPath = intent.getStringExtra("path");//intent传来的照片路径
        bitmap = BitmapFactory.decodeFile(photoPath);
        recPhoto.setImageBitmap(bitmap);

        recStart=findViewById(R.id.rec_start);
        recStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PhotoRecActivity.this,"identifying",Toast.LENGTH_LONG).show();
                RecognitionUtil.startRecognition(photoPath);
            }
        });


    }


}