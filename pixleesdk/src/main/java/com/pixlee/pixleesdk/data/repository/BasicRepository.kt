package com.pixlee.pixleesdk.data.repository

import com.pixlee.pixleesdk.client.PXLClient
import com.pixlee.pixleesdk.data.PXLPhoto
import com.pixlee.pixleesdk.data.MediaResult
import com.pixlee.pixleesdk.data.PhotoResult
import com.pixlee.pixleesdk.data.api.BasicAPI
import com.pixlee.pixleesdk.network.multiparts.MultipartUtil
import com.pixlee.pixleesdk.util.toHMAC
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import java.io.File
import java.util.*

/**
 * Created by sungjun on 9/11/20.
 */
/**
 * The entry point for accessing basic data.
 */
interface BasicDataSource {
    /**
     * @param sku: product's sku
     * @param filter: filtering options
     * @param sort: sort options
     * @param per_page: how many PXLPhotos you want to receive in a response
     * @param page: this is for pagination, range [(]1 - N], example: first page is 1, the next page is 2, 3, 4, ...
     * @param region_id: region id to get data from a specific region
     * @return PhotoResult
     */
    fun getPhotosWithSKU(
            sku: String,
            filters: String?,
            sort: String?,
            per_page: Int,
            page: Int,
            region_id: Int?
    ): Call<PhotoResult>

    /**
     * @param album_id: album id
     * @param filter: filtering options
     * @param sort: sort options
     * @param per_page: how many PXLPhotos you want to receive in a response
     * @param page: this is for pagination, range [(]1 - N], example: first page is 1, the next page is 2, 3, 4, ...
     * @param region_id: region id to get data from a specific region
     * * @return PhotoResult
     */
    fun getPhotosWithID(
            album_id: String,
            filters: String?,
            sort: String?,
            per_page: Int,
            page: Int,
            region_id: Int?
    ): Call<PhotoResult>

    /**
     * @param album_photo_id: This is returns PXLPhoto with album_photo_id which can be discovered in PXLPhoto.albumPhotoId
     * @return PXLPhoto
     */
    fun getMedia(album_photo_id: String): Call<PXLPhoto>

    /**
     * @param album_photo_id: This is returns PXLPhoto with album_photo_id which can be discovered in PXLPhoto.albumPhotoId
     * @param region_id: region id to get data from a specific region
     * @return PXLPhoto
     */
    fun getPhoto(album_photo_id: String, region_id:Int?): Call<PXLPhoto>

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
    fun postMediaWithURI(json: JSONObject): Call<MediaResult>

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
    fun postMediaWithURI(json: JSONObject, filePath: String): Call<MediaResult>
}

/**
 * This object loads data from and uploads data to the server using BasicAPI.java, a Retrofit HTTP API class.
 */
class BasicRepository(var api: BasicAPI) : BasicDataSource {
    override fun getPhotosWithSKU(sku: String, filters: String?, sort: String?, per_page: Int, page: Int, region_id: Int?): Call<PhotoResult> {
        return api.getPhotosWithSKU(sku, PXLClient.apiKey, filters, sort, per_page, page, region_id)
    }

    override fun getPhotosWithID(album_id: String, filters: String?, sort: String?, per_page: Int, page: Int, region_id: Int?): Call<PhotoResult> {
        return api.getPhotosWithID(album_id, PXLClient.apiKey, filters, sort, per_page, page, region_id)
    }

    override fun getMedia(album_photo_id: String): Call<PXLPhoto> {
        return api.getMedia(album_photo_id, PXLClient.apiKey)
    }

    override fun getPhoto(album_photo_id: String, region_id: Int?): Call<PXLPhoto> {
        return api.getPhoto(album_photo_id, PXLClient.apiKey, region_id)
    }

    override fun postMediaWithURI(json: JSONObject): Call<MediaResult> {
        return api.postMediaWithURI(json.toHMAC(), PXLClient.apiKey, json.toString().toRequestBody(PXLClient.mediaType))
    }

    override fun postMediaWithURI(json: JSONObject, filePath: String): Call<MediaResult> {
        val bodyList: MutableList<MultipartBody.Part> = ArrayList()
        val photo = File(filePath)
        bodyList.add(MultipartUtil().getMultipartBody("file", photo))
        bodyList.add(MultipartBody.Part.createFormData("json", json.toString()))
        return api.postMediaWithFile(json.toHMAC(), PXLClient.apiKey, bodyList)
    }
}
