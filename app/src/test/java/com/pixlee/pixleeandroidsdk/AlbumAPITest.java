package com.pixlee.pixleeandroidsdk;

import com.pixlee.pixleesdk.PXLAlbumFilterOptions;
import com.pixlee.pixleesdk.PXLAlbumSortOptions;
import com.pixlee.pixleesdk.PXLAlbumSortType;
import com.pixlee.pixleesdk.PXLBaseAlbum;
import com.pixlee.pixleesdk.PXLPdpAlbum;
import com.pixlee.pixleesdk.PXLPhoto;
import com.pixlee.pixleesdk.PXLProduct;
import com.pixlee.pixleesdk.data.PhotoResult;

import org.junit.Assert;
import org.junit.Test;

import java.net.HttpURLConnection;
import java.util.List;

import retrofit2.Response;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class AlbumAPITest extends BaseTest {

    @Test
    public void testFromSKUSuccess() throws Exception {
        // mock the response
        intMockServer(
                HttpURLConnection.HTTP_OK,
                getAPIJson("albums.from_sku.json")
        );

        //fileter
        PXLAlbumFilterOptions fo = new PXLAlbumFilterOptions();
        fo.minTwitterFollowers = 0;
        fo.minInstagramFollowers = 0;

        //sort
        PXLAlbumSortOptions so = new PXLAlbumSortOptions();
        so.sortType = PXLAlbumSortType.RECENCY;
        so.descending = true;

        //init album
        String SKU = BuildConfig.PIXLEE_SKU;
        PXLPdpAlbum album = new PXLPdpAlbum(SKU, basicDS, analyticsDS);
        album.setPerPage(40);
        album.setFilterOptions(fo);
        album.setSortOptions(so);

        //fire an API
        Response<PhotoResult> response = album.makeCall().execute();
        album.setData(response.body(), new PXLBaseAlbum.RequestHandlers() {
            @Override
            public void DataLoadedHandler(List<PXLPhoto> photos) {
                //success
                String skuInResponse = null;
                for (PXLPhoto photo : photos) {
                    if (photo.products != null) {
                        for (PXLProduct product : photo.products) {
                            if (product.sku != null) {
                                skuInResponse = product.sku;
                                break;
                            }
                        }
                    }
                }
                Assert.assertEquals(SKU, skuInResponse);
            }

            @Override
            public void DataLoadFailedHandler(String error) {
                //failure
                Assert.fail(error);
            }
        });
    }
}