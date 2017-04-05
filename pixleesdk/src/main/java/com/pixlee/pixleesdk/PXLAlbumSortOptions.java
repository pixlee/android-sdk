package com.pixlee.pixleesdk;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jason on 4/5/2017.
 */

public class PXLAlbumSortOptions {
    public PXLAlbumSortType sortType = PXLAlbumSortType.RECENCY;
    public boolean descending = false;

    public static final String KeyDesc = "desc";

    public String toParamString() {
        JSONObject params = new JSONObject();
        try {
            params.put(sortType.value, true);
            params.put(KeyDesc, descending);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return params.toString();
    }
}
