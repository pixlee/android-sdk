package com.pixlee.pixleesdk.data.repository

import com.pixlee.pixleesdk.client.PXLClient
import com.pixlee.pixleesdk.data.PXLPhoto
import com.pixlee.pixleesdk.data.MediaResult
import com.pixlee.pixleesdk.data.PhotoResult
import com.pixlee.pixleesdk.data.api.BasicAPI
import com.pixlee.pixleesdk.network.HMAC
import com.pixlee.pixleesdk.network.multiparts.MultipartUtil
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
    fun postMedia(json: JSONObject): Call<MediaResult>
    fun uploadImage(json: JSONObject, filePath: String): Call<MediaResult>
}

/**
 * This object loads data from and uploads data to the server using BasicAPI.java, a Retrofit HTTP API class.
 */
class BasicRepository(var api: BasicAPI) : BasicDataSource {
    private fun getSignature(json: JSONObject): String {
        requireNotNull(PXLClient.secretKey) { "no secretKey, please set secretKey before start" }
        var signature: String = ""
        try {
            signature = HMAC.computeHmac(json.toString().replace("\\/", "/"), PXLClient.secretKey)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        }
        return signature
    }

    override fun getPhotosWithSKU(sku: String, api_key: String, filters: String?, sort: String?, per_page: Int, page: Int): Call<PhotoResult> {
        return api.getPhotosWithSKU(sku, api_key, filters, sort, per_page, page)
    }

    override fun getPhotosWithID(album_id: String, api_key: String, filters: String?, sort: String?, per_page: Int, page: Int): Call<PhotoResult> {
        return api.getPhotosWithID(album_id, api_key, filters, sort, per_page, page)
    }

    override fun getMedia(album_photo_id: String, api_key: String): Call<PXLPhoto> {
        return api.getMedia(album_photo_id, api_key)
    }

    override fun postMedia(json: JSONObject): Call<MediaResult> {
        val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), json.toString())
        return api.postMedia(getSignature(json), PXLClient.apiKey, body)
    }

    override fun uploadImage(json: JSONObject, filePath: String): Call<MediaResult> {
        val bodyList: MutableList<MultipartBody.Part> = ArrayList()
        val photo = File(filePath)
        bodyList.add(MultipartUtil().getMultipartBody("file", photo))
        bodyList.add(MultipartBody.Part.createFormData("json", json.toString()))
        return api.uploadImage(getSignature(json), PXLClient.apiKey, bodyList)
    }
}
