package com.pixlee.pixleesdk.data.repository

import com.pixlee.pixleesdk.client.PXLClient
import com.pixlee.pixleesdk.data.*
import com.pixlee.pixleesdk.data.api.KtxBasicAPI
import com.pixlee.pixleesdk.network.multiparts.MultipartUtil
import com.pixlee.pixleesdk.util.toHMAC
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.http.Query
import java.io.File
import java.util.*

/**
 * Created by sungjun on 9/11/20.
 */
/**
 * The entry point for accessing basic data.
 */
interface KtxBasicDataSource {
    /**
     * @param sku: product's sku
     * @param filter: filtering options
     * @param sort: sort options
     * @param per_page: how many PXLPhotos you want to receive in a response
     * @param page: this is for pagination, range [(]1 - N], example: first page is 1, the next page is 2, 3, 4, ...
     * @return PhotoResult
     */
    suspend fun getPhotosWithSKU(
            sku: String,
            filters: PXLAlbumFilterOptions?,
            sort: PXLAlbumSortOptions?,
            per_page: Int,
            page: Int,
            region_id: Int?
    ): PhotoResult

    /**
     * @param album_id: album id
     * @param filter: filtering options
     * @param sort: sort options
     * @param per_page: how many PXLPhotos you want to receive in a response
     * @param page: this is for pagination, range [(]1 - N], example: first page is 1, the next page is 2, 3, 4, ...
     * * @return PhotoResult
     */
    suspend fun getPhotosWithID(
            album_id: String,
            filters: PXLAlbumFilterOptions?,
            sort: PXLAlbumSortOptions?,
            per_page: Int,
            page: Int,
            region_id: Int?
    ): PhotoResult

    /**
     * @param album_photo_id: This is returns PXLPhoto with album_photo_id which can be discovered in PXLPhoto.albumPhotoId
     * @return PXLPhoto
     */
    suspend fun getMedia(album_photo_id: String): PXLPhoto

    /**
     * Requests the next page of photos from the Pixlee album. Make sure to set perPage,
     * sort order, and filter options before calling.
     *
     * @param json: this should have these elements
     *              - title: title or caption of the photo being uploaded
     *              - email: email address of the submitting user
     *              - username: username of the submitting user
     *              - approved: boolean specifying whether the photo should be marked as approved on upload
     *              - photoURI: the URI of the photo/video being submitted (must be a public URI)
     */
    suspend fun postMediaWithURI(json: JSONObject): MediaResult

    /**
     * Requests the next page of photos from the Pixlee album. Make sure to set perPage,
     * sort order, and filter options before calling.
     *
     * @param json: this has these values
     *              - title: title or caption of the photo being uploaded
     *              - email: email address of the submitting user
     *              - username: username of the submitting user
     *              - approved: boolean specifying whether the photo should be marked as approved on upload
     *              - localMediaPath: a local path of the media(photo or video)
     */
    suspend fun postMediaWithFile(json: JSONObject, filePath: String): MediaResult
}

/**
 * This object loads data from and uploads data to the server using BasicAPI.java, a Retrofit HTTP API class.
 */
class KtxBasicRepository(var api: KtxBasicAPI) : KtxBasicDataSource {
    override suspend fun getPhotosWithSKU(sku: String, filters: PXLAlbumFilterOptions?, sort: PXLAlbumSortOptions?, per_page: Int, page: Int, region_id: Int?): PhotoResult {
        return api.getPhotosWithSKU(sku, PXLClient.apiKey, filters?.toParamString(), sort?.toParamString(), per_page, page, region_id)
    }

    override suspend fun getPhotosWithID(album_id: String, filters: PXLAlbumFilterOptions?, sort: PXLAlbumSortOptions?, per_page: Int, page: Int, region_id: Int?): PhotoResult {
        return api.getPhotosWithID(album_id, PXLClient.apiKey, filters?.toParamString(), sort?.toParamString(), per_page, page, region_id)
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
