package com.pixlee.pixleesdk

import com.pixlee.pixleeandroidsdk.BuildConfig
import com.pixlee.pixleesdk.client.PXLAlbum
import com.pixlee.pixleesdk.client.PXLBaseAlbum.RequestHandlers
import com.pixlee.pixleesdk.data.PXLAlbumFilterOptions
import com.pixlee.pixleesdk.data.PXLAlbumSortOptions
import com.pixlee.pixleesdk.data.PXLPhoto
import com.pixlee.pixleesdk.enums.PXLAlbumSortType
import com.pixlee.pixleesdk.ui.viewholder.getDiscountPercentage
import org.junit.Assert
import org.junit.Test
import java.io.IOException
import java.net.HttpURLConnection
import java.util.*

/**
 * Created by sungjun on 2020-02-03.
 */
class QueryAlbumAPITest : BaseTest() {
    var ALBUM_ID = BuildConfig.PIXLEE_ALBUM_ID
    @Throws(IOException::class)
    fun ready(httpCode: Int, body: String?, handlers: RequestHandlers<ArrayList<PXLPhoto>>) {
        // mock the response
        intMockServer(
                httpCode,
                body
        )

        // filter
        val fo = PXLAlbumFilterOptions()
        fo.minTwitterFollowers = 0
        fo.minInstagramFollowers = 0

        // sort
        val so = PXLAlbumSortOptions()
        so.sortType = PXLAlbumSortType.RECENCY
        so.descending = true

        // init album
        val album = PXLAlbum(ALBUM_ID, basicDS, analyticsDS)
        album.setPerPage(40)
        album.setFilterOptions(fo)
        album.setSortOptions(so)

        // fire an API
        val response = album.makeGetAlbumCall(handlers).execute()
        album.setData(response, handlers)
    }

    @Test
    @Throws(Exception::class)
    fun success() {
        // mock the response
        intMockServer(
                HttpURLConnection.HTTP_OK,
                getAPIJson("albums.from_sku.json")
        )

        // filter
        val fo = PXLAlbumFilterOptions()
        fo.minTwitterFollowers = 0
        fo.minInstagramFollowers = 0

        // sort
        val so = PXLAlbumSortOptions()
        so.sortType = PXLAlbumSortType.RECENCY
        so.descending = true

        // init album
        val album = PXLAlbum(ALBUM_ID, basicDS, analyticsDS)
        album.setPerPage(40)
        album.setFilterOptions(fo)
        album.setSortOptions(so)
        val handlers: RequestHandlers<ArrayList<PXLPhoto>> = object : RequestHandlers<ArrayList<PXLPhoto>> {
            override fun onComplete(photos: ArrayList<PXLPhoto>) {
                // success
                Assert.assertEquals(ALBUM_ID, album.album_id)
            }

            override fun onError(error: String) {
                // failure
                Assert.fail(error)
            }
        }
        // fire an API
        val response = album.makeGetAlbumCall(handlers).execute()
        album.setData(response, handlers)
    }

    @Test
    @Throws(Exception::class)
    fun success_with_sales_price() {
        // mock the response
        ready(
                HttpURLConnection.HTTP_OK,
                getAPIJson("pxl_product.json"),
                object : RequestHandlers<ArrayList<PXLPhoto>> {
                    override fun onComplete(photos: ArrayList<PXLPhoto>) {
                        // success
                        Assert.assertTrue(photos.isNotEmpty())
                        Assert.assertTrue(photos.first().products.isNotEmpty())
                        Assert.assertEquals(75.toBigDecimal(), photos.first().products.first().getDiscountPercentage())
                    }

                    override fun onError(error: String) {
                        // failure
                        Assert.fail(error)
                    }
                }
        )
    }

    @Test
    @Throws(Exception::class)
    fun success_manyFormats() {
        // mock the response
        ready(
                HttpURLConnection.HTTP_OK,
                getAPIJson("albums.from_sku.many_formats.json"),
                object : RequestHandlers<ArrayList<PXLPhoto>> {
                    override fun onComplete(photos: ArrayList<PXLPhoto>) {
                        // success
                        Assert.assertTrue(photos.size > 0)
                        Assert.assertEquals("yosemite", photos[0].photoTitle)
                        Assert.assertEquals("382949209", photos[0].albumPhotoId)
                        Assert.assertEquals(12608567, photos[0].albumId.toLong())
                    }

                    override fun onError(error: String) {
                        // failure
                        Assert.fail(error)
                    }
                }
        )
    }

    @Test
    @Throws(Exception::class)
    fun error_401() {
        // mock the response
        ready(
                HttpURLConnection.HTTP_UNAUTHORIZED,
                getAPIJson("error.401.json"),
                object : RequestHandlers<ArrayList<PXLPhoto>> {
                    override fun onComplete(photos: ArrayList<PXLPhoto>) {
                        // fail
                        Assert.fail()
                    }

                    override fun onError(error: String) {
                        // an expected case
                        Assert.assertEquals("status: 401, error: Auth failed.", error)
                    }
                }
        )
    }

    @Test
    @Throws(Exception::class)
    fun error_404() {
        // mock the response
        ready(
                HttpURLConnection.HTTP_NOT_FOUND,
                getAPIJson("error.404.json"),
                object : RequestHandlers<ArrayList<PXLPhoto>> {
                    override fun onComplete(photos: ArrayList<PXLPhoto>) {
                        // fail
                        Assert.fail()
                    }

                    override fun onError(error: String) {
                        // an expected case
                        Assert.assertEquals("status: 404, error: Product does not exist.", error)
                    }
                }
        )
    }

    @Test
    @Throws(Exception::class)
    fun error_emptyBody() {
        // mock the response
        ready(
                HttpURLConnection.HTTP_NOT_FOUND,
                null,
                object : RequestHandlers<ArrayList<PXLPhoto>> {
                    override fun onComplete(result: ArrayList<PXLPhoto>) {
                        // fail if
                        Assert.fail()
                    }
                    override fun onError(error: String) {
                        // failure
                        Assert.assertEquals("status: 404", error)
                    }


                }
        )
    }

    @Test
    @Throws(Exception::class)
    fun error_501() {
        // mock the response
        ready(
                HttpURLConnection.HTTP_INTERNAL_ERROR,
                getAPIJson("error.500.json"),
                object : RequestHandlers<ArrayList<PXLPhoto>> {
                    override fun onComplete(photos: ArrayList<PXLPhoto>) {
                        //this cannot be succeeded
                        Assert.fail()
                    }

                    override fun onError(error: String) {
                        // failure
                        Assert.assertEquals("status: 500, error: Internal error.", error)
                    }
                }
        )
    }
}