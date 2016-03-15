package org.itishka.pointim;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import org.itishka.pointim.network.ImgurConnectionManager;
import org.itishka.pointim.network.PointConnectionManager;
import org.itishka.pointim.utils.ImageSearchHelper;

/**
 * Created by Tishka17 on 03.08.2015.
 */
public class PointApplication extends Application {
    Picasso mPicasso;
    private OkHttpClient mOkHttpClient;

    @Override
    public void onCreate() {
        super.onCreate();
        ImagePipelineConfig config = OkHttpImagePipelineConfigFactory
                .newBuilder(this, new OkHttpClient())
                .build();
        Fresco.initialize(this, config);
        ImageSearchHelper.initCache(this);
        PointConnectionManager.getInstance().init(this);
        ImgurConnectionManager.getInstance().init(this);
        mOkHttpClient = new OkHttpClient();
        mPicasso = new Picasso.Builder(this)
                .downloader(new OkHttpDownloader(mOkHttpClient))
                .build();
    }

    public Picasso getPicasso() {
        return mPicasso;
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }
}
