package org.itishka.pointim.network;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.itishka.pointim.PointApplication;
import org.itishka.pointim.model.point.LoginResult;
import org.itishka.pointim.model.point.TextWithImages;
import org.itishka.pointim.utils.DateDeserializer;
import org.itishka.pointim.utils.TextParser;

import java.io.IOException;
import java.util.Date;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Tishka17 on 21.10.2014.
 */
public class PointConnectionManager extends ConnectionManager {
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String ENDPOINT = "https://point.im";
    private static final String PREFERENCE = "PointConnectionManager";
    private static final PointConnectionManager sInstance = new PointConnectionManager();
    private final Gson mGson = new GsonBuilder()
            .setDateFormat(DATE_FORMAT)
            .registerTypeAdapter(Date.class, new DateDeserializer())
            .registerTypeAdapter(TextWithImages.class, new TextParser())
            .create();
    public PointIm pointIm = null;
    public PointImAuth pointAuthService = null;
    public LoginResult loginResult = null;

    private PointConnectionManager() {
    }

    public static PointConnectionManager getInstance() {
        return sInstance;
    }

    @Override
    protected Gson getGson() {
        return mGson;
    }

    @Override
    public void updateAuthorization(Context context, Object loginResult) {
        synchronized (this) {
            this.loginResult = (LoginResult) loginResult;
            saveAuthorization(context, PREFERENCE, loginResult);
            createService();
        }
    }

    @Override
    public void init(PointApplication application) {
        super.init(application);
        pointAuthService = new Retrofit.Builder()
                .client(application.getOkHttpClient())
                .baseUrl(ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create(mGson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
                .create(PointImAuth.class);

        synchronized (this) {
            if (this.loginResult == null) {
                loginResult = loadAuthorization(application, PREFERENCE, LoginResult.class);
                createService();
            }
        }
    }

    @Override
    protected void createService() {
        OkHttpClient httpClient = getApplication().getOkHttpClient()
                .newBuilder()
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    request = request.newBuilder()
                            .header("Authorization", loginResult.token)
                            .header("X-CSRF", loginResult.csrf_token)
                            .build();
                    return chain.proceed(request);
                })
                .build();
        pointIm = new Retrofit.Builder()
                .client(httpClient)
                .baseUrl(ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create(mGson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
                .create(PointIm.class);
    }

    @Override
    synchronized public boolean isAuthorized() {
        return loginResult != null && !TextUtils.isEmpty(loginResult.csrf_token);
    }

    @Override
    public void resetAuthorization(Context context) {
        synchronized (this) {
            loginResult = null;
            saveAuthorization(context, PREFERENCE, loginResult);
            init((PointApplication) context.getApplicationContext());
        }
    }
}
