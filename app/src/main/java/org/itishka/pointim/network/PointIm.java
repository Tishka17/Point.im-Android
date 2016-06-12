package org.itishka.pointim.network;

import org.itishka.pointim.model.point.ExtendedUser;
import org.itishka.pointim.model.point.NewPostResponse;
import org.itishka.pointim.model.point.PointResult;
import org.itishka.pointim.model.point.Post;
import org.itishka.pointim.model.point.PostList;
import org.itishka.pointim.model.point.TagList;
import org.itishka.pointim.model.point.UserList;

import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by atikhonov on 28.04.2014.
 */
public interface PointIm {
    @GET("/api/all")
    Observable<PostList> getAll();

    @GET("/api/all")
    Observable<PostList> getAll(@Query("before") long before);

    @GET("/api/bookmarks")
    Observable<PostList> getBookmarks();

    @GET("/api/bookmarks")
    Observable<PostList> getBookmarks(@Query("before") long before);

    @GET("/api/recent")
    Observable<PostList> getRecent();

    @GET("/api/recent")
    Observable<PostList> getRecent(@Query("before") long before);

    @GET("/api/comments")
    Observable<PostList> getCommented();

    @GET("/api/comments")
    Observable<PostList> getCommented(@Query("before") long before);

    @GET("/api/messages/incoming")
    Observable<PostList> getIncoming();

    @GET("/api/messages/incoming")
    Observable<PostList> getIncoming(@Query("before") long before);

    @GET("/api/messages/outgoing")
    Observable<PostList> getOutgoing();

    @GET("/api/messages/outgoing")
    Observable<PostList> getOutgoing(@Query("before") long before);

    @GET("/api/blog/login/{login}")
    Observable<PostList> getBlog(@Path("login") String login);

    @GET("/api/blog/login/{login}")
    Observable<PostList> getBlog(@Path("login") String login, @Query("before") long before);

    @GET("/api/user/login/{login}")
    Observable<ExtendedUser> getUserInfo(@Path("login") String login);

    @GET("/api/user/login/{login}/subscriptions")
    Observable<UserList> getUserSubscriptions(@Path("login") String login);

    @FormUrlEncoded
    @POST("/api/user/s/{login}")
    Observable subscribeUser(@Path("login") String login, @Field("text") String text);

    @DELETE("/api/user/s/{login}")
    Observable<PointResult> unsubscribeUser(@Path("login") String login);

    @FormUrlEncoded
    @POST("/api/user/sr/{login}")
    Observable subscribeUserRecommendations(@Path("login") String login, @Field("text") String text);

    @DELETE("/api/user/sr/{login}")
    Observable<PointResult> unsubscribeUserRecommendations(@Path("login") String login);

    @GET("/api/tags/login/{login}")
    Observable<TagList> getTags(@Path("login") String login);

    @GET("/api/tags")
    Observable<PostList> getPostsByTag(@Query("tag") String tag);

    @GET("/api/tags")
    Observable<PostList> getPostsByTag(@Query("before") long before, @Query("tag") String tag);

    @GET("/api/tags/login/{login}")
    Observable<PostList> getPostsByUserTag(@Path("login") String login, @Query("tag") String tag);

    @GET("/api/tags/login/{login}")
    Observable<PostList> getPostsByUserTag(@Path("login") String login, @Query("before") long before, @Query("tag") String tag);

    @GET("/api/post/{id}")
    Observable<Post> getPost(@Path("id") String id);

    @FormUrlEncoded
    @POST("/api/post/{id}/b")
    Observable<PointResult> addBookmark(@Path("id") String id, @Field("text") String text);

    @DELETE("/api/post/{id}/b")
    Observable deleteBookmark(@Path("id") String id);

    @FormUrlEncoded
    @POST("/api/post/{id}")
    Observable<PointResult> addComment(@Path("id") String id, @Field("text") String text);

    @FormUrlEncoded
    @POST("/api/post/{id}")
    Observable<PointResult> addComment(@Path("id") String id, @Field("text") String text, @Field("comment_id") String commentId);

    @FormUrlEncoded
    @POST("/api/post/{id}/r")
    Observable<PointResult> recommend(@Path("id") String id, @Field("text") String text);

    @DELETE("/api/post/{id}/r")
    Observable<PointResult> notRecommend(@Path("id") String id);

    @FormUrlEncoded
    @POST("/api/post/{id}/{cid}/r")
    Observable<PointResult> recommendCommend(@Path("id") String id, @Path("cid") long cid, @Field("text") String text);

    @DELETE("/api/post/{id}/{cid}/r")
    Observable<PointResult> notRecommendComment(@Path("id") String id, @Path("cid") long cid);

    @FormUrlEncoded
    @POST("/api/post/")
    Observable<NewPostResponse> createPost(@Field("text") String text, @Field("tag") String[] tags);

    @FormUrlEncoded
    @POST("/api/post/")
    Observable<NewPostResponse> createPrivatePost(@Field("text") String text, @Field("tag") String[] tags, @Field("private") boolean reserved);

    @FormUrlEncoded
    @PUT("/api/post/{id}")
    Observable<NewPostResponse> editPost(@Path("id") String id, @Field("text") String text, @Field("tag") String[] tags);

    @DELETE("/api/post/{id}")
    Observable<PointResult> deletePost(@Path("id") String id);

    @DELETE("/api/post/{id}/{cid}")
    Observable<PointResult> deleteComment(@Path("id") String id, @Path("cid") long cid);
}
