package com.pixlee.pixleesdk;

import android.content.Context;
import android.util.Log;
import com.android.volley.VolleyError;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jason on 4/4/2017.
 */

public class PXLAlbum implements RequestCallbacks {
    public static final int DefaultPerPage = 20;

    private String id = null;
    private int page;
    private int perPage;
    private boolean hasMore;
    private int lastPageLoaded;
    private ArrayList<PXLPhoto> photos;
    private PXLAlbumFilterOptions filterOptions;
    private PXLAlbumSortOptions sortOptions;
    private HashMap<Integer, Boolean> pagesLoading;
    private RequestHandlers handlers;
    private Context context;

    @Override
    public void JsonReceived(JSONObject response) {
        Log.w("pxlalbum", response.toString());
        try {
            this.page = response.getInt("page");
            this.perPage = response.getInt(("per_page"));
            this.hasMore = response.getBoolean(("next"));
            //add placeholders for photos if they haven't been loaded yet
            //TODO: is this possible?
            if (this.photos.size() < (this.page - 1) * this.perPage) {
                for (int i = this.photos.size(); i < (this.page - 1) * this.perPage; i++) {
                    this.photos.add(null);
                }
            }
            this.photos.addAll(this.photos.size(), PXLPhoto.fromJsonArray(response.getJSONArray("data"), this));
            this.lastPageLoaded = Math.max(this.page, this.lastPageLoaded);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (handlers != null) {
            handlers.DataLoadedHandler(this.photos);
        }
    }

    @Override
    public void ErrorResponse(VolleyError error) {
        Log.e("pxlalbum", "failed to make call");
        if (handlers != null) {
            handlers.DataLoadFailedHandler(error.toString());
        }
    }

    public interface RequestHandlers {
        void DataLoadedHandler(ArrayList<PXLPhoto> photos);
        void DataLoadFailedHandler(String error);
    }

    public PXLAlbum(String id, Context context) {
        this.id = id;
        this.page = 0;
        this.perPage = DefaultPerPage;
        this.hasMore = true;
        this.lastPageLoaded = 0;
        this.photos = new ArrayList<>();
        this.pagesLoading = new HashMap<>();
        this.context = context;
        Log.v("pxlalbum", "album initialized with id " + id);
    }

    /***
     * requests the next page of photos from the Pixlee album
     * @param handlers - called upon success/failure of the request
     * @return true if the request was attempted, false if aborted before the attempt was made
     */
    public boolean loadNextPageOfPhotos(final RequestHandlers handlers) {
        if (id == null) {
            return false;
        }
        if (this.hasMore) {
            int desiredPage = this.lastPageLoaded + 1;
            if (pagesLoading.get(desiredPage) != null && pagesLoading.get(desiredPage)) {
                Log.d("pxlalbum", String.format("page %s already loading", desiredPage));
                return false;
            }
            PXLClient pxlClient = PXLClient.getInstance(context);
            String requestPath = String.format("albums/%s/photos", this.id);
            Log.w("pxlalbum", String.format("making a request to %s", requestPath));
            this.pagesLoading.put(desiredPage, true);
            this.handlers = handlers;
            pxlClient.makeCall(requestPath, getRequestParams(desiredPage), this);
        }

        return true;
    }

    public void setPerPage(int perPage) {
        this.perPage = perPage;
        this.resetState();
    }

    public void setFilterOptions(PXLAlbumFilterOptions filterOptions) {
        this.filterOptions = filterOptions;
        this.resetState();
    }

    public void setSortOptions(PXLAlbumSortOptions sortOptions) {
        this.sortOptions = sortOptions;
        this.resetState();
    }

    private void resetState() {
        this.photos.clear();
        this.lastPageLoaded = 0;
        this.hasMore = true;
        this.pagesLoading.clear();
    }

    private HashMap<String, Object> getRequestParams(int desiredPage) {
        HashMap<String, Object> paramMap = new HashMap<>();
        if (filterOptions != null) {
            paramMap.put(PXLClient.KeyFilters, filterOptions.toParamString());
        }
        if (sortOptions != null) {
            paramMap.put(PXLClient.KeySort, sortOptions.toParamString());
        }
        paramMap.put(PXLClient.KeyPerPage, perPage);
        paramMap.put(PXLClient.KeyPage, desiredPage);
        return paramMap;
    }
}
