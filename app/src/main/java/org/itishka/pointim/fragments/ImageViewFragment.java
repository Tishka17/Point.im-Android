package org.itishka.pointim.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.davemorrissey.labs.subscaleview.decoder.DecoderFactory;
import com.davemorrissey.labs.subscaleview.decoder.ImageDecoder;
import com.davemorrissey.labs.subscaleview.decoder.ImageRegionDecoder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.Picasso;

import org.itishka.pointim.PointApplication;
import org.itishka.pointim.R;
import org.itishka.pointim.utils.PicassoDecoder;
import org.itishka.pointim.utils.PicassoRegionDecoder;

/**
 * A placeholder fragment containing a simple view.
 */
public class ImageViewFragment extends SpicedFragment {

    private static final String ARG_URL = "url";
    private String mUrl;
    private SubsamplingScaleImageView mImageView;
    private Picasso mPicasso;

    public ImageViewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_view, container, false);
    }

    @Override
    public void onDestroyView() {
        mPicasso.cancelTag(mUrl);
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mImageView = (SubsamplingScaleImageView) view.findViewById(R.id.imageView);

        final OkHttpClient client = ((PointApplication) mImageView.getContext().getApplicationContext()).getOkHttpClient();
        mPicasso =((PointApplication) mImageView.getContext().getApplicationContext()).getPicasso();

        mImageView.setBitmapDecoderFactory(new DecoderFactory<ImageDecoder>() {
            public ImageDecoder make() {
                return new PicassoDecoder(mUrl, mPicasso);
            }
        });
        mImageView.setRegionDecoderFactory(new DecoderFactory<ImageRegionDecoder>() {
            @Override
            public ImageRegionDecoder make() throws IllegalAccessException, java.lang.InstantiationException {
                return new PicassoRegionDecoder(client);
            }
        });
        mImageView.setOrientation(SubsamplingScaleImageView.ORIENTATION_USE_EXIF);
        mImageView.setImage(ImageSource.uri(mUrl));
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
