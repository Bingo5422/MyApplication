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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

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
    private String currentApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);
        back=findViewById(R.id.cho_back);
        camera=findViewById(R.id.camera);
        album=findViewById(R.id.album);


        currentApi = "Baidu";
        final RadioGroup radgroup = (RadioGroup) findViewById(R.id.group1);
        radgroup.setOnCheckedChangeListener(((group, checkedId) -> {
            RadioButton radioButton = (RadioButton) findViewById(checkedId);
            currentApi = (String)radioButton.getText();
            Toast.makeText(getApplicationContext(),"The current api is："+currentApi,Toast.LENGTH_SHORT).show();
        }));

        Log.d(TAG, "Current api is: " +currentApi);


        picPath = this.getFilesDir().getAbsolutePath() + File.separator + "photos";
        File temp = new File(picPath);

        //create folder
        if (!temp.exists()) {
            temp.mkdirs();
            Log.d(TAG, "onCreate: Folder created with path = " + temp.getAbsolutePath());
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
        Log.d(TAG, "fileName: A random file is created with path =  " + file.getAbsolutePath());
        return file;
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Determine whether it is the current request
        if (requestCode == 1 && resultCode == RESULT_OK) {

            PhotoUtil.compressPhoto(file, 4);
            String absolutePath = file.getAbsolutePath();
            Intent intent = new Intent(ChoiceActivity.this, PhotoRecActivity.class);
            intent.putExtra("path", absolutePath);
            intent.putExtra("api",currentApi);
            startActivity(intent);

        }

        //After selecting a photo in the album, call back to the current activity
        if(requestCode==2 && resultCode == RESULT_OK){
            try {

                Uri selectedImage = data.getData(); //Get the Uri of the photo returned by the system
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);//Query the photo corresponding to the specified Uri from the system table
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String path = cursor.getString(columnIndex);  //get photo path
                cursor.close();


               File selectFile = new File(path);
               Log.d(TAG, "压缩前的大小 == "+ selectFile.length());
               String fileName = getFileName(path);

               //memory permissions lead to
                //String tempPath = getCacheDir() + File.separator + "image" + File.separator + fileName;
                String tempPath = picPath + File.separator +fileName;
                File resultFile = PhotoUtil.compressPhoto(selectFile, 4, tempPath);
                String absolutePath = resultFile.getAbsolutePath();
                Log.d(TAG, "Compressed size == "+ resultFile.length());
                Log.d(TAG, "open in photo album"+absolutePath);
                Intent intent = new Intent(ChoiceActivity.this, PhotoRecActivity.class);
                intent.putExtra("path", absolutePath);
                intent.putExtra("api",currentApi);
                startActivity(intent);
            } catch (Exception e) {
                // TODO Auto-generatedcatch block
                e.printStackTrace();
            }
        }




    }

    public String getFileName(String path) {

        File tempFile =new File( path.trim());
        String fileName = tempFile.getName();
        return fileName;
    }
}