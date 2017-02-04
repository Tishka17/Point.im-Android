package org.itishka.pointim.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import org.itishka.pointim.PointApplication;
import org.itishka.pointim.utils.ImageSearchHelper;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


/**
 * Created by Tishka17 on 27.02.2015.
 */
public class RxFragment extends Fragment {
    private boolean mAutoload = true;
    private CompositeDisposable mCompositeDisposable;

//    protected WaterfallCache getCache() {
//        return ((PointApplication) getContext().getApplicationContext()).getCache();
//    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            mAutoload = false;
        mCompositeDisposable = new CompositeDisposable();
    }

    protected void addSubscription(Disposable subscription) {
        mCompositeDisposable.add(subscription);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.dispose();
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
