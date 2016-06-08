package org.itishka.pointim.network;

import org.itishka.pointim.model.point.ExtendedUser;
import org.itishka.pointim.model.point.NewPostResponse;
import org.itishka.pointim.model.point.PointResult;
import org.itishka.pointim.model.point.Post;
import org.itishka.pointim.model.point.PostList;
import org.itishka.pointim.model.point.TagList;
import org.itishka.pointim.model.point.UserList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by atikhonov on 28.04.2014.
 */
public interface PointIm {
    @GET("/api/all")
    Call<PostList> getAll();

    @GET("/api/all")
    Call<PostList> getAll(@Query("before") long before);

    @GET("/api/bookmarks")
    Call<PostList> getBookmarks();

    @GET("/api/bookmarks")
    Call<PostList> getBookmarks(@Query("before") long before);

    @GET("/api/recent")
    Call<PostList> getRecent();

    @GET("/api/recent")
    Call<PostList> getRecent(@Query("before") long before);

    @GET("/api/comments")
    Call<PostList> getCommented();

    @GET("/api/comments")
    Call<PostList> getCommented(@Query("before") long before);

    @GET("/api/messages/incoming")
    Call<PostList> getIncoming();

    @GET("/api/messages/incoming")
    Call<PostList> getIncoming(@Query("before") long before);

    @GET("/api/messages/outgoing")
    Call<PostList> getOutgoing();

    @GET("/api/messages/outgoing")
    Call<PostList> getOutgoing(@Query("before") long before);

    @GET("/api/blog/login/{login}")
    Call<PostList> getBlog(@Path("login") String login);

    @GET("/api/blog/login/{login}")
    Call<PostList> getBlog(@Query("before") long before, @Path("login") String login);

    @GET("/api/user/login/{login}")
    Call<ExtendedUser> getUserInfo(@Path("login") String login);

    @GET("/api/user/login/{login}/subscriptions")
    Call<UserList> getUserSubscriptions(@Path("login") String login);

    @FormUrlEncoded
    @POST("/api/user/s/{login}")
    Call subscribeUser(@Path("login") String login, @Field("text") String text, Callback<Void> callback);

    @DELETE("/api/user/s/{login}")
    Call unsubscribeUser(@Path("login") String login, Callback<PointResult> callback);

    @FormUrlEncoded
    @POST("/api/user/sr/{login}")
    Call subscribeUserRecommendations(@Path("login") String login, @Field("text") String text, Callback<Void> callback);

    @DELETE("/api/user/sr/{login}")
    Call unsubscribeUserRecommendations(@Path("login") String login, Callback<PointResult> callback);

    @GET("/api/tags/login/{login}")
    Call<TagList> getTags(@Path("login") String login);

    @GET("/api/tags")
    Call<PostList> getPostsByTag(@Query("tag") String tag);

    @GET("/api/tags")
    Call<PostList> getPostsByTag(@Query("before") long before, @Query("tag") String tag);

    @GET("/api/tags/login/{login}")
    Call<PostList> getPostsByUserTag(@Path("login") String login, @Query("tag") String tag);

    @GET("/api/tags/login/{login}")
    Call<PostList> getPostsByUserTag(@Query("before") long before, @Path("login") String login, @Query("tag") String tag);

    @GET("/api/post/{id}")
    Call<Post> getPost(@Path("id") String id);

    @FormUrlEncoded
    @POST("/api/post/{id}/b")
    Call addBookmark(@Path("id") String id, @Field("text") String text, Callback<PointResult> callback);

    @DELETE("/api/post/{id}/b")
    Call deleteBookmark(@Path("id") String id, Callback<Void> callback);

    @FormUrlEncoded
    @POST("/api/post/{id}")
    Call addComment(@Path("id") String id, @Field("text") String text, Callback<PointResult> callback);

    @FormUrlEncoded
    @POST("/api/post/{id}")
    Call addComment(@Path("id") String id, @Field("text") String text, @Field("comment_id") String commentId, Callback<PointResult> callback);

    @FormUrlEncoded
    @POST("/api/post/{id}/r")
    Call recommend(@Path("id") String id, @Field("text") String text, Callback<PointResult> callback);

    @DELETE("/api/post/{id}/r")
    Call notRecommend(@Path("id") String id, Callback<PointResult> callback);

    @FormUrlEncoded
    @POST("/api/post/{id}/{cid}/r")
    Call recommendCommend(@Path("id") String id, @Path("cid") long cid, @Field("text") String text, Callback<PointResult> callback);

    @DELETE("/api/post/{id}/{cid}/r")
    Call notRecommendComment(@Path("id") String id, @Path("cid") long cid, Callback<PointResult> callback);

    @FormUrlEncoded
    @POST("/api/post/")
    Call createPost(@Field("text") String text, @Field("tag") String[] tags, Callback<NewPostResponse> callback);

    @FormUrlEncoded
    @POST("/api/post/")
    Call createPrivatePost(@Field("text") String text, @Field("tag") String[] tags, @Field("private") boolean reserved, Callback<NewPostResponse> callback);

    @FormUrlEncoded
    @PUT("/api/post/{id}")
    Call editPost(@Path("id") String id, @Field("text") String text, @Field("tag") String[] tags, Callback<NewPostResponse> callback);

    @DELETE("/api/post/{id}")
    Call deletePost(@Path("id") String id, Callback<PointResult> callback);

    @DELETE("/api/post/{id}/{cid}")
    Call deleteComment(@Path("id") String id, @Path("cid") long cid, Callback<PointResult> callback);
}
