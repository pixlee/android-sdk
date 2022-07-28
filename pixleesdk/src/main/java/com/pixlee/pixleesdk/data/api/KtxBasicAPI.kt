package com.pixlee.pixleesdk.data.api

import com.pixlee.pixleesdk.data.*
import com.serjltt.moshi.adapters.Wrapped
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * This is for Kotlin.coroutines' suspend
 * - Restful API Access Object for basic APIs
 * - Retrofit Document: https://square.github.io/retrofit/
 */
interface KtxBasicAPI {
    @GET("getDisplayOption")
    suspend fun getDisplayOption(
        @Query("api_key") api_key: String,
        @Query("filter_id") filter_id: Long
    ): WidgetResult

    @GET("getJSON")
    suspend fun getWidgetPhotos(
        @Query("api_key") api_key: String,
        @Query("filter_id") filter_id: Long,
        @Query("unique_id") unique_id: Int?,
        @Query("per_page") per_page: Int,
        @Query("page") page: Int
    ): PhotoResult

    @GET("api/v2/albums/from_sku")
    suspend fun getPhotosWithSKU(
            @Query("sku") sku: String,
            @Query("api_key") api_key: String,
            @Query("filters") filters: String?,
            @Query("sort") sort: String?,
            @Query("per_page") per_page: Int,
            @Query("page") page: Int,
            @Query("region_id") region_id: Int?
    ): PhotoResult

    @GET("api/v2/albums/{album_id}/photos")
    suspend fun getPhotosWithID(
            @Path("album_id") album_id: String,
            @Query("api_key") api_key: String,
            @Query("filters") filters: String?,
            @Query("sort") sort: String?,
            @Query("per_page") per_page: Int,
            @Query("page") page: Int,
            @Query("region_id") region_id: Int?
    ): PhotoResult

    @GET("api/v2/media/{album_photo_id}")
    @Wrapped(path = ["data"])
    suspend fun getMedia(@Path("album_photo_id") album_photo_id: String, @Query("api_key") api_key: String): PXLPhoto

    @GET("getPhoto")
    suspend fun getPhoto(@Query("album_photo_id") album_photo_id: String, @Query("api_key") api_key: String, @Query("region_id") region_id: Int?): PXLPhoto

    @POST("api/v2/media")
    suspend fun postMediaWithURI(
            @Header("Signature") Signature: String,
            @Query("api_key") api_key: String,
            @Body body: RequestBody
    ): MediaResult

    @Multipart
    @POST("api/v2/media/file")
    suspend fun postMediaWithFile(
            @Header("Signature") Signature: String,
            @Query("api_key") api_key: String,
            @Part partList: List<MultipartBody.Part>): MediaResult
}
