package org.itishka.pointim;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.itishka.pointim.api.ConnectionManager;
import org.itishka.pointim.api.data.PostList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public abstract class PostListFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private PostListAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefresh;

    public PostListFragment() {
    }

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
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new PostListAdapter(getActivity(), null);
        mAdapter.setOnPostClickListener(mOnPostClickListener);
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

    private Callback<PostList> mCallback = new Callback<PostList>() {
        @Override
        public void success(PostList postList, Response response) {
            mSwipeRefresh.setRefreshing(false);
            if (postList.isSuccess()) {
                mAdapter.setData(postList);
            } else {
                Toast.makeText(getActivity(), postList.error, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void failure(RetrofitError error) {
            mSwipeRefresh.setRefreshing(false);
            Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    protected Callback<PostList> getCallback() {
        return mCallback;
    }

    protected abstract void update(Callback<PostList> callback);
}
