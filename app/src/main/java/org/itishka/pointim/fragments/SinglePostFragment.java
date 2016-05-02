package org.itishka.pointim.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.itishka.pointim.R;
import org.itishka.pointim.activities.NewPostActivity;
import org.itishka.pointim.adapters.SinglePostAdapter;
import org.itishka.pointim.adapters.UserCompletionAdapter;
import org.itishka.pointim.listeners.SimplePointClickListener;
import org.itishka.pointim.listeners.SimplePostActionsListener;
import org.itishka.pointim.model.point.Comment;
import org.itishka.pointim.model.point.ExtendedPost;
import org.itishka.pointim.model.point.PointResult;
import org.itishka.pointim.model.point.UserList;
import org.itishka.pointim.network.PointConnectionManager;
import org.itishka.pointim.network.requests.SinglePostRequest;
import org.itishka.pointim.network.requests.UserSubscriptionsRequest;
import org.itishka.pointim.utils.Utils;
import org.itishka.pointim.widgets.ImageUploadingPanel;
import org.itishka.pointim.widgets.ScrollButton;
import org.itishka.pointim.widgets.SymbolTokenizer;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SinglePostFragment extends SpicedFragment {
    private static final String ARG_POST = "post";
    private static final int RESULT_LOAD_IMAGE = 17;

    private String mPost;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefresh;
    private LinearLayoutManager mLayoutManager;
    private SinglePostAdapter mAdapter;
    private TextView mCommentId;
    private MultiAutoCompleteTextView mText;
    private ImageButton mSendButton;
    private View mBottomBar;
    private ExtendedPost mPointPost;
    private Dialog mProgressDialog;
    private ImageUploadingPanel mImagesPanel;
    private ImageButton mAttachButton;
    private ShareActionProvider mShareActionProvider;
    private UserCompletionAdapter mUsersListAdapter;
    private ScrollButton mUpButton;
    private ScrollButton mDownButton;

    private SimplePointClickListener mOnPointClickListener = new SimplePointClickListener(this);
    private SimplePostActionsListener mOnPostActionsListener = new SimplePostActionsListener(this);

    private void hideDialog() {
        if (mProgressDialog != null) mProgressDialog.hide();
        mProgressDialog = null;
    }

    private void showDialog() {
        mProgressDialog = new MaterialDialog.Builder(getActivity())
                .cancelable(false)
                .customView(R.layout.dialog_progress, false)
                .build();
        mProgressDialog.show();
    }

    private RequestListener<ExtendedPost> mUpdateRequestListener = new RequestListener<ExtendedPost>() {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            mSwipeRefresh.setRefreshing(false);
            if (!isDetached())
                Toast.makeText(getActivity(), spiceException.toString(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRequestSuccess(ExtendedPost extendedPost) {
            mSwipeRefresh.setRefreshing(false);
            if (extendedPost != null && extendedPost.isSuccess()) {
                mAdapter.setData(extendedPost);
                mPointPost = extendedPost;
                addAuthorsToCompletion();
                mUsersListAdapter.notifyDataSetChanged();
                mDownButton.updateVisibility();
                if (!isDetached())
                    getActivity().supportInvalidateOptionsMenu();
            } else {
                if (!isDetached())
                    Toast.makeText(getActivity(), (extendedPost == null) ? "null" : extendedPost.error, Toast.LENGTH_SHORT).show();
            }
        }
    };
    private RequestListener<ExtendedPost> mCacheRequestListener = new RequestListener<ExtendedPost>() {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            mSwipeRefresh.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefresh.setRefreshing(true);
                    update();
                }
            });
        }

        @Override
        public void onRequestSuccess(ExtendedPost extendedPost) {
            if (extendedPost != null && extendedPost.isSuccess()) {
                mAdapter.setData(extendedPost);
                mPointPost = extendedPost;
                addAuthorsToCompletion();
                mUsersListAdapter.notifyDataSetChanged();
                if (!isDetached())
                    getActivity().supportInvalidateOptionsMenu();
                if (shouldAutoload()) {
                    mSwipeRefresh.setRefreshing(true);
                    update();
                }
            } else {
                mSwipeRefresh.post(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefresh.setRefreshing(true);
                        update();
                    }
                });
            }
        }
    };

    private Callback<PointResult> mRecommendCallback = new Callback<PointResult>() {
        @Override
        public void success(PointResult post, Response response) {
            hideDialog();
            if (post.isSuccess()) {
                if (!isDetached()) {
                    Toast.makeText(getActivity(), getString(R.string.toast_recommended), Toast.LENGTH_SHORT).show();
                    update();
                }
            } else {
                if (!isDetached())
                    Toast.makeText(getActivity(), post.error, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void failure(RetrofitError error) {
            hideDialog();
            if (!isDetached())
                Toast.makeText(getActivity(), error.toString() + "\n\n" + error.getCause(), Toast.LENGTH_SHORT).show();
        }
    };

    private Callback<PointResult> mCommentCallback = new Callback<PointResult>() {
        @Override
        public void success(PointResult post, Response response) {
            mBottomBar.setEnabled(true);
            hideDialog();
            if (post.isSuccess()) {
                mCommentId.setVisibility(View.GONE);
                mCommentId.setText("");
                mText.setText("");
                mImagesPanel.reset();
                if (!isDetached()) {
                    update();
                    Toast.makeText(getActivity(), getString(R.string.toast_commented), Toast.LENGTH_SHORT).show();
                }
            } else {
                if (!isDetached())
                    Toast.makeText(getActivity(), post.error, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void failure(RetrofitError error) {
            mBottomBar.setEnabled(true);
            hideDialog();
            if (!isDetached())
                Toast.makeText(getActivity(), error.toString() + "\n\n" + error.getCause(), Toast.LENGTH_SHORT).show();
        }
    };
    private Callback<PointResult> mDeleteCallback = new Callback<PointResult>() {
        @Override
        public void success(PointResult pointResult, Response response) {
            hideDialog();
            if (isDetached())
                return;
            if (pointResult.isSuccess()) {

                Toast.makeText(getActivity(), getString(R.string.toast_deleted), Toast.LENGTH_SHORT).show();
                getActivity().finish();
            } else {
                Toast.makeText(getActivity(), pointResult.error, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void failure(RetrofitError error) {
            hideDialog();
            if (!isDetached())
                Toast.makeText(getActivity(), error.toString() + "\n\n" + error.getCause(), Toast.LENGTH_SHORT).show();
        }
    };

    public SinglePostFragment() {
        // Required empty public constructor
    }

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
        PointConnectionManager manager = PointConnectionManager.getInstance();
        if (manager.isAuthorized()) {
            SinglePostRequest request = createRequest();
            getSpiceManager().getFromCache(ExtendedPost.class, request.getCacheName(), DurationInMillis.ALWAYS_RETURNED, mCacheRequestListener);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_single_post, container, false);
        mUsersListAdapter = new UserCompletionAdapter(getActivity());
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.post);
        mSwipeRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                PointConnectionManager manager = PointConnectionManager.getInstance();
                if (manager.isAuthorized()) {
                    update();
                }
            }
        });
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new SinglePostAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mUpButton = (ScrollButton) rootView.findViewById(R.id.scroll_up);
        mUpButton.setRecyclerView(mRecyclerView);
        mDownButton = (ScrollButton) rootView.findViewById(R.id.scroll_down);
        mDownButton.setRecyclerView(mRecyclerView);

        mBottomBar = rootView.findViewById(R.id.bottom_bar);
        mCommentId = (TextView) rootView.findViewById(R.id.comment_id);
        mCommentId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCommentId.setText("");
                mCommentId.setVisibility(View.GONE);
            }
        });
        mText = (MultiAutoCompleteTextView) rootView.findViewById(R.id.text);
        mText.setInputType(mText.getInputType() & ~EditorInfo.TYPE_TEXT_FLAG_AUTO_COMPLETE | EditorInfo.TYPE_TEXT_FLAG_AUTO_CORRECT);
        mText.setAdapter(mUsersListAdapter);
        mText.setTokenizer(new SymbolTokenizer('@'));
        mSendButton = (ImageButton) rootView.findViewById(R.id.send);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = mText.getText().toString();
                if (!mImagesPanel.isUploadFinished()) {
                    Toast.makeText(getActivity(), getString(R.string.toast_upload_not_finished), Toast.LENGTH_SHORT).show();
                    return;
                }
                StringBuilder sb = new StringBuilder(text);
                for (String l : mImagesPanel.getLinks()) {
                    sb.append("\n").append(l);
                }
                text = sb.toString().trim();

                if (TextUtils.isEmpty(text)) {
                    Toast.makeText(getActivity(), getString(R.string.toast_empty_comment), Toast.LENGTH_SHORT).show();
                    return;
                }

                String comment = mCommentId.getText().toString();
                mBottomBar.setEnabled(false);
                showDialog();
                if (TextUtils.isEmpty(comment)) {
                    PointConnectionManager.getInstance().pointIm.addComment(mPost, text, mCommentCallback);
                } else {
                    PointConnectionManager.getInstance().pointIm.addComment(mPost, text, comment, mCommentCallback);
                }
            }
        });

        mAdapter.setOnPointClickListener(mOnPointClickListener);
        mAdapter.setOnPostActionsListener(mOnPostActionsListener);
        mAdapter.setOnCommentClickListener(new SinglePostAdapter.OnCommentActionClickListener() {
            @Override
            public void onCommentClicked(View view, String commentId) {
                mCommentId.setText(commentId);
                mCommentId.setVisibility(View.VISIBLE);
            }

            @Override
            public void onRecommendCommentClicked(View view, String commentId) {
                final String cid = commentId;
                final MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                        .title(String.format(getString(R.string.dialog_recommend_comment_title_template), mPost, commentId))
                        .positiveText(android.R.string.ok)
                        .negativeText(android.R.string.cancel)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                super.onPositive(dialog);
                                String text = ((EditText) (dialog.findViewById(R.id.recommend_text))).getText().toString();
                                showDialog();
                                PointConnectionManager.getInstance().pointIm.recommendCommend(mPost, cid, text, mRecommendCallback);
                            }
                        })
                        .customView(R.layout.dialog_input, true)
                        .build();
                dialog.show();
            }

            @Override
            public void onPostClicked(View view) {
                mCommentId.setVisibility(View.GONE);
                mCommentId.setText("");
            }
        });

        mImagesPanel = (ImageUploadingPanel) rootView.findViewById(R.id.imagesPanel);
        mAttachButton = (ImageButton) rootView.findViewById(R.id.attach);
        mAttachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, RESULT_LOAD_IMAGE);
            }
        });

        UserSubscriptionsRequest request2 = new UserSubscriptionsRequest(PointConnectionManager.getInstance().loginResult.login);
        getSpiceManager().getFromCacheAndLoadFromNetworkIfExpired(request2, request2.getCacheName(), DurationInMillis.ONE_DAY, mUsersRequestListener);
        return rootView;
    }

    protected SinglePostRequest createRequest() {
        return new SinglePostRequest(mPost);
    }

    protected void update() {
        SinglePostRequest request = createRequest();
        getSpiceManager().execute(request, request.getCacheName(), DurationInMillis.ALWAYS_EXPIRED, mUpdateRequestListener);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.setGroupVisible(R.id.group_my,
                mPointPost != null &&
                        mPointPost.post.author.login.equalsIgnoreCase(PointConnectionManager.getInstance().loginResult.login)
        );
        menu.setGroupVisible(R.id.group_my_editable,
                mPointPost != null &&
                        mPointPost.post.author.login.equalsIgnoreCase(PointConnectionManager.getInstance().loginResult.login)
                // mPointPost.editable //FIXME
        );
        menu.setGroupVisible(R.id.group_not_recommended,
                mPointPost != null &&
                        !mPointPost.post.author.login.equalsIgnoreCase(PointConnectionManager.getInstance().loginResult.login) &&
                        !mPointPost.recommended
        );

        menu.setGroupVisible(R.id.group_loaded, mPointPost != null);
        //share intent
        if (mPointPost != null) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            StringBuilder sb = new StringBuilder();
            sb.append("@")
                    .append(mPointPost.post.author.login)
                    .append(":");
            if (mPointPost.post.tags != null)
                for (String tag : mPointPost.post.tags) {
                    sb.append(" *").append(tag);
                }
            sb.append("\n\n")
                    .append(mPointPost.post.text.text)
                    .append("\n\n")
                    .append(Utils.generateSiteUri(mPost));
            sendIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
            mShareActionProvider.setShareIntent(sendIntent);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_single_post, menu);

        MenuItem item = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = new ShareActionProvider(getActivity());
        MenuItemCompat.setActionProvider(item, mShareActionProvider);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            mSwipeRefresh.setRefreshing(true);
            update();
            return true;
        } else if (id == R.id.action_recommend) {
            final MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                    .title(String.format(getString(R.string.dialog_recommend_title_template), mPost))
                    .positiveText(android.R.string.ok)
                    .negativeText("Cancel")
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            super.onPositive(dialog);
                            String text = ((EditText) (dialog.findViewById(R.id.recommend_text))).getText().toString();
                            showDialog();
                            PointConnectionManager.getInstance().pointIm.recommend(mPost, text, mRecommendCallback);
                        }
                    })
                    .customView(R.layout.dialog_input, true)
                    .build();
            dialog.show();
            return true;
        } else if (id == R.id.action_delete) {
            final MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                    .title(String.format(getString(R.string.dialog_delete_title_template), mPost))
                    .positiveText(android.R.string.ok)
                    .negativeText(android.R.string.cancel)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            super.onPositive(dialog);
                            showDialog();
                            PointConnectionManager.getInstance().pointIm.deletePost(mPost, mDeleteCallback);
                        }
                    })
                    .build();
            dialog.show();
            return true;
        } else if (id == R.id.action_edit) {
            Intent intent = new Intent(getActivity(), NewPostActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(NewPostActivity.EXTRA_ID, mPost);
            bundle.putBoolean(NewPostActivity.EXTRA_PRIVATE, mPointPost.post.isPrivate);
            bundle.putString(NewPostActivity.EXTRA_TEXT, mPointPost.post.text.text.toString());
            bundle.putStringArray(NewPostActivity.EXTRA_TAGS, mPointPost.post.tags.toArray(new String[mPointPost.post.tags.size()]));
            intent.putExtras(bundle);
            getActivity().startActivity(intent);
        } else {
            mOnPostActionsListener.onMenuClicked(mPointPost.post, null, item);//// FIXME: 02.05.2016 
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
            mImagesPanel.addImage(data.getData(), data.getType());
        }
    }


    private RequestListener<UserList> mUsersRequestListener = new RequestListener<UserList>() {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            //
        }

        @Override
        public void onRequestSuccess(UserList users) {
            Log.d("SinglePostFragment", "users: " + users);
            if (users != null) {
                mUsersListAdapter.setData(users);
                addAuthorsToCompletion();
                mUsersListAdapter.notifyDataSetChanged();
            }
        }
    };

    private void addAuthorsToCompletion() {
        if (mPointPost == null)
            return;
        mUsersListAdapter.addIfAbsent(mPointPost.post.author);
        for (Comment c : mPointPost.comments) {
            mUsersListAdapter.addIfAbsent(c.author);
        }
    }
}
