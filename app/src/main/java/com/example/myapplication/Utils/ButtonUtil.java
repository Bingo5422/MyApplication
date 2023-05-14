package com.example.myapplication.Utils;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.example.myapplication.ui.me.ServerUploadActivity;

public class ButtonUtil {
    private static long lastClickTime = 0;
    private static int lastButtonId;

    public static boolean isFastDoubleClick(long diff, Context context, int buttonId) {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (lastButtonId == buttonId && lastClickTime > 0 && timeD < diff) {
//            Log.v("isFastDoubleClick", "短时间内按钮多次触发");
            if (Looper.myLooper()==null)
                Looper.prepare();
            Toast t = Toast.makeText(context,
                    "You have just sent the email. Please send again some time later.",Toast.LENGTH_SHORT);
            t.setGravity(Gravity.CENTER,0,0);
            t.show();
            Looper.loop();
            return true;
        }
        lastClickTime = time;
        lastButtonId = buttonId;
        return false;
    }
}
