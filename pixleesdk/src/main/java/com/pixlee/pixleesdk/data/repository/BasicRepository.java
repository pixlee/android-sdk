package com.pixlee.pixleesdk.data.repository;

import android.util.Base64;

import com.pixlee.pixleesdk.PXLClient;
import com.pixlee.pixleesdk.data.api.BasicAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;

public class BasicRepository implements BasicDataSource {
    BasicAPI api;

    public BasicRepository(BasicAPI api) {
        this.api = api;
    }


    @Override
    public Call<String> getPhotosWithSKU(String sku, String api_key, String filters, String sort, int per_page, int page) {
        return api.getPhotosWithSKU(sku, api_key, filters, sort, per_page, page);
    }

    @Override
    public Call<String> getPhotosWithID(String id, String api_key, String filters, String sort, int per_page, int page) {
        return api.getPhotosWithID(id, api_key, filters, sort, per_page, page);
    }

    @Override
    public Call<String> getMedia(String id, String api_key) {
        return api.getMedia(id, api_key);
    }

    @Override
    public Call<String> postMedia(String api_key, String album_id, String title, String email, String username, String photo_uri, boolean approved) {
        String signiture = null;
        JSONObject json = new JSONObject();
        try {

            json.put("album_id", Integer.parseInt(album_id));
            json.put("title", title);
            json.put("approved", false);
            json.put("email", email);
            json.put("username",username);
            json.put("photo_uri", photo_uri);
            signiture = computeHmac(json.toString().replace("\\/", "/" ), PXLClient.secretKey);
        } catch (JSONException e) {
            e.printStackTrace();
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
        //return Base64.encodeToString(bytes, Base64.DEFAULT);
        return Base64.encodeToString(bytes, Base64.NO_WRAP); //Use Base64.NO_WRAP because Base64.DEFAULT put \n at the end of its string
    }
}
