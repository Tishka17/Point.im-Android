package org.itishka.pointim;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.itishka.pointim.api.ConnectionManager;
import org.itishka.pointim.api.data.Comment;
import org.itishka.pointim.api.data.ExtendedPost;
import org.itishka.pointim.api.data.PointResult;
import org.itishka.pointim.dialogs.CustomDialog;
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
    private TextView mCommentId;
    private EditText mText;
    private ImageButton mSendButton;
    private View mBottomBar;


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
        setHasOptionsMenu(true);
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

        mBottomBar = rootView.findViewById(R.id.bottom_bar);
        mCommentId = (TextView)rootView.findViewById(R.id.comment_id);
        mCommentId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCommentId.setText("");
                mCommentId.setVisibility(View.GONE);
            }
        });
        mText = (EditText)rootView.findViewById(R.id.text);
        mSendButton = (ImageButton)rootView.findViewById(R.id.send);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = mText.getText().toString();
                if (TextUtils.isEmpty(text)) {
                    Toast.makeText(getActivity(), "Empty comment", Toast.LENGTH_SHORT).show();
                    return;
                }
                String comment = mCommentId.getText().toString();
                mBottomBar.setEnabled(false);
                if (TextUtils.isEmpty(comment)) {
                    ConnectionManager.getInstance().pointService.addComment(mPost, text, mCommentCallback);
                } else {
                    ConnectionManager.getInstance().pointService.addComment(mPost, text, comment, mCommentCallback);
                }
            }
        });

        ConnectionManager manager = ConnectionManager.getInstance();
        if (manager.isAuthorized()) {
            mSwipeRefresh.setRefreshing(true);
            update(getCallback());
        }
        ItemClickSupport itemClick = ItemClickSupport.addTo(mRecyclerView);

        itemClick.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, View view, int i, long l) {
                Object item = mAdapter.getItem(i);
                if (item instanceof Comment) {
                    mCommentId.setText(((Comment) item).id);
                    mCommentId.setVisibility(View.VISIBLE);
                } else {
                    mCommentId.setVisibility(View.GONE);
                    mCommentId.setText("");
                }
            }
        });
        return rootView;
    }

    private Callback<PointResult> mCommentCallback = new Callback<PointResult>() {
        @Override
        public void success(PointResult post, Response response) {
            mBottomBar.setEnabled(true);
            if (post.isSuccess()) {
                mCommentId.setVisibility(View.GONE);
                mCommentId.setText("");
                mText.setText("");
                update(getCallback());
                Toast.makeText(getActivity(), "Comment added!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), post.error, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void failure(RetrofitError error) {
            mBottomBar.setEnabled(true);
            Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
        }
    };
    private Callback<ExtendedPost> mCallback = new Callback<ExtendedPost>() {
        @Override
        public void success(ExtendedPost post, Response response) {
            mSwipeRefresh.setRefreshing(false);
            if (post.isSuccess()) {
                mAdapter.setData(post);
            } else {
                Toast.makeText(getActivity(), post.error, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void failure(RetrofitError error) {
            mSwipeRefresh.setRefreshing(false);
            Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    protected Callback<ExtendedPost> getCallback() {
        return mCallback;
    }

    protected void update(Callback<ExtendedPost> callback) {
        ConnectionManager.getInstance().pointService.getPost(mPost, getCallback());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_single_post, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_recomend) {
            final View v = getLayoutInflater(new Bundle()).inflate(R.layout.input_dialog, null);
            final CustomDialog dialog = new CustomDialog.Builder(getActivity(), "Really recommend #" + mPost + "?", "Ok")
                    .positiveColor(getActivity().getResources().getColor(R.color.material_blue_700))
                    .negativeText("Cancel")
                    .build();

            dialog.setClickListener(new CustomDialog.ClickListener() {
                @Override
                public void onConfirmClick() {
                    String text = ((EditText) (v.findViewById(R.id.recommend_text))).getText().toString();
                    if (TextUtils.isEmpty(text)) {
                        ConnectionManager.getInstance().pointService.recommend(mPost, mRecommendCallback);
                    } else {
                        ConnectionManager.getInstance().pointService.recommend(mPost, text, mRecommendCallback);
                    }
                }

                @Override
                public void onCancelClick() {

                }
            });
            dialog.setCustomView(v);
            dialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

    private Callback<PointResult> mRecommendCallback = new Callback<PointResult>() {
        @Override
        public void success(PointResult post, Response response) {
            if (post.isSuccess()) {
                Toast.makeText(getActivity(), "Reommended!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), post.error, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void failure(RetrofitError error) {
            Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
        }
    };
}
