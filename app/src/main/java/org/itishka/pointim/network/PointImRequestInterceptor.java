package org.itishka.pointim.network;

import org.itishka.pointim.model.point.LoginResult;

import retrofit.RequestInterceptor;

/**
 * Created by Tishka17 on 04.02.2015.
 */
public class PointImRequestInterceptor implements RequestInterceptor {
    public static final String USER_AGENT = "Tishka17 Point.im Client";
    private LoginResult mLoginResult;

    PointImRequestInterceptor() {
    }

    public void setAuthorization(LoginResult loginResult) {
        mLoginResult = loginResult;
    }

    @Override
    public void intercept(RequestInterceptor.RequestFacade requestFacade) {
        requestFacade.addHeader("Authorization", mLoginResult.token);
        requestFacade.addHeader("X-CSRF", mLoginResult.csrf_token);
        requestFacade.addHeader("User-Agent", USER_AGENT);
    }
}
