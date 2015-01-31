package org.itishka.pointim.fragments;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.itishka.pointim.R;
import org.itishka.pointim.activities.SinglePostActivity;
import org.itishka.pointim.activities.TagViewActivity;
import org.itishka.pointim.adapters.PostListAdapter;
import org.itishka.pointim.api.ConnectionManager;
import org.itishka.pointim.api.data.Post;
import org.itishka.pointim.api.data.PostList;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public abstract class PostListFragment extends Fragment {
    PostListAdapter.OnPostClickListener mOnPostClickListener = new PostListAdapter.OnPostClickListener() {

        @Override
        public void onPostClicked(View view, String post) {
            Intent intent = new Intent(getActivity(), SinglePostActivity.class);
            intent.putExtra("post", post);
            ActivityCompat.startActivity(getActivity(), intent, null);
        }

        @Override
        public void onTagClicked(View view, String tag) {
            Intent intent = new Intent(getActivity(), TagViewActivity.class);
            intent.putExtra("tag", tag);
            ActivityCompat.startActivity(getActivity(), intent, null);
        }
    };
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mLayoutManager;
    private PostListAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefresh;
    private Callback<PostList> mCallback = new Callback<PostList>() {
        @Override
        public void success(PostList postList, Response response) {
            mSwipeRefresh.setRefreshing(false);
            if (postList.isSuccess()) {
                mAdapter.setData(postList);
            } else {
                if (!isDetached())
                    Toast.makeText(getActivity(), postList.error, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void failure(RetrofitError error) {
            mSwipeRefresh.setRefreshing(false);
            if (!isDetached())
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
        }
    };
    private boolean mIsLoadingMore = false;
    private Callback<PostList> mLoadMoreCallback = new Callback<PostList>() {
        @Override
        public void success(PostList postList, Response response) {
            if (postList.isSuccess()) {
                mAdapter.appendData(postList);
            } else {
                if (!isDetached())
                    Toast.makeText(getActivity(), postList.error, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void failure(RetrofitError error) {
            if (!isDetached())
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    public PostListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_posts_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            mSwipeRefresh.setRefreshing(true);
            update(mCallback);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mLayoutManager.setSpanCount(getSpanCount(newConfig));
    }

    private int getSpanCount(Configuration config) {
        if ((config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            return config.orientation == Configuration.ORIENTATION_PORTRAIT ? 2 : 3;
        } else if ((config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            return config.orientation == Configuration.ORIENTATION_PORTRAIT ? 2 : 3;
        } else if ((config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            return config.orientation == Configuration.ORIENTATION_PORTRAIT ? 1 : 2;
        }
        return 1;
    }

    public PostListAdapter getAdapter() {
        return mAdapter;
    }

    protected PostListAdapter createAdapter() {
        return new PostListAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.posts);
        mSwipeRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ConnectionManager manager = ConnectionManager.getInstance();
                if (manager.isAuthorized()) {
                    update(getCallback());
                }
            }
        });
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new StaggeredGridLayoutManager(
                getSpanCount(getActivity().getResources().getConfiguration()),
                StaggeredGridLayoutManager.VERTICAL
        );
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = createAdapter();
        mAdapter.setOnPostClickListener(mOnPostClickListener);
        mAdapter.setOnLoadMoreRequestListener(new PostListAdapter.OnLoadMoreRequestListener() {
            @Override
            public boolean onLoadMoreRequested() {
                if (mIsLoadingMore) {
                    //do nothing
                } else {
                    List<Post> posts = mAdapter.getPostList().posts;
                    if (posts.size() < 1) {
                        mAdapter.getPostList().has_next = false;
                        return false;
                    } else {
                        loadMore(posts.get(posts.size() - 1).uid, getLoadMoreCallback());
                    }
                }
                return true;
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ConnectionManager manager = ConnectionManager.getInstance();
        if (manager.isAuthorized()) {
            mSwipeRefresh.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefresh.setRefreshing(true);
                    update(getCallback());
                }
            });
        }
    }

    protected Callback<PostList> getCallback() {
        return mCallback;
    }

    protected Callback<PostList> getLoadMoreCallback() {
        return mLoadMoreCallback;
    }

    protected abstract void update(Callback<PostList> callback);

    protected abstract void loadMore(long before, Callback<PostList> callback);
}
