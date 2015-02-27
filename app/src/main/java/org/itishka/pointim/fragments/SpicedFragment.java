package org.itishka.pointim.fragments;

import android.support.v4.app.Fragment;

import com.octo.android.robospice.SpiceManager;

import org.itishka.pointim.network.PointService;

/**
 * Created by Tishka17 on 27.02.2015.
 */
public class SpicedFragment extends Fragment {
    protected SpiceManager mSpiceManager = new SpiceManager(PointService.class);
    protected SpiceManager getSpiceManager() {
        return mSpiceManager;
    }

    @Override
    public void onStart() {
        super.onStart();
        getSpiceManager().start(getActivity());
    }

    @Override
    public void onStop() {
        if (getSpiceManager().isStarted()) {
            getSpiceManager().shouldStop();
        }
        super.onStop();
    }
}
