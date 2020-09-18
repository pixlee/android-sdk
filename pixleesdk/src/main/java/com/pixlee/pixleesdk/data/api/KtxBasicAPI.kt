package com.pixlee.pixleesdk.data.api

import com.pixlee.pixleesdk.data.MediaResult
import com.pixlee.pixleesdk.data.PXLPhoto
import com.pixlee.pixleesdk.data.PhotoResult
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
    @GET("albums/from_sku")
    suspend fun getPhotosWithSKU(
            @Query("sku") sku: String,
            @Query("api_key") api_key: String?,
            @Query("filters") filters: String?,
            @Query("sort") sort: String?,
            @Query("per_page") per_page: Int,
            @Query("page") page: Int
    ): PhotoResult

    @GET("albums/{album_id}/photos")
    suspend fun getPhotosWithID(
            @Path("album_id") album_id: String,
            @Query("api_key") api_key: String?,
            @Query("filters") filters: String?,
            @Query("sort") sort: String?,
            @Query("per_page") per_page: Int,
            @Query("page") page: Int
    ): PhotoResult

    @GET("media/{album_photo_id}")
    @Wrapped(path = ["data"])
    suspend fun getMedia(@Path("album_photo_id") album_photo_id: String, @Query("api_key") api_key: String?): PXLPhoto

    @POST("media")
    suspend fun postMedia(
            @Header("Signature") Signature: String,
            @Query("api_key") api_key: String?,
            @Body body: RequestBody
    ): MediaResult

    @Multipart
    @POST("media/file")
    suspend fun uploadImage(
            @Header("Signature") Signature: String,
            @Query("api_key") api_key: String?,
            @Part partList: List<MultipartBody.Part>): MediaResult
}
