package org.itishka.pointim;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.itishka.pointim.api.ConnectionManager;
import org.itishka.pointim.api.data.PostList;

import retrofit.Callback;

/**
 * A placeholder fragment containing a simple view.
 */
public class TagViewFragment extends PostListFragment {

    private String mTag;

    public static TagViewFragment newInstance(String tag) {
        TagViewFragment fragment = new TagViewFragment();
        Bundle args = new Bundle();
        args.putString("tag", tag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTag = getArguments().getString("tag");
    }

    @Override
    protected void update(Callback<PostList> callback) {
        ConnectionManager.getInstance().pointService.getPostsByTag(mTag, callback);
    }
}
