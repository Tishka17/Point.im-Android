package org.itishka.pointim.api;

import org.itishka.pointim.api.data.ExtendedPost;
import org.itishka.pointim.api.data.PointResult;
import org.itishka.pointim.api.data.PostList;
import org.itishka.pointim.api.data.Tag;
import org.itishka.pointim.api.data.User;

import java.util.List;

import retrofit.Callback;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by atikhonov on 28.04.2014.
 */
public interface PointService {
    @GET("/api/all")
    void getAll(Callback<PostList> callback);

    @GET("/api/all")
    void getAll(@Query("before") long before, Callback<PostList> callback);

    @GET("/api/bookmarks")
    void getBookmarks(Callback<PostList> callback);

    @GET("/api/bookmarks")
    void getBookmarks(@Query("before") long before, Callback<PostList> callback);

    @GET("/api/recent")
    void getRecent(Callback<PostList> callback);

    @GET("/api/recent")
    void getRecent(@Query("before") long before, Callback<PostList> callback);

    @GET("/api/comments")
    void getCommented(Callback<PostList> callback);

    @GET("/api/comments")
    void getCommented(@Query("before") long before, Callback<PostList> callback);

    @GET("/api/messages/incoming")
    void getIncoming(Callback<PostList> callback);

    @GET("/api/messages/outgoing")
    void getOutgoing(Callback<PostList> callback);

    @GET("/api/blog/{login}")
    void getBlog(@Path("login") String login, Callback<PostList> callback);

    @GET("/api/blog/{login}")
    void getBlog(@Query("before") long before, @Path("login") String login, Callback<PostList> callback);

    @GET("/api/user/{login}")
    void getUserInfo(@Path("login") String login, Callback<User> callback);

    @GET("/api/tags/{login}")
    void getTags(@Path("login") String login, Callback<List<Tag>> callback);

    @GET("/api/tags")
    void getPostsByTag(@Query("tag") String tag, Callback<PostList> callback);

    @GET("/api/tags")
    void getPostsByTag(@Query("before") long before, @Query("tag") String tag, Callback<PostList> callback);

    @GET("/api/tags/{login}")
    void getPostsByUserTag(@Path("login") String login, @Query("tag") String tag, Callback<PostList> callback);

    @GET("/api/tags/{login}")
    void getPostsByUserTag(@Query("before") long before, @Path("login") String login, @Query("tag") String tag, Callback<PostList> callback);

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

    @FormUrlEncoded
    @POST("/api/post/")
    void createPost(@Field("text") String text, @Field("tag") String[] tags, Callback<PointResult> callback);

    @DELETE("/api/post/{id}")
    void deletePost(@Path("id") String id, Callback<PointResult> callback);
}
