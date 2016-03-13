package org.itishka.pointim.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.itishka.pointim.R;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * A placeholder fragment containing a simple view.
 */
public class ImageViewFragment extends SpicedFragment {

    private static final String ARG_INDEX = "index";
    private static final String ARG_URL = "url";
    private int mIndex;
    private String mUrl;
    private ImageView mImageView;
    private PhotoViewAttacher mAttacher;

    public ImageViewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_view, container, false);
    }

    @Override
    public void onDestroyView() {
        mImageView.setImageDrawable(null);
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mImageView = (ImageView) view.findViewById(R.id.imageView);
        mAttacher = new PhotoViewAttacher(mImageView);
        Picasso.with(getActivity())
                .load(mUrl)
                .into(mImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        mAttacher.update();
                    }

                    @Override
                    public void onError() {
                        mAttacher.update();
                    }
                });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mIndex = getArguments().getInt(ARG_INDEX);
            mUrl = getArguments().getString(ARG_URL);
        }
    }

    public static Fragment newInstance(int index, String url) {
        ImageViewFragment fragment = new ImageViewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_INDEX, index);
        args.putString(ARG_URL, url);
        fragment.setArguments(args);
        return fragment;
    }
}
