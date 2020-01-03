package com.pixlee.pixleesdk.data.repository;

import okhttp3.ResponseBody;
import retrofit2.Call;

public interface BasicDataSource {
    Call<String> getPhotosWithSKU(
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
            String album_id,
            String title,
            String email,
            String username,
            String photo_uri,
            boolean approved
    );
}
