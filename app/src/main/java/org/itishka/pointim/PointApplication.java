package org.itishka.pointim;

import android.app.Application;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;

import org.itishka.pointim.network.ImgurConnectionManager;
import org.itishka.pointim.network.PointConnectionManager;
import org.itishka.pointim.utils.ImageSearchHelper;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Tishka17 on 03.08.2015.
 */
public class PointApplication extends Application {
    private OkHttpClient mOkHttpClient;
    String USER_AGENT = "Tishka17 Point.im Client";
    private static final int MAX_CACHE_SIZE = 50 * 1024 * 1024; //50 MiB

    @Override
    public void onCreate() {
        super.onCreate();
//        mCache = WaterfallCache.builder()
//                .addMemoryCache(1000)
//                .addDiskCache(this, MAX_CACHE_SIZE)
//                .build();

        mOkHttpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    request = request.newBuilder()
                            .header("User-Agent", USER_AGENT)
                            .build();
                    return chain.proceed(request);
                })
                .cache(new Cache(getExternalCacheDir(), MAX_CACHE_SIZE))
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Glide
                .get(this)
                .register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(mOkHttpClient));
        ImageSearchHelper.initCache(this);
        PointConnectionManager.getInstance().init(this);
        ImgurConnectionManager.getInstance().init(this);
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

//    public WaterfallCache getCache() {
//        return mCache;
//    }
}
