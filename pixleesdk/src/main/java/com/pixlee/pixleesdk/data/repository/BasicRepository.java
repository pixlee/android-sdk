package com.pixlee.pixleesdk.data.repository;

import com.pixlee.pixleesdk.data.api.BasicAPI;

import okhttp3.ResponseBody;
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
        return api.postMedia(api_key, album_id, title, email, username, photo_uri, approved);
    }
}
