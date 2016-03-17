package org.itishka.pointim.widgets;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.View;

/**
 * Created by Tishka17 on 17.03.2016.
 */
public class HideAnimationHelper implements Animator.AnimatorListener {
    private View mView;
    private boolean mIsHidingProgress = false;
    private ObjectAnimator mAnimator = null;

    public HideAnimationHelper(View view) {
        this.mView = view;
    }

    @Override
    public void onAnimationStart(Animator animator) {
        mIsHidingProgress = true;
    }

    @Override
    public void onAnimationEnd(Animator animator) {
        mView.setVisibility(View.INVISIBLE);
        mIsHidingProgress = false;
    }

    @Override
    public void onAnimationCancel(Animator animator) {
        mIsHidingProgress = false;
    }

    @Override
    public void onAnimationRepeat(Animator animator) {
    }

    public boolean isHidingProgress() {
        return mIsHidingProgress;
    }

    public void showView() {
        if (isViewHiddenOrHiding()) {
            Log.d("SCrollButton", "show()");
            mView.setVisibility(View.VISIBLE);
            if (mAnimator != null) mAnimator.cancel();
            mAnimator = ObjectAnimator.ofFloat(mView, "alpha", 1f);
            mAnimator.setDuration(250);
            mAnimator.start();
        }
    }

    public void hideView() {
        if (!isViewHiddenOrHiding()) {
            if (mAnimator != null) mAnimator.cancel();
            mAnimator = ObjectAnimator.ofFloat(mView, "alpha", 0f);
            mAnimator.setDuration(250);
            mAnimator.addListener(this);
            mAnimator.start();
        }
    }

    public boolean isViewHiddenOrHiding() {
        return mIsHidingProgress || mView.getVisibility() == View.INVISIBLE || mView.getVisibility() == View.GONE;
    }

    public void toggleView() {
        if (isViewHiddenOrHiding())
            showView();
        else
            hideView();
    }
}
