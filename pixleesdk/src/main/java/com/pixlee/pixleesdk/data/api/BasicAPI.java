package com.pixlee.pixleesdk.data.api;

import com.pixlee.pixleesdk.PXLPhoto;
import com.pixlee.pixleesdk.data.MediaResult;
import com.pixlee.pixleesdk.data.PhotoResult;
import com.serjltt.moshi.adapters.Wrapped;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Restful API Access Object for basic APIs
 * Retrofit Document: https://square.github.io/retrofit/
 */
public interface BasicAPI {
    @GET("albums/from_sku")
    Call<PhotoResult> getPhotosWithSKU(
            @Query("sku")String sku,
            @Query("api_key")String api_key,
            @Query("filters")String filters,
            @Query("sort")String sort,
            @Query("per_page")int per_page,
            @Query("page")int page
    );

    @GET("albums/{album_id}/photos")
    Call<PhotoResult> getPhotosWithID(
            @Path("album_id")String album_id,
            @Query("api_key")String api_key,
            @Query("filters")String filters,
            @Query("sort")String sort,
            @Query("per_page")int per_page,
            @Query("page")int page
    );

    @GET("media/{album_photo_id}")
    @Wrapped(path = {"data"})
    Call<PXLPhoto> getMedia(@Path("album_photo_id")String album_photo_id, @Query("api_key")String api_key);

    @POST("media")
    Call<MediaResult> postMedia(
            @Header("Signature") String Signature,
            @Query("api_key")String api_key,
            @Body RequestBody body
    );

    @Multipart
    @POST("media/file")
    Call<MediaResult> uploadImage(
            @Header("Signature") String Signature,
            @Query("api_key")String api_key,
            @Part List<MultipartBody.Part> partList);
}
