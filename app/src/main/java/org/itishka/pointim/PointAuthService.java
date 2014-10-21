package org.itishka.pointim;

import org.itishka.pointim.api.data.LoginResult;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by atikhonov on 28.04.2014.
 */
public interface PointAuthService {
    @FormUrlEncoded
    @POST("/api/login")
    void login(@Field("login") String login, @Field("password") String password, Callback<LoginResult> callback);

    @FormUrlEncoded
    @POST("/api/logout")
    void logout(@Field("csrf_token") String csrf_token, Callback<LoginResult> callback);
}
