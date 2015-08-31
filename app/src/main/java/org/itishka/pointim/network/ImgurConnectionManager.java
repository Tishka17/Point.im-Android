package org.itishka.pointim.network;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;

import org.itishka.pointim.BuildConfig;
import org.itishka.pointim.model.imgur.Token;

import java.util.concurrent.TimeUnit;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * Created by Tishka17 on 21.10.2014.
 */
public class ImgurConnectionManager extends ConnectionManager {
    private static final String PREFERENCE = "ImgurConnectionManager";

    public static final String IMGUR_ENDPOINT = "https://api.imgur.com/3/";
    public static final String IMGUR_AUTH_ENDPOINT = "https://api.imgur.com/oauth2/";

    private static final ImgurConnectionManager sInstance = new ImgurConnectionManager();
    private final OkHttpClient mOkHttpClient;
    private final OkClient mOkClient;
    private final Gson mGson = new GsonBuilder().create();

    public Token token = null;
    public Imgur imgurService = null;
    public ImgurAuth imgurAuthService = null;

    private ImgurConnectionManager() {
        mOkHttpClient = new OkHttpClient();
        mOkHttpClient.setReadTimeout(120, TimeUnit.SECONDS);
        mOkClient = new OkClient(mOkHttpClient);

        RestAdapter imgurAuthRestAdapter = new RestAdapter.Builder()
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade requestFacade) {
                        requestFacade.addHeader("User-Agent", USER_AGENT);
                    }
                })
                .setClient(mOkClient)
                .setEndpoint(IMGUR_AUTH_ENDPOINT)
                .setConverter(new GsonConverter(mGson))
                .build();
        imgurAuthService = imgurAuthRestAdapter.create(ImgurAuth.class);
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
    public void init(Context context) {
        synchronized (this) {
            if (this.token == null) {
                token = loadAuthorization(context, PREFERENCE, Token.class);
                createService();
            }
        }
    }

    @Override
    protected void createService() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade requestFacade) {
                        requestFacade.addHeader("Authorization", token.access_token);
                        requestFacade.addHeader("User-Agent", USER_AGENT);
                    }
                })
                .setClient(mOkClient)
                .setEndpoint(IMGUR_ENDPOINT)
                .setConverter(new GsonConverter(mGson))
                .build();
        imgurService = restAdapter.create(Imgur.class);
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
            init(context);
        }
    }
}
