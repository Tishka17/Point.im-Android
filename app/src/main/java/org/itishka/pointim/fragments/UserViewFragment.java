package org.itishka.pointim.fragments;

import android.os.Bundle;

import org.itishka.pointim.api.ConnectionManager;
import org.itishka.pointim.api.data.PostList;

import retrofit.Callback;

/**
 * A placeholder fragment containing a simple view.
 */
public class UserViewFragment extends PostListFragment {

    private String mUser;

    public static UserViewFragment newInstance(String tag) {
        UserViewFragment fragment = new UserViewFragment();
        Bundle args = new Bundle();
        args.putString("user", tag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUser = getArguments().getString("user");
    }

    @Override
    protected void update(Callback<PostList> callback) {
        ConnectionManager.getInstance().pointService.getBlog(mUser, callback);
    }

    @Override
    protected void loadMore(long before, Callback<PostList> callback) {
        ConnectionManager.getInstance().pointService.getBlog(before, mUser, callback);
    }
}
