package org.itishka.pointim.network;

import org.itishka.pointim.model.point.LoginResult;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by atikhonov on 28.04.2014.
 */
public interface PointImAuth {
    @FormUrlEncoded
    @POST("/api/login")
    Call<LoginResult> login(@Field("login") String login, @Field("password") String password);

    @FormUrlEncoded
    @POST("/api/logout")
    Call<LoginResult> logout(@Field("csrf_token") String csrf_token);
}
