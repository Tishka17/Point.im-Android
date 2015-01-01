package org.itishka.pointim;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import org.itishka.pointim.api.ConnectionManager;
import org.itishka.pointim.api.data.ImgurImage;
import org.itishka.pointim.api.data.ImgurUploadResult;
import org.itishka.pointim.api.data.PointResult;
import org.itishka.pointim.widgets.ImageUploadingPanel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class NewPostFragment extends Fragment {

    private static final int RESULT_LOAD_IMAGE = 17;
    private static final String ARG_TEXT = "text";
    private static final String ARG_IMAGES = "images";
    private AlertDialog mProgressDialog;

    public NewPostFragment() {
    }

    public static NewPostFragment newInstance(String text) {
        NewPostFragment fragment = new NewPostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }

    public static NewPostFragment newInstance(ArrayList<Parcelable> images) {
        NewPostFragment fragment = new NewPostFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_IMAGES, images);
        fragment.setArguments(args);
        return fragment;
    }

    public static NewPostFragment newInstance(Uri image) {
        ArrayList<Parcelable> images = new ArrayList<>(1);
        images.add(image);
        return newInstance(images);
    }

    public static NewPostFragment newInstance() {
        return new NewPostFragment();
    }

    EditText mPostText;
    EditText mPostTags;
    private ImageUploadingPanel mImagesPanel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_post, container, false);
        mPostText = (EditText) rootView.findViewById(R.id.postText);
        mPostTags = (EditText) rootView.findViewById(R.id.postTags);
        mImagesPanel = (ImageUploadingPanel) rootView.findViewById(R.id.imagesPanel);
        setHasOptionsMenu(true);
        if (savedInstanceState==null) {
            Bundle args = getArguments();
            if (args!=null) {
                mPostText.setText(args.getString(ARG_TEXT, ""));
                ArrayList<Uri> images = args.getParcelableArrayList(ARG_IMAGES);
                if (images != null) for (Uri image : images) {
                    mImagesPanel.addImage(image);
                }
            }
        }

        mProgressDialog = new MaterialDialog.Builder(getActivity())
                .cancelable(false)
                .customView(R.layout.dialog_progress, false)
                .build();
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
            mImagesPanel.addImage(data.getData());
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
            StringBuffer sb = new StringBuffer(text);
            for (String l : mImagesPanel.getLinks()) {
                sb.append("\n").append(l);
            }
            mProgressDialog.show();
            ConnectionManager.getInstance().pointService.createPost(sb.toString().trim(), tags, mNewPostCallback);
            return true;
        } else if (id == R.id.attach) {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_LOAD_IMAGE);
        }
        return super.onOptionsItemSelected(item);
    }

    private Callback<PointResult> mNewPostCallback = new Callback<PointResult>() {
        @Override
        public void success(PointResult post, Response response) {
            mProgressDialog.hide();
            if (post.isSuccess()) {
                Toast.makeText(getActivity(), "Post sent!", Toast.LENGTH_SHORT).show();
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
}
