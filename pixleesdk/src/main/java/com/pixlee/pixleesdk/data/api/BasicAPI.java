package com.pixlee.pixleesdk.data.api;

import com.pixlee.pixleesdk.PXLClient;
import com.pixlee.pixleesdk.PXLPhoto;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BasicAPI {
    @GET("/albums/{id}/photos")
    List<PXLPhoto> getPhoto(
            @Path("id")String id,
            @Query("api_key")String api_key,
            @Query("filters")int filters
    );

        if (filterOptions != null) {
        paramMap.put(PXLClient.KeyFilters, filterOptions.toParamString());
    }
        if (sortOptions != null) {
        paramMap.put(PXLClient.KeySort, sortOptions.toParamString());
    }
        paramMap.put(PXLClient.KeyPerPage, perPage);
        paramMap.put(PXLClient.KeyPage, desiredPage);
}
