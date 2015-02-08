package org.itishka.pointim.network.requests;

import org.itishka.pointim.model.PostList;

/**
 * Created by Tishka17 on 04.02.2015.
 */
public class RecentRequest extends PostListRequest {
    public RecentRequest(int before) {
        super(before);
    }

    public RecentRequest() {
        super();
    }

    @Override
    public PostList load() throws Exception {
        return getService().getRecent();
    }

    @Override
    public PostList loadBefore(int before) throws Exception {
        return getService().getRecent(before);
    }

}
