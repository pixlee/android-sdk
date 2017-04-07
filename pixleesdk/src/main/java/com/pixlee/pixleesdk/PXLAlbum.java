package com.pixlee.pixleesdk;

import android.util.Log;
import com.android.volley.VolleyError;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jason on 4/4/2017.
 */

public class PXLAlbum {
    public static final int DefaultPerPage = 20;

    private String id = null;
    private int page;
    private int perPage;
    private int totalPages;
    private boolean hasMore;
    private int lastPageLoaded;
    private ArrayList<PXLPhoto> photos;
    private PXLAlbumFilterOptions filterOptions;
    private PXLAlbumSortOptions sortOptions;
    private HashMap<Integer, Boolean> pagesLoading;

    public interface RequestHandlers {
        void DataLoadedHandler(ArrayList<PXLPhoto> photos);
        void DataLoadFailedHandler(String error);
    }

    public PXLAlbum(String id) {
        this.id = id;
        this.page = 0;
        this.perPage = DefaultPerPage;
        this.hasMore = true;
        this.lastPageLoaded = 0;
        this.photos = new ArrayList<>();
        this.pagesLoading = new HashMap<>();
        Log.v("pxlalbum", "album initialized with id " + id);
    }

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
            //TODO: add pagination logic
            PXLClient pxlClient = PXLClient.getInstance();
            String requestPath = String.format("albums/%s/photos", this.id);
            Log.w("pxlalbum", String.format("making a request to %s", requestPath));
            this.pagesLoading.put(desiredPage, true);
            pxlClient.makeCall(requestPath, getRequestParams(desiredPage), this, new RequestCallbacks() {
                @Override
                public void JsonReceived(Object caller, JSONObject response) {
                    Log.w("pxlalbum", response.toString());
                    PXLAlbum parent = (PXLAlbum) caller;
                    try {
                        parent.page = response.getInt("page");
                        parent.perPage = response.getInt(("per_page"));
                        parent.totalPages = response.getInt(("total"));
                        parent.hasMore = response.getBoolean(("next"));
                        //add placeholders for photos if they haven't been loaded yet
                        //TODO: is this possible?
                        if (parent.photos.size() < (parent.page - 1) * parent.perPage) {
                            for (int i = parent.photos.size(); i < (parent.page - 1) * parent.perPage; i++) {
                                parent.photos.add(null);
                            }
                        }
                        parent.photos.addAll(parent.photos.size(), PXLPhoto.fromJsonArray(response.getJSONArray("data")));
                        //parent.photos = PXLPhoto.fromJsonArray(response.getJSONArray("data"));
                        parent.lastPageLoaded = Math.max(parent.page, parent.lastPageLoaded);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (handlers != null) {
                        handlers.DataLoadedHandler(parent.photos);
                    }
                }

                @Override
                public void ErrorResponse(VolleyError error) {
                    Log.e("pxlalbum", "failed to make call");
                    if (handlers != null) {
                        handlers.DataLoadFailedHandler(error.toString());
                    }
                }
            });
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
