package org.itishka.pointim;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

import org.itishka.pointim.api.ConnectionManager;
import org.itishka.pointim.utils.ImageSearchHelper;

/**
 * Created by Tishka17 on 03.08.2015.
 */
public class PointApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ImagePipelineConfig config = OkHttpImagePipelineConfigFactory
                .newBuilder(this, ConnectionManager.getInstance().okHttpClient)
                .build();
        Fresco.initialize(this, config);
        ImageSearchHelper.initCache(this);
    }
}
