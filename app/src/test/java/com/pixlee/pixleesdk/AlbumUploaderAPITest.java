package com.pixlee.pixleesdk;

import com.pixlee.pixleeandroidsdk.BuildConfig;
import com.pixlee.pixleesdk.data.MediaResult;
import com.pixlee.pixleesdk.data.PhotoResult;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

import retrofit2.Response;

/**
 * Created by sungjun on 2020-02-03.
 */
public class AlbumUploaderAPITest extends BaseTest {
    final String ALBUM_ID = BuildConfig.PIXLEE_ALBUM_ID;
    final String TITLE = "seoulTower";
    final String EMAIL = "sungjun.app@gmail.com";
    final String USERNAME = "jun";
    final String PHOTO_URI = "https://www.dragonhilllodge.com/application/files/3215/5479/9457/fall_DHL5.jpg";
    final Boolean APPROVED = true;

    void ready(
            String title, String email, String username, String photoURI, Boolean approved,
            int httpCode, String body, PXLBaseAlbum.RequestHandlers<MediaResult> handlers) throws IOException {
        // mock the response
        intMockServer(
                httpCode,
                body
        );

        //init album
        PXLAlbum album = new PXLAlbum(ALBUM_ID, basicDS, analyticsDS);

        //fire an API
        Response<MediaResult> response = album.makePostUploadImage(
                title,
                email,
                username,
                photoURI,
                approved
        ).execute();
        album.processReponse(response, handlers);
    }


    @Test
    public void success() throws Exception {
        // mock the response
        ready(
                TITLE,
                EMAIL,
                USERNAME,
                PHOTO_URI,
                APPROVED,
                HttpURLConnection.HTTP_OK,
                getAPIJson("media.post.json"),
                new PXLBaseAlbum.RequestHandlers<MediaResult>() {
                    @Override
                    public void onComplete(MediaResult result) {
                        //success
                        Assert.assertTrue(result != null);
                        Assert.assertTrue(result.albumPhotoId != null);
                        Assert.assertTrue(result.connectedUserId != null);
                    }

                    @Override
                    public void onError(String error) {
                        //failure
                        Assert.fail(error);
                    }
                }
        );
    }

    @Test
    public void error_401() throws Exception {
        // mock the response
        ready(
                TITLE,
                EMAIL,
                USERNAME,
                PHOTO_URI,
                APPROVED,
                HttpURLConnection.HTTP_UNAUTHORIZED,
                getAPIJson("error.401.json"),
                new PXLBaseAlbum.RequestHandlers<MediaResult>() {
                    @Override
                    public void onComplete(MediaResult result) {
                        //fail
                        Assert.fail();
                    }

                    @Override
                    public void onError(String error) {
                        //an expected case
                        Assert.assertEquals("status: 401, error: Auth failed.", error);
                    }
                }
        );
    }

    @Test
    public void error_404() throws Exception {
        // mock the response
        ready(
                TITLE,
                EMAIL,
                USERNAME,
                PHOTO_URI,
                APPROVED,
                HttpURLConnection.HTTP_NOT_FOUND,
                getAPIJson("error.404.json"),
                new PXLBaseAlbum.RequestHandlers<MediaResult>() {
                    @Override
                    public void onComplete(MediaResult result) {
                        //fail
                        Assert.fail();
                    }

                    @Override
                    public void onError(String error) {
                        //an expected case
                        Assert.assertEquals("status: 404, error: Product does not exist.", error);
                    }
                }
        );
    }

    @Test
    public void error_nullEmail() throws Exception {
        // mock the response
        ready(
                TITLE,
                null,
                USERNAME,
                PHOTO_URI,
                APPROVED,
                HttpURLConnection.HTTP_NOT_FOUND,
                getAPIJson("media.post.error.400.json"),
                new PXLBaseAlbum.RequestHandlers<MediaResult>() {
                    @Override
                    public void onComplete(MediaResult result) {
                        //fail
                        Assert.fail();
                    }

                    @Override
                    public void onError(String error) {
                        //an expected case
                        Assert.assertEquals("status: 404", error);
                    }
                }
        );
    }

    @Test
    public void error_501() throws Exception {
        // mock the response
        ready(
                TITLE,
                null,
                USERNAME,
                PHOTO_URI,
                APPROVED,
                HttpURLConnection.HTTP_INTERNAL_ERROR,
                getAPIJson("media.post.error.500.json"),
                new PXLBaseAlbum.RequestHandlers<MediaResult>() {
                    @Override
                    public void onComplete(MediaResult result) {
                        //fail
                        Assert.fail();
                    }

                    @Override
                    public void onError(String error) {
                        //an expected case
                        Assert.assertEquals("status: 500", error);
                    }
                }
        );
    }
}