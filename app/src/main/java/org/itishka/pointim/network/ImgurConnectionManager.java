package org.itishka.pointim.network;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.itishka.pointim.BuildConfig;
import org.itishka.pointim.PointApplication;
import org.itishka.pointim.model.imgur.Token;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by Tishka17 on 21.10.2014.
 */
public class ImgurConnectionManager extends ConnectionManager {
    private static final String PREFERENCE = "ImgurConnectionManager";

    public static final String IMGUR_ENDPOINT = "https://api.imgur.com/3/";
    public static final String IMGUR_AUTH_ENDPOINT = "https://api.imgur.com/oauth2/";

    private static final ImgurConnectionManager sInstance = new ImgurConnectionManager();
    private final Gson mGson = new GsonBuilder().create();

    public Token token = null;
    public Retrofit imgurService = null;
    public Retrofit imgurAuthService = null;

    private ImgurConnectionManager() {

    }

    public static ImgurConnectionManager getInstance() {
        return sInstance;
    }

    @Override
    protected Gson getGson() {
        return mGson;
    }

    @Override
    public void updateAuthorization(Context context, Object token) {
        synchronized (this) {
            this.token = (Token) token;
            saveAuthorization(context, PREFERENCE, token);
            createService();
        }
    }

    @Override
    public void init(PointApplication application) {
        super.init(application);
        OkHttpClient httpClient = getApplication().getOkHttpClient()
                .newBuilder()
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    request = request.newBuilder()
                            .header("Authorization", "Client-ID " + BuildConfig.IMGUR_ID)
                            .build();
                    return chain.proceed(request);
                })
                .build();

        imgurAuthService = new Retrofit.Builder()
                .client(httpClient)
                .baseUrl(IMGUR_AUTH_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create(mGson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        synchronized (this) {
            if (this.token == null) {
                token = loadAuthorization(application, PREFERENCE, Token.class);
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
                            .header("Authorization",
                                    token == null ? "Client-ID " + BuildConfig.IMGUR_ID : "Bearer " + token.access_token)
                            .build();
                    return chain.proceed(request);
                })
                .build();

        imgurService = new Retrofit.Builder()
                .client(httpClient)
                .baseUrl(IMGUR_AUTH_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create(mGson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    @Override
    synchronized public boolean isAuthorized() {
        return token != null && !TextUtils.isEmpty(token.access_token);
    }

    @Override
    public void resetAuthorization(Context context) {
        synchronized (this) {
            token = null;
            saveAuthorization(context, PREFERENCE, token);
            init((PointApplication) context.getApplicationContext());
        }
    }
}
