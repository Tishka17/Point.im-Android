package org.itishka.pointim.api;

import android.content.Context;
import android.text.Spannable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;

import org.itishka.pointim.BuildConfig;
import org.itishka.pointim.api.data.LoginResult;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * Created by Татьяна on 21.10.2014.
 */
public class ConnectionManager {
    private static final ConnectionManager instance = new ConnectionManager();
    private OkClient okClient;

    public static ConnectionManager getInstance() {
        return instance;
    }

    private ConnectionManager() {
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(120, TimeUnit.SECONDS);
        okClient = new OkClient(okHttpClient);

        RestAdapter authRestAdapter = new RestAdapter.Builder()
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade requestFacade) {
                        requestFacade.addHeader("User-Agent", USER_AGENT);
                    }
                })
                .setClient(okClient)
                .setEndpoint(ENDPOINT)
                .setConverter(new GsonConverter(mGson))
                .build();
        pointAuthService = authRestAdapter.create(PointAuthService.class);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade requestFacade) {
                        requestFacade.addHeader("Authorization", "Client-ID "+ BuildConfig.IMGUR_ID);
                        requestFacade.addHeader("User-Agent", USER_AGENT);
                    }
                })
                .setClient(okClient)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(IMGUR_ENDPOINT)
                .setConverter(new GsonConverter(mGson))
                .build();
        imgurService = restAdapter.create(ImgurService.class);
    }

    public static final String USER_AGENT = "Tishka17 Point.im Client";
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String ENDPOINT = "https://point.im";
    public PointService pointService = null;
    private final Gson mGson = new GsonBuilder()
            .setDateFormat(DATE_FORMAT)
            .registerTypeAdapter(Date.class, new DateDeserializer())
            .registerTypeAdapter(Spannable.class, new TextParser())
            .create();
    public PointAuthService pointAuthService = null;
    public LoginResult loginResult = null;

    public void updateAuthorization(Context context, LoginResult loginResult) {
        synchronized (this) {
            this.loginResult = loginResult;
            AuthSaver.saveLoginResult(context, loginResult);
            createPointService();
        }
    }

    public void updateAuthorization(Context context) {
        synchronized (this) {
            this.loginResult = AuthSaver.loadLoginResult(context);
            createPointService();
        }
    }

    private void createPointService() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade requestFacade) {
                        requestFacade.addHeader("Authorization", loginResult.token);
                        requestFacade.addHeader("X-CSRF", loginResult.csrf_token);
                        requestFacade.addHeader("User-Agent", USER_AGENT);
                    }
                })
                .setClient(okClient)
                .setEndpoint(ENDPOINT)
                .setConverter(new GsonConverter(mGson))
                .build();
        pointService = restAdapter.create(PointService.class);
    }

    public boolean isAuthorized() {
        return !TextUtils.isEmpty(loginResult.csrf_token);
    }
    public void resetAuthorization(Context context) {
        synchronized (this) {
            loginResult.csrf_token = "";
            AuthSaver.saveLoginResult(context, loginResult);
        }
    }

    //----- IMGUR ---

    public ImgurService imgurService = null;
    public static final String IMGUR_ENDPOINT = "https://api.imgur.com/3/";
}
