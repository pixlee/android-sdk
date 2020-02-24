package com.pixlee.pixleesdk.data.repository;

import android.util.Base64;

import com.pixlee.pixleesdk.PXLClient;
import com.pixlee.pixleesdk.PXLPhoto;
import com.pixlee.pixleesdk.data.MediaResult;
import com.pixlee.pixleesdk.data.PhotoResult;
import com.pixlee.pixleesdk.data.api.BasicAPI;
import com.pixlee.pixleesdk.network.HMAC;
import com.pixlee.pixleesdk.network.multiparts.MultipartUtil;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

/**
 * This object loads data from and uploads data to the server using BasicAPI.java, a Retrofit HTTP API class.
 */
public class BasicRepository implements BasicDataSource {
    BasicAPI api;

    public BasicRepository(BasicAPI api) {
        this.api = api;
    }

    private String getSignature(JSONObject json) {
        String signature = null;
        try {
            signature = HMAC.computeHmac(json.toString().replace("\\/", "/"), PXLClient.secretKey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return signature;
    }

    @Override
    public Call<PhotoResult> getPhotosWithSKU(String sku, String api_key, String filters, String sort, int per_page, int page) {
        return api.getPhotosWithSKU(sku, api_key, filters, sort, per_page, page);
    }

    @Override
    public Call<PhotoResult> getPhotosWithID(String album_id, String api_key, String filters, String sort, int per_page, int page) {
        return api.getPhotosWithID(album_id, api_key, filters, sort, per_page, page);
    }

    @Override
    public Call<PXLPhoto> getMedia(String album_photo_id, String api_key) {
        return api.getMedia(album_photo_id, api_key);
    }

    @Override
    public Call<MediaResult> postMedia(JSONObject json) {
        if (PXLClient.secretKey == null) {
            throw new IllegalArgumentException("no secretKey, please set secretKey before start");
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
        return api.postMedia(getSignature(json), PXLClient.apiKey, body);
    }

    @Override
    public Call<MediaResult> uploadImage(JSONObject json, String filePath) {
        List<MultipartBody.Part> bodyList = new ArrayList<>();
        File photo = new File(filePath);
        bodyList.add(new MultipartUtil().getMultipartBody("file", photo));
        bodyList.add(MultipartBody.Part.createFormData("json", json.toString()));
        return api.uploadImage(getSignature(json), PXLClient.apiKey, bodyList);
    }
}
