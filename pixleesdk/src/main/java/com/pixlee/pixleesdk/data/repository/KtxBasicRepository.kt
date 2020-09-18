package com.pixlee.pixleesdk.data.repository

import com.pixlee.pixleesdk.client.PXLClient
import com.pixlee.pixleesdk.data.*
import com.pixlee.pixleesdk.data.api.BasicAPI
import com.pixlee.pixleesdk.data.api.KtxBasicAPI
import com.pixlee.pixleesdk.network.HMAC
import com.pixlee.pixleesdk.network.multiparts.MultipartUtil
import com.pixlee.pixleesdk.util.toHMAC
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import java.io.File
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.util.*

/**
 * Created by sungjun on 9/11/20.
 */
/**
 * The entry point for accessing basic data.
 */
interface KtxBasicDataSource {
    suspend fun getPhotosWithSKU(
            sku: String,
            filters: PXLAlbumFilterOptions?,
            sort: PXLAlbumSortOptions?,
            per_page: Int,
            page: Int
    ): PhotoResult

    suspend fun getPhotosWithID(
            album_id: String,
            filters: PXLAlbumFilterOptions?,
            sort: PXLAlbumSortOptions?,
            per_page: Int,
            page: Int
    ): PhotoResult

    suspend fun getMedia(album_photo_id: String): PXLPhoto
    suspend fun postMedia(json: JSONObject): MediaResult
    suspend fun uploadImage(json: JSONObject, filePath: String): MediaResult
}

/**
 * This object loads data from and uploads data to the server using BasicAPI.java, a Retrofit HTTP API class.
 */
class KtxBasicRepository(var api: KtxBasicAPI) : KtxBasicDataSource {
    override suspend fun getPhotosWithSKU(sku: String, filters: PXLAlbumFilterOptions?, sort: PXLAlbumSortOptions?, per_page: Int, page: Int): PhotoResult {
        return api.getPhotosWithSKU(sku, PXLClient.apiKey, filters?.toParamString(), sort?.toParamString(), per_page, page)
    }

    override suspend fun getPhotosWithID(album_id: String, filters: PXLAlbumFilterOptions?, sort: PXLAlbumSortOptions?, per_page: Int, page: Int): PhotoResult {
        return api.getPhotosWithID(album_id, PXLClient.apiKey, filters?.toParamString(), sort?.toParamString(), per_page, page)
    }

    override suspend fun getMedia(album_photo_id: String): PXLPhoto {
        return api.getMedia(album_photo_id, PXLClient.apiKey)
    }

    override suspend fun postMedia(json: JSONObject): MediaResult {
        requireNotNull(PXLClient.secretKey) { "no secretKey, please set secretKey before start" }
        return api.postMedia(json.toHMAC(), PXLClient.apiKey, json.toString().toRequestBody(PXLClient.mediaType))
    }

    override suspend fun uploadImage(json: JSONObject, filePath: String): MediaResult {
        val bodyList: MutableList<MultipartBody.Part> = ArrayList()
        val photo = File(filePath)
        bodyList.add(MultipartUtil().getMultipartBody("file", photo))
        bodyList.add(MultipartBody.Part.createFormData("json", json.toString()))
        return api.uploadImage(json.toHMAC(), PXLClient.apiKey, bodyList)
    }
}
