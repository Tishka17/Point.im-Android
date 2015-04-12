package org.itishka.pointim.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

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
            y = motionEvent.getY();
            stopToolbarSlide();
            scrolledDistance = getToolbarOffset();
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
            float dy = motionEvent2.getY() - y;
            y = motionEvent2.getY();
            Log.d("SrollableFrame", "onScroll dist=" + scrolledDistance + ", dy=" + dy + ", ev2=" + motionEvent2);
            int top = getToolbarOffset();
            if ((top >= 0 && dy < 0)
                    || (top <= -mToolbar.getHeight() && dy > 0)
                    || (top > -mToolbar.getHeight()) && top < 0) {
                scrolledDistance += dy;
                if (scrolledDistance < -mToolbar.getHeight()) {
                    scrolledDistance = -mToolbar.getHeight();
                } else if (scrolledDistance > 0) {
                    scrolledDistance = 0;
                }
                scrollToolbar((int) scrolledDistance);
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
    private Toolbar mToolbar = null;

    public void setToolbar(Toolbar toolbar) {
        mToolbar = toolbar;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean result = super.dispatchTouchEvent(ev);
        result = mGestureDetector.onTouchEvent(ev) || result;
        if (ev.getAction() == MotionEvent.ACTION_UP && mIsScrolling) {
            slideToolebar();
        }
        return result;
    }


    public void scrollToolbar(int offset) {
        setToolbarTop(mToolbar, offset);
    }

    public void slideToolebar() {
        if (getToolbarTop(mToolbar) < -mToolbar.getHeight() / 2) {
            animateTop(mToolbar, -mToolbar.getHeight(), 1000);
        } else {
            animateTop(mToolbar, 0, 1000);
        }
    }

    public void stopToolbarSlide() {
        Animation animation = mToolbar.getAnimation();
        if (animation != null) animation.cancel();
    }

    public int getToolbarOffset() {
        return getToolbarTop(mToolbar);
    }

    private static void animateTop(final Toolbar toolbar, final int top, int duration) {
        final int start = getToolbarTop(toolbar);
        final int realDuration = duration * Math.abs(top - start) / toolbar.getHeight();
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                setToolbarTop(toolbar, start + (int) ((top - start) * interpolatedTime));
            }
        };
        a.setDuration(realDuration);
        toolbar.startAnimation(a);
    }

    private static void setToolbarTop(Toolbar toolbar, int top) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) toolbar.getLayoutParams();
        if (top < -toolbar.getHeight()) top = -toolbar.getHeight();
        if (top > 0) top = 0;
        params.topMargin = top;
        toolbar.setLayoutParams(params);
    }

    private static int getToolbarTop(Toolbar toolbar) {
        return ((LinearLayout.LayoutParams) toolbar.getLayoutParams()).topMargin;
    }
}
