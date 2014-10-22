package org.itishka.pointim;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.itishka.pointim.api.ConnectionManager;
import org.itishka.pointim.api.data.ExtendedPost;
import org.itishka.pointim.api.data.PostList;
import org.lucasr.twowayview.ItemClickSupport;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SinglePostFragment extends Fragment {
    private static final String ARG_POST = "post";

    private String mPost;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefresh;
    private LinearLayoutManager mLayoutManager;
    private SinglePostAdapter mAdapter;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param post Post ID.
     * @return A new instance of fragment SinglePostFragment.
     */
    public static SinglePostFragment newInstance(String post) {
        SinglePostFragment fragment = new SinglePostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_POST, post);
        fragment.setArguments(args);
        return fragment;
    }

    public SinglePostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPost = getArguments().getString(ARG_POST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_single_post, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.post);
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
        mAdapter = new SinglePostAdapter(getActivity(), null);
        mRecyclerView.setAdapter(mAdapter);
        ConnectionManager manager = ConnectionManager.getInstance();
        if (manager.isAuthorized()) {
            mSwipeRefresh.setRefreshing(true);
            update(getCallback());
        }
        ItemClickSupport itemClick = ItemClickSupport.addTo(mRecyclerView);

        itemClick.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, View view, int i, long l) {

            }
        });
        return rootView;
    }

    private Callback<ExtendedPost> mCallback = new Callback<ExtendedPost>() {
        @Override
        public void success(ExtendedPost post, Response response) {
            mSwipeRefresh.setRefreshing(false);
            if (post.isSuccess()) {
                mAdapter.setData(post);
                mRecyclerView.setAdapter(mAdapter);
            } else {
                Toast.makeText(getActivity(), post.error, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void failure(RetrofitError error) {
            mSwipeRefresh.setRefreshing(false);
            Toast.makeText(getActivity(), error.getBody().toString(), Toast.LENGTH_SHORT).show();
        }
    };

    protected Callback<ExtendedPost> getCallback() {
        return mCallback;
    }

    protected void update(Callback<ExtendedPost> callback) {
        ConnectionManager.getInstance().pointService.getPost(mPost, getCallback());
    }
}
