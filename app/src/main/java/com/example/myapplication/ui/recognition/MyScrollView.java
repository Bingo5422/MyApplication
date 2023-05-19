package com.example.myapplication.ui.recognition;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.HorizontalScrollView;

import androidx.annotation.RequiresApi;

import com.example.myapplication.R;


public class MyScrollView extends HorizontalScrollView {

    int btn_width;
    Button btn;

    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setOverScrollMode(OVER_SCROLL_NEVER);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(changed) {

            btn = findViewById(R.id.btnD);
            btn_width = btn.getWidth();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                //Determine whether to display the delete button according to the sliding distance
                changeScrollx();
                return false;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    public void changeScrollx(){
        //The distance of the touch swipe is greater than half the width of the delete button
        if(getScrollX() >= (btn_width/2)){
            //Show delete button
            this.smoothScrollTo(btn_width, 0);
        }else{
            //hide delete button
            this.smoothScrollTo(0, 0);
        }
    }
}
