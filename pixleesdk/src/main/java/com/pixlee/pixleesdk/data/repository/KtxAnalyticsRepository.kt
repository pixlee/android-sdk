package com.pixlee.pixleesdk.data.repository

import android.util.Log
import com.pixlee.pixleesdk.client.PXLBaseAlbum
import com.pixlee.pixleesdk.client.PXLClient
import com.pixlee.pixleesdk.client.PXLClient.Companion.android_id
import com.pixlee.pixleesdk.data.PXLPhoto
import com.pixlee.pixleesdk.data.api.KtxAnalyticsAPI
import com.pixlee.pixleesdk.enums.PXLWidgetType
import com.pixlee.pixleesdk.network.NetworkModule
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.IllegalArgumentException
import java.util.*

/**
 * Created by sungjun on 9/11/20.
 */

/**
 * The entry point for accessing analytics data.
 */
interface KtxAnalyticsDataSource {

    /***
     * Makes a call to the Pixlee Analytics API (limitless beyond). Appends api key, unique id and platform to the request body.
     * on success/error.
     * @param requestPath - path to hit (will be appended to the base Pixlee Analytics api endpoint)
     * @param body - key/values to be stored in analytics events
     * @return false if no api key set yet, true otherwise
     */
    suspend fun makeAnalyticsCall(requestPath: String, body: JSONObject): String

    /**
     * Analytics Name: Add To Cart
     * Document: https://developers.pixlee.com/reference#add-to-cart
     *
     * @param sku
     * @param price
     * @param quantity
     * @param currency
     */
    suspend fun addToCart(sku: String, price: String, quantity: Int, currency: String? = null): String

    /**
     * Analytics Name: Conversion
     * Document: https://developers.pixlee.com/reference#conversion
     *
     * @param cartContents
     * @param cartTotal
     * @param cartTotalQuantity
     * @param orderId
     * @param currency
     */
    suspend fun conversion(cartContents: ArrayList<HashMap<String, Any>>, cartTotal: String, cartTotalQuantity: Int, orderId: String?, currency: String?): String

    /**
     * openedLightbox Analytics
     * @param albumId albumId
     * @param photo This is to get PXLPhoto.albumPhotoId
     */
    suspend fun openedLightbox(albumId: String, pxlPhoto: PXLPhoto): String

    /**
     * openedLightbox Analytics
     *
     * @param albumId albumId
     * @param albumPhotoId PXLPhoto.albumPhotoId
     */
    suspend fun openedLightbox(albumId: String, albumPhotoId: String): String

    /**
     * actionClicked Analytics
     * @param albumId albumId
     * @param photo      This is to get PXLPhoto.albumPhotoId
     * @param actionLink
     */
    suspend fun actionClicked(albumId: String, pxlPhoto: PXLPhoto, actionLink: String): String

    /**
     * actionClicked Analytics
     * @param albumId albumId
     * @param albumPhotoId PXLPhoto.albumPhotoId
     * @param actionLink
     */
    suspend fun actionClicked(albumId: String, albumPhotoId: String, actionLink: String): String

    /**
     * openedWidget Analytics
     * @param albumId albumId
     * @param pxlPhotos all PXLPhoto data being shown in your RecyclerView, ListView or any View
     * @param perPage: current perPage variable used for requesting an api call [albums/{album_id}/photos, albums/from_sku]
     * @param page: current page count
     * @param widgetType: PXLWidgetType enum class
     * @return String: plain response text
     */
    suspend fun openedWidget(albumId: String, pxlPhotos: List<PXLPhoto>, perPage: Int, page: Int, pxlWidgetType: PXLWidgetType): String

    /**
     * openedWidget Analytics
     * @param albumId albumId
     * @param pxlPhotos all PXLPhoto data being shown in your RecyclerView, ListView or any View
     * @param perPage: current perPage variable used for requesting an api call [albums/{album_id}/photos, albums/from_sku]
     * @param page: current page count
     * @param widgetType: String
     * @return String: plain response text
     */
    suspend fun openedWidget(albumId: String, pxlPhotos: List<PXLPhoto>, perPage: Int, page: Int, pxlWidgetType: String): String

    /**
     * widgetVisible Analytics
     * @param albumId albumId
     * @param pxlPhotos all PXLPhoto data being shown in your RecyclerView, ListView or any View
     * @param perPage: current perPage variable used for requesting an api call [albums/{album_id}/photos, albums/from_sku]
     * @param page: current page count
     * @param widgetType: PXLWidgetType enum class
     * @return String: plain response text
     */
    suspend fun widgetVisible(albumId: String, pxlPhotos: List<PXLPhoto>, perPage: Int, page: Int, pxlWidgetType: PXLWidgetType): String

    /**
     * widgetVisible Analytics
     * @param albumId albumId
     * @param pxlPhotos all PXLPhoto data being shown in your RecyclerView, ListView or any View
     * @param perPage: current perPage variable used for requesting an api call [albums/{album_id}/photos, albums/from_sku]
     * @param page: current page count
     * @param widgetType: String
     * @return String: plain response text
     */
    suspend fun widgetVisible(albumId: String, pxlPhotos: List<PXLPhoto>, perPage: Int, page: Int, pxlWidgetType: String): String

    /**
     * loadMore Analytics
     * @param albumId albumId
     * @param pxlPhotos all PXLPhoto data being shown in your RecyclerView, ListView or any View
     * @param perPage: current perPage variable used for requesting an api call [albums/{album_id}/photos, albums/from_sku]
     * @param page: current page count
     * @return String: plain response text
     */
    suspend fun loadMore(albumId: String, pxlPhotos: List<PXLPhoto>, perPage: Int, page: Int): String
}

/**
 * This object transfers analytics data to the server using AnalyticsAPI.java, a Retrofit HTTP API class.
 */
class KtxAnalyticsRepository(var api: KtxAnalyticsAPI) : KtxAnalyticsDataSource {
    val messageForNoAlbumId = "missing album id. " +
            "When using PXLAlbum, you need to set album_id or when using PXLPdpAlbum, you should use loadNextPageOfPhotos() and get 200(http code) from the api so that this object will receive album_id from it"
    override suspend fun makeAnalyticsCall(requestPath: String, json: JSONObject): String {
        try {
            json.put("API_KEY", PXLClient.apiKey)
            json.put("uid", PXLClient.android_id)
            json.put("platform", "android")
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return api.makeAnalyticsCall(NetworkModule.analyticsUrl + requestPath, json.toString().toRequestBody(PXLClient.mediaType))
    }

    override suspend fun addToCart(sku: String, price: String, quantity: Int, currency: String?): String {
        return makeAnalyticsCall("events/addToCart", JSONObject().apply {
            put("product_sku", sku)
            put("price", price)
            put("quantity", quantity)
            if (currency != null) {
                put("currency", currency)
            }
            if (android_id != null) {
                put("fingerprint", android_id)
            }
        })
    }

    override suspend fun conversion(cartContents: ArrayList<HashMap<String, Any>>, cartTotal: String, cartTotalQuantity: Int, orderId: String?, currency: String?): String {
        return makeAnalyticsCall("events/conversion", JSONObject().apply {
            put("cart_contents", JSONArray(cartContents))
            put("cart_total", cartTotal)
            put("cart_total_quantity", cartTotalQuantity)
            if (currency != null) {
                put("currency", currency)
            }
            if (orderId != null) {
                put("order_id", orderId)
            }
        })
    }

    override suspend fun openedLightbox(albumId: String, albumPhotoId: String): String {
        return makeAnalyticsCall("events/openedLightbox", JSONObject().apply {
            put("album_id", albumId.toInt())
            put("album_photo_id", albumPhotoId.toInt())
        })
    }

    override suspend fun openedLightbox(albumId: String, pxlPhoto: PXLPhoto): String {
        return openedLightbox(pxlPhoto.id, pxlPhoto.albumPhotoId)
    }

    override suspend fun actionClicked(albumId: String, albumPhotoId: String, actionLink: String): String {
        return makeAnalyticsCall("events/actionClicked", JSONObject().apply {
            put("album_id", albumId.toInt())
            put("album_photo_id", albumPhotoId.toInt())
            put("action_link_url", actionLink)
        })
    }

    override suspend fun actionClicked(albumId: String, pxlPhoto: PXLPhoto, actionLink: String): String {
        return actionClicked(pxlPhoto.id, pxlPhoto.albumPhotoId, actionLink)
    }

    override suspend fun openedWidget(albumId: String, pxlPhotos: List<PXLPhoto>, perPage: Int, page: Int, pxlWidgetType: PXLWidgetType): String {
        return openedWidget(albumId, pxlPhotos, perPage, page, pxlWidgetType.type)
    }

    override suspend fun openedWidget(albumId: String, pxlPhotos: List<PXLPhoto>, perPage: Int, page: Int, pxlWidgetType: String): String {
        return widgetAPI("events/openedWidget", albumId, pxlPhotos, perPage, page, pxlWidgetType)
    }

    override suspend fun widgetVisible(albumId: String, pxlPhotos: List<PXLPhoto>, perPage: Int, page: Int, pxlWidgetType: PXLWidgetType): String {
        return widgetVisible(albumId, pxlPhotos, perPage, page, pxlWidgetType.type)
    }

    override suspend fun widgetVisible(albumId: String, pxlPhotos: List<PXLPhoto>, perPage: Int, page: Int, pxlWidgetType: String): String {
        return widgetAPI("events/widgetVisible", albumId, pxlPhotos, perPage, page, pxlWidgetType)
    }

    override suspend fun loadMore(albumId: String, pxlPhotos: List<PXLPhoto>, perPage: Int, page: Int): String {
        if (page < 2) {
            Log.w(PXLBaseAlbum.TAG, "first load detected")
            throw IllegalArgumentException("first load detected")
        }
        return widgetAPI("events/loadMore", albumId, pxlPhotos, perPage, page)
    }

    suspend fun widgetAPI(requestPath: String, albumId: String, pxlPhotos: List<PXLPhoto>, perPage: Int, page: Int, pxlWidgetType: String? = null): String {
        return makeAnalyticsCall(requestPath, JSONObject().apply {
            val stringBuilder = StringBuilder();
            pxlPhotos.forEach {
                if (stringBuilder.isNotEmpty()) {
                    stringBuilder.append(",");
                }
                stringBuilder.append(it.id)
            }

            try {
                if (pxlWidgetType != null) {
                    put("widget", pxlWidgetType)
                }
                put("album_id", Integer.parseInt(albumId))
                put("per_page", perPage)
                put("page", page)
                put("photos", stringBuilder.toString())
            } catch (e: Exception) {
                e.printStackTrace();
            }
        })
    }
}
