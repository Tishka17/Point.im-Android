package org.itishka.pointim.network;

import android.content.Context;

import com.google.gson.Gson;

/**
 * Created by Tishka17 on 31.08.2015.
 */
public abstract class ConnectionManager {
    String USER_AGENT = "Tishka17 Point.im Client";
    private static final String KEY = "AuthorizationData";

    protected abstract Gson getGson();

    public abstract void updateAuthorization(Context context, Object loginResult);

    public abstract void init(Context context);

    public abstract boolean isAuthorized();

    public abstract void resetAuthorization(Context context);

    protected abstract void createService();

    protected void saveAuthorization(Context context, String preference, Object data) {
        context.getSharedPreferences(preference, Context.MODE_PRIVATE).edit().putString(KEY, getGson().toJson(data)).commit();
    }

    protected <T> T loadAuthorization(Context context, String preference, Class<T> clazz) {
        String v = context.getSharedPreferences(preference, Context.MODE_PRIVATE).getString(KEY, null);
        if (v == null) {
            return null;
        } else {
            return getGson().fromJson(v, clazz);
        }
    }
}
