package org.itishka.pointim.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;

import com.davemorrissey.labs.subscaleview.decoder.ImageRegionDecoder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;

import java.io.InputStream;

/**
 * Created by Tishka17 on 15.03.2016.
 */
public class PicassoRegionDecoder implements ImageRegionDecoder {
    private OkHttpClient client;
    private BitmapRegionDecoder decoder;
    private final Object decoderLock = new Object();

    public PicassoRegionDecoder(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public Point init(Context context, Uri uri) throws Exception {
        OkHttpDownloader downloader = new OkHttpDownloader(client);
        InputStream inputStream = downloader.load(uri, 0).getInputStream();
        this.decoder = BitmapRegionDecoder.newInstance(inputStream, false);

        return new Point(this.decoder.getWidth(), this.decoder.getHeight());
    }

    @Override
    public Bitmap decodeRegion(Rect rect, int sampleSize) {
        synchronized (this.decoderLock) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = sampleSize;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            Bitmap bitmap = this.decoder.decodeRegion(rect, options);
            if (bitmap == null) {
                throw new RuntimeException("Region decoder returned null bitmap - image format may not be supported");
            } else {
                return bitmap;
            }
        }
    }

    @Override
    public boolean isReady() {
        return this.decoder != null && !this.decoder.isRecycled();
    }

    @Override
    public void recycle() {
        this.decoder.recycle();
    }
}