package org.itishka.pointim.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.octo.android.robospice.retrofit.RetrofitGsonSpiceService;

import org.itishka.pointim.model.LoginResult;
import org.itishka.pointim.model.TextWithImages;
import org.itishka.pointim.utils.DateDeserializer;
import org.itishka.pointim.utils.TextParser;

import java.util.Date;

import retrofit.RestAdapter;
import retrofit.converter.Converter;
import retrofit.converter.GsonConverter;

public class PointService extends RetrofitGsonSpiceService {
    public static final String BASE_URL = "https://point.im";
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    private PointImRequestInterceptor mRequestInterceptor = new PointImRequestInterceptor();

    public PointService() {
    }

    @Override
    protected Converter createConverter() {
        Gson gson = new GsonBuilder()
                .setDateFormat(DATE_FORMAT)
                .registerTypeAdapter(Date.class, new DateDeserializer())
                .registerTypeAdapter(TextWithImages.class, new TextParser())
                .create();
        return new GsonConverter(gson);
    }

    public void updateAuthorization(LoginResult loginResult) {
        mRequestInterceptor.setAuthorization(loginResult);
    }

    @Override
    protected RestAdapter.Builder createRestAdapterBuilder() {
        return super.createRestAdapterBuilder()
                .setRequestInterceptor(mRequestInterceptor);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        addRetrofitInterface(PointIm.class);
    }

    @Override
    protected String getServerUrl() {
        return BASE_URL;
    }
}
