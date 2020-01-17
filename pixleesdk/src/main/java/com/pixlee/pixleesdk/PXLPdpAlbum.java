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

    /***
     * Requests the next page of photos from the Pixlee album. Make sure to set perPage,
     * sort order, and filter options before calling.
     * @param handlers - called upon success/failure of the request
     * @return true if the request was attempted, false if aborted before the attempt was made
     */
    @Override
    public boolean loadNextPageOfPhotos(final RequestHandlers handlers) {
        if (sku == null) {
            Log.w(TAG, "No sku specified");
            return false;
        }
        if (this.hasMore) {
            int desiredPage = this.lastPageLoaded + 1;
            if (pagesLoading.get(desiredPage) != null && pagesLoading.get(desiredPage)) {
                Log.d(TAG, String.format("page %s already loading", desiredPage));
                return false;
            }
            this.pagesLoading.put(desiredPage, true);

            try {
                basicRepo.getPhotosWithSKU(
                        sku,
                        PXLClient.apiKey,
                        filterOptions != null ? filterOptions.toParamString() : null,
                        sortOptions != null ? sortOptions.toParamString() : null,
                        perPage,
                        desiredPage
                ).enqueue(new Callback<PhotoResult>() {
                    @Override
                    public void onResponse(Call<PhotoResult> call, Response<PhotoResult> response) {
                        PhotoResult result = response.body();
                        Log.e("retrofit result", "retrofit result:" + result.total);
                        Log.e("retrofit result", "retrofit result:" + result.photos.size());
                        for (PXLPhoto photo : result.photos) {
                            Log.e("retrofit result", "retrofit cdnSmallUrl:" + photo.cdnMediumUrl);

                        }
                        //JSONObject json = new JSONObject(result);
                        if (handlers != null) {
                            handlers.DataLoadedHandler(result.photos);
                        }
                    }

                    @Override
                    public void onFailure(Call<PhotoResult> call, Throwable t) {
                        if (handlers != null) {
                            handlers.DataLoadFailedHandler(t.toString());
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return true;
    }
}
