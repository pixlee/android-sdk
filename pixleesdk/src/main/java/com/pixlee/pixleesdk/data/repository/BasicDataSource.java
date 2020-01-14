package com.pixlee.pixleesdk.data.repository;

import com.pixlee.pixleesdk.data.AlbumResult;

import org.json.JSONObject;

import retrofit2.Call;

/**
 * The entry point for accessing basic data.
 */
public interface BasicDataSource {
    Call<AlbumResult> getPhotosWithSKU(
            String sku,
            String api_key,
            String filters,
            String sort,
            int per_page,
            int page
    );

    Call<String> getPhotosWithID(
            String id,
            String api_key,
            String filters,
            String sort,
            int per_page,
            int page
    );

    Call<String> getMedia(String id, String api_key);

    Call<String> postMedia(
            String api_key,
            JSONObject json
    );
}
