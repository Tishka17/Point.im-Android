package org.itishka.pointim.widgets;

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
    private final int MIN_SCROLL_DISTANCE = 10;

    private RecyclerView.OnScrollListener mOnScrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (dy>0 && mDirection>0 && recyclerView.canScrollVertically(1)
                    || dy<0 && mDirection<0 && recyclerView.canScrollVertically(-1)) {
                if (getVisibility()==INVISIBLE) setVisibility(VISIBLE);
            } else {
                if (getVisibility()==VISIBLE) setVisibility(INVISIBLE);
            }
        }
    };

    @Override
    public boolean callOnClick() {
        return super.callOnClick();
    }


    public ScrollButton(Context context) {
        super(context);
        super.setOnClickListener(this);
    }

    public ScrollButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseAttrs(context, attrs);
        super.setOnClickListener(this);
    }

    public ScrollButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        parseAttrs(context, attrs);
        super.setOnClickListener(this);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ScrollButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        parseAttrs(context, attrs);
        super.setOnClickListener(this);
    }

    RecyclerView.OnScrollListener getOnScrollListener() {
        return mOnScrollListener;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        mOnClickListener = l;
    }

    private void parseAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ScrollButton,
                0, 0);

        try {
            mDirection = a.getInteger(R.styleable.ScrollButton_direction, DIRECTION_NO);
        } finally {
            a.recycle();
        }
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        if (mRecyclerView!=null) {
            mRecyclerView.removeOnScrollListener(mOnScrollListener);
        }
        mRecyclerView = recyclerView;
        mRecyclerView.addOnScrollListener(mOnScrollListener);
    }

    @Override
    public void onClick(View view) {
        if (mRecyclerView != null) {
            if (mDirection <0)
                mRecyclerView.scrollToPosition(0);
            else if (mDirection >0)
                mRecyclerView.scrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
        }
        if (mOnClickListener!=null) {
            mOnClickListener.onClick(view);
        }
    }
}
