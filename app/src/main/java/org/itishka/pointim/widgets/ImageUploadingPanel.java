package org.itishka.pointim.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.itishka.pointim.ImgurUploadTask;
import org.itishka.pointim.R;
import org.itishka.pointim.api.data.ImgurUploadResult;

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
        ImageButton cancel;
        ImageView viewFinished;
        TextView progress;

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

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancel();
    }

    public void addImage(Uri uri) {
        final Image img = new Image();
        final View view = inflate(getContext(), R.layout.image_uploading_panel_item, null);
        img.imageView = (ImageView) view.findViewById(R.id.imageView);
        img.viewFinished = (ImageView) view.findViewById(R.id.viewFinished);
        img.viewFinished.setVisibility(GONE);
        img.progress = (TextView) view.findViewById(R.id.imageProgress);
        img.cancel = (ImageButton) view.findViewById(R.id.action_cancel);
        img.cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (img.task!=null && !img.task.isCancelled()) {
                    img.task.cancel(true);
                }
                ImageUploadingPanel.this.mLayout.removeView(view);
                mImages.remove(img);
            }
        });
        img.imageView.setImageURI(uri);
        Picasso.with(getContext())
                .load(uri)
                .transform(new CropSquareTransformation())
                .fit()
                .into(img.imageView);
        mLayout.addView(view);
        img.originalPath = uri;
        mImages.add(img);

        img.task = new ImgurUploadTask(getContext(), uri) {
            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                img.progress.setText(values[0]+"%");
            }

            @Override
            protected void onPostExecute(ImgurUploadResult result) {
                super.onPostExecute(result);
                if (result!=null && result.success) {
                    img.viewFinished.setVisibility(VISIBLE);
                    img.progress.setVisibility(GONE);
                } else {
                    //Toast.makeText(getActivity(), "Error uploading photo", Toast.LENGTH_SHORT).show();
                }
            }
        };
        img.task.execute();
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

    public boolean isUploadFinished() {
        for (Image i: mImages) {
            if (!i.uploaded) return false;
        }
        return true;
    }

    public void reset() {
        cancel();
        mImages.clear();
        mLayout.removeAllViews();
    }

    private class CropSquareTransformation implements Transformation {
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
