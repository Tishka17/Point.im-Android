package org.itishka.pointim;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.squareup.okhttp.Cache;
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
    private OkHttpClient mOkHttpClient;
    private static final int MAX_CACHE_SIZE = 50*1024*1024; //50 MiB

    @Override
    public void onCreate() {
        super.onCreate();
        mOkHttpClient = new OkHttpClient();
        mOkHttpClient.setCache(new Cache(getCacheDir(), MAX_CACHE_SIZE));


        ImagePipelineConfig config = OkHttpImagePipelineConfigFactory
                .newBuilder(this, mOkHttpClient)
                .build();
        Fresco.initialize(this, config);
        ImageSearchHelper.initCache(this);
        PointConnectionManager.getInstance().init(this);
        ImgurConnectionManager.getInstance().init(this);

        Picasso picasso = new Picasso.Builder(this)
                .downloader(new OkHttpDownloader(mOkHttpClient))
                .build();
        Picasso.setSingletonInstance(picasso);
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }
}
