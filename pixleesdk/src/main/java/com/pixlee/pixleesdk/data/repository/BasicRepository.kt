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
    fun getPhotosWithSKU(
            sku: String,
            api_key: String,
            filters: String?,
            sort: String?,
            per_page: Int,
            page: Int
    ): Call<PhotoResult>

    fun getPhotosWithID(
            album_id: String,
            api_key: String,
            filters: String?,
            sort: String?,
            per_page: Int,
            page: Int
    ): Call<PhotoResult>

    fun getMedia(album_photo_id: String, api_key: String): Call<PXLPhoto>
    fun postMediaWithURI(json: JSONObject): Call<MediaResult>
    fun postMediaWithURI(json: JSONObject, filePath: String): Call<MediaResult>
}

/**
 * This object loads data from and uploads data to the server using BasicAPI.java, a Retrofit HTTP API class.
 */
class BasicRepository(var api: BasicAPI) : BasicDataSource {
    override fun getPhotosWithSKU(sku: String, api_key: String, filters: String?, sort: String?, per_page: Int, page: Int): Call<PhotoResult> {
        return api.getPhotosWithSKU(sku, api_key, filters, sort, per_page, page)
    }

    override fun getPhotosWithID(album_id: String, api_key: String, filters: String?, sort: String?, per_page: Int, page: Int): Call<PhotoResult> {
        return api.getPhotosWithID(album_id, api_key, filters, sort, per_page, page)
    }

    override fun getMedia(album_photo_id: String, api_key: String): Call<PXLPhoto> {
        return api.getMedia(album_photo_id, api_key)
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
