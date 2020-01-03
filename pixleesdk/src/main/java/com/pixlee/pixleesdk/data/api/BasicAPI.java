package com.pixlee.pixleesdk.data.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BasicAPI {
    @GET("albums/from_sku")
    Call<String> getPhotosWithSKU(
            @Query("sku")String sku,
            @Query("api_key")String api_key,
            @Query("filters")String filters,
            @Query("sort")String sort,
            @Query("per_page")int per_page,
            @Query("page")int page
    );

    @GET("albums/{id}/photos")
    Call<String> getPhotosWithID(
            @Path("id")String id,
            @Query("api_key")String api_key,
            @Query("filters")String filters,
            @Query("sort")String sort,
            @Query("per_page")int per_page,
            @Query("page")int page
    );

    @GET("media/{id}")
    Call<String> getMedia(@Path("id")String id, @Query("api_key")String api_key);

    @FormUrlEncoded
    @POST("media")
    Call<String> postMedia(
            @Field("api_key")String api_key,
            @Field("album_id")String album_id,
            @Field("title")String title,
            @Field("email")String email,
            @Field("username")String username,
            @Field("photo_uri")String photo_uri,
            @Field("approved")boolean approved
    );
}
