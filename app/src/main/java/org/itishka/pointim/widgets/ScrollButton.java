package org.itishka.pointim.widgets;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

import org.itishka.pointim.R;

/**
 * Created by Tishka17 on 07.08.2015.
 */
public class ScrollButton extends ImageButton implements View.OnClickListener {

    public final static int DIRECTION_NO = 0;
    private int mDirection = DIRECTION_NO;
    private RecyclerView mRecyclerView = null;
    private OnClickListener mOnClickListener = null;
    private boolean mAutoHide = true;

    private Animator.AnimatorListener mOnHideAnimationEnd = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animator) {
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            ScrollButton.this.setVisibility(INVISIBLE);
        }

        @Override
        public void onAnimationCancel(Animator animator) {
        }

        @Override
        public void onAnimationRepeat(Animator animator) {
        }
    };

    private RecyclerView.OnScrollListener mOnScrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (mAutoHide) {
                if (dy > 0 && mDirection > 0 && recyclerView.canScrollVertically(1)
                        || dy < 0 && mDirection < 0 && recyclerView.canScrollVertically(-1)) {
                    if (getAlpha() == 0) {
                        setVisibility(VISIBLE);
                        if (mAnimator != null) mAnimator.cancel();
                        mAnimator = ObjectAnimator.ofFloat(ScrollButton.this, "alpha", 1f);
                        mAnimator.setDuration(100);
                        mAnimator.start();
                    }
                } else {
                    if (getAlpha() == 1) {
                        if (mAnimator != null) mAnimator.cancel();
                        mAnimator = ObjectAnimator.ofFloat(ScrollButton.this, "alpha", 0f);
                        mAnimator.setDuration(100);
                        mAnimator.addListener(mOnHideAnimationEnd);
                        mAnimator.start();
                    }
                }
            }
        }
    };

    private ObjectAnimator mAnimator = null;


    @Override
    public boolean callOnClick() {
        return super.callOnClick();
    }


    public ScrollButton(Context context) {
        this(context, null, R.attr.scrollButtonStyle);
    }

    public ScrollButton(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.scrollButtonStyle);
    }

    public ScrollButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        parseAttrs(context, attrs);
        initView();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ScrollButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        parseAttrs(context, attrs);
        initView();
    }

    RecyclerView.OnScrollListener getOnScrollListener() {
        return mOnScrollListener;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        mOnClickListener = l;
    }

    private void initView() {
        super.setOnClickListener(this);
        if (mAutoHide) {
            setAlpha(0f);
            setVisibility(INVISIBLE);
        }
    }

    private void parseAttrs(Context context, AttributeSet attrs) {
        if (attrs==null)
            return;
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ScrollButton,
                0, 0);

        try {
            mDirection = a.getInteger(R.styleable.ScrollButton_direction, DIRECTION_NO);
            mAutoHide = a.getBoolean(R.styleable.ScrollButton_auto_hide, true);
        } finally {
            a.recycle();
        }
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        if (mRecyclerView != null) {
            mRecyclerView.removeOnScrollListener(mOnScrollListener);
        }
        mRecyclerView = recyclerView;
        mRecyclerView.addOnScrollListener(mOnScrollListener);
        setAlpha(0f);
    }

    @Override
    public void onClick(View view) {
        if (mRecyclerView != null) {
            if (mDirection < 0)
                mRecyclerView.scrollToPosition(0);
            else if (mDirection > 0)
                mRecyclerView.scrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
        }
        if (mOnClickListener != null) {
            mOnClickListener.onClick(view);
        }
    }
}
