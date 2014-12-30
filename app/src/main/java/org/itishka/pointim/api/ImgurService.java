package org.itishka.pointim.api;

import org.itishka.pointim.api.data.ImgurBaseResponse;

import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

/**
 * Created by Tishka17 on 30.12.2014.
 */
public interface ImgurService {
    @Multipart
    @POST("/upload")
    ImgurBaseResponse uploadFile(@Part("image") TypedFile resource, @Query("path") String path);
}
