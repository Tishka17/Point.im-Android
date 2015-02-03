package org.itishka.pointim.fragments;

import android.widget.Toast;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.itishka.pointim.api.ConnectionManager;
import org.itishka.pointim.model.PostList;
import org.itishka.pointim.network.PointService;
import org.itishka.pointim.network.RecentRequest;

import retrofit.Callback;

/**
 * Created by Tishka17 on 21.10.2014.
 */
public class RecentFragment extends PostListFragment {
    protected SpiceManager spiceManager = new SpiceManager(PointService.class);

    @Override
    public void onStart() {
        super.onStart();
        spiceManager.start(getActivity());
    }

    @Override
    public void onStop() {
        if (spiceManager.isStarted()) {
            spiceManager.shouldStop();
        }
        super.onStop();
    }

    @Override
    protected void update(Callback<PostList> callback) {
        //ConnectionManager.getInstance().pointIm.getRecent(callback);
        RecentRequest request = new RecentRequest();
        spiceManager.getFromCache(PostList.class, "recents", DurationInMillis.ALWAYS_RETURNED, new PostListResultListener());
        spiceManager.execute(request, "recents", DurationInMillis.ALWAYS_EXPIRED, new PostListResultListener());
    }

    @Override
    protected void loadMore(long before, Callback<PostList> callback) {
        ConnectionManager.getInstance().pointIm.getRecent(before, callback);
    }

    class PostListResultListener implements RequestListener<PostList> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            if (!isDetached())
                Toast.makeText(getActivity(), spiceException.toString(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRequestSuccess(PostList postList) {
            if (postList!=null && postList.isSuccess()) {
                getAdapter().setData(postList);
            } else {
                if (!isDetached())
                    Toast.makeText(getActivity(), postList==null?"null":postList.error, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
