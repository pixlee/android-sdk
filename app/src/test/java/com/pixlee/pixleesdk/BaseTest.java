package com.pixlee.pixleesdk;

import com.pixlee.pixleeandroidsdk.BuildConfig;
import com.pixlee.pixleeandroidsdk.util.FileUtils;
import com.pixlee.pixleesdk.client.PXLClient;
import com.pixlee.pixleesdk.data.api.AnalyticsAPI;
import com.pixlee.pixleesdk.data.api.BasicAPI;
import com.pixlee.pixleesdk.data.repository.AnalyticsDataSource;
import com.pixlee.pixleesdk.data.repository.AnalyticsRepository;
import com.pixlee.pixleesdk.data.repository.BasicDataSource;
import com.pixlee.pixleesdk.data.repository.BasicRepository;
import com.pixlee.pixleesdk.network.NetworkModule;

import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

/**
 * Created by Sungjun on 2020-02-03.
 */
public class BaseTest {
    MockWebServer mockWebServer = new MockWebServer();

    BasicDataSource basicDS;
    AnalyticsDataSource analyticsDS;

    @Before
    public void setup() throws IOException {
        mockWebServer.start();

        basicDS = new BasicRepository(
                NetworkModule.provideRetrofit(
                        mockWebServer.url("/").toString(),
                        NetworkModule.provideOkHttpClient()
                ).create(BasicAPI.class)
        );

        analyticsDS = new AnalyticsRepository(
                NetworkModule.provideRetrofit(
                        mockWebServer.url("/").toString(),
                        NetworkModule.provideOkHttpClient()
                ).create(AnalyticsAPI.class)
        );
    }

    @After
    public void teardown() throws IOException {
        mockWebServer.shutdown();
    }


    /**
     * This to set HTTP-code and response body
     * PIXLEE_API_KEY and PIXLEE_SECRET_KEY will be set automatically
     *
     * @param httpCode
     * @param bodyResponse
     */
    void intMockServer(int httpCode, String bodyResponse) {
        intMockServer(com.pixlee.pixleeandroidsdk.BuildConfig.PIXLEE_API_KEY, BuildConfig.PIXLEE_SECRET_KEY, httpCode, bodyResponse);
    }

    /**
     * This to set PIXLEE_API_KEY, PIXLEE_SECRET_KEY, HTTP-code and response body
     *
     * @param apiKey
     * @param secretKey
     * @param httpCode
     * @param bodyResponse
     */
    void intMockServer(String apiKey, String secretKey, int httpCode, String bodyResponse) {
        MockResponse mock = new MockResponse()
                .setResponseCode(httpCode);
        if (bodyResponse != null)
            mock.setBody(bodyResponse);
        mockWebServer.enqueue(mock);

        PXLClient.Companion.initialize(apiKey, secretKey);
    }

    /**
     * This returns a string in /app/src/test/resources/api/{fileName}
     *
     * @param fileName
     * @return file string
     * @throws Exception
     */
    String getAPIJson(String fileName) throws Exception {
        return readContentFromFilePath("api", fileName);
    }

    /**
     * This returns a string in /app/src/test/resources/{folderName}/{fileName}
     *
     * @param folderName
     * @param fileName
     * @return file string
     * @throws Exception
     */
    String readContentFromFilePath(String folderName, String fileName) throws Exception {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(folderName + "/" + fileName);
        String s = FileUtils.readTextStream(inputStream);
        return s;
    }

}
