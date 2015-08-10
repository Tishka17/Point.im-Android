package org.itishka.pointim.widgets;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.itishka.pointim.R;

import java.util.List;

/**
 * Created by Tishka17 on 01.01.2015.
 */
public class ImageList extends FrameLayout {

    SharedPreferences mPreferences;

    private static final int[] sImageIds = new int[]{
            R.id.imageView0,
            R.id.imageView1,
            R.id.imageView2,
            R.id.imageView3,
            R.id.imageView4,
            R.id.imageView5,
            R.id.imageView6,
            R.id.imageView7,
            R.id.imageView8,
            R.id.imageView9
    };
    private final Transformation transformation = new Transformation() {

        @Override
        public Bitmap transform(Bitmap source) {

            int targetHeight = mImageViews[0].getHeight();

            double aspectRatio = (double) source.getWidth() / (double) source.getHeight();
            int targetWidth = (int) (targetHeight * aspectRatio);
            if (targetWidth == 0 || targetHeight == 0)
                return source;
            Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
            if (result != source) {
                // Same bitmap is returned if sizes are the same
                source.recycle();
            }
            return result;
        }

        @Override
        public String key() {
            return "transformation" + " desiredWidth";
        }
    };
    private final ImageView[] mImageViews = new ImageView[sImageIds.length];
    private final OnClickListener imageClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse((String) view.getTag()));
            getContext().startActivity(browserIntent);
        }
    };

    public ImageList(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ImageList(Context context) {
        super(context);
        init();
    }

    public ImageList(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mPreferences = getContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        inflate(getContext(), R.layout.image_list, this);
        for (int i = 0; i < sImageIds.length; i++) {
            mImageViews[i] = (ImageView) findViewById(sImageIds[i]);
            mImageViews[i].setVisibility(GONE);
            mImageViews[i].setOnClickListener(imageClickListener);
        }
    }

    public void setImageUrls(List<String> urls, List<String> files) {
        if (!mPreferences.getBoolean("loadImages", true)) {
            for (int i = 0; i < sImageIds.length; i++)
                mImageViews[i].setVisibility(GONE);
            return;
        }
        int urlCount = urls == null ? 0 : urls.size();
        int fileCount = files == null ? 0 : files.size();
        for (int i = 0; i < sImageIds.length; i++) {
            String url = null;
            if (i < urlCount) {
                url = urls.get(i);
            } else if (i - urlCount < fileCount) {
                url = files.get(i - urlCount);
            }
            if (url != null) {
                mImageViews[i].setVisibility(VISIBLE);
                mImageViews[i].setTag(url);
                Picasso.with(getContext())
                        .load(url)
                        .transform(transformation)
                        .into(mImageViews[i]);
            } else {
                mImageViews[i].setVisibility(GONE);
            }
        }
    }
}
