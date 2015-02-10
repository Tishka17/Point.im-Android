package org.itishka.pointim.fragments;

import android.os.Bundle;
import android.text.TextUtils;

import org.itishka.pointim.api.ConnectionManager;
import org.itishka.pointim.model.PostList;
import org.itishka.pointim.network.requests.PostListRequest;

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
    protected PostListRequest createRequest() {
        return new TagRequest();
    }

    @Override
    protected PostListRequest createRequest(long before) {
        return new TagRequest(before);
    }

    public class TagRequest extends PostListRequest {
        public TagRequest(long before) {
            super(before);
        }

        public TagRequest() {
            super();
        }

        @Override
        public PostList load() throws Exception {
            return getService().getPostsByTag(mTag);
        }

        @Override
        public PostList loadBefore(long before) throws Exception {
            return getService().getPostsByTag(before, mTag);
        }

    }
}
