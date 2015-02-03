package org.itishka.pointim.fragments;

import org.itishka.pointim.api.ConnectionManager;
import org.itishka.pointim.model.PostList;

import retrofit.Callback;

/**
 * Created by Tishka17 on 21.10.2014.
 */
public class BookmarksFragment extends PostListFragment {
    @Override
    protected void update(Callback<PostList> callback) {
        ConnectionManager.getInstance().pointIm.getBookmarks(callback);
    }

    @Override
    protected void loadMore(long before, Callback<PostList> callback) {
        ConnectionManager.getInstance().pointIm.getBookmarks(before, callback);
    }
}
