package org.itishka.pointim;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
    private PostAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefresh;

    public PostListFragment() {
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
                    mSwipeRefresh.setRefreshing(true);
                    update();
                }
            }
        });
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new PostAdapter(getActivity(), null);
        mRecyclerView.setAdapter(mAdapter);
        ConnectionManager manager = ConnectionManager.getInstance();
        if (manager.isAuthorized()) {
            mSwipeRefresh.setRefreshing(true);
            update();
        }

        return rootView;
    }

    private Callback<PostList> mCallback = new Callback<PostList>() {
        @Override
        public void success(PostList postList, Response response) {
            if (postList.isSuccess()) {
                mAdapter.setData(postList);
                mRecyclerView.setAdapter(mAdapter);
            } else {
                mSwipeRefresh.setRefreshing(false);
                Toast.makeText(getActivity(), postList.error, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void failure(RetrofitError error) {
            Toast.makeText(getActivity(), error.getBody().toString(), Toast.LENGTH_SHORT).show();
            mSwipeRefresh.setRefreshing(false);
        }
    };

    protected Callback<PostList> getCallback() {
        return mCallback;
    }

    protected abstract void update();
}
