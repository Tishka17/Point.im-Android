package org.itishka.pointim.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.simonpercic.waterfallcache.WaterfallCache;

import org.itishka.pointim.PointApplication;
import org.itishka.pointim.utils.ImageSearchHelper;

/**
 * Created by Tishka17 on 27.02.2015.
 */
public class SpicedFragment extends Fragment {
    private boolean mAutoload = true;

    protected WaterfallCache getCache() {
        return ((PointApplication)getContext().getApplicationContext()).getCache();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            mAutoload = false;
    }

    protected boolean shouldAutoload() {
        return mAutoload;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        //unsubscribe in rx?
        ImageSearchHelper.saveCache(getActivity());
        super.onStop();
    }
}
