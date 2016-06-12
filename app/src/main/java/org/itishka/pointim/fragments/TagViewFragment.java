package org.itishka.pointim.fragments;

import android.os.Bundle;
import android.text.TextUtils;

import org.itishka.pointim.model.point.PostList;
import org.itishka.pointim.network.PointConnectionManager;

import rx.Observable;

/**
 * A placeholder fragment containing a simple view.
 */
public class TagViewFragment extends PostListFragment {

    public static final String ARG_USER = "user";
    public static final String ARG_TAG = "tag";
    private String mTag;
    private String mUser;

    public static TagViewFragment newInstance(String user, String tag) {
        TagViewFragment fragment = new TagViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TAG, tag);
        args.putString(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTag = getArguments().getString(ARG_TAG);
        mUser = getArguments().getString(ARG_USER);
    }

    @Override
    protected Observable<PostList> createRequest() {
        if (TextUtils.isEmpty(mUser))
            return PointConnectionManager.getInstance().pointIm.getPostsByTag(mTag);
        else
            return PointConnectionManager.getInstance().pointIm.getPostsByUserTag(mUser, mTag);
    }

    @Override
    protected Observable<PostList> createRequest(long before) {
        if (TextUtils.isEmpty(mUser))
            return PointConnectionManager.getInstance().pointIm.getPostsByTag(before, mTag);
        else
            return PointConnectionManager.getInstance().pointIm.getPostsByUserTag(mUser, before, mTag);
    }
}
