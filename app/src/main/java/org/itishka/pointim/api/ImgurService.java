package org.itishka.pointim.api;

import org.itishka.pointim.model.ImgurImage;
import org.itishka.pointim.model.ImgurUploadResult;

import retrofit.Callback;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.mime.TypedFile;

/**
 * Created by Tishka17 on 30.12.2014.
 */
public interface ImgurService {
    @Multipart
    @POST("/upload")
    ImgurUploadResult uploadFile(@Part("image") TypedFile resource);


    @GET("/image/{id}")
    ImgurImage getImageInfo(@Path("id") String id);

    @DELETE("/image/{id}")
    void deleteImage(@Path("id") String id, Callback<Void> callback);
}
