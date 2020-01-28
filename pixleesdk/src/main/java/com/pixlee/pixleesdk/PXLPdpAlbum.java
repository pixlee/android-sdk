package com.pixlee.pixleesdk;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PXLPdpAlbum extends PXLAlbum {
    private static final String TAG = "PXLPdpAlbum";
    private final String sku;

    /***
     * Constructor requires the product sku and context, which will be passed along to the PXLClient
     * for volley configuration.
     * @param sku - product sku
     * @param context - context which will be used for volley configuration
     */
    public PXLPdpAlbum(String sku, Context context) {
        super(sku, context);
        this.sku = sku;
        this.id = null;
        this.page = 0;
        this.perPage = DefaultPerPage;
        this.hasMore = true;
        this.lastPageLoaded = 0;
        this.photos = new ArrayList<>();
        this.pagesLoading = new HashMap<>();
        this.context = context;
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
            PXLClient pxlClient = PXLClient.getInstance(context);
            this.pagesLoading.put(desiredPage, true);
            this.handlers = handlers;

            try {
                pxlClient
                        .getBasicrepo()
                        .getPhotosWithSKU(
                                sku,
                                PXLClient.apiKey,
                                filterOptions != null ? filterOptions.toParamString() : null,
                                sortOptions != null ? sortOptions.toParamString() : null,
                                perPage,
                                desiredPage
                        ).enqueue(
                        new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                try {
                                    processResponse(response);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                if (handlers != null) {
                                    handlers.DataLoadFailedHandler(t.toString());
                                }
                            }
                        }
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    @Override
    protected HashMap<String, Object> getRequestParams(int desiredPage) {
        HashMap<String, Object> paramMap = new HashMap<>();
        if (filterOptions != null) {
            paramMap.put(PXLClient.KeyFilters, filterOptions.toParamString());
        }
        if (sortOptions != null) {
            paramMap.put(PXLClient.KeySort, sortOptions.toParamString());
        }
        paramMap.put(PXLClient.KeyPerPage, perPage);
        paramMap.put(PXLClient.KeyPage, desiredPage);
        paramMap.put(PXLClient.KeySku, sku);
        return paramMap;
    }
}
