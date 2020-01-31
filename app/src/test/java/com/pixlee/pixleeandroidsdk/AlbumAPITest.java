package com.pixlee.pixleeandroidsdk;

import com.pixlee.pixleeandroidsdk.util.FileUtils;
import com.pixlee.pixleesdk.PXLAlbumFilterOptions;
import com.pixlee.pixleesdk.PXLAlbumSortOptions;
import com.pixlee.pixleesdk.PXLAlbumSortType;
import com.pixlee.pixleesdk.PXLBaseAlbum;
import com.pixlee.pixleesdk.PXLClient;
import com.pixlee.pixleesdk.PXLPdpAlbum;
import com.pixlee.pixleesdk.PXLPhoto;
import com.pixlee.pixleesdk.data.api.AnalyticsAPI;
import com.pixlee.pixleesdk.data.api.BasicAPI;
import com.pixlee.pixleesdk.data.repository.AnalyticsRepository;
import com.pixlee.pixleesdk.data.repository.BasicRepository;
import com.pixlee.pixleesdk.network.NetworkModule;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class AlbumAPITest {
    private MockWebServer mockWebServer = new MockWebServer();

    private BasicAPI basicRepo;
    private AnalyticsAPI analyticsRepo;

    @Before
    public void setup() throws IOException {
        mockWebServer.start();

        basicRepo = NetworkModule.provideRetrofit(
                mockWebServer.url("/").toString(),
                NetworkModule.provideOkHttpClient()
        ).create(BasicAPI.class);

        analyticsRepo = NetworkModule.provideRetrofit(
                mockWebServer.url("/").toString(),
                NetworkModule.provideOkHttpClient()
        ).create(AnalyticsAPI.class);
    }

    @After
    public void teardown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void testAppVersions() throws Exception {
        // Assign
        MockResponse response = new MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(readContentFromFilePath());
        mockWebServer.enqueue(response);

        PXLClient.initialize(BuildConfig.PIXLEE_API_KEY, BuildConfig.PIXLEE_SECRET_KEY);

        PXLAlbumFilterOptions fo = new PXLAlbumFilterOptions();
        fo.minTwitterFollowers = 0;
        fo.minInstagramFollowers = 0;

        PXLAlbumSortOptions so = new PXLAlbumSortOptions();
        so.sortType = PXLAlbumSortType.RECENCY;
        so.descending = true;

        PXLPdpAlbum album = new PXLPdpAlbum(BuildConfig.PIXLEE_SKU, new BasicRepository(basicRepo), new AnalyticsRepository(analyticsRepo));
        album.setPerPage(40);
        album.setFilterOptions(fo);
        album.setSortOptions(so);

        album.loadNextPageOfPhotosBase(new PXLBaseAlbum.RequestHandlers() {
            @Override
            public void DataLoadedHandler(List<PXLPhoto> photos) {
                System.out.println("hong photos:" + photos);
                if (photos != null) {
                    System.out.println("hong photos.size():" + photos.size());
                }
            }

            @Override
            public void DataLoadFailedHandler(String error) {
                System.out.println("error:" + error);
            }
        });
    }

    String readContentFromFilePath() throws Exception {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("api/albums.from_sku.json");
        String s = FileUtils.readTextStream(inputStream);
        System.out.println("file text: " + s);
        return s;
    }

}