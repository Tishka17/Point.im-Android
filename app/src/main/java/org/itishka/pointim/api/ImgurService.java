package org.itishka.pointim.api;

import org.itishka.pointim.api.data.ImgurBaseResponse;
import org.itishka.pointim.api.data.ImgurImage;

import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

/**
 * Created by Tishka17 on 30.12.2014.
 */
public interface ImgurService {
    @Multipart
    @POST("/upload")
    ImgurBaseResponse uploadFile(@Part("image") TypedFile resource);


    @GET("/image/{id}")
    ImgurImage getImageInfo(@Path("login") String id);
}
