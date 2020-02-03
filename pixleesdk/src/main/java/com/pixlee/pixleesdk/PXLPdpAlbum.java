package com.pixlee.pixleesdk;

import android.content.Context;
import android.util.Log;

import com.pixlee.pixleesdk.data.PhotoResult;
import com.pixlee.pixleesdk.data.repository.AnalyticsDataSource;
import com.pixlee.pixleesdk.data.repository.BasicDataSource;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ProductAlbum ViewModel of MVVM architecture
 */
public class PXLPdpAlbum extends PXLBaseAlbum {
    private static final String TAG = "PXLPdpAlbum";
    private final String sku;

    /***
     * Constructor requires the product sku and context, which will be passed along to the PXLClient
     * for volley configuration.
     * @param sku - product sku
     * @param basicRepo     Restful API for photos
     * @param analyticsRepo Restful API for analytics
     */
    public PXLPdpAlbum(String sku, BasicDataSource basicRepo, AnalyticsDataSource analyticsRepo) {
        super(basicRepo, analyticsRepo);
        this.sku = sku;
    }

    /**
     * This is for unit test. Not for the use
     * @return
     */
    @Override
    Call<PhotoResult> makeCall() {
        if (sku == null) {
            Log.w(TAG, "No sku specified");
            return null;
        }
        if (!this.hasMore) {
            Log.w(TAG, "no need to load more");
            return null;
        }

        int desiredPage = this.lastPageLoaded + 1;
        Boolean isPageLoading = pagesLoading.get(desiredPage);
        if (isPageLoading != null && isPageLoading) {
            Log.d(TAG, String.format("page %s already loading", desiredPage));
            return null;
        }

        this.pagesLoading.put(desiredPage, true);

        return basicRepo.getPhotosWithSKU(
                sku,
                PXLClient.apiKey,
                filterOptions != null ? filterOptions.toParamString() : null,
                sortOptions != null ? sortOptions.toParamString() : null,
                perPage,
                desiredPage
        );
    }
}
