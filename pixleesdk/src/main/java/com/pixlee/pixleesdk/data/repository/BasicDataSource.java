package com.pixlee.pixleesdk.data.repository;

import com.pixlee.pixleesdk.PXLPhoto;
import com.pixlee.pixleesdk.data.PhotoResult;

import org.json.JSONObject;

import retrofit2.Call;

/**
 * The entry point for accessing basic data.
 */
public interface BasicDataSource {
    Call<PhotoResult> getPhotosWithSKU(
            String sku,
            String api_key,
            String filters,
            String sort,
            int per_page,
            int page
    );

    Call<PhotoResult> getPhotosWithID(
            String album_id,
            String api_key,
            String filters,
            String sort,
            int per_page,
            int page
    );

    Call<PXLPhoto> getMedia(String album_photo_id, String api_key);

    Call<PhotoResult> postMedia(
            String api_key,
            JSONObject json
    );
}
