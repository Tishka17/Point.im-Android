package org.itishka.pointim.api;

import android.content.Context;
import android.content.SharedPreferences;

import org.itishka.pointim.api.data.LoginResult;

/**
 * Created by atikhonov on 05.05.2014.
 */
public class AuthSaver {
    private static final String PREFS_NAME = "AUTH_DATA";
    public static void saveLoginResult(Context context, LoginResult result) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, 0).edit();
        editor.putString("token", result.token);
        editor.putString("csrf_token", result.csrf_token);
        editor.putString("login", result.login);
        editor.apply();
    }

    public static LoginResult loadLoginResult(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        LoginResult result = new LoginResult();
        result.token = settings.getString("token", "");
        result.csrf_token = settings.getString("csrf_token", "");
        result.login = settings.getString("login", "");
        return result;
    }
}
