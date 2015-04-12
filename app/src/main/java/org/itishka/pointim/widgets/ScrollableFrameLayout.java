package org.itishka.pointim.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import org.itishka.pointim.activities.ToolBarActivity;

/**
 * Created by Tishka17 on 11.04.2015.
 */
public class ScrollableFrameLayout extends FrameLayout {
    public ScrollableFrameLayout(Context context) {
        super(context);
        mGestureDetector = new GestureDetector(context, mOnGestureListener);
    }

    public ScrollableFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mGestureDetector = new GestureDetector(context, mOnGestureListener);
    }

    public ScrollableFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mGestureDetector = new GestureDetector(context, mOnGestureListener);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ScrollableFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mGestureDetector = new GestureDetector(context, mOnGestureListener);
    }

    private GestureDetector.OnGestureListener mOnGestureListener = new GestureDetector.OnGestureListener() {
        private float y = 0;
        private float scrolledDistance = 0;

        @Override
        public boolean onDown(MotionEvent motionEvent) {
            ToolBarActivity activity = (ToolBarActivity) getContext();
            y = motionEvent.getY();
            activity.stopToolbarSlide();
            scrolledDistance = activity.getToolbarOffset();
            Log.d("SrollableFrame", "onDown dist=" + scrolledDistance);
            return true;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
            mIsScrolling = true;
            boolean result = false;
            ToolBarActivity activity = (ToolBarActivity) getContext();
            float dy = motionEvent2.getY() - y;
            y = motionEvent2.getY();
            Log.d("SrollableFrame", "onScroll dist=" + scrolledDistance + ", dy=" + dy + ", ev2=" + motionEvent2);
            int top = activity.getToolbarOffset();
            if ((top >= 0 && dy < 0)
                    || (top <= -activity.getSupportActionBar().getHeight() && dy > 0)
                    || (top > -activity.getSupportActionBar().getHeight()) && top < 0) {
                scrolledDistance += dy;
                if (scrolledDistance < -activity.getSupportActionBar().getHeight()) {
                    scrolledDistance = -activity.getSupportActionBar().getHeight();
                } else if (scrolledDistance > 0) {
                    scrolledDistance = 0;
                }
                activity.scrollToolbar((int) scrolledDistance);
                result = true;
            }
            return result;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
            return false;
        }
    };

    private GestureDetector mGestureDetector;
    private boolean mIsScrolling = false;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean result = super.dispatchTouchEvent(ev);
        result = mGestureDetector.onTouchEvent(ev) || result;
        if (ev.getAction() == MotionEvent.ACTION_UP && mIsScrolling) {
            ToolBarActivity activity = (ToolBarActivity) getContext();
            activity.slideToolebar();
        }
        return result;
    }
}
