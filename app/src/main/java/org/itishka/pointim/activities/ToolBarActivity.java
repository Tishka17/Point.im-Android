package org.itishka.pointim.activities;

import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

/**
 * Created by Tishka17 on 11.04.2015.
 */
public class ToolBarActivity extends ActionBarActivity {
    Toolbar mToolbar;

    @Override
    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        super.setSupportActionBar(toolbar);
        mToolbar = toolbar;
    }

    public void scrollToolbar(int offset) {
        setToolbarTop(mToolbar, offset);
    }

    public boolean isToolbarVisible() {
        return getToolbarTop(mToolbar)>-mToolbar.getHeight();
    }

    public void slideToolebar() {
        if (getToolbarTop(mToolbar)<-mToolbar.getHeight()/2) {
            animateTop(mToolbar, -mToolbar.getHeight(), 1000);
        } else {
            animateTop(mToolbar, 0, 1000);
        }
    }

    public void stopToolbarSlide() {
        Animation animation = mToolbar.getAnimation();
        if (animation!=null) animation.cancel();
    }

    public int getToolbarOffset() {
        return getToolbarTop(mToolbar);
    }

    private static void animateTop(final Toolbar toolbar, final int top, int duration) {
        final int start = getToolbarTop(toolbar);
        final int realDuration = duration * Math.abs(top-start)/toolbar.getHeight();
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                setToolbarTop(toolbar, start + (int) ((top-start) * interpolatedTime));
            }
        };
        a.setDuration(realDuration);
        toolbar.startAnimation(a);
    }

    private static void setToolbarTop(Toolbar toolbar, int top) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) toolbar.getLayoutParams();
        if (top<-toolbar.getHeight()) top = -toolbar.getHeight();
        if (top>0) top = 0;
        params.topMargin = top;
        toolbar.setLayoutParams(params);
    }

    private static int getToolbarTop(Toolbar toolbar) {
        return ((LinearLayout.LayoutParams) toolbar.getLayoutParams()).topMargin;
    }
}
