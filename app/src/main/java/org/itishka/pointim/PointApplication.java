package org.itishka.pointim;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.squareup.okhttp.OkHttpClient;

import org.itishka.pointim.network.ImgurConnectionManager;
import org.itishka.pointim.network.PointConnectionManager;
import org.itishka.pointim.utils.ImageSearchHelper;

/**
 * Created by Tishka17 on 03.08.2015.
 */
public class PointApplication extends Application {
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
    }
}
