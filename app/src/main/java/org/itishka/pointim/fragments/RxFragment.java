package org.itishka.pointim.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.simonpercic.waterfallcache.WaterfallCache;

import org.itishka.pointim.PointApplication;
import org.itishka.pointim.utils.ImageSearchHelper;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Tishka17 on 27.02.2015.
 */
public class RxFragment extends Fragment {
    private boolean mAutoload = true;
    private CompositeSubscription mCompositeSubscription;

    protected WaterfallCache getCache() {
        return ((PointApplication) getContext().getApplicationContext()).getCache();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            mAutoload = false;
        mCompositeSubscription = new CompositeSubscription();
    }

    protected void addSubscription(Subscription subscription) {
        mCompositeSubscription.add(subscription);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompositeSubscription.unsubscribe();
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
        ImageSearchHelper.saveCache(getActivity());
        super.onStop();
    }
}
