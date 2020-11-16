package com.pixlee.pixleesdk.client

import android.content.Context
import com.pixlee.pixleesdk.data.PXLAlbumFilterOptions
import com.pixlee.pixleesdk.data.PXLAlbumSortOptions
import com.pixlee.pixleesdk.data.PXLPhoto
import com.pixlee.pixleesdk.data.repository.KtxAnalyticsDataSource
import com.pixlee.pixleesdk.data.repository.KtxBasicDataSource

/**
 * this class is to hide variables that
 * PXLKtxAlbum, extending this class, doesn't need to know keeping PXLKtxAlbum's codes simple.
 */
open class PXLKtxBaseAlbum {
    val ktxBasicDataSource: KtxBasicDataSource
    val ktxAnalyticsDataSource: KtxAnalyticsDataSource

    constructor(context: Context) {
        val client = PXLClient.getInstance(context)
        ktxBasicDataSource = client.ktxBasicDataSource
        ktxAnalyticsDataSource = client.ktxAnalyticsDataSource
    }

    constructor(client: PXLClient) {
        ktxBasicDataSource = client.ktxBasicDataSource
        ktxAnalyticsDataSource = client.ktxAnalyticsDataSource
    }

    constructor(ktxBasicDataSource: KtxBasicDataSource,
                ktxAnalyticsDataSource: KtxAnalyticsDataSource) {
        this.ktxBasicDataSource = ktxBasicDataSource
        this.ktxAnalyticsDataSource = ktxAnalyticsDataSource
    }

    // managed by this SDK internally for analytics
    internal val allPXLPhotos: ArrayList<PXLPhoto> = ArrayList()

    // managed by this SDK internally for analytics
    internal var currentAlbumId: Int? = null

    // managed by this SDK internally for pagination
    internal var lastPageLoaded: Int = 0

    // managed by this SDK internally for pagination
    internal  var hasMore: Boolean = true


    // this is to force the client to choose between Album and Product.
    // Because of this sealed class, we can use only one class in Kotlin while the java version uses PXLAlbum and PXLPdpAlbum.
    sealed class SearchId {
        class Album(val id: String) : SearchId()
        class Product(val sku: String) : SearchId()
    }

    // request parameters
    class Params(val searchId: SearchId,
                 var perPage: Int = 30,
                 var filterOptions: PXLAlbumFilterOptions? = null,
                 var sortOptions: PXLAlbumSortOptions? = null,
                 var regionId:Int? = null)

    var params: Params? = null
        set(value) {
            // when param variable is set a new value, we clear all variables.
            // This is because the meaning of changing the 'params' says the API search starts from the scratch.
            field = value
            currentAlbumId = null
            lastPageLoaded = 0
            allPXLPhotos.clear()
        }

    fun getAlbumIdParam():Int {
        return currentAlbumId ?: throw java.lang.IllegalArgumentException("missing album id")
    }

    fun getPerPageParam():Int{
        return params?.perPage?: throw IllegalArgumentException("params is not initiated. retrieve photos by using getFirstPage() first then try this again.")
    }
}