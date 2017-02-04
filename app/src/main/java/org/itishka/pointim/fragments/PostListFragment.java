package org.itishka.pointim.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import org.itishka.pointim.adapters.PostListAdapter;
import org.itishka.pointim.listeners.OnPostChangedListener;
import org.itishka.pointim.listeners.SimplePointClickListener;
import org.itishka.pointim.listeners.SimplePostActionsListener;
import org.itishka.pointim.model.point.Post;
import org.itishka.pointim.model.point.PostList;
import org.itishka.pointim.network.PointConnectionManager;
import org.itishka.pointim.widgets.ScrollButton;

import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public abstract class PostListFragment extends RxFragment {

    private SimplePointClickListener mOnPointClickListener = new SimplePointClickListener(this);
    private SimplePostActionsListener mOnPostActionsListener = new SimplePostActionsListener(this);
    private OnPostChangedListener onPostChangedListener = new OnPostChangedListener() {
        @Override
        public void onChanged(Post post) {
            mAdapter.notifyPostChanged(post);
            getCache().put(getCacheName(), getAdapter().getPostList());
        }

        @Override
        public void onDeleted(Post post) {
            mAdapter.removePost(post);
            getCache().put(getCacheName(), getAdapter().getPostList());
        }
    };

    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mLayoutManager;
    private PostListAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefresh;
    private boolean mIsLoadingMore = false;
    private Subscription mSubscription;
    private Subscription mCacheSubscription;

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
            update();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mLayoutManager.setSpanCount(getSpanCount(newConfig));
    }

    @Override
    public void onPause() {
        super.onPause();
        mSwipeRefresh.setRefreshing(false);
    }

    private int getSpanCount(Configuration config) {
        SharedPreferences prefs = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        if (!prefs.getBoolean("multiColumns", true))
            return 1;

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
        mSwipeRefresh.setOnRefreshListener(() -> {
            PointConnectionManager manager = PointConnectionManager.getInstance();
            if (manager.isAuthorized()) {
                update();
            }
        });
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new StaggeredGridLayoutManager(
                getSpanCount(getActivity().getResources().getConfiguration()),
                StaggeredGridLayoutManager.VERTICAL
        );
        mRecyclerView.setLayoutManager(mLayoutManager);
        ((ScrollButton) rootView.findViewById(R.id.scroll_up)).setRecyclerView(mRecyclerView);
        mAdapter = createAdapter();
        mAdapter.setOnPointClickListener(mOnPointClickListener);
        mOnPostActionsListener.setOnPostChangedListener(onPostChangedListener);
        mAdapter.setOnPostActionsListener(mOnPostActionsListener);
        mAdapter.setOnLoadMoreRequestListener(() -> {
            if (mIsLoadingMore) {
                //do nothing
            } else {
                List<Post> posts = mAdapter.getPostList().posts;
                if (posts.size() < 1) {
                    mAdapter.getPostList().has_next = false;
                    return false;
                } else {
                    loadMore(posts.get(posts.size() - 1).uid);
                }
            }
            return true;
        });
        mRecyclerView.setAdapter(mAdapter);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PointConnectionManager manager = PointConnectionManager.getInstance();
        if (manager.isAuthorized()) {
            Observable<PostList> observable = getCache().get(getCacheName(), PostList.class);
            mCacheSubscription = observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(postList -> {
                        if (postList != null && postList.isSuccess()) {
                            getCache().put(getCacheName(), postList);
                            mAdapter.setData(getActivity(), postList);
                            mRecyclerView.scrollToPosition(0);
                        }
                        update();
                    });
            addSubscription(mCacheSubscription);
        }
    }

    protected void update() {
        mSwipeRefresh.setRefreshing(true);
        if (mCacheSubscription != null && !mCacheSubscription.isUnsubscribed()) {
            mCacheSubscription.unsubscribe();
            mCacheSubscription = null;
        }
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
        mSubscription = createRequest()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(postList -> {
                    mSwipeRefresh.setRefreshing(false);
                    if (postList != null && postList.isSuccess()) {
                        getCache().put(getCacheName(), postList);
                        mAdapter.setData(getActivity(), postList);
                        mRecyclerView.scrollToPosition(0);
                    } else {
                        if (!isDetached())
                            Toast.makeText(getActivity(), (postList == null) ? "null" : postList.error, Toast.LENGTH_SHORT).show();
                    }
                }, error -> {
                    Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                });
    }

    protected void loadMore(long before) {
        mSubscription = createRequest(before)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(postList -> {
                    if (postList != null && postList.isSuccess()) {
                        getCache().put(getCacheName(), postList);
                        mAdapter.appendData(getActivity(), postList);
                        getCache().put(getCacheName(), getAdapter().getPostList());
                    } else {
                        if (!isDetached())
                            Toast.makeText(getActivity(), (postList == null) ? "null" : postList.error, Toast.LENGTH_SHORT).show();
                    }
                }, error -> {
                    if (!isDetached())
                        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                });
        addSubscription(mSubscription);
    }

    protected abstract Observable<PostList> createRequest();

    protected abstract Observable<PostList> createRequest(long before);

    protected String getCacheName() {
        return getClass().getCanonicalName();
    }
}

