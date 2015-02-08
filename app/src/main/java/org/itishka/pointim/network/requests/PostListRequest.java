package org.itishka.pointim.network.requests;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import org.itishka.pointim.model.PostList;
import org.itishka.pointim.network.PointIm;

/**
 * Created by Tishka17 on 08.02.2015.
 */
public abstract class PostListRequest extends RetrofitSpiceRequest<PostList, PointIm> {
    private final int mBefore;

    public PostListRequest() {
        super(PostList.class, PointIm.class);
        mBefore = 0;
    }

    public PostListRequest(int before) {
        super(PostList.class, PointIm.class);
        mBefore = before;
    }

    @Override
    final public PostList loadDataFromNetwork() throws Exception {
        if (mBefore == 0)
            return load();
        else
            return loadBefore(mBefore);
    }

    public abstract PostList load() throws Exception;

    public abstract PostList loadBefore(int before) throws Exception;

    public String getCacheName() {
        return getCacheName()+"."+mBefore;
    }
}
