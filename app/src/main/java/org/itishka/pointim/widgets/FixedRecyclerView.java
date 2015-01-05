package org.itishka.pointim.widgets;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Татьяна on 21.10.2014.
 */
public class FixedRecyclerView extends RecyclerView {
    public FixedRecyclerView(Context context) {
        super(context);
    }

    public FixedRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FixedRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean canScrollVertically(int direction) {
        // check if scrolling up
        if (direction < 1) {
            boolean original = super.canScrollVertically(direction);
            View child = getChildAt(0);
            if (child == null)
                return original;
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
            return child.getTop() < lp.topMargin || original;
        }
        return super.canScrollVertically(direction);

    }
}
