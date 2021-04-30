package com.pixlee.pixleeandroidsdk.ui.uioptions

import android.content.Context
import com.pixlee.pixleeandroidsdk.BuildConfig
import com.pixlee.pixleesdk.client.PXLKtxAlbum
import com.pixlee.pixleesdk.client.PXLKtxBaseAlbum
import com.pixlee.pixleesdk.data.PXLAlbumFilterOptions
import com.pixlee.pixleesdk.data.PXLAlbumSortOptions
import com.pixlee.pixleesdk.data.api.KtxAnalyticsAPI
import com.pixlee.pixleesdk.data.api.KtxBasicAPI
import com.pixlee.pixleesdk.data.repository.KtxAnalyticsDataSource
import com.pixlee.pixleesdk.data.repository.KtxAnalyticsRepository
import com.pixlee.pixleesdk.data.repository.KtxBasicDataSource
import com.pixlee.pixleesdk.data.repository.KtxBasicRepository
import com.pixlee.pixleesdk.enums.PXLAlbumSortType
import com.pixlee.pixleesdk.network.NetworkModule
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer

/**
 * Created by sungjun on 4/29/21.
 */
class MockAlbumUtil {
    val mockWebServer: MockWebServer by lazy {
        MockWebServer()
    }

    lateinit var basicDS: KtxBasicDataSource
    lateinit var analyticsDS: KtxAnalyticsDataSource
    fun setupMockedWebServer() {
        mockWebServer.start()

        basicDS = KtxBasicRepository(
                NetworkModule.provideRetrofit(
                        mockWebServer.url("/").toString(),
                        NetworkModule.provideOkHttpClient()
                ).create(KtxBasicAPI::class.java)
        )

        analyticsDS = KtxAnalyticsRepository(
                NetworkModule.provideRetrofit(
                        mockWebServer.url("/").toString(),
                        NetworkModule.provideOkHttpClient()
                ).create(KtxAnalyticsAPI::class.java)
        )
    }

    fun intMockServer(context: Context?, assetFileName: String) {
        val mock = MockResponse().setResponseCode(200)
        val bodyResponse = getJsonFromAsset(context, assetFileName)
        mock.setBody(bodyResponse)
        mockWebServer.enqueue(mock)
    }

    fun release() {
        mockWebServer.shutdown()
    }

    val album by lazy {
        PXLKtxAlbum(basicDS, analyticsDS).apply {
            params = PXLKtxBaseAlbum.Params(
                    // album images
                    perPage = 15,
                    searchId = PXLKtxBaseAlbum.SearchId.Album(BuildConfig.PIXLEE_ALBUM_ID), // product images: searchId = PXLKtxBaseAlbum.SearchId.Product(BuildConfig.PIXLEE_SKU),
                    filterOptions = PXLAlbumFilterOptions().apply {
                        // hasProduct and hasPermission are often used together for displaying photos with tagged products and gotten the permission from their creators
                        // if you don't see any photos after the loading is done, go to https://app.pixlee.com/app#albums/{your album id} and make sure your photos have the same filter conditions as your filterOptions.
                        hasProduct = true
                        hasPermission = true

                        // more filter options
                        // - hasPermission = true
                        // - inStockOnly = true
                        // - .. there are more. Please check README or PXLAlbumFilterOptions class for more filter options
                    },
                    sortOptions = PXLAlbumSortOptions().apply {
                        sortType = PXLAlbumSortType.RECENCY
                        descending = false
                    }
            )
        }
    }

    fun getJsonFromAsset(context: Context?, fileName: String): String {
        return context?.assets?.open("json/$fileName")?.bufferedReader()?.use { it.readText() }
                ?: ""
    }
}