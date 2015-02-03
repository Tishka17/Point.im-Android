package org.itishka.pointim.network;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import org.itishka.pointim.model.PostList;

/**
 * Created by Tishka17 on 04.02.2015.
 */
public class RecentRequest extends RetrofitSpiceRequest<PostList,PointIm> {

    public RecentRequest() {
        super(PostList.class, PointIm.class);
    }

    @Override
    public PostList loadDataFromNetwork() throws Exception {
        return getService().getRecent();
    }
}
