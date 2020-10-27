package com.pixlee.pixleesdk.client

import android.content.Context
import android.util.Log
import com.pixlee.pixleesdk.data.PXLPhoto
import com.pixlee.pixleesdk.data.PhotoResult
import com.pixlee.pixleesdk.data.repository.KtxAnalyticsDataSource
import com.pixlee.pixleesdk.data.repository.KtxBasicDataSource
import com.pixlee.pixleesdk.enums.PXLWidgetType
import kotlinx.coroutines.*
import org.json.JSONObject
import java.util.ArrayList
import java.util.HashMap

/**
 * Album and Analytics APIs supporting Kotlin.coroutines
 */
class PXLKtxAlbum : PXLKtxBaseAlbum {
    /***
     * This gets the PXLClient instance or creates it if it doesn't exist.
     * @param context - used for generating data sources of album and analytics.
     */
    constructor(context: Context) : super(context)

    /***
     * @param client - used for generating data sources of album and analytics.
     */
    constructor(client: PXLClient) : super(client)

    /***
     * @param ktxBasicDataSource: KtxBasicDataSource - used for firing album related APIs
     * @param ktxAnalyticsDataSource: KtxAnalyticsDataSource - used for firing analytics related APIs
     */
    constructor(ktxBasicDataSource: KtxBasicDataSource, ktxAnalyticsDataSource: KtxAnalyticsDataSource) : super(ktxBasicDataSource, ktxAnalyticsDataSource)

    /**
     * Use this if you want to clear the current search result.
     */
    fun resetState() {
        allPXLPhotos.clear()
        lastPageLoaded = 0
        hasMore = true
    }

    /**
     * retrieve the first page from Pixlee server.
     * Also you can use this method to reset the current search result in this class and start a new search
     */
    suspend fun getFirstPage(): PhotoResult {
        resetState()
        return getNextPage(false)
    }

    /**
     * retrieve the first page or the next page from Pixlee server
     * @param callLoadMoreAnalytics: default(false), true if you need to fire `loadMore` analytics after the API call
     */
    suspend fun getNextPage(callLoadMoreAnalytics: Boolean = true): PhotoResult {
        if (params == null) {
            throw IllegalArgumentException("params is not initiated. Please assign essential variables to params and retry this again")
        }

        val page = ++lastPageLoaded
        val isFirstPage = page == 1
        return params!!.let {
            when (it.searchId) {
                is SearchId.Album -> ktxBasicDataSource.getPhotosWithID(it.searchId.id, it.filterOptions, it.sortOptions, it.perPage, page, it.regionId)
                is SearchId.Product -> ktxBasicDataSource.getPhotosWithSKU(it.searchId.sku, it.filterOptions, it.sortOptions, it.perPage, page, it.regionId)
            }.apply {
                // update albumId with the albumId from the response
                currentAlbumId = albumId
                hasMore = next

                if (photos.isNotEmpty()) {
                    allPXLPhotos.addAll(photos)
                }

                Log.e("KTXAlbum", "before load ui, albumId: $albumId")
                if (!isFirstPage && callLoadMoreAnalytics) {
                    GlobalScope.launch {
                        try {
                            delay(1000)
                            Log.e("KTXAlbum", "before load supervisorScope , albumId: $albumId")
                            loadMore()
                            Log.e("KTXAlbum", "after load supervisorScope , albumId: $albumId")

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                Log.e("KTXAlbum", "after load ui , albumId: $albumId")
            }
        }
    }

    /**
     * get Photo data
     *
     * @param pxlPhoto: PXLPhoto
     * @return PXLPhoto
     */
    suspend fun getPhotoWithId(pxlPhoto: PXLPhoto): PXLPhoto {
        return ktxBasicDataSource.getMedia(pxlPhoto.albumPhotoId)
    }

    /**
     * get Photo data
     *
     * @param albumPhotoId: PXLPhoto.albumPhotoId
     * @return PXLPhoto
     */
    suspend fun getPhotoWithId(albumPhotoId: String): PXLPhoto {
        return ktxBasicDataSource.getMedia(albumPhotoId)
    }

    /**
     * Requests the next page of photos from the Pixlee album. Make sure to set perPage,
     * sort order, and filter options before calling.
     *
     * @param title    - title or caption of the photo being uploaded
     * @param email    - email address of the submitting user
     * @param username - username of the submitting user
     * @param photoURI - the URI of the photo/video being submitted (must be a public URI)
     * @param approved - boolean specifying whether the photo should be marked as approved on upload
     */
    suspend fun postMediaWithURI(title: String, email: String, username: String, photoURI: String, approved: Boolean) {
        ktxBasicDataSource.postMediaWithURI(JSONObject().apply {
            put("album_id", getAlbumIdParam())
            put("title", title)
            put("email", email)
            put("username", username)
            put("photo_uri", photoURI)
            put("approved", approved)
        })
    }

    /**
     * Requests the next page of photos from the Pixlee album. Make sure to set perPage,
     * sort order, and filter options before calling.
     *
     * @param title    - title or caption of the photo being uploaded
     * @param email    - email address of the submitting user
     * @param username - username of the submitting user
     * @param approved - boolean specifying whether the photo should be marked as approved on upload
     * @param localMediaPath - a local path of the media(photo or video)
     */
    suspend fun postMediaWithFile(title: String, email: String, username: String, approved: Boolean, localMediaPath: String) {
        ktxBasicDataSource.postMediaWithFile(JSONObject().apply {
            put("album_id", getAlbumIdParam())
            put("title", title)
            put("email", email)
            put("username", username)
            put("approved", approved)
        }, localMediaPath)
    }

    /**
     * openedWidget Analytics
     * @param widgetType: PXLWidgetType enum class
     * @return true: api called, false: cannot use this. please see the LogCat
     */
    suspend fun openedWidget(widgetType: PXLWidgetType) {
        ktxAnalyticsDataSource.openedWidget(getAlbumIdParam(),
                allPXLPhotos,
                getPerPageParam(),
                lastPageLoaded,
                widgetType)
    }

    /**
     * openedWidget Analytics
     * @param widgetType: String
     * @return true: api called, false: cannot use this. please see the LogCat
     */
    suspend fun openedWidget(widgetType: String) {
        ktxAnalyticsDataSource.openedWidget(getAlbumIdParam(),
                allPXLPhotos,
                getPerPageParam(),
                lastPageLoaded,
                widgetType)
    }

    /**
     * loadMore Analytics
     * After you use getNextPage(callLoadMoreAnalytics:Boolean = false), you must to call this analytics.
     */
    suspend fun loadMore() {
        ktxAnalyticsDataSource.loadMore(getAlbumIdParam(), allPXLPhotos, getPerPageParam(), lastPageLoaded)
    }

    /**
     * widgetVisible Analytics
     * @param widgetType: PXLWidgetType enum class
     * @return true: api called, false: cannot use this. please see the LogCat
     */
    suspend fun widgetVisible(widgetType: PXLWidgetType) {
        ktxAnalyticsDataSource.widgetVisible(getAlbumIdParam(), allPXLPhotos, getPerPageParam(), lastPageLoaded, widgetType)
    }

    /**
     * widgetVisible Analytics
     * @param widgetType: String
     * @return true: api called, false: cannot use this. please see the LogCat
     */
    suspend fun widgetVisible(widgetType: String) {
        ktxAnalyticsDataSource.widgetVisible(getAlbumIdParam(), allPXLPhotos, getPerPageParam(), lastPageLoaded, widgetType)
    }

    /**
     * openedLightbox Analytics
     *
     * @param albumPhotoId PXLPhoto.albumPhotoId
     */
    suspend fun openedLightbox(albumPhotoId: String) {
        ktxAnalyticsDataSource.openedLightbox(getAlbumIdParam(), albumPhotoId)
    }

    /**
     * openedLightbox Analytics
     *
     * @param photo This is to get PXLPhoto.albumPhotoId
     */
    suspend fun openedLightbox(pxlPhoto: PXLPhoto) {
        ktxAnalyticsDataSource.openedLightbox(getAlbumIdParam(), pxlPhoto.albumPhotoId)
    }

    /**
     * actionClicked Analytics
     *
     * @param albumPhotoId PXLPhoto.albumPhotoId
     * @param actionLink
     */
    suspend fun actionClicked(albumPhotoId: String, actionLink: String) {
        ktxAnalyticsDataSource.actionClicked(getAlbumIdParam(), albumPhotoId, actionLink)
    }

    /**
     * actionClicked Analytics
     *
     * @param photo      This is to get PXLPhoto.albumPhotoId
     * @param actionLink
     */
    suspend fun actionClicked(pxlPhoto: PXLPhoto, actionLink: String) {
        ktxAnalyticsDataSource.actionClicked(getAlbumIdParam(), pxlPhoto.albumPhotoId, actionLink)
    }

    /**
     * Analytics Name: Add To Cart
     * Document: https://developers.pixlee.com/reference#add-to-cart
     *
     * @param sku
     * @param price
     * @param quantity
     * @param currency
     */
    suspend fun addToCart(sku: String, price: String, quantity: Int, currency: String? = null) {
        ktxAnalyticsDataSource.addToCart(sku, price, quantity, currency)
    }

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
    suspend fun conversion(cartContents: ArrayList<HashMap<String, Any>>, cartTotal: String, cartTotalQuantity: Int, orderId: String? = null, currency: String? = null) {
        ktxAnalyticsDataSource.conversion(cartContents, cartTotal, cartTotalQuantity, orderId, currency)
    }

}