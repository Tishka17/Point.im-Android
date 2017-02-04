package org.itishka.pointim.fragments;

import org.itishka.pointim.model.point.PostList;
import org.itishka.pointim.network.PointConnectionManager;

import io.reactivex.Observable;


/**
 * Created by Tishka17 on 21.10.2014.
 */
public class OutgoingFragment extends PostListFragment {


    @Override
    protected Observable<PostList> createRequest() {
        return PointConnectionManager.getInstance().pointIm.getOutgoing();
    }

    @Override
    protected Observable<PostList> createRequest(long before) {
        return PointConnectionManager.getInstance().pointIm.getOutgoing(before);
    }
}
