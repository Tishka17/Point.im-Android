package org.itishka.pointim.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import org.itishka.pointim.R;
import org.itishka.pointim.activities.ToolbarActivity;
import org.itishka.pointim.widgets.HideAnimationHelper;

/**
 * A placeholder fragment containing a simple view.
 */
public class ImageViewFragment extends SpicedFragment {

    private static final String ARG_URL = "url";
    private String mUrl;
    private SubsamplingScaleImageView mImageView;
    private HideAnimationHelper mHideAnimationHelper = null;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mHideAnimationHelper = new HideAnimationHelper(((ToolbarActivity) getActivity()).getToolbar());
    }


    public ImageViewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_view, container, false);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (this.isVisible()) {
            if (!isVisibleToUser) {
                mImageView.resetScaleAndCenter();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mImageView = (SubsamplingScaleImageView) view.findViewById(R.id.imageView);
        mImageView.setOrientation(SubsamplingScaleImageView.ORIENTATION_USE_EXIF);
        Glide.with(this)
                .load(mUrl)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                        mImageView.setImage(ImageSource.bitmap(bitmap));
                    }
                });


        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHideAnimationHelper.toggleView();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUrl = getArguments().getString(ARG_URL);
        }
        setHasOptionsMenu(true);
    }

    public static Fragment newInstance(String url) {
        ImageViewFragment fragment = new ImageViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_URL, url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_image_view, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.weblink) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUrl));
            startActivity(browserIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
