package com.pixlee.pixleesdk.data.api

import com.pixlee.pixleesdk.data.MediaResult
import com.pixlee.pixleesdk.data.PXLPhoto
import com.pixlee.pixleesdk.data.PhotoResult
import com.serjltt.moshi.adapters.Wrapped
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

/**
 * Restful API Access Object for basic APIs
 * Retrofit Document: https://square.github.io/retrofit/
 */
interface BasicAPI {
    @GET("api/v2/albums/from_sku")
    fun getPhotosWithSKU(
            @Query("sku") sku: String,
            @Query("api_key") api_key: String,
            @Query("filters") filters: String?,
            @Query("sort") sort: String?,
            @Query("per_page") per_page: Int,
            @Query("page") page: Int,
            @Query("region_id") region_id: Int?

    ): Call<PhotoResult>

    @GET("api/v2/albums/{album_id}/photos")
    fun getPhotosWithID(
            @Path("album_id") album_id: String,
            @Query("api_key") api_key: String,
            @Query("filters") filters: String?,
            @Query("sort") sort: String?,
            @Query("per_page") per_page: Int,
            @Query("page") page: Int,
            @Query("region_id") region_id: Int?
    ): Call<PhotoResult>

    @GET("api/v2/media/{album_photo_id}")
    @Wrapped(path = ["data"])
    fun getMedia(@Path("album_photo_id") album_photo_id: String, @Query("api_key") api_key: String): Call<PXLPhoto>

    @POST("api/v2/media")
    fun postMediaWithURI(
            @Header("Signature") Signature: String,
            @Query("api_key") api_key: String?,
            @Body body: RequestBody
    ): Call<MediaResult>

    @Multipart
    @POST("api/v2/media/file")
    fun postMediaWithFile(
            @Header("Signature") Signature: String,
            @Query("api_key") api_key: String?,
            @Part partList: List<MultipartBody.Part>): Call<MediaResult>
}
