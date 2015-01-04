package org.itishka.pointim;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import org.itishka.pointim.api.ConnectionManager;
import org.itishka.pointim.api.data.Comment;
import org.itishka.pointim.api.data.ExtendedPost;
import org.itishka.pointim.api.data.PointResult;
import org.itishka.pointim.widgets.ImageUploadingPanel;
import org.lucasr.twowayview.ItemClickSupport;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SinglePostFragment extends Fragment {
    private static final String ARG_POST = "post";
    private static final int RESULT_LOAD_IMAGE = 17;

    private String mPost;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefresh;
    private LinearLayoutManager mLayoutManager;
    private SinglePostAdapter mAdapter;
    private TextView mCommentId;
    private EditText mText;
    private ImageButton mSendButton;
    private View mBottomBar;
    private ExtendedPost mPointPost;
    private Dialog mProgressDialog;
    private ImageUploadingPanel mImagesPanel;
    private ImageButton mAttachButton;


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

        mProgressDialog = new MaterialDialog.Builder(getActivity())
                .cancelable(false)
                .customView(R.layout.dialog_progress, false)
                .build();
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
        mCommentId = (TextView) rootView.findViewById(R.id.comment_id);
        mCommentId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCommentId.setText("");
                mCommentId.setVisibility(View.GONE);
            }
        });
        mText = (EditText) rootView.findViewById(R.id.text);
        mSendButton = (ImageButton) rootView.findViewById(R.id.send);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = mText.getText().toString();
                if (!mImagesPanel.isUploadFinished()) {
                    Toast.makeText(getActivity(), "Wait or check for errors!", Toast.LENGTH_SHORT).show();
                    return;
                }
                StringBuffer sb = new StringBuffer(text);
                for (String l : mImagesPanel.getLinks()) {
                    sb.append("\n").append(l);
                }
                text = sb.toString().trim();

                if (TextUtils.isEmpty(text)) {
                    Toast.makeText(getActivity(), "Empty comment", Toast.LENGTH_SHORT).show();
                    return;
                }

                String comment = mCommentId.getText().toString();
                mBottomBar.setEnabled(false);
                mProgressDialog.show();
                if (TextUtils.isEmpty(comment)) {
                    ConnectionManager.getInstance().pointService.addComment(mPost, text, mCommentCallback);
                } else {
                    ConnectionManager.getInstance().pointService.addComment(mPost, text, comment, mCommentCallback);
                }
            }
        });

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
        mImagesPanel = (ImageUploadingPanel) rootView.findViewById(R.id.imagesPanel);
        mAttachButton = (ImageButton) rootView.findViewById(R.id.attach);
        mAttachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
        return rootView;
    }

    private Callback<PointResult> mCommentCallback = new Callback<PointResult>() {
        @Override
        public void success(PointResult post, Response response) {
            mBottomBar.setEnabled(true);
            mProgressDialog.hide();
            if (post.isSuccess()) {
                mCommentId.setVisibility(View.GONE);
                mCommentId.setText("");
                mText.setText("");
                mImagesPanel.reset();
                update(getCallback());
                Toast.makeText(getActivity(), "Comment added!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), post.error, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void failure(RetrofitError error) {
            mBottomBar.setEnabled(true);
            mProgressDialog.hide();
            Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
        }
    };
    private Callback<ExtendedPost> mCallback = new Callback<ExtendedPost>() {
        @Override
        public void success(ExtendedPost post, Response response) {
            mSwipeRefresh.setRefreshing(false);
            if (post.isSuccess()) {
                mAdapter.setData(post);
                mPointPost = post;
                if (!isDetached())
                    getActivity().supportInvalidateOptionsMenu();
            } else {
                if (!isDetached())
                    Toast.makeText(getActivity(), post.error, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void failure(RetrofitError error) {
            mSwipeRefresh.setRefreshing(false);
            if (!isDetached())
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
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.setGroupVisible(R.id.group_my,
                mPointPost != null &&
                        mPointPost.post.author.login.equalsIgnoreCase(ConnectionManager.getInstance().loginResult.login)
        );
        menu.setGroupVisible(R.id.group_not_recommended,
                mPointPost != null &&
                        !mPointPost.post.author.login.equalsIgnoreCase(ConnectionManager.getInstance().loginResult.login) &&
                        !mPointPost.recommended
        );




    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_single_post, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            mSwipeRefresh.setRefreshing(true);
            update(mCallback);
            return true;
        } else if (id == R.id.action_recommend) {
            final MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                    .title("Really recommend #" + mPost + "?")
                    .positiveText(android.R.string.ok)
                    .negativeText("Cancel")
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            super.onPositive(dialog);
                            String text = ((EditText) (dialog.findViewById(R.id.recommend_text))).getText().toString();
                            mProgressDialog.show();
                            if (TextUtils.isEmpty(text)) {
                                ConnectionManager.getInstance().pointService.recommend(mPost, mRecommendCallback);
                            } else {
                                ConnectionManager.getInstance().pointService.recommend(mPost, text, mRecommendCallback);
                            }
                        }
                    })
                    .customView(R.layout.dialog_input, true)
                    .build();
            dialog.show();
            return true;
        } else if (id == R.id.action_delete) {
            final MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                    .title("Really delete #" + mPost + "?")
                    .positiveText(android.R.string.ok)
                    .negativeText("Cancel")
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            super.onPositive(dialog);
                            mProgressDialog.show();
                            ConnectionManager.getInstance().pointService.deletePost(mPost, mDeleteCallback);
                        }
                    })
                    .build();
            dialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Callback<PointResult> mDeleteCallback = new Callback<PointResult>() {
        @Override
        public void success(PointResult pointResult, Response response) {
            mProgressDialog.hide();
            if (pointResult.isSuccess()) {
                Toast.makeText(getActivity(), "Deleted!", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            } else {
                Toast.makeText(getActivity(), pointResult.error, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void failure(RetrofitError error) {
            mProgressDialog.hide();
            Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    private Callback<PointResult> mRecommendCallback = new Callback<PointResult>() {
        @Override
        public void success(PointResult post, Response response) {
            mProgressDialog.hide();
            if (post.isSuccess()) {
                Toast.makeText(getActivity(), "Reommended!", Toast.LENGTH_SHORT).show();
                update(mCallback);
            } else {
                Toast.makeText(getActivity(), post.error, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void failure(RetrofitError error) {
            mProgressDialog.hide();
            Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
            mImagesPanel.addImage(data.getData());
        }
    }
}
