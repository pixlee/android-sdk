package com.pixlee.pixleesdk.data.repository

import com.pixlee.pixleesdk.client.PXLClient
import com.pixlee.pixleesdk.data.*
import com.pixlee.pixleesdk.data.api.KtxBasicAPI
import com.pixlee.pixleesdk.network.multiparts.MultipartUtil
import com.pixlee.pixleesdk.util.toHMAC
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
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
    suspend fun postMediaWithURI(json: JSONObject): MediaResult
    suspend fun postMediaWithFile(json: JSONObject, filePath: String): MediaResult
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

    override suspend fun postMediaWithURI(json: JSONObject): MediaResult {
        requireNotNull(PXLClient.secretKey) { "no secretKey, please set secretKey before start" }
        return api.postMediaWithURI(json.toHMAC(), PXLClient.apiKey, json.toString().toRequestBody(PXLClient.mediaType))
    }

    override suspend fun postMediaWithFile(json: JSONObject, filePath: String): MediaResult {
        val bodyList: MutableList<MultipartBody.Part> = ArrayList()
        val photo = File(filePath)
        bodyList.add(MultipartUtil().getMultipartBody("file", photo))
        bodyList.add(MultipartBody.Part.createFormData("json", json.toString()))
        return api.postMediaWithFile(json.toHMAC(), PXLClient.apiKey, bodyList)
    }
}
