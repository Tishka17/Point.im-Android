package org.itishka.pointim.fragments;

import android.os.Bundle;
import android.text.TextUtils;

import org.itishka.pointim.api.ConnectionManager;
import org.itishka.pointim.api.data.PostList;

import retrofit.Callback;

/**
 * A placeholder fragment containing a simple view.
 */
public class TagViewFragment extends PostListFragment {

    private String mTag;
    private String mUser;

    public static TagViewFragment newInstance(String user, String tag) {
        TagViewFragment fragment = new TagViewFragment();
        Bundle args = new Bundle();
        args.putString("tag", tag);
        args.putString("user", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTag = getArguments().getString("tag");
        mUser = getArguments().getString("user");
    }

    @Override
    protected void update(Callback<PostList> callback) {
        if (TextUtils.isEmpty(mUser))
            ConnectionManager.getInstance().pointService.getPostsByTag(mTag, callback);
        else
            ConnectionManager.getInstance().pointService.getPostsByUserTag(mUser, mTag, callback);
    }

    @Override
    protected void loadMore(long before, Callback<PostList> callback) {
        if (TextUtils.isEmpty(mUser))
            ConnectionManager.getInstance().pointService.getPostsByTag(before, mTag, callback);
        else
            ConnectionManager.getInstance().pointService.getPostsByUserTag(before, mUser, mTag, callback);
    }
}
