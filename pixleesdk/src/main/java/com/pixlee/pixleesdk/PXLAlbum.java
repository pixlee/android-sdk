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
    private String id = null;
    private int page = 1;
    private int perPage = 20;
    private int totalPages;
    private boolean hasMore;
    private ArrayList<PXLPhoto> photos;
    private PXLAlbumFilterOptions filterOptions;
    private PXLAlbumSortOptions sortOptions;

    public interface RequestHandlers {
        public void DataLoadedHandler(ArrayList<PXLPhoto> photos);
        public void DataLoadFailedHandler(String error);
    }

    public PXLAlbum(String id) {
        this.id = id;
        Log.v("pxlalbum", "album initialized with id " + id);
    }

    public boolean loadNextPageOfPhotos(RequestHandlers handlers) {
        if (id == null) {
            return false;
        }

        PXLClient s = PXLClient.getInstance();
        String requestPath = String.format("albums/%s/photos", this.id);
        Log.w("pxlalbum", String.format("making a request to %s", requestPath));
        s.makeCall(this, handlers, requestPath, new RequestCallbacks() {
            @Override
            public void JsonReceived(RequestHandlers requestHandlers, Object caller, JSONObject response) {
                Log.w("pxlalbum", response.toString());
                PXLAlbum parent = (PXLAlbum) caller;
                try {
                    parent.page = response.getInt("page");
                    parent.perPage = response.getInt(("per_page"));
                    parent.totalPages = response.getInt(("total"));
                    parent.hasMore = response.getBoolean(("next"));
                    parent.photos = PXLPhoto.fromJsonArray(response.getJSONArray("data"));//PXLAlbum.parseResponse(response.getJSONArray("data"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (requestHandlers != null) {
                    requestHandlers.DataLoadedHandler(parent.photos);
                }
            }

            @Override
            public void ErrorResponse(VolleyError error) {
                Log.e("pxlalbum", "failed to make call");
            }
        }, getRequestParams());
        return true;
    }

    public void setPerPage(int perPage) {
        this.perPage = perPage;
        //clear and reload???
    }

    public void setFilterOptions(PXLAlbumFilterOptions filterOptions) {
        this.filterOptions = filterOptions;
        //TODO: reset/reload
    }

    public void setSortOptions(PXLAlbumSortOptions sortOptions) {
        this.sortOptions = sortOptions;
        //TODO: reset/reload
    }

    private HashMap<String, Object> getRequestParams() {
        HashMap<String, Object> paramMap = new HashMap<>();
        if (filterOptions != null) {
            paramMap.put(PXLClient.KeyFilters, filterOptions.toParamString());
        }
        if (sortOptions != null) {
            paramMap.put(PXLClient.KeySort, sortOptions.toParamString());
        }
        paramMap.put(PXLClient.KeyPerPage, perPage);
        paramMap.put(PXLClient.KeyPage, page);
        return paramMap;
    }
}
