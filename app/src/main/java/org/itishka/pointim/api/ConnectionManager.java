package org.itishka.pointim.api;

import android.content.Context;
import android.text.Spannable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.itishka.pointim.api.data.LoginResult;

import java.util.Date;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by Татьяна on 21.10.2014.
 */
public class ConnectionManager {
    private static final ConnectionManager instance = new ConnectionManager();

    public static ConnectionManager getInstance() {
        return instance;
    }

    private ConnectionManager() {
        RestAdapter authRestAdapter = new RestAdapter.Builder()
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade requestFacade) {
                        requestFacade.addHeader("User-Agent", USER_AGENT);
                    }
                })
                .setEndpoint(ENDPOINT)
                .setConverter(new GsonConverter(mGson))
                .build();
        pointAuthService = authRestAdapter.create(PointAuthService.class);
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
}
