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
     * @param regionId: region id that differentiates analytics events by region
     * @param body - key/values to be stored in analytics events
     * @return false if no api key set yet, true otherwise
     */
    suspend fun makeAnalyticsCall(requestPath: String, regionId: Int? = null, body: JSONObject): String

    /**
     * Analytics Name: Add To Cart
     * Document: https://developers.pixlee.com/reference#add-to-cart
     *
     * @param sku
     * @param price
     * @param quantity
     * @param currency
     * @param regionId: region id that differentiates analytics events by region
     */
    suspend fun addToCart(sku: String, price: String, quantity: Int, currency: String? = null, regionId: Int?): String

    /**
     * Analytics Name: Conversion
     * Document: https://developers.pixlee.com/reference#conversion
     * @param regionId: region id that differentiates analytics events by region
     *
     * @param cartContents
     * @param cartTotal
     * @param cartTotalQuantity
     * @param orderId
     * @param currency
     */
    suspend fun conversion(cartContents: ArrayList<HashMap<String, Any>>, cartTotal: String, cartTotalQuantity: Int, orderId: String?, currency: String?, regionId: Int?): String

    /**
     * openedLightbox Analytics
     * @param albumId albumId
     * @param photo This is to get PXLPhoto.albumPhotoId
     * @param regionId: region id that differentiates analytics events by region
     */
    suspend fun openedLightbox(albumId: Int, pxlPhoto: PXLPhoto, regionId: Int? = null): String

    /**
     * openedLightbox Analytics
     *
     * @param albumId albumId
     * @param albumPhotoId PXLPhoto.albumPhotoId
     * @param regionId: region id that differentiates analytics events by region
     */
    suspend fun openedLightbox(albumId: Int, albumPhotoId: String, regionId: Int? = null): String

    /**
     * actionClicked Analytics
     * @param albumId albumId
     * @param photo      This is to get PXLPhoto.albumPhotoId
     * @param regionId: region id that differentiates analytics events by region
     * @param actionLink
     */
    suspend fun actionClicked(albumId: Int, pxlPhoto: PXLPhoto, actionLink: String, regionId: Int? = null): String

    /**
     * actionClicked Analytics
     * @param albumId albumId
     * @param albumPhotoId PXLPhoto.albumPhotoId
     * @param regionId: region id that differentiates analytics events by region
     * @param actionLink
     */
    suspend fun actionClicked(albumId: Int, albumPhotoId: String, actionLink: String, regionId: Int? = null): String

    /**
     * openedWidget Analytics
     * @param albumId albumId
     * @param pxlPhotos all PXLPhoto data being shown in your RecyclerView, ListView or any View
     * @param perPage: current perPage variable used for requesting an api call [albums/{album_id}/photos, albums/from_sku]
     * @param page: current page count
     * @param widgetType: PXLWidgetType enum class
     * @param regionId: region id that differentiates analytics events by region
     * @return String: plain response text
     */
    suspend fun openedWidget(albumId: Int, pxlPhotos: List<PXLPhoto>, perPage: Int, page: Int, pxlWidgetType: PXLWidgetType, regionId: Int? = null): String

    /**
     * openedWidget Analytics
     * @param albumId albumId
     * @param pxlPhotos all PXLPhoto data being shown in your RecyclerView, ListView or any View
     * @param perPage: current perPage variable used for requesting an api call [albums/{album_id}/photos, albums/from_sku]
     * @param page: current page count
     * @param widgetType: String
     * @param regionId: region id that differentiates analytics events by region
     * @return String: plain response text
     */
    suspend fun openedWidget(albumId: Int, pxlPhotos: List<PXLPhoto>, perPage: Int, page: Int, pxlWidgetType: String, regionId: Int? = null): String

    /**
     * widgetVisible Analytics
     * @param albumId albumId
     * @param pxlPhotos all PXLPhoto data being shown in your RecyclerView, ListView or any View
     * @param perPage: current perPage variable used for requesting an api call [albums/{album_id}/photos, albums/from_sku]
     * @param page: current page count
     * @param widgetType: PXLWidgetType enum class
     * @param regionId: region id that differentiates analytics events by region
     * @return String: plain response text
     */
    suspend fun widgetVisible(albumId: Int, pxlPhotos: List<PXLPhoto>, perPage: Int, page: Int, pxlWidgetType: PXLWidgetType, regionId: Int? = null): String

    /**
     * widgetVisible Analytics
     * @param albumId albumId
     * @param pxlPhotos all PXLPhoto data being shown in your RecyclerView, ListView or any View
     * @param perPage: current perPage variable used for requesting an api call [albums/{album_id}/photos, albums/from_sku]
     * @param page: current page count
     * @param widgetType: String
     * @param regionId: region id that differentiates analytics events by region
     * @return String: plain response text
     */
    suspend fun widgetVisible(albumId: Int, pxlPhotos: List<PXLPhoto>, perPage: Int, page: Int, pxlWidgetType: String, regionId: Int? = null): String

    /**
     * loadMore Analytics
     * @param albumId albumId
     * @param pxlPhotos all PXLPhoto data being shown in your RecyclerView, ListView or any View
     * @param perPage: current perPage variable used for requesting an api call [albums/{album_id}/photos, albums/from_sku]
     * @param page: current page count
     * @param regionId: region id that differentiates analytics events by region
     * @return String: plain response text
     */
    suspend fun loadMore(albumId: Int, pxlPhotos: List<PXLPhoto>, perPage: Int, page: Int, regionId: Int? = null): String
}

/**
 * This object transfers analytics data to the server using AnalyticsAPI.java, a Retrofit HTTP API class.
 */
class KtxAnalyticsRepository(var api: KtxAnalyticsAPI) : KtxAnalyticsDataSource {
    val messageForNoAlbumId = "missing album id. " +
            "When using PXLAlbum, you need to set album_id or when using PXLPdpAlbum, you should use loadNextPageOfPhotos() and get 200(http code) from the api so that this object will receive album_id from it"

    override suspend fun makeAnalyticsCall(requestPath: String, regionId: Int?, json: JSONObject): String {
        try {
            regionId?.also { json.put("region_id", it) }
            json.put("API_KEY", PXLClient.apiKey)
            json.put("uid", PXLClient.android_id)
            json.put("platform", "android")
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return api.makeAnalyticsCall(NetworkModule.analyticsUrl + requestPath, json.toString().toRequestBody(PXLClient.mediaType))
    }

    override suspend fun addToCart(sku: String, price: String, quantity: Int, currency: String?, regionId: Int?): String {
        return makeAnalyticsCall("events/addToCart", regionId, JSONObject().apply {
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

    override suspend fun conversion(cartContents: ArrayList<HashMap<String, Any>>, cartTotal: String, cartTotalQuantity: Int, orderId: String?, currency: String?, regionId: Int?): String {
        return makeAnalyticsCall("events/conversion", regionId, JSONObject().apply {
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

    override suspend fun openedLightbox(albumId: Int, albumPhotoId: String, regionId: Int?): String {
        return makeAnalyticsCall("events/openedLightbox", regionId, JSONObject().apply {
            put("album_id", albumId.toInt())
            put("album_photo_id", albumPhotoId.toInt())
        })
    }

    override suspend fun openedLightbox(albumId: Int, pxlPhoto: PXLPhoto, regionId: Int?): String {
        return openedLightbox(albumId, pxlPhoto.albumPhotoId, regionId)
    }

    override suspend fun actionClicked(albumId: Int, albumPhotoId: String, actionLink: String, regionId: Int?): String {
        return makeAnalyticsCall("events/actionClicked", regionId, JSONObject().apply {
            put("album_id", albumId.toInt())
            put("album_photo_id", albumPhotoId.toInt())
            put("action_link_url", actionLink)
        })
    }

    override suspend fun actionClicked(albumId: Int, pxlPhoto: PXLPhoto, actionLink: String, regionId: Int?): String {
        return actionClicked(albumId, pxlPhoto.albumPhotoId, actionLink, regionId)
    }

    override suspend fun openedWidget(albumId: Int, pxlPhotos: List<PXLPhoto>, perPage: Int, page: Int, pxlWidgetType: PXLWidgetType, regionId: Int?): String {
        return openedWidget(albumId, pxlPhotos, perPage, page, pxlWidgetType.type, regionId)
    }

    override suspend fun openedWidget(albumId: Int, pxlPhotos: List<PXLPhoto>, perPage: Int, page: Int, pxlWidgetType: String, regionId: Int?): String {
        return widgetAPI("events/openedWidget", albumId, pxlPhotos, perPage, page, pxlWidgetType, regionId)
    }

    override suspend fun widgetVisible(albumId: Int, pxlPhotos: List<PXLPhoto>, perPage: Int, page: Int, pxlWidgetType: PXLWidgetType, regionId: Int?): String {
        return widgetVisible(albumId, pxlPhotos, perPage, page, pxlWidgetType.type, regionId)
    }

    override suspend fun widgetVisible(albumId: Int, pxlPhotos: List<PXLPhoto>, perPage: Int, page: Int, pxlWidgetType: String, regionId: Int?): String {
        return widgetAPI("events/widgetVisible", albumId, pxlPhotos, perPage, page, pxlWidgetType, regionId)
    }

    override suspend fun loadMore(albumId: Int, pxlPhotos: List<PXLPhoto>, perPage: Int, page: Int, regionId: Int?): String {
        if (page < 2) {
            Log.w(PXLBaseAlbum.TAG, "first load detected")
            throw IllegalArgumentException("first load detected")
        }
        return widgetAPI("events/loadMore", albumId, pxlPhotos, perPage, page, null, regionId)
    }

    suspend fun widgetAPI(requestPath: String, albumId: Int, pxlPhotos: List<PXLPhoto>, perPage: Int, page: Int, pxlWidgetType: String? = null, regionId: Int?): String {
        return makeAnalyticsCall(requestPath, regionId, JSONObject().apply {
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
                put("album_id", albumId)
                put("per_page", perPage)
                put("page", page)
                put("photos", stringBuilder.toString())
            } catch (e: Exception) {
                e.printStackTrace();
            }
        })
    }
}
