package com.pixlee.pixleesdk;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

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
            String requestPath = "albums/from_sku";
            this.pagesLoading.put(desiredPage, true);
            this.handlers = handlers;
            pxlClient.makeCall(requestPath, getRequestParams(desiredPage), this);
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
