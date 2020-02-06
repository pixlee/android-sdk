package com.pixlee.pixleesdk;

import android.util.Log;

import com.pixlee.pixleeandroidsdk.BuildConfig;
import com.pixlee.pixleesdk.data.PhotoResult;

import org.junit.Assert;
import org.junit.Test;

import java.net.HttpURLConnection;
import java.util.List;

import retrofit2.Response;

/**
 * Created by sungjun on 2020-02-03.
 */
public class QueryFromSkuAPITest extends BaseTest {
    void ready(int httpCode, String body, PXLBaseAlbum.RequestHandlers handlers) throws Exception {
        // mock the response
        intMockServer(
                httpCode,
                body
        );

        // fileter
        PXLAlbumFilterOptions fo = new PXLAlbumFilterOptions();
        fo.minTwitterFollowers = 0;
        fo.minInstagramFollowers = 0;

        // sort
        PXLAlbumSortOptions so = new PXLAlbumSortOptions();
        so.sortType = PXLAlbumSortType.RECENCY;
        so.descending = true;

        // init album
        PXLPdpAlbum album = new PXLPdpAlbum(BuildConfig.PIXLEE_SKU, basicDS, analyticsDS);
        album.setPerPage(40);
        album.setFilterOptions(fo);
        album.setSortOptions(so);

        // fire an API
        Response<PhotoResult> response = album.makeGetAlbumCall().execute();
        album.setData(response, handlers);
    }

    @Test
    public void success() throws Exception {
        // mock the response
        ready(
                HttpURLConnection.HTTP_OK,
                getAPIJson("albums.from_sku.json"),
                new PXLBaseAlbum.RequestHandlers<List<PXLPhoto>>() {
                    @Override
                    public void onComplete(List<PXLPhoto> photos) {
                        // success
                        Assert.assertTrue(photos.size() > 0);
                    }

                    @Override
                    public void onError(String error) {
                        // failure
                        Assert.fail(error);
                    }
                }
        );
    }

    @Test
    public void success_manyFormats() throws Exception {
        // mock the response
        ready(
                HttpURLConnection.HTTP_OK,
                getAPIJson("albums.from_sku.many_formats.json"),
                new PXLBaseAlbum.RequestHandlers<List<PXLPhoto>>() {
                    @Override
                    public void onComplete(List<PXLPhoto> photos) {
                        // success
                        Assert.assertTrue(photos.size() > 0);
                    }

                    @Override
                    public void onError(String error) {
                        // failure
                        Assert.fail(error);
                    }
                }
        );
    }

    @Test
    public void error_401() throws Exception {
        // mock the response
        ready(
                HttpURLConnection.HTTP_UNAUTHORIZED,
                getAPIJson("error.401.json"),
                new PXLBaseAlbum.RequestHandlers<List<PXLPhoto>>() {
                    @Override
                    public void onComplete(List<PXLPhoto> photos) {
                        // if this is called, there is a problem
                        Assert.fail();
                    }

                    @Override
                    public void onError(String error) {
                        // this test has to get an 401 error
                        Assert.assertEquals("status: 401, error: Auth failed.", error);
                    }
                }
        );
    }

    @Test
    public void error_404() throws Exception {
        // mock the response
        ready(
                HttpURLConnection.HTTP_NOT_FOUND,
                getAPIJson("error.404.json"),
                new PXLBaseAlbum.RequestHandlers<List<PXLPhoto>>() {
                    @Override
                    public void onComplete(List<PXLPhoto> photos) {
                        // if this is called, there is a problem
                        Assert.fail();
                    }

                    @Override
                    public void onError(String error) {
                        // this test has to get an 404 error
                        Assert.assertEquals("status: 404, error: Product does not exist.", error);
                    }
                }
        );
    }

    @Test
    public void error_emptyBody() throws Exception {
        // mock the response
        ready(
                HttpURLConnection.HTTP_NOT_FOUND,
                null,
                new PXLBaseAlbum.RequestHandlers<List<PXLPhoto>>() {
                    @Override
                    public void onComplete(List<PXLPhoto> photos) {
                        // if this is called, there is a problem
                        Assert.fail();
                    }

                    @Override
                    public void onError(String error) {
                        // this test has to get an 404 error
                        Assert.assertEquals("status: 404", error);
                    }
                }
        );
    }

    @Test
    public void error_501() throws Exception {
        // mock the response
        ready(
                HttpURLConnection.HTTP_INTERNAL_ERROR,
                getAPIJson("error.500.json"),
                new PXLBaseAlbum.RequestHandlers<List<PXLPhoto>>() {
                    @Override
                    public void onComplete(List<PXLPhoto> photos) {
                        // if this is called, there is a problem
                        Assert.fail();
                    }

                    @Override
                    public void onError(String error) {
                        // this test has to get an 500 error
                        Assert.assertEquals("status: 500, error: Internal error.", error);
                    }
                }
        );
    }
}