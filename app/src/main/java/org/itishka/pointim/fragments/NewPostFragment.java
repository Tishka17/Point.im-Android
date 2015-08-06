package org.itishka.pointim.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Switch;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.itishka.pointim.R;
import org.itishka.pointim.api.ConnectionManager;
import org.itishka.pointim.model.NewPostResponse;
import org.itishka.pointim.model.Tag;
import org.itishka.pointim.model.TagList;
import org.itishka.pointim.network.requests.TagsRequest;
import org.itishka.pointim.widgets.ImageUploadingPanel;
import org.itishka.pointim.widgets.SymbolTokenizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class NewPostFragment extends SpicedFragment {

    private static final int RESULT_LOAD_IMAGE = 17;
    private static final String ARG_TEXT = "text";
    private static final String ARG_IMAGES = "images";
    private static final String ARG_ID = "id";
    private static final String ARG_TAGS = "tags";
    private static final String ARG_MIME = "mime";
    private MultiAutoCompleteTextView mPostText;
    private Switch mIsPrivate;
    private String mPostId;
    private String mMime;
    private MultiAutoCompleteTextView mPostTags;
    private MaterialDialog mProgressDialog;
    private ArrayAdapter<String> mUsersListAdapter;
    private ArrayAdapter<Tag> mTagsListAdapter;
    private List<String> mUsers = Arrays.asList(new String[]{"tishka17", "arts"});
    private ImageUploadingPanel mImagesPanel;
    private Callback<NewPostResponse> mNewPostCallback = new Callback<NewPostResponse>() {
        @Override
        public void success(NewPostResponse post, Response response) {
            mProgressDialog.hide();
            if (post.isSuccess()) {
                if (getActivity().getCallingActivity() != null) {
                    Intent intent = new Intent();
                    intent.putExtra("post", post.id);
                    getActivity().setResult(Activity.RESULT_OK, intent);
                } else {
                    Toast.makeText(getActivity(), String.format("Post #%s sent", post.id), Toast.LENGTH_SHORT).show();
                }
                getActivity().finish();
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

    public NewPostFragment() {
    }

    public static NewPostFragment newInstance(String text) {
        NewPostFragment fragment = new NewPostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }

    public static NewPostFragment newInstance(ArrayList<Parcelable> images, String mime) {
        NewPostFragment fragment = new NewPostFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_IMAGES, images);
        args.putString(ARG_MIME, mime);
        fragment.setArguments(args);
        return fragment;
    }

    public static NewPostFragment newInstance(Uri image, String mime) {
        ArrayList<Parcelable> images = new ArrayList<>(1);
        images.add(image);
        return newInstance(images, mime);
    }

    public static NewPostFragment newInstanceForEdit(String id, String text, String[] tags) {
        NewPostFragment fragment = new NewPostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID, id);
        args.putString(ARG_TEXT, text);
        args.putStringArray(ARG_TAGS, tags);
        fragment.setArguments(args);
        return fragment;
    }

    public static NewPostFragment newInstance() {
        return new NewPostFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_post, container, false);
        mPostText = (MultiAutoCompleteTextView) rootView.findViewById(R.id.postText);
        mUsersListAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line);
        mPostText.setAdapter(mUsersListAdapter);
        mPostText.setTokenizer(new SymbolTokenizer('@'));
        mUsersListAdapter.addAll(mUsers);
        mIsPrivate = (Switch) rootView.findViewById(R.id.isPrivate);
        mTagsListAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line);
        mPostTags = (MultiAutoCompleteTextView) rootView.findViewById(R.id.postTags);
        mPostTags.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        mPostTags.setAdapter(mTagsListAdapter);
        mImagesPanel = (ImageUploadingPanel) rootView.findViewById(R.id.imagesPanel);
        setHasOptionsMenu(true);
        if (savedInstanceState == null) {
            Bundle args = getArguments();
            if (args != null) {
                mPostId = args.getString(ARG_ID);
                mPostText.setText(args.getString(ARG_TEXT, ""));
                String[] tags = args.getStringArray(ARG_TAGS);
                if (tags != null) {
                    mPostTags.setText(TextUtils.join(", ", tags));
                }
                ArrayList<Uri> images = args.getParcelableArrayList(ARG_IMAGES);
                if (images != null) for (Uri image : images) {
                    mImagesPanel.addImage(image, mMime);
                }
                mMime = args.getString(ARG_MIME);
            }
        }
        if (mPostId == null) {
            mIsPrivate.setVisibility(View.VISIBLE);
        } else {
            mIsPrivate.setVisibility(View.GONE);
        }
        mProgressDialog = new MaterialDialog.Builder(getActivity())
                .cancelable(false)
                .customView(R.layout.dialog_progress, false)
                .build();

        TagsRequest request = new TagsRequest(ConnectionManager.getInstance().loginResult.login);
        //getSpiceManager().getFromCache(TagList.class, request.getCacheName(), DurationInMillis.ALWAYS_RETURNED, mRequestListener);
        getSpiceManager().getFromCacheAndLoadFromNetworkIfExpired(request, request.getCacheName(), DurationInMillis.ONE_DAY, mRequestListener);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_new_post, menu);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
            mImagesPanel.addImage(data.getData(), data.getType());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.send) {
            if (!mImagesPanel.isUploadFinished()) {
                Toast.makeText(getActivity(), "Wait or check for errors!", Toast.LENGTH_SHORT).show();
                return true;
            }
            final String text = mPostText.getText().toString();
            final String[] tags = mPostTags.getText().toString().split("\\s*,\\s*");
            StringBuilder sb = new StringBuilder(text);
            for (String l : mImagesPanel.getLinks()) {
                sb.append("\n").append(l);
            }
            mProgressDialog.show();
            if (TextUtils.isEmpty(mPostId)) {
                if (mIsPrivate.isChecked())
                    ConnectionManager.getInstance().pointIm.createPrivatePost(sb.toString().trim(), tags, mIsPrivate.isChecked(), mNewPostCallback);
                else
                    ConnectionManager.getInstance().pointIm.createPost(sb.toString().trim(), tags, mNewPostCallback);
            } else {
                ConnectionManager.getInstance().pointIm.editPost(mPostId, sb.toString().trim(), tags, mNewPostCallback);
            }
            return true;
        } else if (id == R.id.attach) {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_LOAD_IMAGE);
        }
        return super.onOptionsItemSelected(item);
    }

    private RequestListener<TagList> mRequestListener = new RequestListener<TagList>() {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            //
        }

        @Override
        public void onRequestSuccess(TagList tags) {
            Log.d("TAG", "tags: "+tags);
            if (tags != null) {
                mTagsListAdapter.clear();
                mTagsListAdapter.addAll(tags);
                mTagsListAdapter.notifyDataSetChanged();
            }
        }
    };
}
