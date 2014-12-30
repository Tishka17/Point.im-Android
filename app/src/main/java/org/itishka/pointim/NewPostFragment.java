package org.itishka.pointim;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
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
import org.itishka.pointim.api.data.ImgurBaseResponse;
import org.itishka.pointim.api.data.ImgurImage;
import org.itishka.pointim.api.data.PointResult;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class NewPostFragment extends Fragment {

    private static final int RESULT_LOAD_IMAGE = 17;
    private AlertDialog mProgressDialog;
    private String mImagePath = null;
    private String mImageMime;

    public NewPostFragment() {
    }

    EditText mPostText;
    EditText mPostTags;
    ImageView mImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_post, container, false);
        mPostText = (EditText) rootView.findViewById(R.id.postText);
        mPostTags = (EditText) rootView.findViewById(R.id.postTags);
        mImageView = (ImageView) rootView.findViewById(R.id.imageView);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mImageView.setVisibility(View.GONE);
                mImagePath = null;
            }
        });
        setHasOptionsMenu(true);

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
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.MIME_TYPE};


            Bitmap bitmap = null;
            Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            mImagePath = null;
            mImageMime = cursor.getString(cursor.getColumnIndex(filePathColumn[1]));
            cursor.close();

            File outputDir = getActivity().getCacheDir(); // context being the Activity pointer
            File outputFile = new File(outputDir, "UPLOADING.jpg");
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                outputFile.createNewFile();
                FileOutputStream fos = new FileOutputStream(outputFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.close();
                mImagePath = outputFile.getAbsolutePath();
                mImageView.setImageURI(selectedImage);
                mImageView.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.send) {
            final String text = mPostText.getText().toString();
            final String[] tags = mPostTags.getText().toString().split("\\s*,\\s*");
            if (mImagePath != null) {

                final MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                        .cancelable(false)
                        .customView(R.layout.dialog_progress, false)
                        .build();
                dialog.show();
                final ProgressBar progressBar = (ProgressBar) dialog.findViewById(R.id.google_progress);
                new ImgurUploadTask(mImagePath, mImageMime) {
                    @Override
                    protected void onProgressUpdate(Integer... values) {
                        super.onProgressUpdate(values);
                        progressBar.setProgress(values[0]);
                    }

                    @Override
                    protected void onPostExecute(ImgurImage imgurImage) {
                        super.onPostExecute(imgurImage);
                        dialog.hide();
                        mProgressDialog.show();
                        String newText = String.format("%s\n%s", text, imgurImage.link);
                        ConnectionManager.getInstance().pointService.createPost(newText, tags, mNewPostCallback);
                    }
                }.execute();
            } else {
                mProgressDialog.show();
                ConnectionManager.getInstance().pointService.createPost(text, tags, mNewPostCallback);
            }
            return true;
        } else if (id == R.id.attach) {
            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
