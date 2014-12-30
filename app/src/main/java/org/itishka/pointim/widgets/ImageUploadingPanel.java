package org.itishka.pointim.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.itishka.pointim.ImgurUploadTask;
import org.itishka.pointim.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tishka17 on 31.12.2014.
 */
public class ImageUploadingPanel extends FrameLayout {

    private class Image {
        Uri originalPath;
        String link;
        ImageView imageView;
        boolean uploaded = false;
        ImgurUploadTask task = null;
    }
    private ViewGroup mLayout;
    private ArrayList<Image> mImages = new ArrayList<>(1);
    public ImageUploadingPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.image_uploading_panel, this);
        mLayout = (ViewGroup) findViewById(R.id.contentView);
    }

    public ImageUploadingPanel(Context context) {
        super(context);
        init();
    }
    public ImageUploadingPanel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void addImage(Uri uri) {
        Image img = new Image();
        View view = inflate(getContext(), R.layout.image_uploading_panel_item, null);
        img.imageView = (ImageView) view.findViewById(R.id.imageView);
        img.imageView.setImageURI(uri);
        Picasso.with(getContext())
                .load(uri)
                .transform(new CropSquareTransformation())
                .into(img.imageView);
        mLayout.addView(view);
        img.originalPath = uri;
        mImages.add(img);
    }

    public List<String> getLinks() {
        List<String> links = new ArrayList<>(mImages.size());
        for (Image i: mImages) {
            links.add(i.link);
        }
        return links;
    }

    public void cancel() {
        for (Image i: mImages) {
            if (i.task!=null && !i.task.isCancelled()) {
                i.task.cancel(true);
            }
        }
    }

    public void reset() {
        cancel();
        mImages.clear();
        mLayout.removeAllViews();
    }

    public class CropSquareTransformation implements Transformation {
        @Override public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());
            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;
            Bitmap result = Bitmap.createBitmap(source, x, y, size, size);
            if (result != source) {
                source.recycle();
            }
            return result;
        }

        @Override public String key() { return "square()"; }
    }
}
