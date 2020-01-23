package com.pixlee.pixleesdk.data.repository;

import android.util.Base64;

import com.pixlee.pixleesdk.PXLClient;
import com.pixlee.pixleesdk.PXLPhoto;
import com.pixlee.pixleesdk.data.PhotoResult;
import com.pixlee.pixleesdk.data.api.BasicAPI;

import org.json.JSONObject;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.MediaType;
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
    public Call<PhotoResult> postMedia(String api_key, JSONObject json) {
        if (PXLClient.secretKey == null ) {
            throw new IllegalArgumentException("no secretKey, please set secretKey before start");
        }
        String signiture = null;
        try {
            signiture = computeHmac(json.toString().replace("\\/", "/" ), PXLClient.secretKey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
        return api.postMedia(signiture, api_key, body);
    }

    private String computeHmac(String baseString, String secretKey)
            throws NoSuchAlgorithmException, InvalidKeyException, IllegalStateException {
        SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(key);
        byte[] bytes = mac.doFinal(baseString.getBytes());
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }
}
