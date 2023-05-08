package com.example.myapplication.ui.me;


import static com.example.myapplication.MainActivity.DomainURL;
import static com.example.myapplication.ui.me.MeFragment.client;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.Utils.CookieJarImpl;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EditInfoActivity extends AppCompatActivity {

    private static final int WRITE_SDCARD_PERMISSION_REQUEST_CODE = 1;
    private static final int CHOICE_FROM_ALBUM_REQUEST_CODE = 4;
    private static final int CROP_PHOTO_REQUEST_CODE = 5;

    private Uri photoUri = null;
    private Uri photoOutputUri = null;

    private LinearLayout ll_edit_photo, ll_edit_name;
    private ImageView iv_edit_user_photo;
    private TextView tv_edit_nickname;
//    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);

        ll_edit_photo = findViewById(R.id.ll_edit_photo);
        ll_edit_name = findViewById(R.id.ll_edit_name);
        iv_edit_user_photo = findViewById(R.id.iv_edit_user_photo);
        tv_edit_nickname = findViewById(R.id.tv_edit_nickname);


        CookieJarImpl cookieJar = new CookieJarImpl(EditInfoActivity.this);
        client.newBuilder().cookieJar(cookieJar)
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS).build();
//        client = new OkHttpClient.Builder()
//                .connectTimeout(10, TimeUnit.SECONDS)
//                .writeTimeout(5, TimeUnit.SECONDS)
//                .readTimeout(5, TimeUnit.SECONDS)
//                .cookieJar(cookieJar).build();//创建OkHttpClient对象。

        setInfo();  //设置初始值

        ll_edit_photo.setOnClickListener(clickListener);

        ll_edit_name.setOnClickListener(clickListener);




        /**
         * 先判断用户以前有没有对我们的应用程序允许过读写内存卡内容的权限，
         * 用户处理的结果在 onRequestPermissionResult 中进行处理
         */
//        if(ContextCompat.checkSelfPermission(EditInfoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            // 申请读写内存卡内容的权限
//            ActivityCompat.requestPermissions(EditInfoActivity.this,
//                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_SDCARD_PERMISSION_REQUEST_CODE);
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] per = {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE};
            for (int i = 0; i < per.length; i++) {
                int check = ActivityCompat.checkSelfPermission(EditInfoActivity.this, per[i]);
                if (check == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(
                            this,
                            new String[]{per[i]}, WRITE_SDCARD_PERMISSION_REQUEST_CODE);
                }
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(EditInfoActivity.this, MainActivity.class);
//        intent.putExtra("flag", 3);
        startActivity(intent);

    }

    private View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if (view == ll_edit_photo) {
                choiceFromAlbum();
            } else if (view == ll_edit_name) {
                startActivity(new Intent(EditInfoActivity.this, EditNameActivity.class));
            }
        }
    };

    private void choiceFromAlbum() {
        Intent choiceFromAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
        choiceFromAlbumIntent.setType("image/*");
        startActivityForResult(choiceFromAlbumIntent, CHOICE_FROM_ALBUM_REQUEST_CODE);
    }

    private void cropPhoto(Uri inputUri) {
        Intent cropPhotoIntent = new Intent("com.android.camera.action.CROP");
        cropPhotoIntent.setDataAndType(inputUri, "image/*");
        cropPhotoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cropPhotoIntent.putExtra("crop", true);
        cropPhotoIntent.putExtra("aspectX", 1);
        cropPhotoIntent.putExtra("aspectY", 1);
        cropPhotoIntent.putExtra("outputX", 300);
        cropPhotoIntent.putExtra("outputY", 300);

//        String outputdir = getFilesDir().getAbsolutePath();
        // 设置图片的最终输出目录
//        cropPhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT,
//                photoOutputUri = Uri.parse("file://" + outputdir + "/photo.jpg"));
        cropPhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                photoOutputUri = Uri.parse("file:///sdcard/photo.jpg"));
        startActivityForResult(cropPhotoIntent, CROP_PHOTO_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case WRITE_SDCARD_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this, "读写内存卡内容权限被拒绝", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // 通过返回码判断是哪个应用返回的数据
            switch (requestCode) {
                // 相册选择
                case CHOICE_FROM_ALBUM_REQUEST_CODE:
                    cropPhoto(data.getData());
                    break;
                // 裁剪图片
                case CROP_PHOTO_REQUEST_CODE:
                    Upload_Photo_Request(client, EditInfoActivity.this);
//                    setInfo();

//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Upload_Photo_Request(client, EditInfoActivity.this);
////                            Get_Photo_Request(client);
//                        }
//                    });



//                        if (file.exists()) {
//                            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
//                            iv_edit_test.setImageBitmap(bitmap);
//                            file.delete(); // 选取完后删除照片
//                        } else {
//                            Toast.makeText(this, "找不到照片", Toast.LENGTH_SHORT).show();
//                        }
                    break;
            }
        }
    }

    private void setInfo(){
        SharedPreferences preferences = this.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        // 从本地调取基本用户信息先进行显示
        String user = preferences.getString("nickname", "new user");
        tv_edit_nickname.setText(user);
        String photo_path = preferences.getString("photo", "");
        if(photo_path!=""){
            Bitmap bitmap = BitmapFactory.decodeFile(photo_path);
            iv_edit_user_photo.setImageBitmap(bitmap);
        }

    }

    private void Upload_Photo_Request(OkHttpClient client, Context context) {
        File file = new File(photoOutputUri.getPath());
        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());

        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        builder.addFormDataPart("files", "photo.jpg",
                (RequestBody.create(MediaType.parse("image/*jpg"), file)));


        Request request = new Request.Builder()//创建Request 对象。
                .url(DomainURL + "/info/set_photo")
                .post(builder.build())//传递请求体
                .build();

        List<Cookie> cookie = client.cookieJar().loadForRequest(request.url());
        if (!cookie.isEmpty()) {
            request.newBuilder().addHeader(cookie.get(0).name(), cookie.get(0).value());

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    JSONObject res_json = null;
                    try {
                        res_json = new JSONObject(response.body().string());
                        if (res_json.getBoolean("if_success")) {
                            //更新sharedpreference中的信息
                            SharedPreferences preferences = context.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("photo", file.getAbsolutePath()); //存储返回的用户名
                            editor.commit();

                            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                            iv_edit_user_photo.post(new Runnable() {
                                @Override
                                public void run() {
                                    iv_edit_user_photo.setImageBitmap(bitmap);
                                }
                            });

                            if (Looper.myLooper() == null)
                                Looper.prepare();
                            Toast.makeText(context, "Upload photo successfully.", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    }


    private void Get_Photo_Request(OkHttpClient client) {
        Request request = new Request.Builder()//创建Request 对象。
                .url(DomainURL + "/info/get_photo")
                .build();

        List<Cookie> cookie = client.cookieJar().loadForRequest(request.url());
        if (!cookie.isEmpty()) {
            request.newBuilder().addHeader(cookie.get(0).name(), cookie.get(0).value());
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    byte[] byteArr = response.body().bytes();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(byteArr, 0, byteArr.length);
                    iv_edit_user_photo.post(new Runnable() {
                        @Override
                        public void run() {
                            iv_edit_user_photo.setImageBitmap(bitmap);
                        }
                    });
                }
            });

        }
    }
}

