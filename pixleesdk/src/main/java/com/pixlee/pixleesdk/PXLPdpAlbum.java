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

    /***
     * Requests the next page of photos from the Pixlee album. Make sure to set perPage,
     * sort order, and filter options before calling.
     * @param handlers - called upon success/failure of the request
     * @return true if the request was attempted, false if aborted before the attempt was made
     */
    @Override
    public void loadNextPageOfPhotos(final RequestHandlers handlers) {
        Call<PhotoResult> call = makeCall();

        if(call==null)
            return;

        call.enqueue(new Callback<PhotoResult>() {
            @Override
            public void onResponse(Call<PhotoResult> call, Response<PhotoResult> response) {
                setData(response, handlers);
            }

            @Override
            public void onFailure(Call<PhotoResult> call, Throwable t) {
                if (handlers != null) {
                    handlers.DataLoadFailedHandler(t.toString());
                }
            }
        });
    }
}
