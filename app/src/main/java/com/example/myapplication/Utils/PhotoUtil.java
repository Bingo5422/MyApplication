package com.example.myapplication.Utils;

import static com.example.myapplication.MainActivity.getContext;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.text.TextUtils;

import com.example.myapplication.MainActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;




public class PhotoUtil {



    //获取图片角度
    public static int imgDegree(String path) {
        if (TextUtils.isEmpty(path)) {
            return 0;
        }
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (Exception e) {
            //相应异常处理
        }
        return degree;
    }

    //图片旋转
    public static Bitmap rotateImage(Bitmap bitmap, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return bmp;
    }

    //图片压缩
    public static File compressPhoto(File file, int scale) {
        //String photo_path = MainActivity.getContext().getFilesDir() + File.separator + "photos" + File.separator + "temp.jpg";
        File targetFile = new File(file.getAbsolutePath());
        try {
            if (!targetFile.exists()) {
                if (!targetFile.getParentFile().exists()){
                    targetFile.getParentFile().mkdirs();
                }

                targetFile.createNewFile();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int degree = imgDegree(file.getAbsolutePath());
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

        bitmap = rotateImage(bitmap, degree);
        Bitmap resultBitmap = Bitmap.createBitmap(bitmap.getWidth() / scale, bitmap.getHeight() / scale, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(resultBitmap);
        RectF rectF = new RectF(0, 0, resultBitmap.getWidth(), resultBitmap.getHeight());
        canvas.drawBitmap(bitmap, null, rectF, null);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        resultBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

        try {
            FileOutputStream fos = new FileOutputStream(targetFile);
            fos.write(bos.toByteArray());//覆盖
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return targetFile;
    }
}
