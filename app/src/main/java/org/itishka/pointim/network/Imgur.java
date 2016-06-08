package org.itishka.pointim.network;

import org.itishka.pointim.model.imgur.Image;
import org.itishka.pointim.model.imgur.UploadResult;

import okhttp3.RequestBody;
import retrofit2.Callback;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Tishka17 on 30.12.2014.
 */
public interface Imgur {
    @Multipart
    @POST("/upload")
    Observable<UploadResult> uploadFile(@Part("image") RequestBody resource);


    @GET("/image/{id}")
    Observable<Image> getImageInfo(@Path("id") String id);

    @DELETE("/image/{id}")
    Observable<Void> deleteImage(@Path("id") String id, Callback<Void> callback);
}
