package org.itishka.pointim.api;

import org.itishka.pointim.api.data.ExtendedPost;
import org.itishka.pointim.api.data.PointResult;
import org.itishka.pointim.api.data.Post;
import org.itishka.pointim.api.data.PostList;
import org.itishka.pointim.api.data.Tag;

import java.util.List;

import retrofit.Callback;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by atikhonov on 28.04.2014.
 */
public interface PointService {
    @GET("/api/recent")
    void getRecent(Callback<PostList> callback);

    @GET("/api/comments")
    void getCommented(Callback<PostList> callback);

    @GET("/api/messages/incoming")
    void getIncoming(Callback<PostList> callback);

    @GET("/api/messages/outgoing")
    void getOutgoing(Callback<PostList> callback);

    @GET("/api/blog/{login}")
    void getBlog(@Path("login") String login, Callback<PostList> callback);

    @GET("/api/tags/{login}")
    List<Tag> getTags(@Path("login") String login);

    @GET("/api/tags")
    PostList getPostsByTag(@Query("tag") String tag);

    @GET("/api/tags/{login}")
    PostList getPostsByUserTag(@Path("login") String login, @Query("tag") String tag);

    @GET("/api/post/{id}")
    void getPost(@Path("id") String id, Callback<ExtendedPost> callback);

    @FormUrlEncoded
    @POST("/api/post/{id}/b")
    void addBookmark(@Path("id") String id, @Field("text") String text, Callback<PointResult> callback);

    @DELETE("/api/post/{id}/b")
    void deleteBookmark(@Path("id") String id, Callback<PointResult> callback);

    @FormUrlEncoded
    @POST("/api/post/{id}")
    void addComment(@Path("id") String id, @Field("text") String text, Callback<PointResult> callback);

    @FormUrlEncoded
    @POST("/api/post/{id}")
    void addComment(@Path("id") String id, @Field("text") String text, @Field("comment_id") String commentId, Callback<PointResult> callback);

    @POST("/api/post/{id}/r")
    void recommend(@Path("id") String id, Callback<PointResult> callback);

    @FormUrlEncoded
    @POST("/api/post/{id}/r")
    void recommend(@Path("id") String id, @Field("text") String text, Callback<PointResult> callback);

    @DELETE("/api/post/{id}/r")
    void unRecommend(@Path("id") String id, Callback<PointResult> callback);

    @POST("/api/post/{id}/{cid}/r")
    void recommendCommend(@Path("id") String id, @Path("cid") String cid, Callback<PointResult> callback);

    @FormUrlEncoded
    @POST("/api/post/{id}/{cid}/r")
    void recommendCommend(@Path("id") String id, @Path("cid") String cid, @Field("text") String text, Callback<PointResult> callback);

    @DELETE("/api/post/{id}/{cid}/r")
    void unRecommendComment(@Path("id") String id, @Path("cid") String cid, Callback<PointResult> callback);

}
