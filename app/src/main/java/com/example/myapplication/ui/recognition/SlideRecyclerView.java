
package com.example.myapplication.ui.recognition;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

public class SlideRecyclerView extends RecyclerView {

    private static final String TAG = "SlideRecyclerView";
    private static final int INVALID_POSITION = -1; // The touched point is not within the scope of the child View
    private static final int INVALID_CHILD_WIDTH = -1;  // Child ItemView does not contain two child Views
    private static final int SNAP_VELOCITY = 600;   // minimum sliding speed

    private VelocityTracker mVelocityTracker;   // speed tracker
    private int mTouchSlop; // The minimum distance considered to be sliding (generally provided by the system)
    private Rect mTouchFrame;   // The rectangular range where the child View is located
    private Scroller mScroller;
    private float mLastX;   //Record the last touch point X during sliding
    private float mFirstX, mFirstY; // first touch range
    private boolean mIsSlide;   // Whether to slide the child View
    private ViewGroup mFlingView;   // Touched child View
    private int mPosition;  // The position of the touched view
    private int mMenuViewWidth;    // The position of the touched view

    public SlideRecyclerView(Context context) {
        this(context, null);
    }

    public SlideRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mScroller = new Scroller(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        int x = (int) e.getX();
        int y = (int) e.getY();
        obtainVelocity(e);
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {  // Immediately terminates the animation if it has not stopped
                    mScroller.abortAnimation();
                }
                mFirstX = mLastX = x;
                mFirstY = y;
                mPosition = pointToPosition(x, y);  //Get the position of the touch point
                if (mPosition != INVALID_POSITION) {
                    View view = mFlingView;
                    // Get the view where the touch point is located
                    mFlingView = (ViewGroup) getChildAt(mPosition - ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition());
                    // Here to judge if the previously touched view has been opened, and the currently touched view is not that view, close the previous view immediately.
                    //
                    // There is no need to worry about the animation not completing the conflict, because the abortAnimation has been done before
                    if (view != null && mFlingView != view && view.getScrollX() != 0) {
                        view.scrollTo(0, 0);
                    }
                    // There is a mandatory requirement here, the sub-ViewGroup of RecyclerView must have 2 sub-views, so that the menu button will have a value,
                    // It should be noted that if you do not customize the sub-View of RecyclerView, the sub-View must have a fixed width.
                    // For example, use LinearLayout as the root layout, and the width of the content part is already match_parent.
                    // At this time, if the menu view uses wrap_content, the width of the menu will be 0.
                    if (mFlingView.getChildCount() == 2) {
                        mMenuViewWidth = mFlingView.getChildAt(1).getWidth();
                    } else {
                        mMenuViewWidth = INVALID_CHILD_WIDTH;
                    }




                }
                break;
            case MotionEvent.ACTION_MOVE:
                mVelocityTracker.computeCurrentVelocity(1000);
                // There are two judgments here, and if one of them is met, it is considered a side slip:
                // 1. If the speed in the x direction is greater than the speed in the y direction, and greater than the minimum speed limit;
                // 2. If the sideslip distance in the x direction is greater than the sliding distance in the y direction, and the x direction reaches the minimum sliding distance;
                float xVelocity = mVelocityTracker.getXVelocity();
                float yVelocity = mVelocityTracker.getYVelocity();
                if (Math.abs(xVelocity) > SNAP_VELOCITY && Math.abs(xVelocity) > Math.abs(yVelocity)
                        || Math.abs(x - mFirstX) >= mTouchSlop
                        && Math.abs(x - mFirstX) > Math.abs(y - mFirstY)) {
                    mIsSlide = true;
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                releaseVelocity();
                break;
        }
        return super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (mIsSlide && mPosition != INVALID_POSITION) {
            float x = e.getX();
            obtainVelocity(e);
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:   // Because there is no interception, it will not be called
                    break;
                case MotionEvent.ACTION_MOVE:
                    //swipe with finger
                    if (mMenuViewWidth != INVALID_CHILD_WIDTH) {
                        float dx = mLastX - x;
                        if (mFlingView.getScrollX() + dx <= mMenuViewWidth
                                && mFlingView.getScrollX() + dx > 0) {
                            mFlingView.scrollBy((int) dx, 0);
                        }
                        mLastX = x;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (mMenuViewWidth != INVALID_CHILD_WIDTH) {
                        int scrollX = mFlingView.getScrollX();
                        mVelocityTracker.computeCurrentVelocity(1000);
                        // There are two reasons here to decide whether to open the menu:
                        // 1. The width of the menu being pulled out is greater than half of the width of the menu;
                        // 2. The horizontal sliding speed is greater than the minimum sliding speed;
                        // Note: The reason why it is smaller than the negative value is because the speed is negative when sliding to the left
                        if (mVelocityTracker.getXVelocity() < -SNAP_VELOCITY) {    // Swipe to the left to reach the minimum speed of sideslip, then open
                            mScroller.startScroll(scrollX, 0, mMenuViewWidth - scrollX, 0, Math.abs(mMenuViewWidth - scrollX));
                        } else if (mVelocityTracker.getXVelocity() >= SNAP_VELOCITY) {  // Slip to the right to reach the minimum speed of sideslip, then turn off
                            mScroller.startScroll(scrollX, 0, -scrollX, 0, Math.abs(scrollX));
                        } else if (scrollX >= mMenuViewWidth / 2) { // Open if more than half of the delete button
                            mScroller.startScroll(scrollX, 0, mMenuViewWidth - scrollX, 0, Math.abs(mMenuViewWidth - scrollX));
                        } else {    // Close otherwise
                            mScroller.startScroll(scrollX, 0, -scrollX, 0, Math.abs(scrollX));
                        }
                        invalidate();
                    }
                    mMenuViewWidth = INVALID_CHILD_WIDTH;
                    mIsSlide = false;
                    mPosition = INVALID_POSITION;
                    // The reason why it is called here is because if it is intercepted before, ACTION_UP will not be executed, and the tracking needs to be released here

                    releaseVelocity();
                    break;
            }
            return true;
        } else {
            // Here to prevent the RecyclerView from sliding normally, the menu is not closed
            //closeMenu();
            // Velocity, the release here is to prevent RecyclerView from being intercepted normally, but it is not released in onTouchEvent;
            // There are three situations: 1. onInterceptTouchEvent is not intercepted, in the onInterceptTouchEvent method, a pair of DOWN and UP is acquired and released;
            // 2. onInterceptTouchEvent is intercepted, and DOWN is acquired, but the event is not handled by the sideslip, and needs to be released here;
            // 3. onInterceptTouchEvent is intercepted, DOWN is acquired, and the event is handled by sliding, it is released in the UP of onTouchEvent.
            releaseVelocity();
        }
        return super.onTouchEvent(e);
    }

    private void releaseVelocity() {
        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    private void obtainVelocity(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    public int pointToPosition(int x, int y) {
        if (null == getLayoutManager()) return INVALID_POSITION;
        int firstPosition = ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
        Rect frame = mTouchFrame;
        if (frame == null) {
            mTouchFrame = new Rect();
            frame = mTouchFrame;
        }

        final int count = getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            final View child = getChildAt(i);
            if (child.getVisibility() == View.VISIBLE) {
                child.getHitRect(frame);
                if (frame.contains(x, y)) {
                    return firstPosition + i;
                }
            }
        }
        return INVALID_POSITION;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            mFlingView.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }

    /**
     * Close the subview that displays the submenu
     *
     * since the item is not customized, it is not easy to listen to the click event, so the caller needs to close it manually
     */
    public void closeMenu() {
        if (mFlingView != null && mFlingView.getScrollX() != 0) {
            mFlingView.scrollTo(0, 0);
        }
    }
}